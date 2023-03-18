package cn.meshed.cloud.rd.domain.deployment.gateway;

import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.IQuery;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.core.ISelect;
import cn.meshed.cloud.rd.deployment.query.WarehousePageQry;
import cn.meshed.cloud.rd.domain.deployment.Warehouse;
import cn.meshed.cloud.rd.domain.deployment.gateway.param.WarehouseSelectParam;
import com.alibaba.cola.dto.PageResponse;

import java.util.List;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface WarehouseGateway extends IPageList<WarehousePageQry, PageResponse<Warehouse>>,
        ISave<Warehouse, Warehouse>, IQuery<String, Warehouse>, ISelect<WarehouseSelectParam, List<Warehouse>> {

    /**
     * 判断仓库名称是否唯一 （全局唯一）
     *
     * @param warehouseName 仓库名称
     * @return
     */
    boolean existWarehouseName(String warehouseName);
}
