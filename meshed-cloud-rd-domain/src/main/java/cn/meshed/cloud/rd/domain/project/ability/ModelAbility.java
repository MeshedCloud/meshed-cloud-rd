package cn.meshed.cloud.rd.domain.project.ability;

import cn.meshed.cloud.core.IDelete;
import cn.meshed.cloud.core.IDetails;
import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.rd.project.command.ModelCmd;
import cn.meshed.cloud.rd.project.data.ModelDTO;
import cn.meshed.cloud.rd.project.data.ModelDetailDTO;
import cn.meshed.cloud.rd.project.query.ModelAvailableKeyQry;
import cn.meshed.cloud.rd.project.query.ModelPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * <h1>模型能力</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface ModelAbility extends IPageList<ModelPageQry, PageResponse<ModelDTO>>,
        IDetails<String, SingleResponse<ModelDetailDTO>>, ISave<ModelCmd, Response>, IDelete<String, Response> {


    /**
     * 可用模型唯一标识
     *
     * @param modelAvailableKeyQry 模型唯一标识参数
     * @return 是否可用
     */
    Response availableKey(@Valid ModelAvailableKeyQry modelAvailableKeyQry);

    /**
     * 模型选项
     *
     * @param projectKey 项目唯一标识
     * @return {@link SingleResponse< List <String>>}
     */
    SingleResponse<Set<String>> select(String projectKey);

    /**
     * 完成服务
     *
     * @param uuid 服务编码
     * @return {@link Response}
     */
    Response complete(String uuid);

    /**
     * 撤销完成
     *
     * @param uuid 服务编码
     * @return {@link Response}
     */
    Response revoke(String uuid);

    /**
     * 废弃
     *
     * @param uuid 服务编码
     * @return {@link Response}
     */
    Response discard(String uuid);

}
