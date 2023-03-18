package cn.meshed.cloud.rd.deployment.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.deployment.command.WarehouseAddCmd;
import cn.meshed.cloud.rd.deployment.enums.WarehouseOperateEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehouseRelationEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehouseRepoTypeEnum;
import cn.meshed.cloud.rd.deployment.event.WarehouseInitializeEvent;
import cn.meshed.cloud.rd.domain.deployment.Warehouse;
import cn.meshed.cloud.rd.domain.deployment.gateway.WarehouseGateway;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.rd.domain.repo.Branch;
import cn.meshed.cloud.rd.domain.repo.CommitRepositoryFile;
import cn.meshed.cloud.rd.domain.repo.CreateRepository;
import cn.meshed.cloud.rd.domain.repo.Repository;
import cn.meshed.cloud.rd.domain.repo.RepositoryFile;
import cn.meshed.cloud.rd.domain.repo.gateway.RepositoryGateway;
import cn.meshed.cloud.rd.project.enums.ProjectAccessModeEnum;
import cn.meshed.cloud.stream.StreamBridgeSender;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.SysException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static cn.meshed.cloud.rd.domain.deployment.constant.MqConstant.WAREHOUSE_INITIALIZE;
import static cn.meshed.cloud.rd.domain.repo.constant.RepoConstant.DEVELOP;
import static cn.meshed.cloud.rd.domain.repo.constant.RepoConstant.MASTER;
import static cn.meshed.cloud.rd.domain.repo.constant.RepoConstant.RELEASE;

