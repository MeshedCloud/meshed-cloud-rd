package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.iam.account.data.UserDTO;
import cn.meshed.cloud.rd.domain.project.ProjectMember;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectMemberGateway;
import cn.meshed.cloud.rd.project.data.ProjectMemberDTO;
import cn.meshed.cloud.rd.project.query.ProjectMemberPageQry;
import cn.meshed.cloud.rd.wrapper.user.UserWrapper;
import cn.meshed.cloud.utils.CopyUtils;
import com.alibaba.cola.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1>项目成员分页查询</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ProjectMemberPageQryExe implements QueryExecute<ProjectMemberPageQry, PageResponse<ProjectMemberDTO>> {

    private final ProjectMemberGateway projectMemberGateway;
    private final UserWrapper userWrapper;

    /**
     * <h1>查询执行器</h1>
     *
     * @param pageQry 分页查询 {@link ProjectMemberPageQry}
     * @return {@link PageResponse<ProjectMemberDTO>}
     */
    @Override
    public PageResponse<ProjectMemberDTO> execute(ProjectMemberPageQry pageQry) {
        PageResponse<ProjectMember> pageResponse = projectMemberGateway.searchPageList(pageQry);
        Set<Long> uidSet = pageResponse.getData().stream().map(ProjectMember::getUid).collect(Collectors.toSet());
        Map<Long, UserDTO> userMap = userWrapper.getUserMap(uidSet);
        List<ProjectMemberDTO> list = pageResponse.getData().stream()
                .map(projectMember -> toProjectMemberDTO(projectMember, userMap.get(projectMember.getUid())))
                .collect(Collectors.toList());

        return PageResponse.of(list, pageResponse.getTotalPages(), pageQry.getPageSize(), pageQry.getPageIndex());
    }

    private ProjectMemberDTO toProjectMemberDTO(ProjectMember projectMember, UserDTO userDTO) {
        ProjectMemberDTO projectMemberDTO = null;
        if (userDTO != null) {
            projectMemberDTO = CopyUtils.copy(userDTO, ProjectMemberDTO.class);
        } else {
            projectMemberDTO = new ProjectMemberDTO();
            projectMemberDTO.setName("已注销");
        }
        projectMemberDTO.setId(projectMember.getId());
        projectMemberDTO.setUid(projectMember.getUid());
        projectMemberDTO.setThirdUid(projectMember.getThirdUid());
        projectMemberDTO.setProjectRole(projectMember.getProjectRole());
        return projectMemberDTO;
    }
}
