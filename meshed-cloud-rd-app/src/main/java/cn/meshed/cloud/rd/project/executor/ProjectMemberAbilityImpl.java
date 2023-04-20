package cn.meshed.cloud.rd.project.executor;

import cn.meshed.cloud.rd.domain.project.ability.ProjectMemberAbility;
import cn.meshed.cloud.rd.project.command.ProjectMemberCmd;
import cn.meshed.cloud.rd.project.data.ProjectMemberDTO;
import cn.meshed.cloud.rd.project.executor.command.ProjectMemberCmdExe;
import cn.meshed.cloud.rd.project.executor.command.ProjectMemberDelCmdExe;
import cn.meshed.cloud.rd.project.executor.query.ProjectMemberPageQryExe;
import cn.meshed.cloud.rd.project.query.ProjectMemberPageQry;
import com.alibaba.cola.dto.PageResponse;
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
public class ProjectMemberAbilityImpl implements ProjectMemberAbility {

    private final ProjectMemberPageQryExe projectMemberPageQryExe;
    private final ProjectMemberCmdExe projectMemberCmdExe;
    private final ProjectMemberDelCmdExe projectMemberDelCmdExe;

    /**
     * <h1>删除对象</h1>
     *
     * @param id
     * @return {@link Response}
     */
    @Override
    public Response delete(Integer id) {
        return projectMemberDelCmdExe.execute(id);
    }

    /**
     * <h1>分页搜索</h1>
     *
     * @param pageQry 分页参数
     * @return {@link PageResponse<ProjectMemberDTO>}
     */
    @Override
    public PageResponse<ProjectMemberDTO> searchPageList(ProjectMemberPageQry pageQry) {
        return projectMemberPageQryExe.execute(pageQry);
    }

    /**
     * <h1>保存对象</h1>
     *
     * @param projectMemberCmd 成员操作
     * @return {@link Response}
     */
    @Override
    public Response save(ProjectMemberCmd projectMemberCmd) {
        return projectMemberCmdExe.execute(projectMemberCmd);
    }
}
