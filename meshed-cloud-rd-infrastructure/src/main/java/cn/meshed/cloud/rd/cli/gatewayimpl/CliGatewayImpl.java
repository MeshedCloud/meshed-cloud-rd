package cn.meshed.cloud.rd.cli.gatewayimpl;

import cn.hutool.core.io.FileUtil;
import cn.meshed.cloud.rd.codegen.GenerateClassExecute;
import cn.meshed.cloud.rd.domain.cli.*;
import cn.meshed.cloud.rd.domain.cli.gateway.CliGateway;
import cn.meshed.cloud.rd.domain.cli.utils.GenerateUtils;
import cn.meshed.cloud.rd.domain.repo.Branch;
import cn.meshed.cloud.rd.domain.repo.CommitRepositoryFile;
import cn.meshed.cloud.rd.domain.repo.RepositoryFile;
import cn.meshed.cloud.rd.domain.repo.gateway.RepositoryGateway;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.IdUtils;
import com.alibaba.cola.exception.SysException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.cli.MavenCli;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.meshed.cloud.rd.domain.cli.constant.MavenConstant.*;
import static cn.meshed.cloud.rd.domain.repo.constant.RepoConstant.WORKSPACE;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CliGatewayImpl implements CliGateway {

    private final RepositoryGateway repositoryGateway;
    private final GenerateClassExecute generateClassExecute;
    @Value("${rd.cli.workspace}")
    private String workspace;
    @Value("${rd.cli.setting.url}")
    private String settingUrl;

    /**
     * 原型构建
     *
     * @param buildArchetype 构建信息
     * @return 工作路径
     * @throws SysException 构建异常信息
     */
    @Override
    public String buildArchetype(BuildArchetype buildArchetype) throws SysException {
        Archetype archetype = buildArchetype.getArchetype();
        Artifact artifact = buildArchetype.getArtifact();
        AssertUtils.isTrue(archetype != null, "原型信息不能为空");
        AssertUtils.isTrue(artifact != null, "生成信息不能为空");
        //构建
        MavenCli cli = new MavenCli();
        String mvnHome = MavenCli.USER_MAVEN_CONFIGURATION_HOME.getAbsolutePath();
        System.getProperties().setProperty(MULTI_MODULE_PROJECT_DIRECTORY, mvnHome);
        List<String> args = new ArrayList<>();
        args.add(ARCHETYPE_GENERATE_ARG);
        addArg(args, ARCHETYPE_GROUP_ID, archetype.getArchetypeGroupId());
        addArg(args, ARCHETYPE_ARTIFACT_ID, archetype.getArchetypeArtifactId());
        addArg(args, ARCHETYPE_VERSION, archetype.getArchetypeVersion());
        addArg(args, GROUP_ID, artifact.getGroupId());
        addArg(args, ARTIFACT_ID, artifact.getArtifactId());
        addArg(args, VERSION, artifact.getVersion());
        addArg(args, PACKAGE, artifact.getPackageName());
        args.add(BUILD_ARG);
        Map<String, String> extendedMap = artifact.getExtendedMap();
        if (extendedMap != null && extendedMap.size() > 0) {
            extendedMap.entrySet().stream().filter(Objects::nonNull)
                    .forEach(entry -> addArg(args, entry.getKey(), entry.getValue()));
        }
        //工作目录
        String workspacePath = getWorkspacePath();

        int status = 0;
        try {
            status = cli.doMain(args.toArray(new String[]{}), workspacePath, System.out, System.out);
        } catch (Exception e) {
            throw new SysException("原型生成失败：" + e.getMessage(), e);
        }
        if (status != 0) {
            throw new SysException("原型生成失败");
        }

        return workspacePath;
    }

    /**
     * 原型构建并推送仓库
     *
     * @param repositoryId   仓库ID
     * @param buildArchetype 构建信息
     * @return 分支
     * @throws SysException
     */
    @Override
    public String archetypeWithPush(String repositoryId, BuildArchetype buildArchetype) throws SysException {
        AssertUtils.isTrue(buildArchetype.getBranch() != null, "分支信息不能为空");
        //重建工作分支，避免已存在
        repositoryGateway.rebuildBranch(repositoryId, buildArchetype.getBranch());
        //构建原型
        String workspacePath = buildArchetype(buildArchetype);
        AssertUtils.isTrue(StringUtils.isNotBlank(workspacePath), "生成失败");
        String projectPath = workspacePath + "/" + buildArchetype.getArtifact().getArtifactId();
        //读取上传文件信息
        List<File> list = FileUtil.loopFiles(projectPath);
        List<RepositoryFile> repositoryFiles = new ArrayList<>();
        list.stream().filter(Objects::nonNull).forEach(file -> {
            String content = FileUtil.readString(file, StandardCharsets.UTF_8);
            String path = file.getPath().substring(projectPath.length()).replaceAll("\\\\", "/");
            repositoryFiles.add(new RepositoryFile(path, content));
        });
        return commitFiles(repositoryId, repositoryFiles, "Initialize scaffold", buildArchetype.getBranch());
    }

    /**
     * 批量提交文件到工作分支
     *
     * @param repositoryId    仓库ID
     * @param repositoryFiles 文件列表
     * @return
     */
    @Nullable
    private String commitFiles(String repositoryId, List<RepositoryFile> repositoryFiles, String commitMessage, Branch branch) {
        //可能存在构建文件不存在
        if (CollectionUtils.isNotEmpty(repositoryFiles)) {
            CommitRepositoryFile commitRepositoryFile = new CommitRepositoryFile();
            commitRepositoryFile.setRepositoryId(repositoryId);
            commitRepositoryFile.setCommitMessage(commitMessage);
            commitRepositoryFile.setBranchName(branch.getBranchName());
            commitRepositoryFile.setFiles(repositoryFiles);
            int commitCount = repositoryGateway.commitRepositoryFile(commitRepositoryFile);
            if (commitCount == repositoryFiles.size()) {
                return WORKSPACE;
            }
            throw new SysException(String.format("代码提交数：%s,成功：%s", repositoryFiles.size(), commitCount));
        }
        return null;
    }

    /**
     * 异步生成模型并推送
     *
     * @param repositoryId  仓库ID
     * @param generateModel 生成模型
     * @return
     */
    @Async
    @Override
    public void asyncGenerateModelWithPush(String repositoryId, GenerateModel generateModel) {
        check(repositoryId, generateModel);
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(generateModel.getModels()), "构建模型不能为空");

        //构建代码并转换数据
        List<RepositoryFile> repositoryFiles = generateModel.getModels().stream().filter(Objects::nonNull).map(model -> {
            String code = generateClassExecute.buildModel(model);
            String path = GenerateUtils.packageToPath(generateModel.getBasePath(), model.getPackageName());
            return new RepositoryFile(path, code);
        }).collect(Collectors.toList());
        //提交
        commitFiles(repositoryId, repositoryFiles, generateModel.getCommitMessage(), generateModel.getBranch());
    }

    /**
     * 异步生成枚举并推送
     *
     * @param repositoryId 仓库ID
     * @param generateEnum 生成枚举
     */
    @Override
    public void asyncGenerateEnumWithPush(String repositoryId, GenerateEnum generateEnum) {
        check(repositoryId, generateEnum);
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(generateEnum.getEnums()), "构建模型不能为空");

        //构建代码并转换数据
        List<RepositoryFile> repositoryFiles = generateEnum.getEnums().stream().filter(Objects::nonNull).map(objectEnum -> {
            String code = generateClassExecute.buildEnum(objectEnum);
            String path = GenerateUtils.packageToPath(generateEnum.getBasePath(), objectEnum.getPackageName());
            return new RepositoryFile(path, code);
        }).collect(Collectors.toList());
        //提交
        commitFiles(repositoryId, repositoryFiles, generateEnum.getCommitMessage(), generateEnum.getBranch());
    }

    /**
     * 异步生成服务并推送
     *
     * @param repositoryId    仓库ID
     * @param generateAdapter 生成服务
     */
    @Async
    @Override
    public void asyncGenerateAdapterWithPush(String repositoryId, GenerateAdapter generateAdapter) {
        check(repositoryId, generateAdapter);
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(generateAdapter.getAdapters()), "构建适配器不能为空");

        //构建代码并转换数据
        List<RepositoryFile> repositoryFiles = generateAdapter.getAdapters().stream().filter(Objects::nonNull).map(adapter -> {
            String code = generateClassExecute.buildAdapter(adapter);
            String path = GenerateUtils.packageToPath(generateAdapter.getBasePath(), adapter.getPackageName());
            return new RepositoryFile(path, code);
        }).collect(Collectors.toList());
        //提交
        commitFiles(repositoryId, repositoryFiles, generateAdapter.getCommitMessage(), generateAdapter.getBranch());
    }

    /**
     * 异步生成服务并推送
     *
     * @param repositoryId 仓库ID
     * @param generateRpc  生成RPC服务接口
     */
    @Override
    public void asyncGenerateRpcWithPush(String repositoryId, GenerateRpc generateRpc) {
        check(repositoryId, generateRpc);
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(generateRpc.getRpcList()), "构建RPC不能为空");
        //构建代码并转换数据
        List<RepositoryFile> repositoryFiles = generateRpc.getRpcList().stream().filter(Objects::nonNull).map(rpc -> {
            String code = generateClassExecute.buildRpc(rpc);
            String path = GenerateUtils.packageToPath(generateRpc.getBasePath(), rpc.getPackageName());
            return new RepositoryFile(path, code);
        }).collect(Collectors.toList());
        //提交
        commitFiles(repositoryId, repositoryFiles, generateRpc.getCommitMessage(), generateRpc.getBranch());
    }

    /**
     * 检查参数
     *
     * @param repositoryId 仓库id
     * @param generateCode 通用生成
     */
    private void check(String repositoryId, GenerateCode generateCode) {
        AssertUtils.isTrue(generateCode != null, "构建参数不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(repositoryId), "仓库ID不能为空");
        assert generateCode != null;
        AssertUtils.isTrue(generateCode.getBranch() != null, "构建分支不能为空");
    }

    /**
     * 构建参数
     *
     * @param parameter
     * @param value
     * @return
     */
    @NotNull
    private void addArg(List<String> args, String parameter, String value) {
        if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(parameter)) {
            args.add(String.format(ARG_FORMAT, parameter, value));
        }
    }

    @NotNull
    private String getWorkspacePath() {
        if (!workspace.endsWith("/")) {
            workspace = workspace + "/";
        }
        return workspace + IdUtils.simpleUUID() + "/";
    }
}
