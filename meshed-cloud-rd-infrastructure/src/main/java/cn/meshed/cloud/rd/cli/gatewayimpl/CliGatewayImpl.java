package cn.meshed.cloud.rd.cli.gatewayimpl;

import cn.hutool.core.io.FileUtil;
import cn.meshed.cloud.rd.codegen.GenerateClassExecute;
import cn.meshed.cloud.rd.domain.cli.Archetype;
import cn.meshed.cloud.rd.domain.cli.Artifact;
import cn.meshed.cloud.rd.domain.cli.BuildArchetype;
import cn.meshed.cloud.rd.domain.cli.GenerateAdapter;
import cn.meshed.cloud.rd.domain.cli.GenerateModel;
import cn.meshed.cloud.rd.domain.cli.GenerateRpc;
import cn.meshed.cloud.rd.domain.cli.gateway.CliGateway;
import cn.meshed.cloud.rd.domain.repo.CommitRepositoryFile;
import cn.meshed.cloud.rd.domain.repo.CreateBranch;
import cn.meshed.cloud.rd.domain.repo.RepositoryFile;
import cn.meshed.cloud.rd.domain.repo.gateway.RepositoryGateway;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.IdUtils;
import com.alibaba.cola.exception.SysException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.cli.MavenCli;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.meshed.cloud.rd.cli.gatewayimpl.MavenConstant.ARCHETYPE_ARTIFACT_ID;
import static cn.meshed.cloud.rd.cli.gatewayimpl.MavenConstant.ARCHETYPE_GENERATE_ARG;
import static cn.meshed.cloud.rd.cli.gatewayimpl.MavenConstant.ARCHETYPE_GROUP_ID;
import static cn.meshed.cloud.rd.cli.gatewayimpl.MavenConstant.ARCHETYPE_VERSION;
import static cn.meshed.cloud.rd.cli.gatewayimpl.MavenConstant.ARG_FORMAT;
import static cn.meshed.cloud.rd.cli.gatewayimpl.MavenConstant.ARTIFACT_ID;
import static cn.meshed.cloud.rd.cli.gatewayimpl.MavenConstant.BUILD_ARG;
import static cn.meshed.cloud.rd.cli.gatewayimpl.MavenConstant.GROUP_ID;
import static cn.meshed.cloud.rd.cli.gatewayimpl.MavenConstant.MULTI_MODULE_PROJECT_DIRECTORY;
import static cn.meshed.cloud.rd.cli.gatewayimpl.MavenConstant.PACKAGE;
import static cn.meshed.cloud.rd.cli.gatewayimpl.MavenConstant.SETTING_PARAM_FORMAT;
import static cn.meshed.cloud.rd.cli.gatewayimpl.MavenConstant.VERSION;
import static cn.meshed.cloud.rd.domain.repo.constant.RepoConstant.MASTER;
import static cn.meshed.cloud.rd.domain.repo.constant.RepoConstant.WORKSPACE;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class CliGatewayImpl implements CliGateway {

    private final RepositoryGateway repositoryGateway;
    private final GenerateClassExecute generateClassExecute;
    @Value("${rd.cli.workspace}")
    private String workspace;

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
        if (StringUtils.isNotBlank(archetype.getSettingPath())) {
            args.add(String.format(SETTING_PARAM_FORMAT, archetype.getSettingPath()));
        }
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

        //构建原型
        String workspacePath = buildArchetype(buildArchetype);

        AssertUtils.isTrue(StringUtils.isNotBlank(workspacePath), "生成失败");
        //已存在会失败，无视异常
        repositoryGateway.createBranch(new CreateBranch(repositoryId, WORKSPACE, MASTER));
        String projectPath = workspacePath + "/" + buildArchetype.getArtifact().getArtifactId();
        //读取上传文件信息
        List<File> list = FileUtil.loopFiles(projectPath);
        List<RepositoryFile> repositoryFiles = new ArrayList<>();
        list.stream().filter(Objects::nonNull).forEach(file -> {
            String content = FileUtil.readString(file, StandardCharsets.UTF_8);
            String path = file.getPath().substring(projectPath.length()).replaceAll("\\\\", "/");
            repositoryFiles.add(new RepositoryFile(path, content));
        });

        //可能存在构建文件不存在
        if (CollectionUtils.isNotEmpty(repositoryFiles)) {
            CommitRepositoryFile commitRepositoryFile = new CommitRepositoryFile();
            commitRepositoryFile.setRepositoryId(repositoryId);
            commitRepositoryFile.setCommitMessage("Initialize scaffold");
            commitRepositoryFile.setBranchName(WORKSPACE);
            commitRepositoryFile.setFiles(repositoryFiles);
            int commitCount = repositoryGateway.commitRepositoryFile(commitRepositoryFile);
            if (commitCount == repositoryFiles.size()) {
                return WORKSPACE;
            }
            throw new SysException(String.format("骨架代码提交数：%s,成功：%s", repositoryFiles.size(), commitCount));
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
        AssertUtils.isTrue(StringUtils.isNotBlank(repositoryId), "仓库ID不能为空");
        AssertUtils.isTrue(generateModel != null
                && CollectionUtils.isNotEmpty(generateModel.getModels()), "参数不能为空");
        assert generateModel != null;
        generateModel.getModels().stream().filter(Objects::nonNull).forEach(model -> {
            String code = generateClassExecute.buildModel(model);
            System.out.println(code);
        });

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
        AssertUtils.isTrue(StringUtils.isNotBlank(repositoryId), "仓库ID不能为空");
        AssertUtils.isTrue(generateAdapter != null
                && CollectionUtils.isNotEmpty(generateAdapter.getAdapters()), "参数不能为空");
        assert generateAdapter != null;
        generateAdapter.getAdapters().stream().filter(Objects::nonNull).forEach(adapter -> {
            String code = generateClassExecute.buildAdapter(adapter);
            System.out.println(code);
        });
    }

    /**
     * 异步生成服务并推送
     *
     * @param repositoryId 仓库ID
     * @param generateRpc  生成RPC服务接口
     */
    @Override
    public void asyncGenerateRpcWithPush(String repositoryId, GenerateRpc generateRpc) {
        AssertUtils.isTrue(StringUtils.isNotBlank(repositoryId), "仓库ID不能为空");
        AssertUtils.isTrue(generateRpc != null
                && CollectionUtils.isNotEmpty(generateRpc.getRpcList()), "参数不能为空");
        assert generateRpc != null;
        generateRpc.getRpcList().stream().filter(Objects::nonNull).forEach(rpc -> {
            String code = generateClassExecute.buildRpc(rpc);
            System.out.println(code);
        });
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