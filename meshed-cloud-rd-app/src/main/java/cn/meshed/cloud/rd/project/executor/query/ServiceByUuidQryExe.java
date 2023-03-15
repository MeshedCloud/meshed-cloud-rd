package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGateway;
import cn.meshed.cloud.rd.project.data.RequestFieldDTO;
import cn.meshed.cloud.rd.project.data.ResponsesFieldDTO;
import cn.meshed.cloud.rd.project.data.ServiceDetailDTO;
import cn.meshed.cloud.utils.CopyUtils;
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
        if (service == null) {
            return ResultUtils.fail("服务不存在");
        }
        ServiceDetailDTO detailDTO = CopyUtils.copy(service, ServiceDetailDTO.class);
        detailDTO.setRequests(CopyUtils.copyListProperties(service.getRequests(), RequestFieldDTO.class));
        detailDTO.setResponses(CopyUtils.copyListProperties(service.getResponses(), ResponsesFieldDTO.class));
        return ResultUtils.of(detailDTO);
    }
}
