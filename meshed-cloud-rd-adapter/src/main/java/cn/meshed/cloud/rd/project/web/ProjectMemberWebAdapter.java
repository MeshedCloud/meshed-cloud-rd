package cn.meshed.cloud.rd.project.web;

import cn.meshed.cloud.rd.domain.project.ability.ProjectMemberAbility;
import cn.meshed.cloud.rd.project.ProjectMemberAdapter;
import cn.meshed.cloud.rd.project.command.ProjectMemberCmd;
import cn.meshed.cloud.rd.project.data.ProjectMemberDTO;
import cn.meshed.cloud.rd.project.query.ProjectMemberPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
public class ProjectMemberWebAdapter implements ProjectMemberAdapter {

    private final ProjectMemberAbility projectMemberAbility;

    /**
     * 列表
     *
     * @param projectMemberPageQry 成员分页查询
     * @return {@link PageResponse < MemberDTO >}
     */
    @Override
    public PageResponse<ProjectMemberDTO> list(@Valid ProjectMemberPageQry projectMemberPageQry) {
        return projectMemberAbility.searchPageList(projectMemberPageQry);
    }

    /**
     * 新增成员
     *
     * @param projectMemberCmd 成员信息
     * @return {@link Response}
     */
    @Override
    public Response add(@Valid ProjectMemberCmd projectMemberCmd) {
        return projectMemberAbility.save(projectMemberCmd);
    }

    /**
     * 删除成员
     *
     * @param id 成员ID
     * @return {@link Response}
     */
    @Override
    public Response delete(Integer id) {
        return projectMemberAbility.delete(id);
    }
}
