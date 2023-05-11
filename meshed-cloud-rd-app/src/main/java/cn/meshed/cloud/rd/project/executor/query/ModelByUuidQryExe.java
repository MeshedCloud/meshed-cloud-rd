package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.project.data.EnumValueDTO;
import cn.meshed.cloud.rd.project.data.ModelDetailDTO;
import cn.meshed.cloud.rd.project.data.RequestFieldDTO;
import cn.meshed.cloud.rd.project.enums.ModelTypeEnum;
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
public class ModelByUuidQryExe implements QueryExecute<String, SingleResponse<ModelDetailDTO>> {

    private final ModelGateway modelGateway;

    /**
     * @param uuid
     * @return
     */
    @Override
    public SingleResponse<ModelDetailDTO> execute(String uuid) {
        Model model = modelGateway.query(uuid);
        if (model == null) {
            return ResultUtils.fail("模型不存在");
        }
        ModelDetailDTO detailDTO = CopyUtils.copy(model, ModelDetailDTO.class);
        detailDTO.setDomain(model.getDomainKey());
        detailDTO.setKey(model.getClassName().replace(model.getType().getExt(), ""));
        if (ModelTypeEnum.ENUM.equals(model.getType())) {
            detailDTO.setEnumValues(CopyUtils.copyListProperties(model.getEnumValues(), EnumValueDTO.class));
        } else {
            detailDTO.setFields(CopyUtils.copyListProperties(model.getFields(), RequestFieldDTO.class));
        }
        return ResultUtils.of(detailDTO);
    }
}
