package cn.meshed.cloud.rd.project.web;

import cn.meshed.cloud.rd.domain.project.ability.ServiceAbility;
import cn.meshed.cloud.rd.project.ServiceAdapter;
import cn.meshed.cloud.rd.project.command.ServiceCmd;
import cn.meshed.cloud.rd.project.data.ServiceDTO;
import cn.meshed.cloud.rd.project.data.ServiceDetailDTO;
import cn.meshed.cloud.rd.project.data.ServiceReleaseCountDTO;
import cn.meshed.cloud.rd.project.query.ServiceByMethodQry;
import cn.meshed.cloud.rd.project.query.ServiceByOneQry;
import cn.meshed.cloud.rd.project.query.ServicePageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
public class ServiceWebAdapter implements ServiceAdapter {

    private final ServiceAbility serviceAbility;

    /**
     * 列表
     *
     * @param projectKey
     * @param servicePageQry
     * @return {@link PageResponse<ServiceDTO>}
     */
    @Override
    public PageResponse<ServiceDTO> list(String projectKey, @Valid ServicePageQry servicePageQry) {
        servicePageQry.setProjectKey(projectKey);
        return serviceAbility.list(servicePageQry);
    }

    /**
     * 详情
     *
     * @param uuid            服务uuid
     * @param serviceByOneQry
     * @return {@link SingleResponse<ServiceDetailDTO>}
     */
    @Override
    public SingleResponse<ServiceDetailDTO> details(String uuid, @Valid ServiceByOneQry serviceByOneQry) {
        serviceByOneQry.setUuid(uuid);
        return serviceAbility.details(serviceByOneQry);
    }

    /**
     * 保存功能
     *
     * @param serviceCmd 服务数据
     * @return {@link Response}
     */
    @Override
    public Response save(@Valid ServiceCmd serviceCmd) {
        return serviceAbility.save(serviceCmd);
    }

    /**
     * jar统计
     *
     * @param projectKey 项目key
     * @return {@link SingleResponse<ServiceReleaseCountDTO>}
     */
    @Override
    public SingleResponse<ServiceReleaseCountDTO> releaseCount(String projectKey) {
        return serviceAbility.releaseCount(projectKey);
    }

    /**
     * 检查方法是否可用（控制器中唯一性）
     *
     * @param serviceByMethodQry 检查参数
     * @return {@link Response}
     */
    @Override
    public Response checkMethod(@Valid ServiceByMethodQry serviceByMethodQry) {
        return null;
    }
}
