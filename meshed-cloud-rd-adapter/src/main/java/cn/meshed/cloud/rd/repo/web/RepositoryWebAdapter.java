package cn.meshed.cloud.rd.repo.web;

import cn.meshed.cloud.rd.domain.repo.ability.RepositoryAbility;
import cn.meshed.cloud.rd.repo.RepositoryAdapter;
import cn.meshed.cloud.rd.repo.data.RepositoryDTO;
import cn.meshed.cloud.rd.repo.data.RepositoryFileDTO;
import cn.meshed.cloud.rd.repo.query.RepositoryBlobQry;
import cn.meshed.cloud.rd.repo.query.RepositoryTreeQry;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
public class RepositoryWebAdapter implements RepositoryAdapter {

    private final RepositoryAbility repositoryAbility;

    /**
     * 查询代码库的文件树
     *
     * @param repositoryTreeQry 参数
     * @return 文件树列表
     */
    @Override
    public MultiResponse<RepositoryFileDTO> listRepositoryTree(RepositoryTreeQry repositoryTreeQry) {
        return repositoryAbility.listRepositoryTree(repositoryTreeQry);
    }

    /**
     * 详情
     *
     * @param repoId 物理仓库ID
     * @return {@link SingleResponse <  RepositoryDTO  >}
     */
    @Override
    public SingleResponse<RepositoryDTO> details(String repoId) {
        return repositoryAbility.details(repoId);
    }

    /**
     * @param repositoryBlobQry
     * @return
     */
    @Override
    public SingleResponse<String> blob(RepositoryBlobQry repositoryBlobQry) {
        return repositoryAbility.blob(repositoryBlobQry);
    }
}
