package cn.meshed.cloud.rd.project.executor.listener;

import cn.meshed.cloud.rd.project.event.ProjectInitializeEvent;
import cn.meshed.cloud.rd.project.executor.command.ProjectApproveEventExe;
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
public class ProjectListener {

    private final ProjectApproveEventExe projectApproveEventExe;

    @Bean
    public Consumer<ProjectInitializeEvent> approveProject() {
        return projectApproveEventExe::execute;
    }

}
