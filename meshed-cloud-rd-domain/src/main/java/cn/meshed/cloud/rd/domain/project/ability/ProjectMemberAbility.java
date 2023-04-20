package cn.meshed.cloud.rd.domain.project.ability;

import cn.meshed.cloud.core.IDelete;
import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.rd.project.command.ProjectMemberCmd;
import cn.meshed.cloud.rd.project.data.ProjectMemberDTO;
import cn.meshed.cloud.rd.project.query.ProjectMemberPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface ProjectMemberAbility extends IPageList<ProjectMemberPageQry, PageResponse<ProjectMemberDTO>>,
        ISave<ProjectMemberCmd, Response>, IDelete<Integer, Response> {
}
