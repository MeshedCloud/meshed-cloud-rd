package cn.meshed.cloud.rd.deployment.executor.command;

import cn.meshed.cloud.cqrs.EventExecute;
import cn.meshed.cloud.rd.domain.log.Trend;
import cn.meshed.cloud.rd.project.event.ProjectInitializeEvent;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * <h1>项目初始化系统配置初始化</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SystemConfigByProjectEventExe implements EventExecute<ProjectInitializeEvent, Response> {
    /**
     * <h1>执行器</h1>
     *
     * @param projectInitializeEvent 执行器 {@link ProjectInitializeEvent}
     * @return {@link Consumer <ProjectInitializeEvent>}
     */
    @Trend(key = "#{projectInitializeEvent.key}", content = "#{projectInitializeEvent.name}+配置项目")
    @Override
    public Response execute(ProjectInitializeEvent projectInitializeEvent) {

        System.out.println("SystemConfig init " + projectInitializeEvent);
        return ResultUtils.ok();
    }
}