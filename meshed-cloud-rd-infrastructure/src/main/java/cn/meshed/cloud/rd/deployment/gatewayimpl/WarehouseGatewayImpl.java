package cn.meshed.cloud.rd.deployment.gatewayimpl;

import cn.meshed.cloud.rd.deployment.data.WarehouseSelectDTO;
import cn.meshed.cloud.rd.deployment.gatewayimpl.database.dataobject.WarehouseDO;
import cn.meshed.cloud.rd.deployment.gatewayimpl.database.mapper.WarehouseMapper;
import cn.meshed.cloud.rd.deployment.query.WarehousePageQry;
import cn.meshed.cloud.rd.domain.deployment.Warehouse;
import cn.meshed.cloud.rd.domain.deployment.gateway.WarehouseGateway;
import cn.meshed.cloud.rd.domain.deployment.gateway.param.WarehouseSelectParam;
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

import java.util.List;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class WarehouseGatewayImpl implements WarehouseGateway {

    private final WarehouseMapper warehouseMapper;

    /**
     * <h1>分页搜索</h1>
     *
     * @param pageQry 分页参数
     * @return {@link PageResponse<Warehouse>}
     */
    @Override
    public PageResponse<Warehouse> searchPageList(WarehousePageQry pageQry) {
        Page<Object> page = PageUtils.startPage(pageQry);
        LambdaQueryWrapper<WarehouseDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(pageQry.getProjectKey()), WarehouseDO::getProjectKey, pageQry.getProjectKey());
        return PageUtils.of(warehouseMapper.selectList(lqw), page, Warehouse::new);
    }

    /**
     * <h1>保存对象</h1>
     *
     * @param warehouse 仓库
     * @return {@link Warehouse}
     */
    @Override
    public Warehouse save(Warehouse warehouse) {
        WarehouseDO warehouseDO = CopyUtils.copy(warehouse, WarehouseDO.class);
        AssertUtils.isTrue(warehouseMapper.insert(warehouseDO) > 0, "新增失败");
        return CopyUtils.copy(warehouseDO, Warehouse.class);
    }

    /**
     * 判断仓库名称是否唯一 （全局唯一）
     *
     * @param warehouseName 仓库名称
     * @return
     */
    @Override
    public boolean existWarehouseName(String warehouseName) {
        LambdaQueryWrapper<WarehouseDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WarehouseDO::getRepoName, warehouseName);
        return warehouseMapper.selectCount(lqw) > 0;
    }

    /**
     * 查询
     *
     * @param uuid 参数
     * @return {@link String}
     */
    @Override
    public Warehouse query(String uuid) {
        return CopyUtils.copy(warehouseMapper.selectById(uuid), Warehouse.class);
    }

    /**
     * <h1>选项查询</h1>
     *
     * @param param
     * @return {@link List<WarehouseSelectDTO>}
     */
    @Override
    public List<Warehouse> select(WarehouseSelectParam param) {
        LambdaQueryWrapper<WarehouseDO> lqw = new LambdaQueryWrapper<>();
        lqw.select(WarehouseDO::getUuid, WarehouseDO::getName, WarehouseDO::getPurposeType, WarehouseDO::getRepoName)
                .eq(StringUtils.isNotBlank(param.getProjectKey()), WarehouseDO::getProjectKey, param.getProjectKey())
                .eq(param.getRepoType() != null, WarehouseDO::getRepoType, param.getRepoType())
                .in(CollectionUtils.isNotEmpty(param.getPurposeTypes()), WarehouseDO::getPurposeType, param.getPurposeTypes());
        return CopyUtils.copyListProperties(warehouseMapper.selectList(lqw), Warehouse.class);
    }
}
