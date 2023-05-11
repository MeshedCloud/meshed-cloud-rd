package cn.meshed.cloud.rd.repo.executor;

import cn.meshed.cloud.rd.domain.repo.ability.RepositoryAbility;
import cn.meshed.cloud.rd.repo.data.RepositoryDTO;
import cn.meshed.cloud.rd.repo.data.RepositoryFileDTO;
import cn.meshed.cloud.rd.repo.executor.query.RepositoryBlobQryExe;
import cn.meshed.cloud.rd.repo.executor.query.RepositoryQryExe;
import cn.meshed.cloud.rd.repo.executor.query.RepositoryTreeQryExe;
import cn.meshed.cloud.rd.repo.query.RepositoryBlobQry;
import cn.meshed.cloud.rd.repo.query.RepositoryTreeQry;
import com.alibaba.cola.dto.MultiResponse;
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
public class RepositoryAbilityImpl implements RepositoryAbility {

    private final RepositoryBlobQryExe repositoryBlobQryExe;
    private final RepositoryTreeQryExe repositoryTreeQryExe;
    private final RepositoryQryExe repositoryQryExe;

    /**
     * 仓库目录树
     *
     * @param repositoryTreeQry 查询参数
     * @return {@link MultiResponse < RepositoryFileDTO >}
     */
    @Override
    public MultiResponse<RepositoryFileDTO> listRepositoryTree(RepositoryTreeQry repositoryTreeQry) {
        return repositoryTreeQryExe.execute(repositoryTreeQry);
    }

    /**
     * @param repositoryBlobQry
     * @return
     */
    @Override
    public SingleResponse<String> blob(RepositoryBlobQry repositoryBlobQry) {
        return repositoryBlobQryExe.execute(repositoryBlobQry);
    }

    /**
     * 详情
     *
     * @param repoId 仓库ID
     * @return
     */
    @Override
    public SingleResponse<RepositoryDTO> details(String repoId) {
        return repositoryQryExe.execute(repoId);
    }
}
