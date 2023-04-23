package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.log.Trend;
import cn.meshed.cloud.rd.domain.project.Member;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.ProjectMember;
import cn.meshed.cloud.rd.domain.project.gateway.MemberGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectMemberGateway;
import cn.meshed.cloud.rd.domain.repo.AddRepositoryMember;
import cn.meshed.cloud.rd.domain.repo.gateway.RepositoryGateway;
import cn.meshed.cloud.rd.project.command.ProjectMemberCmd;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h1>项目成员删除</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ProjectMemberCmdExe implements CommandExecute<ProjectMemberCmd, Response> {

    private final MemberGateway memberGateway;
    private final RepositoryGateway repositoryGateway;
    private final ProjectMemberGateway projectMemberGateway;
    private final ProjectGateway projectGateway;

    /**
     * <h1>执行器</h1>
     *
     * @param projectMemberCmd 项目成员ID
     * @return {@link Response}
     */
    @Override
    @Trend(key = "#{projectMemberCmd.projectKey}", content = "添加项目成员:+#{projectMemberCmd.memberIds} 角色: +#{projectMemberCmd.projectRole}")
    @Transactional
    public Response execute(ProjectMemberCmd projectMemberCmd) {
        AssertUtils.isTrue(StringUtils.isNotBlank(projectMemberCmd.getProjectKey()), "项目标识不能为空");
        Project project = projectGateway.queryByKey(projectMemberCmd.getProjectKey());
        AssertUtils.isTrue(project != null, "项目不存在");
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(projectMemberCmd.getMemberIds()), "成员ID不存在");
        for (Integer mId : projectMemberCmd.getMemberIds()) {
            assert project != null;
            newProjectMember(mId, projectMemberCmd, project);
        }
        return ResultUtils.ok();
    }

    private void newProjectMember(Integer mid, ProjectMemberCmd projectMemberCmd, Project project) {
        Member member = memberGateway.query(mid);
        AddRepositoryMember addRepositoryMember = new AddRepositoryMember();
        addRepositoryMember.setRepoUid(member.getThirdUid());
        addRepositoryMember.setAccessLevel(projectMemberCmd.getProjectRole().getValue());
        assert project != null;
        repositoryGateway.addGroupMember(String.valueOf(project.getThirdId()), addRepositoryMember);
        ProjectMember projectMember = buildProjectMember(projectMemberCmd, member);
        projectMemberGateway.save(projectMember);
    }

    @NotNull
    private ProjectMember buildProjectMember(ProjectMemberCmd projectMemberCmd, Member member) {
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectKey(projectMemberCmd.getProjectKey());
        projectMember.setUid(member.getUid());
        projectMember.setThirdUid(member.getThirdUid());
        projectMember.setProjectRole(projectMemberCmd.getProjectRole());
        return projectMember;
    }
}
