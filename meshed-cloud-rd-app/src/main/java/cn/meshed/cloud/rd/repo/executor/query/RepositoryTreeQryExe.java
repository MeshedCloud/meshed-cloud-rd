package cn.meshed.cloud.rd.repo.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.repo.ListRepositoryTree;
import cn.meshed.cloud.rd.domain.repo.RepositoryTreeItem;
import cn.meshed.cloud.rd.domain.repo.constant.ListRepositoryTreeType;
import cn.meshed.cloud.rd.domain.repo.gateway.RepositoryGateway;
import cn.meshed.cloud.rd.repo.data.RepositoryFileDTO;
import cn.meshed.cloud.rd.repo.enums.RepositoryTreeTypeEnum;
import cn.meshed.cloud.rd.repo.query.RepositoryTreeQry;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.MultiResponse;
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
public class RepositoryTreeQryExe implements QueryExecute<RepositoryTreeQry, MultiResponse<RepositoryFileDTO>> {

    private final RepositoryGateway repositoryGateway;

    /**
     * <h1>查询执行器</h1>
     *
     * @param repositoryTreeQry 执行器 {@link RepositoryTreeQry}
     * @return {@link MultiResponse<RepositoryFileDTO>}
     */
    @Override
    public MultiResponse<RepositoryFileDTO> execute(RepositoryTreeQry repositoryTreeQry) {

        ListRepositoryTree listRepositoryTree = new ListRepositoryTree();
        listRepositoryTree.setRepositoryId(repositoryTreeQry.getRepositoryId());
        listRepositoryTree.setPath(repositoryTreeQry.getPath());
        listRepositoryTree.setType(getType(repositoryTreeQry.getType()));
        listRepositoryTree.setRefName(repositoryTreeQry.getRefName());
        List<RepositoryTreeItem> repositoryTreeItems = repositoryGateway.listRepositoryTree(listRepositoryTree);
        return ResultUtils.copyMulti(repositoryTreeItems, RepositoryFileDTO::new);
    }

    private ListRepositoryTreeType getType(RepositoryTreeTypeEnum type) {
        if (type == null) {
            return ListRepositoryTreeType.DIRECT;
        } else if (type == RepositoryTreeTypeEnum.DIRECT) {
            return ListRepositoryTreeType.DIRECT;
        } else if (type == RepositoryTreeTypeEnum.FLATTEN) {
            return ListRepositoryTreeType.FLATTEN;
        } else if (type == RepositoryTreeTypeEnum.RECURSIVE) {
            return ListRepositoryTreeType.RECURSIVE;
        }
        return ListRepositoryTreeType.DIRECT;
    }
}
