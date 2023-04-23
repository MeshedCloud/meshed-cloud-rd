package cn.meshed.cloud.rd.domain.project.gateway;

import cn.meshed.cloud.core.IDelete;
import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.IQuery;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.rd.domain.project.Member;
import cn.meshed.cloud.rd.project.query.MemberPageQry;
import com.alibaba.cola.dto.PageResponse;

import java.util.Set;

/**
 * <h1>成员网关</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface MemberGateway extends IPageList<MemberPageQry, PageResponse<Member>>,
        ISave<Member, Boolean>, IDelete<Integer, Boolean>, IQuery<Integer, Member> {

    /**
     * 过滤存在成员
     *
     * @param ids 账号ID
     * @return 存在账号ID
     */
    Set<Long> filterPresentByUserId(Set<Long> ids);

    /**
     * 通过用户ID查询成员ID
     *
     * @param uid 用户ID
     * @return
     */
    Integer queryIdByUid(Long uid);
}