/**
 * <h1>新建仓库，非导入</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class WarehouseAddCmdExe implements CommandExecute<WarehouseAddCmd, SingleResponse<Warehouse>> {

    private final WarehouseGateway warehouseGateway;
    private final RepositoryGateway repositoryGateway;
    private final ProjectGateway projectGateway;
    private final StreamBridgeSender streamBridgeSender;

    /**
     * <h1>执行器</h1>
     *
     * @param warehouseAddCmd 执行器 {@link WarehouseAddCmd}
     * @return {@link Response}
     */
    @Override
    public SingleResponse<Warehouse> execute(WarehouseAddCmd warehouseAddCmd) {
        try {
            //校验
            Project project = checkWarehouseParamWithGetProject(warehouseAddCmd);
            //构建实体仓库
            Repository repository = getRepositoryWithBuild(warehouseAddCmd, project);

            //创建逻辑仓库
            Warehouse warehouse = warehouseGateway.save(buildWarehouse(warehouseAddCmd, project, repository));
            //新建需要直接初始化
            if (warehouseAddCmd.getOperate() == WarehouseOperateEnum.NEW) {
                //判断是否根据模板创建
                if (StringUtils.isNotBlank(warehouseAddCmd.getEngineTemplate())) {
                    //发布构建事件
                    publishBuild(warehouseAddCmd.getEngineTemplate(), warehouse, repository, project);
                }
            }
            //后续处理导入的分支业务
            initBranch(repository.getRepositoryId());
            //返回仓库信息
            return ResultUtils.of(warehouse);
        } catch (SysException sysException) {
            return ResultUtils.fail(sysException.getMessage());
        }
    }

    private void initBranch(String repositoryId) {
        //master 需要占位文件
        if (initRepo(repositoryId) != 1) {
            log.error("init master fail");
            return;
        }
        repositoryGateway.createBranch(repositoryId, new Branch(RELEASE, MASTER));
        repositoryGateway.createBranch(repositoryId, new Branch(DEVELOP, MASTER));
    }

    private Integer initRepo(String repositoryId) {
        CommitRepositoryFile commitRepositoryFile = new CommitRepositoryFile();
        commitRepositoryFile.setRepositoryId(repositoryId);
        commitRepositoryFile.setCommitMessage("init");
        commitRepositoryFile.setBranchName(MASTER);
        commitRepositoryFile.setFiles(Collections.singletonList(new RepositoryFile("MeshedCloud.md", "# Meshed Cloud")));
        return repositoryGateway.commitRepositoryFile(commitRepositoryFile);
    }

    /**
     * 发布仓库完成后初始化事件
     *
     * @param engineTemplate 代码构建事件
     * @param warehouse      逻辑仓库
     * @param repository     实体仓库
     * @param project        项目
     */
    private void publishBuild(String engineTemplate, Warehouse warehouse, Repository repository, Project project) {
        //todo 确认模板存在

        WarehouseInitializeEvent warehouseInitializeEvent = new WarehouseInitializeEvent();
        warehouseInitializeEvent.setProjectKey(warehouse.getProjectKey());
        warehouseInitializeEvent.setRepositoryName(repository.getRepositoryName());
        warehouseInitializeEvent.setRepositoryId(repository.getRepositoryId());
        warehouseInitializeEvent.setEngineTemplate(engineTemplate);
        warehouseInitializeEvent.setBasePackage(project.getBasePackage());
        streamBridgeSender.send(WAREHOUSE_INITIALIZE, warehouseInitializeEvent);
    }

    /**
     * 构建逻辑仓库对象
     *
     * @param warehouseAddCmd 逻辑仓库新增参数
     * @param project         项目
     * @param repository      存储库
     * @return
     */
    @NotNull
    private Warehouse buildWarehouse(WarehouseAddCmd warehouseAddCmd, Project project, Repository repository) {
        Warehouse warehouse = new Warehouse();
        warehouse.initNewWarehouse(project.getAccessMode());
        warehouse.setDescription(warehouseAddCmd.getDescription());
        warehouse.setPurposeType(warehouseAddCmd.getPurposeType());
        warehouse.setRepoName(repository.getRepositoryName());
        warehouse.setProjectKey(project.getKey());
        warehouse.setRepoUrl(repository.getRepoUrl());
        warehouse.setName(warehouseAddCmd.getName());
        warehouse.setRepoId(repository.getRepositoryId());
        if (warehouseAddCmd.getOperate() == WarehouseOperateEnum.IMPORT) {
            for (WarehouseRepoTypeEnum warehouseRepoType : WarehouseRepoTypeEnum.values()) {
                if (warehouseAddCmd.getRepoUrl().contains(warehouseRepoType.getKey())) {
                    warehouse.setRepoType(warehouseRepoType);
                    break;
                }
            }
            warehouse.setRelation(WarehouseRelationEnum.IMPORT);
        }
        return warehouse;
    }

    /**
     * 获取构建存储库信息
     *
     * @param warehouseAddCmd 逻辑仓库
     * @param project         项目信息
     * @return 存储库信息
     */
    private Repository getRepositoryWithBuild(WarehouseAddCmd warehouseAddCmd, Project project) {
        Repository repository = repositoryGateway.getRepository(
                project.getIdentity() + "/" + warehouseAddCmd.getRepoName());
        AssertUtils.isTrue(repository == null, "仓库已经存在");
        CreateRepository createRepository = new CreateRepository();
        //核心在仓库层面为私有
        createRepository.setVisible(project.getAccessMode() == ProjectAccessModeEnum.CORE);
        createRepository.setNamespaceId(project.getThirdId());
        createRepository.setDescription(warehouseAddCmd.getName(), warehouseAddCmd.getDescription());
        createRepository.setRepositoryName(warehouseAddCmd.getRepoName());
        createRepository.setImportUrl(warehouseAddCmd.getRepoUrl());
        createRepository.setImportToken(warehouseAddCmd.getAccessToken());
        return repositoryGateway.createRepository(createRepository);
    }

    /**
     * 构建逻辑仓库对象
     *
     * @param warehouseAddCmd 逻辑仓库新增对象
     * @param project         项目
     * @return 返回逻辑仓库模型
     */
    @NotNull
    private Warehouse buildWarehouse(WarehouseAddCmd warehouseAddCmd, Project project) {
        Warehouse warehouse = CopyUtils.copy(warehouseAddCmd, Warehouse.class);
        warehouse.initNewWarehouse(project.getAccessMode());
        return warehouse;
    }

    /**
     * 校验仓库信息
     *
     * @param warehouseAddCmd
     */
    private Project checkWarehouseParamWithGetProject(WarehouseAddCmd warehouseAddCmd) {
        String projectKey = warehouseAddCmd.getProjectKey();
        Project project = projectGateway.queryByKey(projectKey);
        AssertUtils.isTrue(project != null, "归属项目并不存在");
        AssertUtils.isTrue(!warehouseGateway.existWarehouseName(warehouseAddCmd.getRepoName()), "仓库已经存在");
        return project;
    }
}
