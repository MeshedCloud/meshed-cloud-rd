package cn.meshed.cloud.rd.domain.deployment.ability;

import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.ISelect;
import cn.meshed.cloud.rd.deployment.command.WarehouseAddCmd;
import cn.meshed.cloud.rd.deployment.data.WarehouseDTO;
import cn.meshed.cloud.rd.deployment.data.WarehouseSelectDTO;
import cn.meshed.cloud.rd.deployment.query.WarehousePageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;

import java.util.List;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface WarehouseAbility extends IPageList<WarehousePageQry, PageResponse<WarehouseDTO>>,
        ISelect<String, SingleResponse<List<WarehouseSelectDTO>>> {

    /**
     * 新增仓库
     *
     * @param warehouseAddCmd 仓库新增参数
     * @return {@link Response}
     */
    Response add(WarehouseAddCmd warehouseAddCmd);

}
