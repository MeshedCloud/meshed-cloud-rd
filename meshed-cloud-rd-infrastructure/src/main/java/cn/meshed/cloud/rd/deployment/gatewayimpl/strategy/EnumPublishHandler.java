package cn.meshed.cloud.rd.deployment.gatewayimpl.strategy;

import cn.meshed.cloud.rd.codegen.ObjectEnum;
import cn.meshed.cloud.rd.codegen.ObjectEnumValue;
import cn.meshed.cloud.rd.domain.cli.GenerateEnum;
import cn.meshed.cloud.rd.domain.cli.gateway.CliGateway;
import cn.meshed.cloud.rd.domain.common.VersionFormat;
import cn.meshed.cloud.rd.domain.deployment.strategy.AbstractServicePublish;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishHandler;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishType;
import cn.meshed.cloud.rd.domain.deployment.strategy.dto.ModelPublish;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.domain.repo.Branch;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
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
public class EnumPublishHandler extends AbstractServicePublish implements PublishHandler<ModelPublish> {


    public EnumPublishHandler(ModelGateway modelGateway, CliGateway cliGateway) {
        super(modelGateway, cliGateway);
    }

    /**
     * 注册类型
     *
     * @return PublishType
     */
    @Override
    public PublishType getPublishType() {
        return PublishType.ENUM;
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
        Set<Model> models = getModelGateway().waitPublishEnumListByProject(projectKey);
        if (CollectionUtils.isNotEmpty(models)) {
            publishEnum(modelPublish, models);
        }
    }


    /**
     * 发布枚举模型列表
     *
     * @param modelPublish 信息
     * @param models       项目key
     * @return
     */
    private boolean publishEnum(ModelPublish modelPublish, Set<Model> models) {
        //没有模型需要发布，符合正常逻辑
        if (CollectionUtils.isEmpty(models)) {
            return true;
        }
        //转换数据
        Set<ObjectEnum> objectEnums = models.stream().map(this::toObjectEnum).collect(Collectors.toSet());
        return generateModelWithPush(modelPublish.getSourceId(), objectEnums,
                modelPublish.getBasePath(), modelPublish.getMessage(), modelPublish.getBranch());
    }

    private boolean generateModelWithPush(String repositoryId, Set<ObjectEnum> objectEnums,
                                          String basePath, String commitMessage, Branch branch) {
        if (CollectionUtils.isEmpty(objectEnums)) {
            return false;
        }
        //构建并且推送
        GenerateEnum generateEnum = new GenerateEnum();
        generateEnum.setEnums(objectEnums);
        generateEnum.setCommitMessage(commitMessage);
        generateEnum.setBasePath(basePath);
        generateEnum.setBranch(branch);
        getCliGateway().asyncGenerateEnumWithPush(repositoryId, generateEnum);
        return true;
    }

    /**
     * 对象模型转换
     *
     * @param model
     * @return
     */
    private ObjectEnum toObjectEnum(Model model) {
        ObjectEnum objectEnum = new ObjectEnum();
        objectEnum.setPackageName(model.getPackageName());
        objectEnum.setAuthor(getAuthor());
        objectEnum.setDescription(model.getDescription());
        objectEnum.setClassName(model.getClassName());
        objectEnum.setSuperClass(model.getSuperClass());
        objectEnum.setExplain(model.getName());
        objectEnum.setVersion(VersionFormat.version(model.getVersion()));

        if (CollectionUtils.isNotEmpty(model.getFields())) {
            //枚举数据转换
            Set<ObjectEnumValue> enumValues = CopyUtils.copySetProperties(model.getEnumValues(), ObjectEnumValue::new);
            objectEnum.setEnumValues(enumValues);
        }
        return objectEnum;
    }

}
