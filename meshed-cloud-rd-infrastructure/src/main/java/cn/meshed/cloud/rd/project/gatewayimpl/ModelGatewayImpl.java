package cn.meshed.cloud.rd.project.gatewayimpl;

import cn.meshed.cloud.rd.domain.project.Field;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.constant.RelevanceTypeEnum;
import cn.meshed.cloud.rd.domain.project.gateway.FieldGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.project.convertor.ModelConvertor;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.ModelDO;
import cn.meshed.cloud.rd.project.gatewayimpl.database.mapper.ModelMapper;
import cn.meshed.cloud.rd.project.query.ModelPageQry;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1>模型网关实现</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ModelGatewayImpl implements ModelGateway {

    private final ModelMapper modelMapper;
    private final FieldGateway fieldGateway;


    /**
     * @param modelPageQry
     * @return
     */
    @Override
    public PageResponse<Model> searchPageList(ModelPageQry modelPageQry) {
        return null;
    }

    /**
     * @param model
     * @return
     */
    @Transactional
    @Override
    public String save(Model model) {
        if (model == null) {
            return null;
        }
        AssertUtils.isTrue(!existClassName(model.getProjectKey(), model.getClassName()), "生成类名重复");
        ModelDO modelDO = ModelConvertor.toEntity(model, queryByUuid(model.getUuid()));
        //保存模型
        if (StringUtils.isEmpty(modelDO.getUuid())) {
            //判断模型新增是否成功
            AssertUtils.isTrue(modelMapper.insert(modelDO) > 0, "模型新增失败");

        } else {
            //更新模型
            //判断模型新增是否成功
            AssertUtils.isTrue(modelMapper.updateById(modelDO) > 0, "模型更新失败");
        }

        //保存字段
        String uuid = modelDO.getUuid();
        Set<Field> fields = getFields(model, uuid);
        if (CollectionUtils.isNotEmpty(fields)) {
            AssertUtils.isTrue(fieldGateway.saveBatch(RelevanceTypeEnum.MODEL, fields), "字段保存失败");
        }

        //返回uuid
        return uuid;
    }

    @NotNull
    private Set<Field> getFields(Model model, String uuid) {
        if (CollectionUtils.isEmpty(model.getFields())) {
            return Collections.emptySet();
        }
        return model.getFields().stream().peek(field -> {
            field.setRelevanceId(uuid);
            field.setRelevanceType(RelevanceTypeEnum.MODEL);
        }).collect(Collectors.toSet());
    }


    /**
     * @param uuid
     * @return
     */
    @Override
    public Model query(String uuid) {
        Model model = CopyUtils.copy(queryByUuid(uuid), Model.class);
        if (model == null) {
            return null;
        }
        Set<Field> fields = fieldGateway.listByModel(uuid);
        model.setFields(fields);
        return model;
    }

    /**
     * @param uuid
     * @return
     */
    private ModelDO queryByUuid(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            return null;
        }
        return modelMapper.selectById(uuid);
    }

    /**
     * 判断className是否在模型中是否已经存在
     *
     * @param projectKey 项目key 当项目key不存在时会判断这个系统的类名唯一值
     * @param className  类名
     * @return
     */
    @Override
    public boolean existClassName(String projectKey, String className) {
        AssertUtils.isTrue(StringUtils.isNotBlank(className), "类名不能为空");
        LambdaQueryWrapper<ModelDO> lqw = new LambdaQueryWrapper<>();
        //当项目key不存在时会判断这个系统的类名唯一值
        lqw.eq(ModelDO::getProjectKey, projectKey);
        lqw.eq(ModelDO::getClassName, className);
        //仅在项目内保证唯一性，但存在查询全局的情况下可能是会存在多个同名类名的业务
        return modelMapper.selectCount(lqw) > 0;
    }

    /**
     * 查询项目的待发布模型详情列表
     *
     * @param projectKey 项目key
     * @return 详情列表
     */
    @Override
    public Set<Model> waitPublishListByProject(String projectKey) {
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "项目key不能为空");
        LambdaQueryWrapper<ModelDO> lqw = new LambdaQueryWrapper<>();
        //当项目key不存在时会判断这个系统的类名唯一值
        lqw.eq(ModelDO::getProjectKey, projectKey.toUpperCase());
        lqw.eq(ModelDO::getReleaseStatus, ReleaseStatusEnum.PROCESSING);
        Set<Model> models = CopyUtils.copySetProperties(modelMapper.selectList(lqw), Model::new);
        if (CollectionUtils.isEmpty(models)) {
            return models;
        }
        Set<String> uuids = models.stream().map(Model::getUuid).collect(Collectors.toSet());
        Set<Field> fields = fieldGateway.listByModels(uuids);
        if (CollectionUtils.isNotEmpty(fields)) {
            Map<String, Set<Field>> fieldMap = fields.stream()
                    .collect(Collectors.groupingBy(Field::getRelevanceId, Collectors.toSet()));
            models.forEach(model -> model.setFields(fieldMap.get(model.getUuid())));
        }
        return assembleModelDetailsList(models);
    }

    /**
     * 根据类名列表转换出包名列表
     *
     * @param classNames 类名
     * @return 返回
     */
    @Override
    public Set<String> scanPackageNameByClassNames(Set<String> classNames) {
        List<ModelDO> list = getModelSimpleListByClassNames(classNames);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptySet();
        }
        return list.stream().map(ModelDO::getPackageName).collect(Collectors.toSet());
    }

    /**
     * 根据包名列表获取模型简要信息
     * 包名、UUID、类名、模型名称
     *
     * @param classNames
     * @return
     */
    private List<ModelDO> getModelSimpleListByClassNames(Set<String> classNames) {
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(classNames), "类名不能为空");
        LambdaQueryWrapper<ModelDO> lqw = new LambdaQueryWrapper<>();
        lqw.select(ModelDO::getPackageName, ModelDO::getUuid, ModelDO::getClassName, ModelDO::getName)
                .in(ModelDO::getClassName, classNames)
                //编辑中的包无效
                .ne(ModelDO::getReleaseStatus, ReleaseStatusEnum.EDIT);
        List<ModelDO> list = modelMapper.selectList(lqw);
        return list;
    }

    /**
     * 根据类名列表转换出模型UUID列表
     *
     * @param classNames 类名
     * @return 返回
     */
    @Override
    public Set<String> selectUuidListByClassNames(Set<String> classNames) {
        List<ModelDO> list = getModelSimpleListByClassNames(classNames);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptySet();
        }
        return list.stream().map(ModelDO::getUuid).collect(Collectors.toSet());
    }

    /**
     * 批量组装模型列表
     *
     * @param models 模型列表
     * @return 模型列表
     */
    private Set<Model> assembleModelDetailsList(Set<Model> models) {
        if (CollectionUtils.isEmpty(models)) {
            return models;
        }
        Set<String> uuids = models.stream().map(Model::getUuid).collect(Collectors.toSet());
        Set<Field> fields = fieldGateway.listByModels(uuids);
        //如果发布的都没有字段
        if (CollectionUtils.isEmpty(fields)) {
            return models;
        }
        //分类及封装
        Map<String, Set<Field>> listMap = fields.stream().collect(Collectors.groupingBy(Field::getRelevanceId, Collectors.toSet()));
        return models.stream()
                .peek(model -> model.setFields(listMap.get(model.getUuid()))).collect(Collectors.toSet());
    }

    /**
     * <h1>批量保存对象</h1>
     *
     * @param models 模型列表
     * @return 保存个数
     */
    @Override
    public Integer saveBatch(Set<Model> models) {
        if (CollectionUtils.isEmpty(models)) {
            return -1;
        }
        return modelMapper.insertBatch(CopyUtils.copyListProperties(models, ModelDO::new));
    }
}
