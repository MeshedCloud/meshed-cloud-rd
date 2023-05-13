package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.project.ServiceGroup;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGroupGateway;
import cn.meshed.cloud.rd.project.data.ServiceGroupDTO;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1>查询类是否存在</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ServiceGroupByUuidQryExe implements QueryExecute<String, SingleResponse<ServiceGroupDTO>> {

    private final ServiceGroupGateway serviceGroupGateway;

    /**
     * <h1>查询执行器</h1>
     *
     * @param uuid uuid
     * @return {@link SingleResponse<ServiceGroupDTO>}
     */
    @Override
    public SingleResponse<ServiceGroupDTO> execute(String uuid) {
        ServiceGroup serviceGroup = serviceGroupGateway.query(uuid);
        return ResultUtils.copy(serviceGroup, ServiceGroupDTO.class);
    }
}
