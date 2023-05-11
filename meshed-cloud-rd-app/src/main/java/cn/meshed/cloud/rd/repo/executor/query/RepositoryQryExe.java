package cn.meshed.cloud.rd.repo.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.deployment.Warehouse;
import cn.meshed.cloud.rd.domain.deployment.gateway.WarehouseGateway;
import cn.meshed.cloud.rd.domain.repo.gateway.RepositoryGateway;
import cn.meshed.cloud.rd.repo.data.RepositoryDTO;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class RepositoryQryExe implements QueryExecute<String, SingleResponse<RepositoryDTO>> {

    private final WarehouseGateway warehouseGateway;
    private final RepositoryGateway repositoryGateway;

    /**
     * <h1>查询执行器</h1>
     *
     * @param repoId 仓库ID
     * @return {@link SingleResponse<RepositoryDTO>}
     */
    @Override
    public SingleResponse<RepositoryDTO> execute(String repoId) {
        Warehouse warehouse = warehouseGateway.queryByRepoId(repoId);
        AssertUtils.isTrue(warehouse != null, "仓库不存在");
        RepositoryDTO repositoryDTO = CopyUtils.copy(warehouse, RepositoryDTO.class);
        repositoryDTO.setBranchs(getBranchs(repoId));
        return ResultUtils.of(repositoryDTO);
    }

    private List<String> getBranchs(String repoId) {
        return repositoryGateway.branchList(repoId);
    }
}
