package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.project.ServiceGroup;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGroupGateway;
import cn.meshed.cloud.rd.project.data.ServiceGroupDTO;
import cn.meshed.cloud.rd.project.data.ServiceGroupSelectDTO;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * <h1>查询类是否存在</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ServiceGroupBySelectQryExe implements QueryExecute<String, SingleResponse<Set<ServiceGroupSelectDTO>>> {

    private final ServiceGroupGateway serviceGroupGateway;

    /**
     * <h1>查询执行器</h1>
     *
     * @param projectKey 项目唯一标识
     * @return {@link SingleResponse<List<ServiceGroupDTO>>}
     */
    @Override
    public SingleResponse<Set<ServiceGroupSelectDTO>> execute(String projectKey) {
        Set<ServiceGroup> serviceGroups = serviceGroupGateway.select(projectKey);
        return ResultUtils.copySet(serviceGroups, ServiceGroupSelectDTO::new);
    }
}
