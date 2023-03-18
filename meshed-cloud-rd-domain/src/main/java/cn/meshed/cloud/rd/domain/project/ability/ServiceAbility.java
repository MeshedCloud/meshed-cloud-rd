package cn.meshed.cloud.rd.domain.project.ability;

import cn.meshed.cloud.core.IDetails;
import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.rd.project.command.ServiceCmd;
import cn.meshed.cloud.rd.project.data.ServiceDTO;
import cn.meshed.cloud.rd.project.data.ServiceDetailDTO;
import cn.meshed.cloud.rd.project.data.ServiceReleaseCountDTO;
import cn.meshed.cloud.rd.project.query.ServiceAvailableMethodQry;
import cn.meshed.cloud.rd.project.query.ServicePageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;

import javax.validation.Valid;

/**
 * <h1>服务能力</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface ServiceAbility extends IPageList<ServicePageQry, PageResponse<ServiceDTO>>,
        IDetails<String, SingleResponse<ServiceDetailDTO>>, ISave<ServiceCmd, Response> {

    /**
     * jar统计
     *
     * @param projectKey 项目key
     * @return {@link SingleResponse< ServiceReleaseCountDTO >}
     */
    SingleResponse<ServiceReleaseCountDTO> releaseCount(String projectKey);

    /**
     * 可用方法名称
     *
     * @param serviceAvailableMethodQry 方法名称参数
     * @return 是否可用
     */
    Response availableMethodName(@Valid ServiceAvailableMethodQry serviceAvailableMethodQry);
}
