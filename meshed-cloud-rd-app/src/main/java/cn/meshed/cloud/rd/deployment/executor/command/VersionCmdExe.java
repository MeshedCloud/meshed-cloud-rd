package cn.meshed.cloud.rd.deployment.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.deployment.command.VersionCmd;
import cn.meshed.cloud.rd.deployment.enums.PublishTypeEnum;
import cn.meshed.cloud.rd.deployment.enums.VersionStatusEnum;
import cn.meshed.cloud.rd.deployment.enums.VersionTypeEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehousePurposeTypeEnum;
import cn.meshed.cloud.rd.deployment.event.VersionPublishEvent;
import cn.meshed.cloud.rd.domain.deployment.Version;
import cn.meshed.cloud.rd.domain.deployment.Warehouse;
import cn.meshed.cloud.rd.domain.deployment.gateway.VersionGateway;
import cn.meshed.cloud.rd.domain.deployment.gateway.WarehouseGateway;
import cn.meshed.cloud.stream.StreamBridgeSender;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.exception.SysException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private final WarehouseGateway warehouseGateway;
    private final StreamBridgeSender streamBridgeSender;

    @Value("${workflow.approve.enable:false}")
    private boolean approveEnable;

    /**
     * <h1>执行器</h1>
     *
     * @param versionCmd 执行器 {@link VersionCmd}
     * @return {@link Response}
     */
    @Override
    public Response execute(VersionCmd versionCmd) {
        Version version = null;
        //区分新建和版本发布（含不同环境）
        if (versionCmd.getVersionId() == null) {
            AssertUtils.isTrue(StringUtils.isNotBlank(versionCmd.getProjectKey()), "项目唯一标识不能为空");
            //登记版本信息
            version = new Version();
            version.setVersion(versionCmd.getVersion());
            version.setEnvironment(versionCmd.getEnvironment());
            version.setStatus(VersionStatusEnum.SUBMIT);
            version.setProjectKey(versionCmd.getProjectKey());

            Warehouse warehouse = warehouseGateway.query(versionCmd.getWarehouseId());
            AssertUtils.isTrue(warehouse != null, "项目仓库不存在");
            assert warehouse != null;
            version.setSourceId(versionCmd.getWarehouseId());
            version.setType(VersionTypeEnum.valueOf(warehouse.getPurposeType().getKey()));
            versionGateway.registration(version);
        } else {
            //环境存在
            version = versionGateway.query(versionCmd.getVersionId());
            version.setEnvironment(versionCmd.getEnvironment());
            version.setStatus(VersionStatusEnum.SUBMIT);
            //修改状态环境信息
            versionGateway.change(version);
        }
        VersionPublishEvent versionPublishEvent = getVersionPublishEvent(versionCmd, version);
        //如果审批流程启用发起审批，又审批进行触达构建
        if (approveEnable) {

        } else {
            //如果审批流程未启用直接发送构建事件
            streamBridgeSender.send(VERSION_PUBLISH, versionPublishEvent);
        }

        return ResultUtils.ok();
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
        return versionPublishEvent;
    }

    private PublishTypeEnum toPublishType(Warehouse warehouse) {
        if (warehouse.getPurposeType() == WarehousePurposeTypeEnum.CLIENT) {
            return PublishTypeEnum.CLIENT;
        }
        throw new SysException("其他未实现");
    }
}
