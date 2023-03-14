package cn.meshed.cloud.rd.project.executor;

import cn.meshed.cloud.rd.domain.project.ability.ModelAbility;
import cn.meshed.cloud.rd.project.command.ModelCmd;
import cn.meshed.cloud.rd.project.data.ModelDTO;
import cn.meshed.cloud.rd.project.data.ModelDetailDTO;
import cn.meshed.cloud.rd.project.executor.command.ModelCmdExe;
import cn.meshed.cloud.rd.project.executor.query.ModelByUuidQryExe;
import cn.meshed.cloud.rd.project.executor.query.ModelPageQryExe;
import cn.meshed.cloud.rd.project.query.ModelPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1>模型能力实现</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ModelAbilityImpl implements ModelAbility {

    private final ModelPageQryExe modelPageQryExe;
    private final ModelByUuidQryExe modelByUuidQryExe;
    private final ModelCmdExe modelCmdExe;

    /**
     * 列表
     *
     * @param modelPageQry 模型分页查询
     * @return {@link PageResponse < ModelDTO >}
     */
    @Override
    public PageResponse<ModelDTO> list(ModelPageQry modelPageQry) {
        return modelPageQryExe.execute(modelPageQry);
    }

    /**
     * 详情
     *
     * @param uuid
     * @return {@link SingleResponse < ModelDetailDTO >}
     */
    @Override
    public SingleResponse<ModelDetailDTO> details(String uuid) {
        return modelByUuidQryExe.execute(uuid);
    }

    /**
     * 保存功能
     *
     * @param modelCmd 服务数据
     * @return {@link Response}
     */
    @Override
    public Response save(ModelCmd modelCmd) {
        return modelCmdExe.execute(modelCmd);
    }
}
