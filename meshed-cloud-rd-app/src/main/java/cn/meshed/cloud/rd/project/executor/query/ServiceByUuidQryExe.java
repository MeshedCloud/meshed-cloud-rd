package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGateway;
import cn.meshed.cloud.rd.project.data.ServiceDetailDTO;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.SingleResponse;
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
public class ServiceByUuidQryExe implements QueryExecute<String, SingleResponse<ServiceDetailDTO>> {

    private final ServiceGateway serviceGateway;

    /**
     * @param uuid
     * @return
     */
    @Override
    public SingleResponse<ServiceDetailDTO> execute(String uuid) {
        Service service = serviceGateway.query(uuid);
        return ResultUtils.copy(service, ServiceDetailDTO.class);
    }
}
