package cn.meshed.cloud.rd.deployment.executor;

import cn.meshed.cloud.rd.deployment.command.WarehouseAddCmd;
import cn.meshed.cloud.rd.deployment.data.WarehouseDTO;
import cn.meshed.cloud.rd.deployment.data.WarehouseSelectDTO;
import cn.meshed.cloud.rd.deployment.executor.command.WarehouseAddCmdExe;
import cn.meshed.cloud.rd.deployment.executor.query.WarehousePageQryExe;
import cn.meshed.cloud.rd.deployment.executor.query.WarehouseSelectQryExe;
import cn.meshed.cloud.rd.deployment.query.WarehousePageQry;
import cn.meshed.cloud.rd.domain.deployment.ability.WarehouseAbility;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
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
public class WarehouseAbilityImpl implements WarehouseAbility {

    private final WarehouseAddCmdExe warehouseAddCmdExe;
    private final WarehousePageQryExe warehousePageQryExe;
    private final WarehouseSelectQryExe warehouseSelectQryExe;

    /**
     * 仓库列表
     *
     * @param warehousePageQry 仓库分页查询
     * @return
     */
    @Override
    public PageResponse<WarehouseDTO> searchPageList(WarehousePageQry warehousePageQry) {
        return warehousePageQryExe.execute(warehousePageQry);
    }

    /**
     * 新增仓库
     *
     * @param warehouseAddCmd 仓库新增参数
     * @return {@link Response}
     */
    @Override
    public Response add(WarehouseAddCmd warehouseAddCmd) {
        return warehouseAddCmdExe.execute(warehouseAddCmd);
    }

    /**
     * <h1>选项查询</h1>
     *
     * @param projectKey 项目唯一标识
     * @return {@link SingleResponse<List<WarehouseSelectDTO>>}
     */
    @Override
    public MultiResponse<WarehouseSelectDTO> select(String projectKey) {
        return warehouseSelectQryExe.execute(projectKey);
    }
}
