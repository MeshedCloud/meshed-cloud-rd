package cn.meshed.cloud.rd.deployment.executor.command;

import cn.meshed.cloud.cqrs.EventExecute;
import cn.meshed.cloud.rd.deployment.enums.EnvironmentEnum;
import cn.meshed.cloud.rd.deployment.enums.PublishTypeEnum;
import cn.meshed.cloud.rd.deployment.enums.VersionStatusEnum;
import cn.meshed.cloud.rd.deployment.event.VersionPublishEvent;
import cn.meshed.cloud.rd.domain.deployment.Version;
import cn.meshed.cloud.rd.domain.deployment.VersionOccupy;
import cn.meshed.cloud.rd.domain.deployment.VersionOccupyGateway;
import cn.meshed.cloud.rd.domain.deployment.gateway.VersionGateway;
import cn.meshed.cloud.rd.domain.log.Trend;
import cn.meshed.cloud.rd.project.command.ServiceModelPublishCmd;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.enums.ServiceModelTypeEnum;
import cn.meshed.cloud.rd.project.executor.command.ServiceModelPublishCmdExe;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class VersionPublishApproveEventExe implements EventExecute<VersionPublishEvent, Response> {

    private final VersionGateway versionGateway;
    private final VersionOccupyGateway versionOccupyGateway;
    private final ServiceModelPublishCmdExe serviceModelPublishCmdExe;

    /**
     * <h1>执行器</h1>
     *
     * @param versionPublishEvent 执行器 {@link VersionPublishEvent}
     * @return {@link Response}
     */
    @Trend(key = "#{versionPublishEvent.projectKey}", content = "#{versionPublishEvent.message}+发布完成")
    @Transactional
    @Override
    public Response execute(VersionPublishEvent versionPublishEvent) {
        log.info("版本审批事件 {}", JSONObject.toJSONString(versionPublishEvent));
        Version version = versionGateway.query(versionPublishEvent.getVersionId());
        AssertUtils.isTrue(version != null, "版本不能为空");
        //修改相关版本状态
        assert version != null;
        //客户端需要单独更新状态和解除禁用
        if (versionPublishEvent.getPublishType() == PublishTypeEnum.CLIENT) {
            handleServiceModelVersionOccupy(versionPublishEvent, version);
            //清除占用关系
            versionOccupyGateway.delete(version.getId());
        }

        //更新状态
        version.setStatus(VersionStatusEnum.PUBLISHED);
        version.setFlowId("");
        versionGateway.change(version);
        return ResultUtils.ok();
    }

    private void handleServiceModelVersionOccupy(VersionPublishEvent versionPublishEvent, Version version) {
        Set<VersionOccupy> versionOccupies = versionOccupyGateway.searchList(version.getId());
        if (CollectionUtils.isNotEmpty(versionOccupies)) {
            Map<ServiceModelTypeEnum, List<VersionOccupy>> occupyTypeListMap = versionOccupies.stream()
                    .collect(Collectors.groupingBy(VersionOccupy::getType));
            if (!occupyTypeListMap.isEmpty()) {
                occupyTypeListMap.forEach((type, value) -> {

                    if (CollectionUtils.isEmpty(value)) {
                        return;
                    }
                    ServiceModelPublishCmd serviceModelPublishCmd = new ServiceModelPublishCmd();
                    Set<String> uuids = value.stream().map(VersionOccupy::getRelationId).collect(Collectors.toSet());
                    serviceModelPublishCmd.setUuids(uuids);
                    serviceModelPublishCmd.setType(type);
                    serviceModelPublishCmd.setReleaseStatus(
                            versionPublishEvent.getEnvironment() == EnvironmentEnum.SNAPSHOT ?
                                    ReleaseStatusEnum.SNAPSHOT : ReleaseStatusEnum.RELEASE);
                    serviceModelPublishCmdExe.execute(serviceModelPublishCmd);
                });

            }
        } else {
            log.error("发布确认未解除版本关系");
        }
    }

}
