package cn.meshed.cloud.rd.project.executor;

import cn.meshed.cloud.rd.domain.project.ability.ServiceGroupAbility;
import cn.meshed.cloud.rd.project.command.ServiceGroupCmd;
import cn.meshed.cloud.rd.project.data.ServiceGroupSelectDTO;
import cn.meshed.cloud.rd.project.executor.command.ServiceGroupCmdExe;
import cn.meshed.cloud.rd.project.executor.query.ServiceByClassNameQryExe;
import cn.meshed.cloud.rd.project.executor.query.ServiceBySelectQryExe;
import cn.meshed.cloud.rd.project.query.ServiceByClassNameQry;
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
    private final ServiceByClassNameQryExe serviceByClassNameQryExe;
    private final ServiceBySelectQryExe serviceBySelectQryExe;

    /**
     * 服务分组选择获取
     *
     * @param projectKey 项目key
     * @return {@link SingleResponse < List < ServiceGroupDTO >>}
     */
    @Override
    public SingleResponse<Set<ServiceGroupSelectDTO>> select(String projectKey) {
        return serviceBySelectQryExe.execute(projectKey);
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
     * @param serviceByClassNameQry 检查参数
     * @return {@link Response}
     */
    @Override
    public Response checkClassName(ServiceByClassNameQry serviceByClassNameQry) {
        return serviceByClassNameQryExe.execute(serviceByClassNameQry);
    }
}
