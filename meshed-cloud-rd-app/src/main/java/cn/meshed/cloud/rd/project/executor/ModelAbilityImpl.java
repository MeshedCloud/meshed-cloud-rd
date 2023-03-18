package cn.meshed.cloud.rd.project.executor;

import cn.meshed.cloud.rd.domain.project.ability.ModelAbility;
import cn.meshed.cloud.rd.project.command.ModelCmd;
import cn.meshed.cloud.rd.project.data.ModelDTO;
import cn.meshed.cloud.rd.project.data.ModelDetailDTO;
import cn.meshed.cloud.rd.project.executor.command.ModelCmdExe;
import cn.meshed.cloud.rd.project.executor.query.ModelAvailableKeyQryExe;
import cn.meshed.cloud.rd.project.executor.query.ModelByUuidQryExe;
import cn.meshed.cloud.rd.project.executor.query.ModelPageQryExe;
import cn.meshed.cloud.rd.project.executor.query.ModelSelectQryExe;
import cn.meshed.cloud.rd.project.query.ModelAvailableKeyQry;
import cn.meshed.cloud.rd.project.query.ModelPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

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
    private final ModelAvailableKeyQryExe modelAvailableKeyQryExe;
    private final ModelSelectQryExe modelSelectQryExe;
    private final ModelCmdExe modelCmdExe;

    /**
     * 列表
     *
     * @param modelPageQry 模型分页查询
     * @return {@link PageResponse < ModelDTO >}
     */
    @Override
    public PageResponse<ModelDTO> searchPageList(ModelPageQry modelPageQry) {
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

    /**
     * 可用模型唯一标识
     *
     * @param modelAvailableKeyQry 模型唯一标识参数
     * @return 是否可用
     */
    @Override
    public Response availableKey(ModelAvailableKeyQry modelAvailableKeyQry) {
        return modelAvailableKeyQryExe.execute(modelAvailableKeyQry);
    }

    /**
     * 模型选项
     *
     * @param projectKey 项目唯一标识
     * @return {@link SingleResponse<  List  <String>>}
     */
    @Override
    public SingleResponse<Set<String>> select(String projectKey) {
        return modelSelectQryExe.execute(projectKey);
    }
}
