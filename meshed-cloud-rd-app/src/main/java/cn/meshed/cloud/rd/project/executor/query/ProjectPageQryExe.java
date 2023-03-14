package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.rd.project.data.ProjectDTO;
import cn.meshed.cloud.rd.project.query.ProjectPageQry;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.PageResponse;
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
public class ProjectPageQryExe implements QueryExecute<ProjectPageQry, PageResponse<ProjectDTO>> {

    private final ProjectGateway projectGateway;

    /**
     * @param projectPageQry
     * @return
     */
    @Override
    public PageResponse<ProjectDTO> execute(ProjectPageQry projectPageQry) {
        PageResponse<Project> pageResponse = projectGateway.searchPageList(projectPageQry);
        return ResultUtils.copyPage(pageResponse, ProjectDTO::new);
    }
}
