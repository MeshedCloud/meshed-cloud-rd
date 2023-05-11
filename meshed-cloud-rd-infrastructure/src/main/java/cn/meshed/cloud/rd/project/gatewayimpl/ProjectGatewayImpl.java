package cn.meshed.cloud.rd.project.gatewayimpl;

import cn.meshed.cloud.context.SecurityContext;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.rd.project.enums.ProjectStatusEnum;
import cn.meshed.cloud.rd.project.enums.ProjectVisitTypeEnum;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.ProjectDO;
import cn.meshed.cloud.rd.project.gatewayimpl.database.mapper.ProjectMapper;
import cn.meshed.cloud.rd.project.query.ProjectPageQry;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.PageUtils;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * <h1>项目网关</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ProjectGatewayImpl implements ProjectGateway {

    private final ProjectMapper projectMapper;

    /**
     * 通过key查询
     *
     * @param key key
     * @return {@link Project}
     */
    @Override
    public Project queryByKey(String key) {
        AssertUtils.isTrue(StringUtils.isNotBlank(key), "key 不能为空");
        LambdaQueryWrapper<ProjectDO> lqw = new LambdaQueryWrapper<ProjectDO>();
        lqw.eq(ProjectDO::getKey, key.toUpperCase());
        ProjectDO projectDO = projectMapper.selectOne(lqw);
        return CopyUtils.copy(projectDO, Project.class);
    }

    /**
     * 判断项目Key是否已经存在
     *
     * @param key 项目key
     * @return
     */
    @Override
    public boolean existKey(String key) {
        AssertUtils.isTrue(StringUtils.isNotBlank(key), "唯一标识不能为空");
        LambdaQueryWrapper<ProjectDO> lqw = new LambdaQueryWrapper<ProjectDO>();
        lqw.eq(ProjectDO::getKey, key.toUpperCase());
        return projectMapper.selectCount(lqw) > 0;
    }

    /**
     * 判断项目Key是否已经存在
     *
     * @param key    项目key
     * @param status 状态
     * @return
     */
    @Override
    public boolean updateStatus(String key, ProjectStatusEnum status) {
        AssertUtils.isTrue(StringUtils.isNotBlank(key), "唯一标识不能为空");
        AssertUtils.isTrue(status != null, "状态不能为空");
        LambdaUpdateWrapper<ProjectDO> luq = new LambdaUpdateWrapper<>();
        luq.eq(ProjectDO::getKey, key).set(ProjectDO::getStatus, status);
        return projectMapper.update(null, luq) > 0;
    }

    /**
     * <h1>保存对象</h1>
     *
     * @param project
     * @return {@link Project}
     */
    @Override
    public Project save(Project project) {
        AssertUtils.isTrue(!existKey(project.getKey()), "项目KEY已经存在");
        ProjectDO projectDO = CopyUtils.copy(project, ProjectDO.class);
        AssertUtils.isTrue(projectMapper.insert(projectDO) > 0, "新增项目失败");
        return CopyUtils.copy(projectDO, Project.class);
    }


    /**
     * <h1>分页搜索</h1>
     *
     * @param pageQry 项目分页查询参数
     * @return {@link PageResponse<Project>}
     */
    @Override
    public PageResponse<Project> searchPageList(ProjectPageQry pageQry) {
        Page<Object> page = PageUtils.startPage(pageQry);
        LambdaQueryWrapper<ProjectDO> lqw = new LambdaQueryWrapper<>();
        lqw
                .eq(pageQry.getType() != null, ProjectDO::getType, pageQry.getType())
                .eq(pageQry.getAccessMode() != null, ProjectDO::getAccessMode, pageQry.getAccessMode())
                .and(StringUtils.isNotBlank(pageQry.getKeyword()), wrapper -> {
                    wrapper.like(ProjectDO::getName, pageQry.getKeyword())
                            .or().like(ProjectDO::getDescription, pageQry.getKeyword())
                            .or().like(ProjectDO::getKey, pageQry.getKeyword());
                }).orderByAsc(ProjectDO::getName);

        if (pageQry.getVisitType() != null) {
            handleVisitType(lqw, pageQry);
        }

        return PageUtils.of(projectMapper.selectList(lqw), page, Project::new);
    }

    /**
     * 访问
     *
     * @param lqw     查询条件
     * @param pageQry 查询分页
     */
    private void handleVisitType(LambdaQueryWrapper<ProjectDO> lqw, ProjectPageQry pageQry) {
        ProjectVisitTypeEnum visitType = pageQry.getVisitType();
        if (ProjectVisitTypeEnum.LATELY == visitType) {
            //查询最近仓库
        } else if (ProjectVisitTypeEnum.MEMBER == visitType) {
            //todo 查询成员仓库
        } else if (ProjectVisitTypeEnum.OWNER == visitType) {
            lqw.eq(ProjectDO::getOwnerId, SecurityContext.getUserId());
        }
    }
}
