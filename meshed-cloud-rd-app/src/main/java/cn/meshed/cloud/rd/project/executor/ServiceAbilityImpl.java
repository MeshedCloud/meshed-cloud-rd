package cn.meshed.cloud.rd.project.executor;

import cn.meshed.cloud.rd.domain.project.ability.ServiceAbility;
import cn.meshed.cloud.rd.project.command.ServiceCmd;
import cn.meshed.cloud.rd.project.data.ServiceDTO;
import cn.meshed.cloud.rd.project.data.ServiceDetailDTO;
import cn.meshed.cloud.rd.project.data.ServiceReleaseCountDTO;
import cn.meshed.cloud.rd.project.executor.command.ServiceCmdExe;
import cn.meshed.cloud.rd.project.executor.query.ServiceAvailableMethodQryExe;
import cn.meshed.cloud.rd.project.executor.query.ServiceByUuidQryExe;
import cn.meshed.cloud.rd.project.executor.query.ServicePageQryExe;
import cn.meshed.cloud.rd.project.executor.query.ServiceReleaseCountQryExe;
import cn.meshed.cloud.rd.project.query.ServiceAvailableMethodQry;
import cn.meshed.cloud.rd.project.query.ServicePageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1>服务能力实现</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ServiceAbilityImpl implements ServiceAbility {

    private final ServicePageQryExe servicePageQryExe;
    private final ServiceByUuidQryExe serviceByUuidQryExe;
    private final ServiceCmdExe serviceCmdExe;
    private final ServiceReleaseCountQryExe serviceReleaseCountQryExe;
    private final ServiceAvailableMethodQryExe serviceAvailableMethodQryExe;

    /**
     * 列表
     *
     * @param servicePageQry
     * @return {@link PageResponse < ServiceDTO >}
     */
    @Override
    public PageResponse<ServiceDTO> searchPageList(ServicePageQry servicePageQry) {
        return servicePageQryExe.execute(servicePageQry);
    }

    /**
     * 详情
     *
     * @param uuid
     * @return {@link SingleResponse < ServiceDetailDTO >}
     */
    @Override
    public SingleResponse<ServiceDetailDTO> details(String uuid) {
        return serviceByUuidQryExe.execute(uuid);
    }

    /**
     * 保存功能
     *
     * @param serviceCmd 服务数据
     * @return {@link Response}
     */
    @Override
    public Response save(ServiceCmd serviceCmd) {
        return serviceCmdExe.execute(serviceCmd);
    }

    /**
     * jar统计
     *
     * @param projectKey 项目key
     * @return {@link SingleResponse<  ServiceReleaseCountDTO  >}
     */
    @Override
    public SingleResponse<ServiceReleaseCountDTO> releaseCount(String projectKey) {
        return serviceReleaseCountQryExe.execute(projectKey);
    }

    /**
     * 可用方法名称
     *
     * @param serviceAvailableMethodQry 方法名称参数
     * @return 是否可用
     */
    @Override
    public Response availableMethodName(ServiceAvailableMethodQry serviceAvailableMethodQry) {
        return serviceAvailableMethodQryExe.execute(serviceAvailableMethodQry);
    }
}
