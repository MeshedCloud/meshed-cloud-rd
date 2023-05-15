package cn.meshed.cloud.rd.deployment.executor.command;

import cn.meshed.cloud.cqrs.EventExecute;
import cn.meshed.cloud.rd.deployment.enums.EnvironmentEnum;
import cn.meshed.cloud.rd.deployment.enums.PublishTypeEnum;
import cn.meshed.cloud.rd.deployment.enums.VersionStatusEnum;
import cn.meshed.cloud.rd.deployment.event.VersionPublishEvent;
import cn.meshed.cloud.rd.domain.deployment.Version;
import cn.meshed.cloud.rd.domain.deployment.gateway.VersionGateway;
import cn.meshed.cloud.rd.domain.deployment.strategy.AsyncPublishStrategy;
import cn.meshed.cloud.rd.domain.deployment.strategy.Publish;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishType;
import cn.meshed.cloud.rd.domain.log.Trend;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.exception.SysException;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class VersionPublishEventExe implements EventExecute<VersionPublishEvent, Response> {

    private final AsyncPublishStrategy asyncPublishStrategy;
    private final VersionGateway versionGateway;
    /**
     * <h1>执行器</h1>
     *
     * @param versionPublishEvent 执行器 {@link VersionPublishEvent}
     * @return {@link Response}
     */
    @Trend(key = "#{versionPublishEvent.projectKey}", content = "#{versionPublishEvent.message}+发布处理")
    @Transactional
    @Override
    public Response execute(VersionPublishEvent versionPublishEvent) {
        log.info("版本发布事件: {}", JSONObject.toJSONString(versionPublishEvent));
        Version version = versionGateway.query(versionPublishEvent.getVersionId());
        AssertUtils.isTrue(version != null, "版本不存在拒绝执行");
        Publish publish = CopyUtils.copy(versionPublishEvent, Publish.class);
        asyncPublishStrategy.asyncPublish(
                getPublishType(versionPublishEvent.getPublishType(), versionPublishEvent.getEnvironment()), publish);
        assert version != null;
        version.setStatus(VersionStatusEnum.BUILD);
        versionGateway.change(version);
        return ResultUtils.ok();
    }

    @NotNull
    private PublishType getPublishType(PublishTypeEnum publishType, EnvironmentEnum environment) {
        if (publishType == PublishTypeEnum.CLIENT && environment == EnvironmentEnum.SNAPSHOT) {
            return PublishType.CLIENT;
        } else if (publishType == PublishTypeEnum.CLIENT && environment == EnvironmentEnum.RELEASE) {
            return PublishType.RELEASE;
        }
        throw new SysException("其他发布暂未实现");
    }
}
