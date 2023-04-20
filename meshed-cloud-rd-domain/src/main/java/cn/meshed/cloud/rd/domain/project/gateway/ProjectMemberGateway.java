package cn.meshed.cloud.rd.domain.project.gateway;

import cn.meshed.cloud.core.IDelete;
import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.rd.domain.project.ProjectMember;
import cn.meshed.cloud.rd.project.query.ProjectMemberPageQry;
import com.alibaba.cola.dto.PageResponse;

import java.util.Set;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface ProjectMemberGateway extends IPageList<ProjectMemberPageQry, PageResponse<ProjectMember>>,
        ISave<ProjectMember, Boolean>, IDelete<Integer, Boolean> {

    /**
     * 过滤存在成员
     *
     * @param ids 账号ID
     * @return 存在账号ID
     */
    Set<Long> filterPresentByUserId(Set<Long> ids);
}
