package cn.meshed.cloud.rd.domain.repo.ability;

import cn.meshed.cloud.core.IDetails;
import cn.meshed.cloud.rd.repo.data.RepositoryDTO;
import cn.meshed.cloud.rd.repo.data.RepositoryFileDTO;
import cn.meshed.cloud.rd.repo.query.RepositoryBlobQry;
import cn.meshed.cloud.rd.repo.query.RepositoryTreeQry;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface RepositoryAbility extends IDetails<String, SingleResponse<RepositoryDTO>> {

    /**
     * 仓库目录树
     *
     * @param repositoryTreeQry 查询参数
     * @return {@link MultiResponse<RepositoryFileDTO>}
     */
    MultiResponse<RepositoryFileDTO> listRepositoryTree(RepositoryTreeQry repositoryTreeQry);

    SingleResponse<String> blob(RepositoryBlobQry repositoryBlobQry);
}
