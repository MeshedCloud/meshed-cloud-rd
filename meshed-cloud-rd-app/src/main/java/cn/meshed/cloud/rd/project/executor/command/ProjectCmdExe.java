package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.log.Trend;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.rd.domain.repo.CreateRepositoryGroup;
import cn.meshed.cloud.rd.domain.repo.RepositoryGroup;
import cn.meshed.cloud.rd.domain.repo.gateway.RepositoryGateway;
import cn.meshed.cloud.rd.project.command.ProjectCmd;
import cn.meshed.cloud.rd.project.config.WorkflowProperties;
import cn.meshed.cloud.rd.project.enums.ProjectAccessModeEnum;
import cn.meshed.cloud.rd.project.event.ProjectInitializeEvent;
import cn.meshed.cloud.rd.wrapper.workflow.WorkflowWrapper;
import cn.meshed.cloud.stream.StreamBridgeSender;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static cn.meshed.cloud.rd.domain.project.constant.MqConstant.APPROVE_PROJECT;
import static cn.meshed.cloud.rd.domain.project.constant.MqConstant.PROJECT_INITIALIZE;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ProjectCmdExe implements CommandExecute<ProjectCmd, Response> {

    private final ProjectGateway projectGateway;
    private final StreamBridgeSender streamBridgeSender;
    private final RepositoryGateway repositoryGateway;
    private final WorkflowWrapper workflowWrapper;
    private final WorkflowProperties workflowProperties;
    @Value("${rd.project.group-format}")
    private String groupFormat;

    /**
     * 仅新增项目处理器
     *
     * @param projectCmd
     * @return
     */
    @Trend(key = "#{projectCmd.key}", content = "项目立项")
    @Override
    public Response execute(ProjectCmd projectCmd) {
        //构建项目信息
        Project project = buildProject(projectCmd);
        //保存项目信息
        Project saveProject = projectGateway.save(project);
        if (saveProject == null) {
            return ResultUtils.fail("发起立项失败");
        }
        //生成初始化事件信息
        ProjectInitializeEvent event = buildProjectInitializeEvent(projectCmd);

        //走审批流
        if (workflowProperties.getEnable()) {
            initiateApproval(event);
        } else {
            //发起初始化事件
            streamBridgeSender.send(PROJECT_INITIALIZE, event);
            //自动审批
            streamBridgeSender.send(APPROVE_PROJECT, event);
        }
        return ResultUtils.ok();
    }

    @Nullable
    private ProjectInitializeEvent buildProjectInitializeEvent(ProjectCmd projectCmd) {
        ProjectInitializeEvent event = CopyUtils.copy(projectCmd, ProjectInitializeEvent.class);
        return event;
    }

    @NotNull
    private Project buildProject(ProjectCmd projectCmd) {
        Project project = CopyUtils.copy(projectCmd, Project.class);
        project.initProject();
        RepositoryGroup repositoryGroup = buildGroup(projectCmd);
        project.setThirdId(repositoryGroup.getGroupId());
        project.setIdentity(repositoryGroup.getGroupName());
        return project;
    }

    /**
     * 发起审批
     *
     * @param event
     */
    private void initiateApproval(ProjectInitializeEvent event) {
        workflowWrapper.initiate(workflowProperties.getProjectInitiation(), event);
    }

    private RepositoryGroup buildGroup(ProjectCmd projectCmd) {
        CreateRepositoryGroup createRepositoryGroup = new CreateRepositoryGroup();
        createRepositoryGroup.setGroupName(String.format(groupFormat, projectCmd.getKey()));
        createRepositoryGroup.setVisible(getProjectVisible(projectCmd));
        createRepositoryGroup.setDescription(projectCmd.getDescription());
        return repositoryGateway.createRepositoryGroup(createRepositoryGroup);
    }

    private boolean getProjectVisible(ProjectCmd projectCmd) {
        return ProjectAccessModeEnum.CORE != projectCmd.getAccessMode();
    }
}
