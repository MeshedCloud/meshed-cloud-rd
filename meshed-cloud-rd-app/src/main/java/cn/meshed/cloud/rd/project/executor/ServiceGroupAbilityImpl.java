package cn.meshed.cloud.rd.project.executor;

import cn.meshed.cloud.rd.domain.project.ability.ServiceGroupAbility;
import cn.meshed.cloud.rd.project.command.ServiceGroupCmd;
import cn.meshed.cloud.rd.project.data.ServiceGroupDTO;
import cn.meshed.cloud.rd.project.data.ServiceGroupSelectDTO;
import cn.meshed.cloud.rd.project.executor.command.ServiceGroupCmdExe;
import cn.meshed.cloud.rd.project.executor.query.ServiceGroupAvailableClassQryExe;
import cn.meshed.cloud.rd.project.executor.query.ServiceGroupBySelectQryExe;
import cn.meshed.cloud.rd.project.executor.query.ServiceGroupByUuidQryExe;
import cn.meshed.cloud.rd.project.query.ServiceAvailableClassQry;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ServiceGroupAbilityImpl implements ServiceGroupAbility {

    private final ServiceGroupCmdExe serviceGroupCmdExe;
    private final ServiceGroupAvailableClassQryExe serviceGroupAvailableClassQryExe;
    private final ServiceGroupBySelectQryExe serviceGroupBySelectQryExe;
    private final ServiceGroupByUuidQryExe serviceGroupByUuidQryExe;

    /**
     * 服务分组选择获取
     *
     * @param projectKey 项目key
     * @return {@link SingleResponse < List < ServiceGroupDTO >>}
     */
    @Override
    public SingleResponse<Set<ServiceGroupSelectDTO>> select(String projectKey) {
        return serviceGroupBySelectQryExe.execute(projectKey);
    }

    /**
     * 保存功能
     *
     * @param serviceGroupCmd 服务分组数据
     * @return {@link Response}
     */
    @Override
    public Response save(ServiceGroupCmd serviceGroupCmd) {
        return serviceGroupCmdExe.execute(serviceGroupCmd);
    }

    /**
     * 检查方法是否可用（控制器中唯一性）
     *
     * @param serviceAvailableClassQry 检查参数
     * @return {@link Response}
     */
    @Override
    public Response availableClassName(ServiceAvailableClassQry serviceAvailableClassQry) {
        return serviceGroupAvailableClassQryExe.execute(serviceAvailableClassQry);
    }

    /**
     * <h2>查询</h2>
     *
     * @param uuid uuid
     * @return {@link SingleResponse<ServiceGroupDTO>}
     */
    @Override
    public SingleResponse<ServiceGroupDTO> query(String uuid) {
        return serviceGroupByUuidQryExe.execute(uuid);
    }
}
