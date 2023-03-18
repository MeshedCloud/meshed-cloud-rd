package cn.meshed.cloud.rd.deployment.executor.command;

import cn.meshed.cloud.cqrs.EventExecute;
import cn.meshed.cloud.rd.deployment.enums.EnvironmentEnum;
import cn.meshed.cloud.rd.deployment.enums.PublishTypeEnum;
import cn.meshed.cloud.rd.deployment.event.VersionPublishEvent;
import cn.meshed.cloud.rd.domain.deployment.strategy.AsyncPublishStrategy;
import cn.meshed.cloud.rd.domain.deployment.strategy.Publish;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishType;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.exception.SysException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

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

    /**
     * <h1>执行器</h1>
     *
     * @param versionPublishEvent 执行器 {@link VersionPublishEvent}
     * @return {@link Response}
     */
    @Override
    public Response execute(VersionPublishEvent versionPublishEvent) {
        Publish publish = CopyUtils.copy(versionPublishEvent, Publish.class);
        asyncPublishStrategy.asyncPublish(
                getPublishType(versionPublishEvent.getPublishType(), versionPublishEvent.getEnvironment()), publish);
        return ResultUtils.ok();
    }

    @NotNull
    private PublishType getPublishType(PublishTypeEnum publishType, EnvironmentEnum environment) {
        if (publishType == PublishTypeEnum.CLIENT && environment == EnvironmentEnum.SNAPSHOT) {
            return PublishType.CLIENT;
        }
        throw new SysException("其他发布暂未实现");
    }
}
