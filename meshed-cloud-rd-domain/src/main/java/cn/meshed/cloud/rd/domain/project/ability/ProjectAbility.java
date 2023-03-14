package cn.meshed.cloud.rd.domain.project.ability;

import cn.meshed.cloud.rd.project.command.ProjectChangeCmd;
import cn.meshed.cloud.rd.project.command.ProjectCmd;
import cn.meshed.cloud.rd.project.data.ProjectDTO;
import cn.meshed.cloud.rd.project.data.ProjectDetailDTO;
import cn.meshed.cloud.rd.project.query.ProjectPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;

import javax.validation.Valid;

/**
 * <h1>项目能力</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface ProjectAbility {

    /**
     * 列表
     *
     * @param projectPageQry 项目分页查询
     * @return {@link PageResponse < ProjectDTO >}
     */
    PageResponse<ProjectDTO> list(ProjectPageQry projectPageQry);

    /**
     * 详情
     *
     * @param projectKey 项目key
     * @return {@link SingleResponse <ProjectDetailDTO >}
     */
    SingleResponse<ProjectDetailDTO> details(String projectKey);

    /**
     * 保存功能
     *
     * @param projectCmd 项目数据
     * @return {@link Response}
     */
    Response save(@Valid ProjectCmd projectCmd);

    /**
     * 变更项目
     *
     * @return {@link Response}
     */
    Response change(ProjectChangeCmd projectChangeCmd);

    /**
     * 判断项目唯一标识是否存在
     *
     * @param key 项目唯一标识
     * @return 结果
     */
    Response checkKey(String key);
}
