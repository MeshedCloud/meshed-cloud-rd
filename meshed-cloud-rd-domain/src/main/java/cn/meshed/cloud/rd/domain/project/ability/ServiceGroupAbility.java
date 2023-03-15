package cn.meshed.cloud.rd.domain.project.ability;

import cn.meshed.cloud.rd.project.command.ServiceGroupCmd;
import cn.meshed.cloud.rd.project.data.ServiceGroupDTO;
import cn.meshed.cloud.rd.project.data.ServiceGroupSelectDTO;
import cn.meshed.cloud.rd.project.query.ServiceAvailableClassQry;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;

import java.util.List;
import java.util.Set;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface ServiceGroupAbility {

    /**
     * 服务分组选择获取
     *
     * @param projectKey 项目key
     * @return {@link SingleResponse<List<ServiceGroupDTO>>}
     */
    SingleResponse<Set<ServiceGroupSelectDTO>> select(String projectKey);

    /**
     * 保存功能
     *
     * @param serviceGroupCmd 服务分组数据
     * @return {@link Response}
     */
    Response save(ServiceGroupCmd serviceGroupCmd);

    /**
     * 检查方法是否可用（控制器中唯一性）
     *
     * @param serviceAvailableClassQry 检查参数
     * @return {@link Response}
     */
    Response availableClassName(ServiceAvailableClassQry serviceAvailableClassQry);
}
