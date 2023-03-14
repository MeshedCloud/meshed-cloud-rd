package cn.meshed.cloud.rd.project.web;

import cn.meshed.cloud.rd.domain.project.ability.ModelAbility;
import cn.meshed.cloud.rd.project.ModelAdapter;
import cn.meshed.cloud.rd.project.command.ModelCmd;
import cn.meshed.cloud.rd.project.data.ModelDTO;
import cn.meshed.cloud.rd.project.data.ModelDetailDTO;
import cn.meshed.cloud.rd.project.query.ModelByEnnameQry;
import cn.meshed.cloud.rd.project.query.ModelPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <h1>模型Web适配器</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
public class ModelWebAdapter implements ModelAdapter {

    private final ModelAbility modelAbility;

    /**
     * 列表
     *
     * @param projectKey
     * @param modelPageQry 模型分页查询
     * @return {@link PageResponse<ModelDTO>}
     */
    @Override
    public PageResponse<ModelDTO> list(String projectKey, @Valid ModelPageQry modelPageQry) {
        modelPageQry.setProjectKey(projectKey);
        return modelAbility.list(modelPageQry);
    }

    /**
     * 详情
     *
     * @param uuid uuid
     * @return {@link SingleResponse<ModelDetailDTO>}
     */
    @Override
    public SingleResponse<ModelDetailDTO> details(String uuid) {
        return modelAbility.details(uuid);
    }

    /**
     * 保存功能
     *
     * @param modelCmd 服务数据
     * @return {@link Response}
     */
    @Override
    public Response save(@Valid ModelCmd modelCmd) {
        return modelAbility.save(modelCmd);
    }

    /**
     * 检查英文名是否合法
     *
     * @param modelByEnnameQry 检查参数
     * @return {@link Response}
     */
    @Override
    public Response checkEnname(@Valid ModelByEnnameQry modelByEnnameQry) {
        return null;
    }
}
