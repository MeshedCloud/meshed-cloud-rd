package cn.meshed.cloud.rd.repo.gatewayimpl;

import cn.hutool.http.HttpStatus;
import cn.meshed.cloud.rd.domain.repo.AddRepositoryMember;
import cn.meshed.cloud.rd.domain.repo.Branch;
import cn.meshed.cloud.rd.domain.repo.CommitRepositoryFile;
import cn.meshed.cloud.rd.domain.repo.CreateRepository;
import cn.meshed.cloud.rd.domain.repo.CreateRepositoryGroup;
import cn.meshed.cloud.rd.domain.repo.ListRepositoryTree;
import cn.meshed.cloud.rd.domain.repo.Repository;
import cn.meshed.cloud.rd.domain.repo.RepositoryFile;
import cn.meshed.cloud.rd.domain.repo.RepositoryGroup;
import cn.meshed.cloud.rd.domain.repo.RepositoryTreeItem;
import cn.meshed.cloud.rd.domain.repo.constant.ListRepositoryTreeType;
import cn.meshed.cloud.rd.domain.repo.gateway.RepositoryGateway;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import com.alibaba.cola.exception.SysException;
import com.aliyun.devops20210625.Client;
import com.aliyun.devops20210625.models.AddGroupMemberRequest;
import com.aliyun.devops20210625.models.CreateBranchRequest;
import com.aliyun.devops20210625.models.CreateBranchResponse;
import com.aliyun.devops20210625.models.CreateFileRequest;
import com.aliyun.devops20210625.models.CreateFileResponse;
import com.aliyun.devops20210625.models.CreateRepositoryGroupRequest;
import com.aliyun.devops20210625.models.CreateRepositoryGroupResponse;
import com.aliyun.devops20210625.models.CreateRepositoryGroupResponseBody;
import com.aliyun.devops20210625.models.CreateRepositoryRequest;
import com.aliyun.devops20210625.models.CreateRepositoryResponse;
import com.aliyun.devops20210625.models.CreateRepositoryResponseBody;
import com.aliyun.devops20210625.models.DeleteBranchRequest;
import com.aliyun.devops20210625.models.DeleteGroupMemberRequest;
import com.aliyun.devops20210625.models.GetRepositoryRequest;
import com.aliyun.devops20210625.models.GetRepositoryResponse;
import com.aliyun.devops20210625.models.GetRepositoryResponseBody;
import com.aliyun.devops20210625.models.ListRepositoryTreeRequest;
import com.aliyun.devops20210625.models.ListRepositoryTreeResponse;
import com.aliyun.devops20210625.models.ListRepositoryTreeResponseBody;
import com.aliyun.devops20210625.models.UpdateFileRequest;
import com.aliyun.devops20210625.models.UpdateFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.meshed.cloud.rd.domain.repo.constant.RepoConstant.DEVELOP;
import static cn.meshed.cloud.rd.domain.repo.constant.RepoConstant.MASTER;

