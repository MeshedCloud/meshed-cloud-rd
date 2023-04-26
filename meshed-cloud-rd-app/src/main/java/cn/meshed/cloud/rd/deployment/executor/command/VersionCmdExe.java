package cn.meshed.cloud.rd.deployment.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.deployment.command.VersionCmd;
import cn.meshed.cloud.rd.deployment.enums.EnvironmentEnum;
import cn.meshed.cloud.rd.deployment.enums.PublishTypeEnum;
import cn.meshed.cloud.rd.deployment.enums.VersionStatusEnum;
import cn.meshed.cloud.rd.deployment.enums.VersionTypeEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehousePurposeTypeEnum;
import cn.meshed.cloud.rd.deployment.event.VersionPublishEvent;
import cn.meshed.cloud.rd.domain.common.VersionFormat;
import cn.meshed.cloud.rd.domain.deployment.Version;
import cn.meshed.cloud.rd.domain.deployment.Warehouse;
import cn.meshed.cloud.rd.domain.deployment.gateway.VersionGateway;
import cn.meshed.cloud.rd.domain.deployment.gateway.WarehouseGateway;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishType;
import cn.meshed.cloud.rd.domain.log.Trend;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.rd.project.config.WorkflowProperties;
import cn.meshed.cloud.rd.project.enums.ProjectStatusEnum;
import cn.meshed.cloud.rd.project.executor.query.WaitPublishLegalQryExe;
import cn.meshed.cloud.rd.wrapper.workflow.WorkflowWrapper;
import cn.meshed.cloud.stream.StreamBridgeSender;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.exception.SysException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.meshed.cloud.rd.domain.deployment.constant.MqConstant.VERSION_PUBLISH;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class VersionCmdExe implements CommandExecute<VersionCmd, Response> {

    private final VersionGateway versionGateway;
    private final ProjectGateway projectGateway;
    private final WarehouseGateway warehouseGateway;
    private final StreamBridgeSender streamBridgeSender;
    private final WaitPublishLegalQryExe waitPublishLegalQryExe;
    private final WorkflowWrapper workflowWrapper;
    private final WorkflowProperties workflowProperties;
    private final List<VersionStatusEnum> NOT_ALLOW_STATUS = new ArrayList<VersionStatusEnum>() {{
        add(VersionStatusEnum.SUBMIT);
        add(VersionStatusEnum.BUILD);
        add(VersionStatusEnum.BUILDING);
    }};

    /**
     * <h1>执行器</h1>
     *
     * @param versionCmd 执行器 {@link VersionCmd}
     * @return {@link Response}
     */
    @Trend(key = "#{versionCmd.projectKey}", content = "#{versionCmd.commitMessage}: 提交发布")
    @Transactional
    @Override
    public Response execute(VersionCmd versionCmd) {

        Project project = projectGateway.queryByKey(versionCmd.getProjectKey());
        AssertUtils.isTrue(project != null, "项目不存在");
        AssertUtils.isTrue(project.getStatus() != ProjectStatusEnum.APPLY, "项目未被批准");

        Response response = waitPublishLegalQryExe.execute(versionCmd.getProjectKey());
        if (!response.isSuccess()) {
            return response;
        }

        //区分新建和版本发布（含不同环境）
        if (versionCmd.getVersionId() == null) {
            Version version = buildNewVersion(versionCmd);
            //登记版本信息
            versionGateway.registration(version);
        }

        /**
         * 1.代码推送
         * 2.审批（人工合并）
         * 3.修改状态
         */
        Version version = versionGateway.query(versionCmd.getVersionId());
        VersionPublishEvent event = getVersionPublishEvent(versionCmd, version);
        if (workflowProperties.getEnable()) {
            String flowId = initiateApproval(event);
            version.setFlowId(flowId);
        } else {
            //无需流程审批
            streamBridgeSender.send(VERSION_PUBLISH, event);
            //streamBridgeSender.send(VERSION_PUBLISH_APPROVE, event);
        }

        //更新信息

        if (NOT_ALLOW_STATUS.contains(version.getStatus())) {
            throw new SysException("存在发布队列，禁止重复");
        }
        version.setEnvironment(versionCmd.getEnvironment());
        version.setStatus(VersionStatusEnum.SUBMIT);
        //修改状态环境信息
        versionGateway.change(version);
        return ResultUtils.ok();
    }

    @NotNull
    private Version buildNewVersion(VersionCmd versionCmd) {
        AssertUtils.isTrue(StringUtils.isNotBlank(versionCmd.getProjectKey()), "项目唯一标识不能为空");
        Version version;
        version = new Version();
        version.setVersion(versionCmd.getVersion());
        version.setEnvironment(versionCmd.getEnvironment());
        version.setStatus(VersionStatusEnum.SUBMIT);
        version.setProjectKey(versionCmd.getProjectKey());
        Warehouse warehouse = warehouseGateway.query(versionCmd.getWarehouseId());
        AssertUtils.isTrue(warehouse != null, "项目仓库不存在");
        assert warehouse != null;
        version.setSourceId(versionCmd.getWarehouseId());
        version.setType(VersionTypeEnum.valueOf(warehouse.getPurposeType().getExt()));
        return version;
    }

    public String initiateApproval(VersionPublishEvent event) {
        return workflowWrapper.initiate(workflowProperties.getVersionPublish(), event);
    }

    @NotNull
    private VersionPublishEvent getVersionPublishEvent(VersionCmd versionCmd, Version version) {
        Warehouse warehouse = warehouseGateway.query(version.getSourceId());
        VersionPublishEvent versionPublishEvent = new VersionPublishEvent();
        versionPublishEvent.setVersionId(version.getId());
        versionPublishEvent.setMessage(versionCmd.getCommitMessage());
        versionPublishEvent.setSourceId(warehouse.getRepoId());
        versionPublishEvent.setProjectKey(versionCmd.getProjectKey());
        versionPublishEvent.setPublishType(toPublishType(warehouse));
        versionPublishEvent.setEnvironment(versionCmd.getEnvironment());
        versionPublishEvent.setTypeName(versionPublishEvent.getPublishType().getExt());
        versionPublishEvent.setPublishName(warehouse.getName());
        versionPublishEvent.setVersion(VersionFormat.version(version.getVersion()));
        return versionPublishEvent;
    }

    private PublishTypeEnum toPublishType(Warehouse warehouse) {
        if (warehouse.getPurposeType() == WarehousePurposeTypeEnum.CLIENT) {
            return PublishTypeEnum.CLIENT;
        }
        throw new SysException("其他未实现");
    }

    @NotNull
    private PublishType getPublishType(PublishTypeEnum publishType, EnvironmentEnum environment) {
        if (publishType == PublishTypeEnum.CLIENT && environment == EnvironmentEnum.SNAPSHOT) {
            return PublishType.CLIENT;
        }
        throw new SysException("其他发布暂未实现");
    }
}
