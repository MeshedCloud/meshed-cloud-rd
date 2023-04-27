package cn.meshed.cloud.rd.deployment.gatewayimpl;

import cn.meshed.cloud.rd.deployment.gatewayimpl.database.dataobject.PackagesDO;
import cn.meshed.cloud.rd.deployment.gatewayimpl.database.mapper.PackagesMapper;
import cn.meshed.cloud.rd.deployment.query.PackagesPageQry;
import cn.meshed.cloud.rd.domain.deployment.Packages;
import cn.meshed.cloud.rd.domain.deployment.gateway.PackagesGateway;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.PageUtils;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class PackagesGatewayImpl implements PackagesGateway {

    private final PackagesMapper packagesMapper;

    /**
     * <h1>保存对象</h1>
     *
     * @param packages 包
     * @return {@link Boolean}
     */
    @Override
    public Boolean save(Packages packages) {
        return packagesMapper.insert(CopyUtils.copy(packages, PackagesDO.class)) > 0;
    }

    /**
     * <h1>分页列表</h1>
     *
     * @param pageQry 分页查询 {@link PackagesPageQry}
     * @return {@link PageResponse<Packages>}
     */
    @Override
    public PageResponse<Packages> searchPageList(PackagesPageQry pageQry) {
        Page<Object> page = PageUtils.startPage(pageQry);
        LambdaQueryWrapper<PackagesDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(pageQry.getProjectKey()), PackagesDO::getProjectKey, pageQry.getProjectKey());
        lqw.eq(pageQry.getType() != null, PackagesDO::getProjectKey, pageQry.getType());
        lqw.and(StringUtils.isNotBlank(pageQry.getKeyword()), wrapper -> {
            wrapper.like(PackagesDO::getArtifactId, pageQry.getKeyword())
                    .or().like(PackagesDO::getName, pageQry.getKeyword());
        });
        return PageUtils.of(packagesMapper.selectList(lqw), page, Packages::new);
    }
}