/**
 * <h1>存储库 - 云效实现 </h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CodeupRepositoryGatewayImpl implements RepositoryGateway {

    private final Client client;
    private final Integer VISIBILITY = 10;

    @Value("${rd.warehouse.codeup.organization-id}")
    private String organizationId;
    @Value("${rd.warehouse.codeup.root-group}")
    private Long rootGroup;

    /**
     * 创建仓库
     *
     * @param createRepository 代码仓库参数
     * @return 仓库信息
     */
    @Override
    public Repository createRepository(CreateRepository createRepository) {
        AssertUtils.isTrue(StringUtils.isNotBlank(createRepository.getRepositoryName()), "存储库名称不能为空");

        try {

            CreateRepositoryResponse repositoryResponse = client.createRepository(buildCreateRepositoryRequest(createRepository));
            CreateRepositoryResponseBody repositoryBody = repositoryResponse.getBody();
            if (repositoryResponse.getStatusCode() == HttpStatus.HTTP_OK && repositoryBody.getSuccess()) {
                Repository repository = new Repository();
                CreateRepositoryResponseBody.CreateRepositoryResponseBodyResult result = repositoryBody.getResult();
                repository.setRepoUrl(result.getHttpUrlToRepo());
                repository.setRepositoryId(String.valueOf(result.getId()));
                repository.setRepositoryName(result.getName());
                return repository;
            }
            throw new SysException(repositoryBody.errorCode + "#" + repositoryBody.errorMessage);
        } catch (Exception e) {
            throw new SysException(e.getMessage());
        }
    }

    /**
     * 查询代码库，支持按代码库ID和代码库路径(Path)查询
     *
     * @param identity 代码库ID或路径
     * @return 仓库信息
     */
    @Override
    public Repository getRepository(String identity) {
        GetRepositoryRequest getRepositoryRequest = new GetRepositoryRequest();
        getRepositoryRequest.setOrganizationId(organizationId);
        getRepositoryRequest.setIdentity(organizationId + "/" + identity);
        try {
            GetRepositoryResponse repositoryResponse = client.getRepository(getRepositoryRequest);
            GetRepositoryResponseBody repositoryBody = repositoryResponse.getBody();
            if (repositoryResponse.getStatusCode() == HttpStatus.HTTP_OK && repositoryBody.getSuccess()) {
                Repository repository = new Repository();
                GetRepositoryResponseBody.GetRepositoryResponseBodyRepository result = repositoryBody.getRepository();
                repository.setRepoUrl(result.getHttpUrlToRepository());
                repository.setRepositoryId(String.valueOf(result.getId()));
                repository.setRepositoryName(result.getName());
                return repository;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @NotNull
    private CreateRepositoryRequest buildCreateRepositoryRequest(CreateRepository createRepository) {
        CreateRepositoryRequest request = new CreateRepositoryRequest();
        request.setOrganizationId(organizationId);
        request.setSync(true);
        request.setName(createRepository.getRepositoryName());
        request.setNamespaceId(createRepository.getNamespaceId());
        if (StringUtils.isNotBlank(createRepository.getDescription())) {
            request.setDescription(createRepository.getDescription());
        }
        if (createRepository.isVisible()) {
            //0 私有（默认） 10 企业内部公开
            request.setVisibilityLevel(VISIBILITY);
        }
        if (StringUtils.isNotBlank(createRepository.getImportUrl())) {
            request.setImportUrl(createRepository.getImportUrl());
        }
        if (StringUtils.isNotBlank(createRepository.getImportToken())) {
            request.setAccessToken(createRepository.getImportToken());
        }
        return request;
    }

    /**
     * 创建分组/项目（映射项目）
     *
     * @param createRepositoryGroup 分组名称
     * @return 分组信息
     */
    @Override
    public RepositoryGroup createRepositoryGroup(CreateRepositoryGroup createRepositoryGroup) {
        AssertUtils.isTrue(StringUtils.isNotBlank(createRepositoryGroup.getGroupName()), "分组名称不能为空");
        CreateRepositoryGroupRequest request = new CreateRepositoryGroupRequest();
        request.setOrganizationId(organizationId);
        request.setDescription(createRepositoryGroup.getDescription());
        request.setName(createRepositoryGroup.getGroupName());
        request.setPath(createRepositoryGroup.getGroupName());
        request.setParentId(rootGroup);
        if (createRepositoryGroup.isVisible()) {
            request.setVisibilityLevel(VISIBILITY);
        }
        try {
            CreateRepositoryGroupResponse repositoryGroupResponse = client.createRepositoryGroup(request);
            CreateRepositoryGroupResponseBody groupResponseBody = repositoryGroupResponse.getBody();
            if (repositoryGroupResponse.getStatusCode() == HttpStatus.HTTP_OK && groupResponseBody.getSuccess()) {
                RepositoryGroup repositoryGroup = new RepositoryGroup();
                CreateRepositoryGroupResponseBody.CreateRepositoryGroupResponseBodyResult result
                        = groupResponseBody.getResult();
                repositoryGroup.setGroupId(result.getId());
                repositoryGroup.setGroupName(result.getName());
                return repositoryGroup;
            }
            throw new SysException(groupResponseBody.errorCode + "#" + groupResponseBody.errorMessage);
        } catch (Exception e) {
            throw new SysException(e.getMessage());
        }
    }

    /**
     * 查询代码库的文件树
     *
     * @param listRepositoryTree 参数
     * @return 文件树列表
     */
    @Override
    public List<RepositoryTreeItem> listRepositoryTree(ListRepositoryTree listRepositoryTree) {
        ListRepositoryTreeRequest listRepositoryTreeRequest = new ListRepositoryTreeRequest();
        listRepositoryTreeRequest.setOrganizationId(organizationId);
        listRepositoryTreeRequest.setType(ListRepositoryTreeType.RECURSIVE.name());
        try {
            ListRepositoryTreeResponse listTreeResponse
                    = client.listRepositoryTree(String.valueOf(listRepositoryTree.getRepositoryId()),
                    listRepositoryTreeRequest);
            if (listTreeResponse.getStatusCode() == HttpStatus.HTTP_OK && listTreeResponse.getBody().getSuccess()) {
                List<ListRepositoryTreeResponseBody.ListRepositoryTreeResponseBodyResult> result =
                        listTreeResponse.getBody().getResult();
                return CopyUtils.copyListProperties(result, RepositoryTreeItem::new);

            }
            return null;
        } catch (Exception e) {
            throw new SysException(e.getMessage());
        }
    }

    /**
     * 提交文件到仓库
     *
     * @param commitRepositoryFile 提交信息
     * @return 数量
     */
    @Override
    public Integer commitRepositoryFile(CommitRepositoryFile commitRepositoryFile) {
        AssertUtils.isTrue(commitRepositoryFile != null, "请求参数不能为空");
        assert commitRepositoryFile != null;
        AssertUtils.isTrue(StringUtils.isNotBlank(commitRepositoryFile.getCommitMessage()), "提交信息不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(commitRepositoryFile.getBranchName()), "提交分支不能为空");
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(commitRepositoryFile.getFiles()), "提交文件不能为空");
        //查询文件树
        List<RepositoryTreeItem> repositoryTreeList = getRepositoryTreeItemList(commitRepositoryFile);
        //分类更新或修改
        List<RepositoryFile> newFiles = new ArrayList<>();
        List<RepositoryFile> oldFiles = new ArrayList<>();
        handleSaveOrUpdate(commitRepositoryFile.getFiles(), repositoryTreeList, newFiles, oldFiles);
        //新建代码提交
        int commitCount = newCommit(commitRepositoryFile, newFiles);
        //更新代码提交
        commitCount += updateCommit(commitRepositoryFile, oldFiles);

        return commitCount;
    }

    /**
     * 创建分支
     *
     * @param branch 分支
     * @return 成功与否
     */
    @Override
    public boolean createBranch(String repositoryId, Branch branch) {
        CreateBranchRequest createBranchRequest = new CreateBranchRequest();
        createBranchRequest.setOrganizationId(organizationId);
        createBranchRequest.setRef(branch.getRef());
        createBranchRequest.setBranchName(branch.getBranchName());

        try {
            CreateBranchResponse branchResponse = client
                    .createBranch(repositoryId, createBranchRequest);
            if (branchResponse.getBody().getSuccess()) {
                return true;
            }
            log.error("{} create branch error : {}", branch.getBranchName(), branchResponse.getBody().getErrorMessage());
        } catch (Exception e) {
            log.error("{} create branch fail : {} ", branch.getBranchName(), e.getMessage());
        }
        return false;
    }

    /**
     * 重新构建分支
     * 删除原来分支拉取新分支
     *
     * @param repositoryId 仓库ID
     * @param branch       分支
     * @return 成功与否
     */
    @Override
    public boolean rebuildBranch(String repositoryId, Branch branch) {
        //删除已有的分支
        deleteBranch(repositoryId, branch.getBranchName());
        //构建新的分支
        boolean isBuild = createBranch(repositoryId, branch);
        if (!isBuild) {
            log.error("{} rebuild branch fail", branch);
        }
        return false;
    }

    /**
     * 删除分支
     *
     * @param repositoryId 仓库ID
     * @param branch       分支
     * @return 成功与否
     */
    @Override
    public boolean deleteBranch(String repositoryId, String branch) {
        AssertUtils.isTrue(!MASTER.equals(branch), "主分支不允许删除");
        AssertUtils.isTrue(!DEVELOP.equals(branch), "开发分支不允许删除");
        DeleteBranchRequest deleteBranchRequest = new DeleteBranchRequest();
        deleteBranchRequest.setOrganizationId(organizationId);
        deleteBranchRequest.setBranchName(branch);
        try {
            client.deleteBranch(repositoryId, deleteBranchRequest);
        } catch (Exception e) {
            log.error("{} delete branch fail : {} ", branch, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 添加代码组成员
     *
     * @param repositoryId        仓库ID
     * @param addRepositoryMember 添加仓库成员
     * @return 成功与否
     */
    @Override
    public boolean addGroupMember(String repositoryId, AddRepositoryMember addRepositoryMember) {
        AssertUtils.isTrue(StringUtils.isNotBlank(repositoryId), "仓库ID不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(addRepositoryMember.getRepoUid()), "仓库用户ID不能为空");
        AssertUtils.isTrue(addRepositoryMember.getAccessLevel() != null, "项目等级不能为空");

        AddGroupMemberRequest addGroupMemberRequest = new AddGroupMemberRequest();
        addGroupMemberRequest.setOrganizationId(organizationId);
        addGroupMemberRequest.setAliyunPks(addRepositoryMember.getRepoUid());
        addGroupMemberRequest.setAccessLevel(addGroupMemberRequest.accessLevel);
        try {
            client.addGroupMember(repositoryId, addGroupMemberRequest);
        } catch (Exception e) {
            log.error("{} add group member fail : {} ", addRepositoryMember, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 删除代码组成员
     *
     * @param repositoryId 仓库ID
     * @param repoUid      仓库用户ID
     * @return 成功与否
     */
    @Override
    public boolean deleteGroupMember(String repositoryId, String repoUid) {
        AssertUtils.isTrue(StringUtils.isNotBlank(repositoryId), "仓库ID不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(repoUid), "仓库用户ID不能为空");

        DeleteGroupMemberRequest deleteGroupMemberRequest = new DeleteGroupMemberRequest();
        deleteGroupMemberRequest.setOrganizationId(organizationId);
        deleteGroupMemberRequest.setAliyunPk(repoUid);
        try {
            client.deleteGroupMember(repositoryId, deleteGroupMemberRequest);
        } catch (Exception e) {
            log.error("{} delete group member fail : {} ", repoUid, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 更新文件提交
     *
     * @param commitRepositoryFile 提交信息
     * @param oldFiles             旧文件列表
     * @return 成功数
     */
    private int updateCommit(CommitRepositoryFile commitRepositoryFile, List<RepositoryFile> oldFiles) {
        if (CollectionUtils.isNotEmpty(oldFiles)) {
            int success = 0;
            for (RepositoryFile file : oldFiles) {
                log.debug("仓库: {} 更新文件: {}", commitRepositoryFile.getRepositoryId(), file.getFilePath());
                UpdateFileRequest updateFileRequest = new UpdateFileRequest();
                updateFileRequest.setOrganizationId(organizationId);
                updateFileRequest.setBranchName(commitRepositoryFile.getBranchName());
                updateFileRequest.setCommitMessage(commitRepositoryFile.getCommitMessage());
                updateFileRequest.setContent(file.getContent());
                updateFileRequest.setNewPath(file.getFilePath());
                updateFileRequest.setOldPath(file.getOldFilePath());
                try {
                    UpdateFileResponse fileResponse = client
                            .updateFile(commitRepositoryFile.getRepositoryId(), updateFileRequest);
                    if (fileResponse.getBody().getSuccess()) {
                        success++;
                    } else {
                        log.error("Commit repository update file Error:{} | {}",
                                fileResponse.getBody().errorCode, fileResponse.getBody().errorMessage);
                    }
                } catch (Exception e) {
                    //此问题需要排除解决，无法抛出异常
                    log.error("Commit repository update file Exception:{}", e.getMessage(), e);
                }
            }
            return success;
        }
        return 0;
    }

    /**
     * 提交新代码
     *
     * @param commitRepositoryFile 提交信息
     * @param newFiles             新文件列表
     * @return 成功数
     */
    private int newCommit(CommitRepositoryFile commitRepositoryFile, List<RepositoryFile> newFiles) {
        if (CollectionUtils.isNotEmpty(newFiles)) {
            int success = 0;
            for (RepositoryFile file : newFiles) {
                log.debug("仓库: {} 新建文件: {}", commitRepositoryFile.getRepositoryId(), file.getFilePath());
                CreateFileRequest createFileRequest = new CreateFileRequest();
                createFileRequest.setOrganizationId(organizationId);
                createFileRequest.setFilePath(file.getFilePath());
                createFileRequest.setContent(file.getContent());
                createFileRequest.setCommitMessage(commitRepositoryFile.getCommitMessage());
                createFileRequest.setBranchName(commitRepositoryFile.getBranchName());
                try {
                    CreateFileResponse fileResponse = client
                            .createFile(commitRepositoryFile.getRepositoryId(), createFileRequest);
                    if (fileResponse.getBody().getSuccess()) {
                        success++;
                    } else {
                        log.error("Commit repository new file Error:{} | {}",
                                fileResponse.getBody().errorCode, fileResponse.getBody().errorMessage);
                    }
                } catch (Exception e) {
                    //此问题需要排除解决，无法抛出异常
                    log.error("Commit repository new file Exception:{}", e.getMessage(), e);
                }
            }
            return success;
        }
        return 0;
    }

    private List<RepositoryTreeItem> getRepositoryTreeItemList(CommitRepositoryFile commitRepositoryFile) {
        ListRepositoryTree listRepositoryTree = new ListRepositoryTree();
        listRepositoryTree.setRepositoryId(commitRepositoryFile.getRepositoryId());
        listRepositoryTree.setRefName(commitRepositoryFile.getBranchName());
        listRepositoryTree.setType(ListRepositoryTreeType.RECURSIVE);
        List<RepositoryTreeItem> repositoryTreeList = listRepositoryTree(listRepositoryTree);
        return repositoryTreeList;
    }

    private void handleSaveOrUpdate(List<RepositoryFile> files, List<RepositoryTreeItem> repositoryTreeList, List<RepositoryFile> newFiles, List<RepositoryFile> oldFiles) {
        if (CollectionUtils.isNotEmpty(files)) {
            List<String> repoFileList = repositoryTreeList.stream()
                    .map(RepositoryTreeItem::getPath).collect(Collectors.toList());
            for (RepositoryFile file : files) {
                //避免操作空的情况
                if (StringUtils.isBlank(file.getFilePath())) {
                    continue;
                }
                //区分老旧
                if (repoFileList.contains(file.getFilePath())) {
                    oldFiles.add(file);
                } else {
                    newFiles.add(file);
                }
            }
        }
    }


}
