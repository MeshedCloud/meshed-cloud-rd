package cn.meshed.cloud.rd.domain.repo.gateway;

import cn.meshed.cloud.rd.domain.repo.AddRepositoryMember;
import cn.meshed.cloud.rd.domain.repo.Branch;
import cn.meshed.cloud.rd.domain.repo.CommitRepositoryFile;
import cn.meshed.cloud.rd.domain.repo.CreateRepository;
import cn.meshed.cloud.rd.domain.repo.CreateRepositoryGroup;
import cn.meshed.cloud.rd.domain.repo.ListRepositoryTree;
import cn.meshed.cloud.rd.domain.repo.Repository;
import cn.meshed.cloud.rd.domain.repo.RepositoryBlob;
import cn.meshed.cloud.rd.domain.repo.RepositoryGroup;
import cn.meshed.cloud.rd.domain.repo.RepositoryTreeItem;

import java.util.List;

/**
 * <h1>代码仓库</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface RepositoryGateway {

    /**
     * 创建仓库
     *
     * @param createRepository 代码仓库参数
     * @return 仓库信息
     */
    Repository createRepository(CreateRepository createRepository);


    /**
     * 查询代码库，支持按代码库ID和代码库路径(Path)查询
     *
     * @param identity 代码库ID或路径
     * @return 仓库信息
     */
    Repository getRepository(String identity);

    /**
     * 创建分组/项目（映射项目）
     *
     * @param createRepositoryGroup 分组名称
     * @return 分组信息
     */
    RepositoryGroup createRepositoryGroup(CreateRepositoryGroup createRepositoryGroup);

    /**
     * 查询代码库的文件树
     *
     * @param listRepositoryTree 参数
     * @return 文件树列表
     */
    List<RepositoryTreeItem> listRepositoryTree(ListRepositoryTree listRepositoryTree);

    /**
     * 提交文件到仓库
     *
     * @param commitRepositoryFile 提交信息
     * @return 数量
     */
    Integer commitRepositoryFile(CommitRepositoryFile commitRepositoryFile);


    /**
     * 分支列表
     *
     * @param repositoryId 仓库ID
     * @return 分支列表
     */
    List<String> branchList(String repositoryId);

    /**
     * 创建分支
     *
     * @param repositoryId 仓库ID
     * @param branch       分支
     * @return 成功与否
     */
    boolean createBranch(String repositoryId, Branch branch);

    /**
     * 重新构建分支
     * 删除原来分支拉取新分支
     *
     * @param repositoryId 仓库ID
     * @param branch       分支
     * @return 成功与否
     */
    boolean rebuildBranch(String repositoryId, Branch branch);

    /**
     * 删除分支
     *
     * @param repositoryId 仓库ID
     * @param branch       分支
     * @return 成功与否
     */
    boolean deleteBranch(String repositoryId, String branch);

    /**
     * 添加代码组成员
     *
     * @param repositoryId        仓库ID
     * @param addRepositoryMember 添加仓库成员
     * @return 成功与否
     */
    boolean addGroupMember(String repositoryId, AddRepositoryMember addRepositoryMember);

    /**
     * 删除代码组成员
     *
     * @param repositoryId 仓库ID
     * @param repoUid      仓库用户ID
     * @return 成功与否
     */
    boolean deleteGroupMember(String repositoryId, String repoUid);

    /**
     * 获取文件
     *
     * @param repositoryId   仓库ID
     * @param repositoryBlob 文件参数
     * @return 文件内容
     */
    String getBlob(String repositoryId, RepositoryBlob repositoryBlob);
}
