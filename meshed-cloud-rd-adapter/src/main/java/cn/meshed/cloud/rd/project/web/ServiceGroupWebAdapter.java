package cn.meshed.cloud.rd.project.web;

import cn.meshed.cloud.rd.domain.project.ability.ServiceGroupAbility;
import cn.meshed.cloud.rd.project.ServiceGroupAdapter;
import cn.meshed.cloud.rd.project.command.ServiceGroupCmd;
import cn.meshed.cloud.rd.project.data.ServiceGroupDTO;
import cn.meshed.cloud.rd.project.data.ServiceGroupSelectDTO;
import cn.meshed.cloud.rd.project.query.ServiceAvailableClassQry;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
public class ServiceGroupWebAdapter implements ServiceGroupAdapter {

    private final ServiceGroupAbility serviceGroupAbility;


    /**
     * 服务分组选择获取
     *
     * @param projectKey 项目key
     * @return {@link SingleResponse< List <ServiceGroupDTO>>}
     */
    @Override
    public SingleResponse<Set<ServiceGroupSelectDTO>> select(String projectKey) {
        return serviceGroupAbility.select(projectKey);
    }

    /**
     * 保存功能
     *
     * @param serviceGroupCmd 服务分组数据
     * @return {@link Response}
     */
    @Override
    public Response save(@Valid ServiceGroupCmd serviceGroupCmd) {
        return serviceGroupAbility.save(serviceGroupCmd);
    }

    /**
     * 检查类名是否可用（控制器中唯一性）
     *
     * @param serviceAvailableClassQry 检查参数
     * @return {@link Response}
     */
    @Override
    public Response availableClassName(@Valid ServiceAvailableClassQry serviceAvailableClassQry) {
        return serviceGroupAbility.availableClassName(serviceAvailableClassQry);
    }

}
