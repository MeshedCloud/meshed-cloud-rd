package cn.meshed.cloud.rd.repo.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.repo.RepositoryBlob;
import cn.meshed.cloud.rd.domain.repo.gateway.RepositoryGateway;
import cn.meshed.cloud.rd.repo.query.RepositoryBlobQry;
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
public class RepositoryBlobQryExe implements QueryExecute<RepositoryBlobQry, SingleResponse<String>> {

    private final RepositoryGateway repositoryGateway;


    /**
     * <h1>查询执行器</h1>
     *
     * @param repositoryBlobQry 执行器 {@link RepositoryBlobQry
     * @return {@link SingleResponse<String>}
     */
    @Override
    public SingleResponse<String> execute(RepositoryBlobQry repositoryBlobQry) {
        RepositoryBlob repositoryBlob = new RepositoryBlob(repositoryBlobQry.getPath(), repositoryBlobQry.getRefName());
        String blob = repositoryGateway.getBlob(repositoryBlobQry.getRepositoryId(), repositoryBlob);
        return ResultUtils.of(blob);
    }
}
