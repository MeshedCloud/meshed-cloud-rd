package cn.meshed.cloud.rd.deployment.gatewayimpl.strategy;

import cn.meshed.cloud.rd.codegen.ObjectField;
import cn.meshed.cloud.rd.codegen.ObjectModel;
import cn.meshed.cloud.rd.domain.cli.GenerateModel;
import cn.meshed.cloud.rd.domain.cli.gateway.CliGateway;
import cn.meshed.cloud.rd.domain.deployment.strategy.AbstractServicePublish;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishHandler;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishType;
import cn.meshed.cloud.rd.domain.deployment.strategy.dto.ModelPublish;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.domain.repo.Branch;
import cn.meshed.cloud.utils.AssertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Component
public class ModelPublishHandler extends AbstractServicePublish implements PublishHandler<ModelPublish> {


    public ModelPublishHandler(ModelGateway modelGateway, CliGateway cliGateway) {
        super(modelGateway, cliGateway);
    }

    /**
     * 注册类型
     *
     * @return PublishType
     */
    @Override
    public PublishType getPublishType() {
        return PublishType.MODEL;
    }

    /**
     * 发布模型
     *
     * @param modelPublish 模型发布
     */
    @Override
    public void publish(ModelPublish modelPublish) {
        String projectKey = modelPublish.getProjectKey();
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "项目key不允许为空");
        Set<Model> models = getModelGateway().waitPublishListByProject(projectKey);
        if (CollectionUtils.isNotEmpty(models)) {
            publishModel(modelPublish, models);
        }
    }


    /**
     * 发布模型列表
     *
     * @param modelPublish 信息
     * @param models       项目key
     * @return
     */
    private boolean publishModel(ModelPublish modelPublish, Set<Model> models) {
        //没有模型需要发布，符合正常逻辑
        if (CollectionUtils.isEmpty(models)) {
            return true;
        }
        //转换数据
        Set<ObjectModel> objectModels = models.stream().map(this::toObjectModel).collect(Collectors.toSet());
        return generateModelWithPush(modelPublish.getRepositoryId(), objectModels,
                modelPublish.getBasePath(), modelPublish.getCommitMessage(), modelPublish.getBranch());
    }

    private boolean generateModelWithPush(String repositoryId, Set<ObjectModel> objectModels,
                                          String basePath, String commitMessage, Branch branch) {
        if (CollectionUtils.isEmpty(objectModels)) {
            return false;
        }
        //构建并且推送
        GenerateModel generateModel = new GenerateModel();
        generateModel.setModels(objectModels);
        generateModel.setCommitMessage(commitMessage);
        generateModel.setBasePath(basePath);
        generateModel.setBranch(branch);
        getCliGateway().asyncGenerateModelWithPush(repositoryId, generateModel);
        return true;
    }

    /**
     * 对象模型转换
     *
     * @param model
     * @return
     */
    private ObjectModel toObjectModel(Model model) {
        ObjectModel objectModel = new ObjectModel();
        objectModel.setPackageName(model.getPackageName());
        objectModel.setAuthor(getAuthor());
        objectModel.setDescription(model.getDescription());
        objectModel.setClassName(model.getClassName());
        objectModel.setSuperClass(model.getSuperClass());
        objectModel.setExplain(model.getName());
        objectModel.setVersion(model.getVersion().toString());

        if (CollectionUtils.isNotEmpty(model.getFields())) {
            //字段转换
            Set<ObjectField> fields = model.getFields().stream().map(this::toObjectField).collect(Collectors.toSet());
            objectModel.setFields(fields);
            //导入包
            objectModel.addImport(fieldImport(model.getFields()));
        }
        //导入超类包
        objectModel.addImport(classNameImport(model.getSuperClass()));
        return objectModel;
    }

}
