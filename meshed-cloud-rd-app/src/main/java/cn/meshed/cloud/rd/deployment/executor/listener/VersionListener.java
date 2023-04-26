package cn.meshed.cloud.rd.deployment.executor.listener;

import cn.meshed.cloud.rd.deployment.event.VersionPublishEvent;
import cn.meshed.cloud.rd.deployment.executor.command.VersionPublishApproveEventExe;
import cn.meshed.cloud.rd.deployment.executor.command.VersionPublishEventExe;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Configuration
public class VersionListener {

    private final VersionPublishEventExe versionPublishEventExe;
    private final VersionPublishApproveEventExe versionPublishApproveEventExe;

    @Bean
    public Consumer<VersionPublishEvent> versionPublishStrategy() {
        return versionPublishEventExe::execute;
    }

    @Bean
    public Consumer<VersionPublishEvent> versionPublishApprove() {
        return versionPublishApproveEventExe::execute;
    }
}
