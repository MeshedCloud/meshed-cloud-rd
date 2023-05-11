package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.log.Trend;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.rd.project.enums.ProjectStatusEnum;
import cn.meshed.cloud.rd.project.event.ProjectInitializeEvent;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ProjectApproveEventExe implements CommandExecute<ProjectInitializeEvent, Response> {

    private final ProjectGateway projectGateway;

    /**
     * 项目审批通过处理器
     *
     * @param projectInitializeEvent 项目初始化实际参数
     * @return
     */
    @Trend(key = "#{projectInitializeEvent.key}", content = "#{projectInitializeEvent.name}+项目立项通过")
    @Override
    public Response execute(ProjectInitializeEvent projectInitializeEvent) {
        Project project = projectGateway.queryByKey(projectInitializeEvent.getKey());
        AssertUtils.isTrue(project != null, "项目不存在");
        assert project != null;
        AssertUtils.isTrue(project.getStatus() == ProjectStatusEnum.APPLY, "项目状态转换失败");
        projectGateway.updateStatus(projectInitializeEvent.getKey(), ProjectStatusEnum.RD);
        return ResultUtils.ok();
    }

}
