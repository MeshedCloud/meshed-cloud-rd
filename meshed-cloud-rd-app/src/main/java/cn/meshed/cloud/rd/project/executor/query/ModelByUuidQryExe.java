package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.project.data.ModelDetailDTO;
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
public class ModelByUuidQryExe implements QueryExecute<String, SingleResponse<ModelDetailDTO>> {

    private final ModelGateway modelGateway;

    /**
     * @param uuid
     * @return
     */
    @Override
    public SingleResponse<ModelDetailDTO> execute(String uuid) {
        Model model = modelGateway.query(uuid);
        return ResultUtils.copy(model, ModelDetailDTO.class);
    }
}
