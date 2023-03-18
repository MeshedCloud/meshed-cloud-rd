package cn.meshed.cloud.rd.deployment.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.deployment.data.WarehouseDTO;
import cn.meshed.cloud.rd.deployment.query.WarehousePageQry;
import cn.meshed.cloud.rd.domain.deployment.Warehouse;
import cn.meshed.cloud.rd.domain.deployment.gateway.WarehouseGateway;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class WarehousePageQryExe implements QueryExecute<WarehousePageQry, PageResponse<WarehouseDTO>> {

    private final WarehouseGateway warehouseGateway;

    /**
     * @param warehousePageQry
     * @return
     */
    @Override
    public PageResponse<WarehouseDTO> execute(WarehousePageQry warehousePageQry) {
        PageResponse<Warehouse> pageResponse = warehouseGateway.searchPageList(warehousePageQry);
        return ResultUtils.copyPage(pageResponse, WarehouseDTO::new);
    }
}
