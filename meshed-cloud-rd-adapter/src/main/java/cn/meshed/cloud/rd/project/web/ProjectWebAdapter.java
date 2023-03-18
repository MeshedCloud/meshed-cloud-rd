package cn.meshed.cloud.rd.project.web;

import cn.meshed.cloud.rd.domain.project.ability.ProjectAbility;
import cn.meshed.cloud.rd.project.ProjectAdapter;
import cn.meshed.cloud.rd.project.command.ProjectChangeCmd;
import cn.meshed.cloud.rd.project.command.ProjectCmd;
import cn.meshed.cloud.rd.project.data.ProjectDTO;
import cn.meshed.cloud.rd.project.data.ProjectDetailDTO;
import cn.meshed.cloud.rd.project.enums.ProjectTypeEnum;
import cn.meshed.cloud.rd.project.query.ProjectPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
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
public class ProjectWebAdapter implements ProjectAdapter {

    private final ProjectAbility projectAbility;

    /**
     * 列表
     *
     * @param projectPageQry 项目分页查询
     * @return {@link PageResponse<ProjectDTO>}
     */
    @Override
    public PageResponse<ProjectDTO> list(@Valid ProjectPageQry projectPageQry) {
        return projectAbility.searchPageList(projectPageQry);
    }

    /**
     * 详情
     *
     * @param projectKey 项目key
     * @return {@link SingleResponse<ProjectDetailDTO>}
     */
    @Override
    public SingleResponse<ProjectDetailDTO> details(String projectKey) {
        return projectAbility.details(projectKey);
    }

    /**
     * 发起项目（新增项目/申请项目）
     *
     * @param projectCmd 项目数据
     * @return {@link Response}
     */
    @Override
    public Response apply(@Valid ProjectCmd projectCmd) {
        return projectAbility.save(projectCmd);
    }


    /**
     * 变更项目
     *
     * @param type             变更行为： 下线，删除,废弃
     * @param uuid             uuid
     * @param projectChangeCmd
     * @return {@link Response}
     */
    @Override
    public Response change(ProjectTypeEnum type, String uuid, @Valid ProjectChangeCmd projectChangeCmd) {
        projectChangeCmd.setType(type);
        projectChangeCmd.setUuid(uuid);
        return projectAbility.change(projectChangeCmd);
    }

    /**
     * 检查key是否唯一性
     *
     * @param key key
     * @return {@link Response}
     */
    @Override
    public Response availableKey(String key) {
        return projectAbility.availableKey(key);
    }

}
