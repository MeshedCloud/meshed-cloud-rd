package cn.meshed.cloud.rd.deployment.gatewayimpl;

import cn.meshed.cloud.rd.deployment.gatewayimpl.database.dataobject.VersionOccupyDO;
import cn.meshed.cloud.rd.deployment.gatewayimpl.database.mapper.VersionOccupyMapper;
import cn.meshed.cloud.rd.domain.deployment.VersionOccupy;
import cn.meshed.cloud.rd.domain.deployment.VersionOccupyGateway;
import cn.meshed.cloud.rd.project.enums.ServiceModelTypeEnum;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1>版本占用信息网关</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class VersionOccupyGatewayImpl implements VersionOccupyGateway {

    private final VersionOccupyMapper versionOccupyMapper;

    /**
     * 批量新增
     *
     * @param versionId   版本id
     * @param type        类型
     * @param relationIds 关系id列表
     * @return 成功与否
     */
    @Override
    public boolean saveBatch(Long versionId, ServiceModelTypeEnum type, Set<String> relationIds) {
        AssertUtils.isTrue(versionId != null, "版本编码不能为空");
        AssertUtils.isTrue(type != null, "类型不能为空");
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(relationIds), "关系列表不能为空");
        List<VersionOccupyDO> list = relationIds.stream().map(relationId -> {
            VersionOccupyDO versionOccupyDO = new VersionOccupyDO();
            versionOccupyDO.setVersionId(versionId);
            versionOccupyDO.setType(type);
            versionOccupyDO.setRelationId(relationId);
            return versionOccupyDO;
        }).collect(Collectors.toList());
        return versionOccupyMapper.insertBatch(list) > 0;
    }


    /**
     * 通过类型和编码查询版本占用信息
     *
     * @param type 类型
     * @param uuid 编码
     * @return {@link VersionOccupy}
     */
    @Override
    public VersionOccupy query(ServiceModelTypeEnum type, String uuid) {
        AssertUtils.isTrue(type != null, "占用类型不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(uuid), "关系编码不能为空");
        LambdaQueryWrapper<VersionOccupyDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(VersionOccupyDO::getType, type).eq(VersionOccupyDO::getRelationId, uuid);
        return CopyUtils.copy(versionOccupyMapper.selectOne(lqw), VersionOccupy.class);
    }

    /**
     * <h1>删除</h1>
     *
     * @param versionId 版本ID
     * @return {@link Boolean}
     */
    @Override
    public Boolean delete(Long versionId) {
        AssertUtils.isTrue(versionId != null, "关系编码不能为空");
        LambdaQueryWrapper<VersionOccupyDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(VersionOccupyDO::getVersionId, versionId);
        return versionOccupyMapper.delete(lqw) > 0;
    }

    /**
     * <h1>版本占用列表</h1>
     *
     * @param versionId 版本ID
     * @return {@link Set<VersionOccupy>}
     */
    @Override
    public Set<VersionOccupy> searchList(Long versionId) {
        AssertUtils.isTrue(versionId != null, "关系编码不能为空");
        LambdaQueryWrapper<VersionOccupyDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(VersionOccupyDO::getVersionId, versionId);
        return CopyUtils.copySetProperties(versionOccupyMapper.selectList(lqw), VersionOccupy::new);
    }
}
