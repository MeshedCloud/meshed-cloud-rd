package cn.meshed.cloud.rd.project.gatewayimpl;

import cn.meshed.cloud.rd.domain.project.Member;
import cn.meshed.cloud.rd.domain.project.gateway.MemberGateway;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.MemberDO;
import cn.meshed.cloud.rd.project.gatewayimpl.database.mapper.MemberMapper;
import cn.meshed.cloud.rd.project.query.MemberPageQry;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.PageUtils;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class MemberGatewayImpl implements MemberGateway {

    private final MemberMapper memberMapper;

    /**
     * <h1>删除对象</h1>
     *
     * @param id 模型ID
     * @return 成功与否
     */
    @Override
    public Boolean delete(Integer id) {
        return memberMapper.deleteById(id) > 0;
    }

    /**
     * <h1>分页搜索</h1>
     *
     * @param pageQry 分页参数
     * @return {@link PageResponse<Member>}
     */
    @Override
    public PageResponse<Member> searchPageList(MemberPageQry pageQry) {
        Page<Object> page = PageUtils.startPage(pageQry);
        LambdaQueryWrapper<MemberDO> lqw = new LambdaQueryWrapper<>();
        return PageUtils.of(memberMapper.selectList(lqw), page, Member::new);
    }

    /**
     * <h1>保存对象</h1>
     *
     * @param member 成员操作对象
     * @return 成功与否
     */
    @Override
    public Boolean save(Member member) {
        MemberDO memberDO = CopyUtils.copy(member, MemberDO.class);
        return memberMapper.insert(memberDO) > 0;
    }

    /**
     * 查询
     *
     * @param id ID
     * @return {@link Member}
     */
    @Override
    public Member query(Integer id) {
        MemberDO memberDO = memberMapper.selectById(id);
        return CopyUtils.copy(memberDO, Member.class);
    }

    /**
     * 过滤存在成员
     *
     * @param ids 账号ID
     * @return 存在账号ID
     */
    @Override
    public Set<Long> filterPresentByUserId(Set<Long> ids) {
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(ids), "id列表不能为空");
        LambdaQueryWrapper<MemberDO> lqw = new LambdaQueryWrapper<>();
        lqw.in(MemberDO::getUid, ids);
        List<MemberDO> list = memberMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return new HashSet<>();
        }
        return list.stream().map(MemberDO::getUid).collect(Collectors.toSet());
    }

    /**
     * 通过用户ID查询成员ID
     *
     * @param uid 用户ID
     * @return
     */
    @Override
    public Integer queryIdByUid(Long uid) {
        AssertUtils.isTrue(uid != null, "id不能为空");
        LambdaQueryWrapper<MemberDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MemberDO::getUid, uid);
        MemberDO memberDO = memberMapper.selectOne(lqw);
        if (memberDO == null) {
            return null;
        }
        return memberDO.getId();
    }
}
