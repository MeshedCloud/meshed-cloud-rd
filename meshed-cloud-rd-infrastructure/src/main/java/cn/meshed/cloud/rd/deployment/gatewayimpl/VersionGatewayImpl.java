package cn.meshed.cloud.rd.deployment.gatewayimpl;

import cn.meshed.cloud.rd.deployment.gatewayimpl.database.dataobject.VersionDO;
import cn.meshed.cloud.rd.deployment.gatewayimpl.database.mapper.VersionMapper;
import cn.meshed.cloud.rd.deployment.query.VersionPageQry;
import cn.meshed.cloud.rd.domain.deployment.Version;
import cn.meshed.cloud.rd.domain.deployment.gateway.VersionGateway;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.PageUtils;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * <h1>版本网关实现</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class VersionGatewayImpl implements VersionGateway {

    private final VersionMapper versionMapper;

    /**
     * <h1>分页搜索</h1>
     *
     * @param pageQry
     * @return {@link PageResponse<Version>}
     */
    @Override
    public PageResponse<Version> searchPageList(VersionPageQry pageQry) {
        Page<Object> page = PageUtils.startPage(pageQry);
        return PageUtils.of(versionMapper.list(pageQry), page, Version::new);
    }

    /**
     * 登记或变更版本信息
     * 注： 修改仅限于版本状态和环境信息
     *
     * @param version 版本信息
     * @return 操作后版本信息
     */
    @Override
    public Version registration(Version version) {
        AssertUtils.isTrue(getVersionBySourceIdAndVersionDO(version) == null, "版本已经存在无需新建");
        VersionDO versionDO = CopyUtils.copy(version, VersionDO.class);
        AssertUtils.isTrue(versionMapper.insert(versionDO) > 0, "版本注册失败");
        return CopyUtils.copy(versionDO, Version.class);
    }

    /**
     * 改变版本信息 修改仅限于版本状态和环境信息
     *
     * @param version 版本信息
     * @return 操作后版本信息
     */
    @Override
    public Version change(Version version) {
        VersionDO alreadyVersion = getVersionBySourceIdAndVersionDO(version);
        AssertUtils.isTrue(alreadyVersion != null, "变更版本不能为空");
        assert alreadyVersion != null;
        alreadyVersion.setStatus(version.getStatus());
        alreadyVersion.setEnvironments(version.getEnvironments());
        AssertUtils.isTrue(versionMapper.updateById(alreadyVersion) > 0, "版本变更失败");
        return CopyUtils.copy(alreadyVersion, Version.class);
    }

    /**
     * 根据来源信息和版本号获取版本
     *
     * @param version 版本信息
     * @return 返回版本信息
     */
    @Override
    public Version getVersionBySourceIdAndVersion(Version version) {
        return CopyUtils.copy(getVersionBySourceIdAndVersionDO(version), Version.class);
    }

    private VersionDO getVersionBySourceIdAndVersionDO(Version version) {
        AssertUtils.isTrue(StringUtils.isNotBlank(version.getSourceId()), "来源ID不能为空");
        AssertUtils.isTrue(version.getVersion() != null, "版本号不能为空");
        LambdaQueryWrapper<VersionDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(VersionDO::getSourceId, version.getSourceId())
                .eq(VersionDO::getVersion, version.getVersion());
        VersionDO alreadyVersion = versionMapper.selectOne(lqw);
        return alreadyVersion;
    }


    /**
     * 查询
     *
     * @param id id
     * @return {@link Version}
     */
    @Override
    public Version query(Long id) {
        return CopyUtils.copy(versionMapper.selectById(id), Version.class);
    }
}
