package cn.meshed.cloud.rd.project.executor;

import cn.meshed.cloud.rd.domain.project.ability.ProjectAbility;
import cn.meshed.cloud.rd.project.command.ProjectChangeCmd;
import cn.meshed.cloud.rd.project.command.ProjectCmd;
import cn.meshed.cloud.rd.project.data.ProjectDTO;
import cn.meshed.cloud.rd.project.data.ProjectDetailDTO;
import cn.meshed.cloud.rd.project.executor.command.ProjectCmdExe;
import cn.meshed.cloud.rd.project.executor.query.ProjectAvailableKeyQryExe;
import cn.meshed.cloud.rd.project.executor.query.ProjectByKeyQryExe;
import cn.meshed.cloud.rd.project.executor.query.ProjectPageQryExe;
import cn.meshed.cloud.rd.project.query.ProjectPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1>项目能力</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ProjectAbilityImpl implements ProjectAbility {

    private final ProjectCmdExe projectCmdExe;
    private final ProjectByKeyQryExe projectByKeyQryExe;
    private final ProjectPageQryExe projectPageQryExe;
    private final ProjectAvailableKeyQryExe projectAvailableKeyQryExe;

    /**
     * 列表
     *
     * @param projectPageQry 项目分页查询
     * @return {@link PageResponse < ProjectDTO >}
     */
    @Override
    public PageResponse<ProjectDTO> list(ProjectPageQry projectPageQry) {
        return projectPageQryExe.execute(projectPageQry);
    }

    /**
     * 详情
     *
     * @param projectKey 项目key
     * @return {@link SingleResponse < ProjectDetailDTO >}
     */
    @Override
    public SingleResponse<ProjectDetailDTO> details(String projectKey) {
        return projectByKeyQryExe.execute(projectKey);
    }

    /**
     * 保存功能
     *
     * @param projectCmd 项目数据
     * @return {@link Response}
     */
    @Override
    public Response save(ProjectCmd projectCmd) {
        return projectCmdExe.execute(projectCmd);
    }

    /**
     * 变更项目
     *
     * @param projectChangeCmd
     * @return {@link Response}
     */
    @Override
    public Response change(ProjectChangeCmd projectChangeCmd) {
        return null;
    }

    /**
     * 判断项目唯一标识是否存在
     *
     * @param key 项目唯一标识
     * @return 结果
     */
    @Override
    public Response availableKey(String key) {
        return projectAvailableKeyQryExe.execute(key);
    }
}
