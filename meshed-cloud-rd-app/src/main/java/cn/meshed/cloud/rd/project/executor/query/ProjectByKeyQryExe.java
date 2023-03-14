package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.rd.project.data.ProjectDetailDTO;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.SingleResponse;
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
public class ProjectByKeyQryExe implements QueryExecute<String, SingleResponse<ProjectDetailDTO>> {

    private final ProjectGateway projectGateway;

    /**
     * @param projectKey
     * @return
     */
    @Override
    public SingleResponse<ProjectDetailDTO> execute(String projectKey) {
        Project project = projectGateway.queryByKey(projectKey);
        if (project == null) {
            return ResultUtils.fail("项目不存在");
        }
        return ResultUtils.copy(project, ProjectDetailDTO.class);
    }
}
