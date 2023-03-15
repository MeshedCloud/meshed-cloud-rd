package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.dto.ShowType;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ProjectAvailableKeyQryExe implements QueryExecute<String, Response> {

    private final ProjectGateway projectGateway;

    /**
     * @param projectKey
     * @return
     */
    @Override
    public Response execute(String projectKey) {
        return ResultUtils.of(!projectGateway.existKey(projectKey), "项目唯一标识已经存在", ShowType.SILENT);
    }
}
