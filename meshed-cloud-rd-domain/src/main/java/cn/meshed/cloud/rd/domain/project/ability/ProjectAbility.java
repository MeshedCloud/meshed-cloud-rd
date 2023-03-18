package cn.meshed.cloud.rd.domain.project.ability;

import cn.meshed.cloud.core.IDetails;
import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.rd.project.command.ProjectChangeCmd;
import cn.meshed.cloud.rd.project.command.ProjectCmd;
import cn.meshed.cloud.rd.project.data.ProjectDTO;
import cn.meshed.cloud.rd.project.data.ProjectDetailDTO;
import cn.meshed.cloud.rd.project.query.ProjectPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;

/**
 * <h1>项目能力</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface ProjectAbility extends IPageList<ProjectPageQry, PageResponse<ProjectDTO>>,
        IDetails<String, SingleResponse<ProjectDetailDTO>>, ISave<ProjectCmd, Response> {

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
    Response availableKey(String key);
}
