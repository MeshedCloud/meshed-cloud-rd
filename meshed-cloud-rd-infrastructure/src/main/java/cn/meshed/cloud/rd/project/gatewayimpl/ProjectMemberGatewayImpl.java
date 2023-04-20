package cn.meshed.cloud.rd.project.gatewayimpl;

import cn.meshed.cloud.rd.domain.project.ProjectMember;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectMemberGateway;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.ProjectMemberDO;
import cn.meshed.cloud.rd.project.gatewayimpl.database.mapper.ProjectMemberMapper;
import cn.meshed.cloud.rd.project.query.ProjectMemberPageQry;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.PageUtils;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
public class ProjectMemberGatewayImpl implements ProjectMemberGateway {

    private final ProjectMemberMapper projectMemberMapper;

    /**
     * <h1>删除对象</h1>
     *
     * @param id id
     * @return 成功与否
     */
    @Override
    public Boolean delete(Integer id) {
        AssertUtils.isTrue(id != null, "编码不能为空");
        return projectMemberMapper.deleteById(id) > 0;
    }

    /**
     * <h1>分页搜索</h1>
     *
     * @param pageQry 分页查询
     * @return {@link PageResponse<ProjectMember>}
     */
    @Override
    public PageResponse<ProjectMember> searchPageList(ProjectMemberPageQry pageQry) {
        AssertUtils.isTrue(StringUtils.isNotBlank(pageQry.getProjectKey()), "项目标识不能为空");
        Page<Object> page = PageUtils.startPage(pageQry);
        LambdaQueryWrapper<ProjectMemberDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProjectMemberDO::getProjectKey, pageQry.getProjectKey());
        return PageUtils.of(projectMemberMapper.selectList(lqw), page, ProjectMember::new);
    }

    /**
     * <h1>保存对象</h1>
     *
     * @param projectMember 项目成员操作
     * @return 成功与否
     */
    @Override
    public Boolean save(ProjectMember projectMember) {
        ProjectMemberDO projectMemberDO = CopyUtils.copy(projectMember, ProjectMemberDO.class);
        return projectMemberMapper.insert(projectMemberDO) > 0;
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
        LambdaQueryWrapper<ProjectMemberDO> lqw = new LambdaQueryWrapper<>();
        lqw.in(ProjectMemberDO::getUid, ids);
        List<ProjectMemberDO> list = projectMemberMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return new HashSet<>();
        }
        return list.stream().map(ProjectMemberDO::getUid).collect(Collectors.toSet());
    }
}
