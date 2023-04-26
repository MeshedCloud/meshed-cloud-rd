package cn.meshed.cloud.rd.project.web;

import cn.meshed.cloud.rd.domain.project.ability.ModelAbility;
import cn.meshed.cloud.rd.project.ModelAdapter;
import cn.meshed.cloud.rd.project.command.ModelCmd;
import cn.meshed.cloud.rd.project.data.ModelDTO;
import cn.meshed.cloud.rd.project.data.ModelDetailDTO;
import cn.meshed.cloud.rd.project.data.ModelReleaseCountDTO;
import cn.meshed.cloud.rd.project.query.ModelAvailableKeyQry;
import cn.meshed.cloud.rd.project.query.ModelPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

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
        return modelAbility.searchPageList(modelPageQry);
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
     * @param modelAvailableKeyQry 检查参数
     * @return {@link Response}
     */
    @Override
    public Response availableKey(@Valid ModelAvailableKeyQry modelAvailableKeyQry) {
        return modelAbility.availableKey(modelAvailableKeyQry);
    }

    /**
     * 模型选项
     *
     * @param projectKey 项目唯一标识
     * @return {@link SingleResponse< List <String>>}
     */
    @Override
    public SingleResponse<Set<String>> select(String projectKey) {
        return modelAbility.select(projectKey);
    }

    /**
     * @param s
     * @return
     */
    @Override
    public SingleResponse<ModelReleaseCountDTO> releaseCount(String s) {
        return SingleResponse.buildSuccess();
    }

    /**
     * 完成模型
     *
     * @param uuid 模型编码
     * @return {@link Response}
     */
    @Override
    public Response complete(String uuid) {
        return modelAbility.complete(uuid);
    }

    /**
     * 撤销完成
     *
     * @param uuid 模型编码
     * @return {@link Response}
     */
    @Override
    public Response revoke(String uuid) {
        return modelAbility.revoke(uuid);
    }

    /**
     * 废弃
     *
     * @param uuid 模型编码
     * @return {@link Response}
     */
    @Override
    public Response discard(String uuid) {
        return modelAbility.discard(uuid);
    }

    /**
     * 删除（仅支持编辑中的模型）
     *
     * @param uuid 模型编码
     * @return {@link Response}
     */
    @Override
    public Response delete(String uuid) {
        return modelAbility.delete(uuid);
    }

}
