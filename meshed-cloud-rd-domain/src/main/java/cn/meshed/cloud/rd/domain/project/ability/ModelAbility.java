package cn.meshed.cloud.rd.domain.project.ability;

import cn.meshed.cloud.rd.project.command.ModelCmd;
import cn.meshed.cloud.rd.project.data.ModelDTO;
import cn.meshed.cloud.rd.project.data.ModelDetailDTO;
import cn.meshed.cloud.rd.project.query.ModelPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;

/**
 * <h1>模型能力</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface ModelAbility {

    /**
     * 列表
     *
     * @param modelPageQry 模型分页查询
     * @return {@link PageResponse < ModelDTO >}
     */
    PageResponse<ModelDTO> list(ModelPageQry modelPageQry);

    /**
     * 详情
     *
     * @return {@link SingleResponse < ModelDetailDTO >}
     */
    SingleResponse<ModelDetailDTO> details(String uuid);

    /**
     * 保存功能
     *
     * @param modelCmd 服务数据
     * @return {@link Response}
     */
    Response save(ModelCmd modelCmd);
}
