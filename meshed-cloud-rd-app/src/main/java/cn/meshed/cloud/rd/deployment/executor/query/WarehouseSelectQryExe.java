package cn.meshed.cloud.rd.deployment.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.deployment.data.WarehouseSelectDTO;
import cn.meshed.cloud.rd.deployment.enums.WarehousePurposeTypeEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehouseRepoTypeEnum;
import cn.meshed.cloud.rd.domain.deployment.gateway.WarehouseGateway;
import cn.meshed.cloud.rd.domain.deployment.gateway.param.WarehouseSelectParam;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class WarehouseSelectQryExe implements QueryExecute<String, MultiResponse<WarehouseSelectDTO>> {

    private final WarehouseGateway warehouseGateway;

    /**
     * <h1>查询执行器</h1>
     *
     * @param projectKey 项目唯一标识
     * @return {@link SingleResponse<List<WarehouseSelectDTO>>}
     */
    @Override
    public MultiResponse<WarehouseSelectDTO> execute(String projectKey) {
        WarehouseSelectParam warehouseSelectParam = new WarehouseSelectParam();
        warehouseSelectParam.setProjectKey(projectKey);
        warehouseSelectParam.setRepoType(WarehouseRepoTypeEnum.CODEUP);
        warehouseSelectParam.setPurposeTypes(Arrays.asList(WarehousePurposeTypeEnum.CLIENT, WarehousePurposeTypeEnum.ASSEMBLY));
        return ResultUtils.copyMulti(warehouseGateway.select(warehouseSelectParam), WarehouseSelectDTO::new);
    }
}
