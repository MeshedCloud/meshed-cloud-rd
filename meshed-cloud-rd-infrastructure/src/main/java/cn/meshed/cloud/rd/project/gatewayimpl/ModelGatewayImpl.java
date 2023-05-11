package cn.meshed.cloud.rd.project.gatewayimpl;

import cn.meshed.cloud.rd.domain.project.EnumValue;
import cn.meshed.cloud.rd.domain.project.Field;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.constant.RelevanceTypeEnum;
import cn.meshed.cloud.rd.domain.project.gateway.EnumValueGateway;
import cn.meshed.cloud.rd.domain.project.gateway.FieldGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.project.config.ObjectBasicTypeProperties;
import cn.meshed.cloud.rd.project.convertor.ModelConvertor;
import cn.meshed.cloud.rd.project.enums.ModelAccessModeEnum;
import cn.meshed.cloud.rd.project.enums.ModelTypeEnum;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.enums.ServiceModelStatusEnum;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.ModelDO;
import cn.meshed.cloud.rd.project.gatewayimpl.database.mapper.ModelMapper;
import cn.meshed.cloud.rd.project.query.ModelPageQry;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.PageUtils;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final EnumValueGateway enumValueGateway;
    private final ObjectBasicTypeProperties objectBasicTypeProperties;


    /**
     * @param pageQry 分页参数
     * @return {@link PageResponse<Model>}
     */
    @Override
    public PageResponse<Model> searchPageList(ModelPageQry pageQry) {
        Page<Object> page = PageUtils.startPage(pageQry);
        LambdaQueryWrapper<ModelDO> lqw = new LambdaQueryWrapper<>();
        lqw.ne(ModelDO::getAccessMode, ModelAccessModeEnum.PRIVATE)
                .eq(StringUtils.isNotBlank(pageQry.getProjectKey()), ModelDO::getProjectKey, pageQry.getProjectKey())
                .eq(pageQry.getReleaseStatus() != null, ModelDO::getReleaseStatus, pageQry.getReleaseStatus())
                .eq(pageQry.getType() != null, ModelDO::getType, pageQry.getType())
                .and(StringUtils.isNotBlank(pageQry.getKeyword()), queryWrapper -> {
                    queryWrapper.like(ModelDO::getName, pageQry.getKeyword())
                            .or().like(ModelDO::getDescription, pageQry.getKeyword())
                            .or().like(ModelDO::getClassName, pageQry.getKeyword());
                });
        return PageUtils.of(modelMapper.selectList(lqw), page, Model::new);
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
        ModelDO modelDO = ModelConvertor.toEntity(model, queryByUuid(model.getUuid()));
        //保存模型
        if (StringUtils.isEmpty(modelDO.getUuid())) {
            AssertUtils.isTrue(!existClassName(model.getProjectKey(), model.getClassName()), "生成类名重复");
            //判断模型新增是否成功
            AssertUtils.isTrue(modelMapper.insert(modelDO) > 0, "模型新增失败");

        } else {
            //更新模型
            //判断模型新增是否成功
            AssertUtils.isTrue(modelMapper.updateById(modelDO) > 0, "模型更新失败");
        }

        //保存字段
        String uuid = modelDO.getUuid();

        /**
         * 枚举为模型中特殊模型
         */
        if (ModelTypeEnum.ENUM.equals(model.getType())) {
            Set<EnumValue> enumValues = getEnumValues(model.getEnumValues(), uuid);
            if (CollectionUtils.isNotEmpty(enumValues)) {
                //保存枚举值
                enumValueGateway.saveBatch(enumValues);
            }
        } else {
            Set<Field> fields = getFields(model.getFields(), uuid);
            if (CollectionUtils.isNotEmpty(fields)) {
                AssertUtils.isTrue(fieldGateway.saveBatch(RelevanceTypeEnum.MODEL, fields), "字段保存失败");
            }
        }


        //返回uuid
        return uuid;
    }

    private Set<EnumValue> getEnumValues(Set<EnumValue> enumValues, String uuid) {
        if (CollectionUtils.isEmpty(enumValues)) {
            return enumValues;
        }
        return enumValues.stream()
                .peek(enumValue -> enumValue.setMId(uuid))
                .collect(Collectors.toSet());
    }

    /**
     * 批量保存或更新模型（含字段）
     *
     * @param projectKey 项目唯一标识
     * @param models     模型
     * @return 成功与否
     */
    @Override
    public boolean batchSaveOrUpdate(String projectKey, Set<Model> models) {
        if (CollectionUtils.isEmpty(models)) {
            return false;
        }

        /**
         * 批量新增或者更新
         */
        Set<String> classNames = models.stream().map(Model::getClassName).collect(Collectors.toSet());
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(classNames), "【批量保存模型（含字段）或仅更新字段】模型数据不规范异常");
        //查询出已有的模型
        LambdaQueryWrapper<ModelDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ModelDO::getProjectKey, projectKey).in(ModelDO::getClassName, classNames);
        List<ModelDO> modelOldList = modelMapper.selectList(lqw);
        //
        List<ModelDO> newModels = new ArrayList<>();
        List<ModelDO> updateModels = new ArrayList<>();
        //数据库存在已有的值
        if (CollectionUtils.isNotEmpty(modelOldList)) {
            Map<String, ModelDO> modelMap = modelOldList.stream()
                    .collect(Collectors.toMap(ModelDO::getClassName, Function.identity(), ((modelFirst, modelSecond) -> {
                        //如果两个状态都是一样的，比较版本号
                        if (modelFirst.getReleaseStatus() == modelSecond.getReleaseStatus()) {
                            return modelSecond.getVersion() > modelFirst.getVersion() ? modelSecond : modelFirst;
                        }
                        return modelFirst.getReleaseStatus() == ReleaseStatusEnum.RELEASE ? modelSecond : modelFirst;
                    })));
            models.stream().filter(Objects::nonNull)
                    //此方法不支持批量存在枚举对象
                    .filter(model -> !ModelTypeEnum.ENUM.equals(model.getType()))
                    .forEach(model -> {
                        ModelDO modelDO = modelMap.get(model.getClassName());
                        //更新和模型分类
                        if (modelDO != null) {
                            updateModels.add(ModelConvertor.toEntity(model, modelDO));
                        } else {
                            newModels.add(ModelConvertor.toEntity(model, modelDO));
                        }
                    });
        } else {
            //全部是新增
            newModels.addAll(CopyUtils.copySetProperties(models, ModelDO::new));
        }

        if (CollectionUtils.isNotEmpty(newModels)) {
            modelMapper.insertBatch(newModels);
        }
        if (CollectionUtils.isNotEmpty(updateModels)) {
            modelMapper.updateBatch(updateModels);
        }

        //待更新字段
        Map<String, String> classNameUuidMap = Stream.of(newModels, updateModels)
                .flatMap(Collection::stream).collect(Collectors.toMap(ModelDO::getClassName, ModelDO::getUuid));
        //先清理掉旧的字段，再进行新增新字段
        fieldGateway.delByGroupId(classNameUuidMap.keySet(), RelevanceTypeEnum.MODEL);
        Set<Field> fields = models.stream()
                .map(model -> assembleFieldSet(model.getFields(), classNameUuidMap.get(model.getClassName())))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream).collect(Collectors.toSet());
        fieldGateway.saveBatch(RelevanceTypeEnum.MODEL, fields);
        return true;
    }

    /**
     * 更新状态
     *
     * @param uuid          编码
     * @param status        状态
     * @param releaseStatus 发行状态
     * @return 成功与否
     */
    @Override
    public boolean updateStatus(String uuid, ServiceModelStatusEnum status, ReleaseStatusEnum releaseStatus) {
        AssertUtils.isTrue(StringUtils.isNotBlank(uuid), "编码参数不能为空");
        AssertUtils.isTrue(status != null || releaseStatus != null, "状态参数不能为空");
        LambdaUpdateWrapper<ModelDO> luw = new LambdaUpdateWrapper<>();
        luw.set(status != null, ModelDO::getStatus, status)
                .set(releaseStatus != null, ModelDO::getReleaseStatus, releaseStatus)
                .eq(ModelDO::getUuid, uuid);
        return modelMapper.update(null, luw) > 0;
    }

    /**
     * 批量更新状态
     *
     * @param uuids         编码列表
     * @param status        状态
     * @param releaseStatus 发行状态
     * @return 成功与否
     */
    @Override
    public boolean batchUpdateStatus(Set<String> uuids, ServiceModelStatusEnum status, ReleaseStatusEnum releaseStatus) {
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(uuids), "关系列表不能为空");
        AssertUtils.isTrue(status != null || releaseStatus != null, "状态不能为空");
        List<ModelDO> models = modelMapper.selectBatchIds(uuids);
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(models), "不存在模型");
        List<ModelDO> list = models.stream().peek(modelDO -> {
            if (status != null) {
                modelDO.setStatus(status);
            }
            if (releaseStatus != null) {
                modelDO.setReleaseStatus(releaseStatus);
            }
        }).collect(Collectors.toList());
        return modelMapper.updateBatch(list) > 0;
    }

    /**
     * 模型检查合法性
     *
     * @param uuid
     * @return 合法性
     */
    @Override
    public boolean checkLegal(String uuid) {
        Set<Field> fields = fieldGateway.listByModel(uuid);
        //没有字段视为合法
        if (CollectionUtils.isEmpty(fields)) {
            return true;
        }
        Set<String> classNames = fields.stream().map(Field::getFieldType).collect(Collectors.toSet());
        //检查是否存在类名还在编辑状态或者删除
        return checkLegalByClassNames(classNames);
    }

    /**
     * 通过类名检查模型合法性
     *
     * @param classNames 类名列表
     * @return 合法性
     */
    @Override
    public boolean checkLegalByClassNames(Set<String> classNames) {
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(classNames), "类名列表为空");
        //过滤掉基本类型
        classNames = filterBasicType(classNames);
        //全部是基本类型合法
        if (CollectionUtils.isEmpty(classNames)) {
            return true;
        }
        //查询类名的所有对象且对象不是编辑状态
        LambdaQueryWrapper<ModelDO> lqw = new LambdaQueryWrapper<>();
        lqw.select(ModelDO::getClassName, ModelDO::getReleaseStatus)
                .in(ModelDO::getClassName, classNames)
                .ne(ModelDO::getReleaseStatus, ReleaseStatusEnum.EDIT);
        List<ModelDO> list = modelMapper.selectList(lqw);
        AssertUtils.isTrue(list.size() == classNames.size(), "模型可能存在丢失或还在编辑");
        return true;
    }

    @NotNull
    private Set<String> filterBasicType(Set<String> classNames) {
        List<String> basicTypes = objectBasicTypeProperties.getTypes();
        if (CollectionUtils.isEmpty(basicTypes)) {
            return classNames;
        }
        classNames = classNames.stream().filter(className -> !basicTypes.contains(className)).collect(Collectors.toSet());
        return classNames;
    }


    /**
     * 设置关系id 返回字段列表
     *
     * @param fields 模型
     * @param uuid
     * @return
     */
    private Set<Field> assembleFieldSet(Set<Field> fields, String uuid) {
        if (CollectionUtils.isEmpty(fields)) {
            return fields;
        }
        fields.forEach(field -> {
            field.setRelevanceId(uuid);
            field.setRelevanceType(RelevanceTypeEnum.MODEL);
        });
        return fields;
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
        //查询已经存在的类

        return modelMapper.insertBatch(CopyUtils.copyListProperties(models, ModelDO::new));
    }

    @NotNull
    private Set<Field> getFields(Set<Field> fields, String uuid) {
        if (CollectionUtils.isEmpty(fields)) {
            return Collections.emptySet();
        }
        return fields.stream().peek(field -> {
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
        if (ModelTypeEnum.ENUM.equals(model.getType())) {
            //枚举查询信息
            Set<EnumValue> enumValues = enumValueGateway.getEnumValues(model.getUuid());
            model.setEnumValues(enumValues);
        } else {
            //正常模型查询字段
            Set<Field> fields = fieldGateway.listByModel(uuid);
            model.setFields(fields);
        }

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
    public Set<Model> waitPublishModelListByProject(String projectKey) {
        Set<Model> models = getWaitPublishModels(projectKey, false);
        if (CollectionUtils.isEmpty(models)) {
            return new HashSet<>();
        }
        return assembleModelDetailsList(models);
    }


    /**
     * 查询项目的待发布枚举详情列表
     *
     * @param projectKey 项目key
     * @return 详情列表
     */
    @Override
    public Set<Model> waitPublishEnumListByProject(String projectKey) {
        Set<Model> models = getWaitPublishModels(projectKey, true);
        if (CollectionUtils.isEmpty(models)) {
            return new HashSet<>();
        }
        return assembleEnumDetailsList(models);
    }

    private Set<Model> getWaitPublishModels(String projectKey, boolean isEnum) {
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "项目key不能为空");
        LambdaQueryWrapper<ModelDO> lqw = new LambdaQueryWrapper<>();
        //当项目key不存在时会判断这个系统的类名唯一值
        //去除枚举这个特殊的
        if (isEnum) {
            lqw.eq(ModelDO::getType, ModelTypeEnum.ENUM);
        } else {
            lqw.ne(ModelDO::getType, ModelTypeEnum.ENUM);
        }
        lqw.eq(ModelDO::getReleaseStatus, ReleaseStatusEnum.PROCESSING)
                .eq(ModelDO::getProjectKey, projectKey.toUpperCase());
        List<ModelDO> list = modelMapper.selectList(lqw);
        return CopyUtils.copySetProperties(list, Model::new);
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
            return new HashSet<>();
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
     * 批量组装枚举模型列表
     *
     * @param models 模型列表
     * @return 模型列表
     */
    private Set<Model> assembleEnumDetailsList(Set<Model> models) {
        if (CollectionUtils.isEmpty(models)) {
            return models;
        }
        Set<String> uuids = models.stream().map(Model::getUuid).collect(Collectors.toSet());
        Map<String, Set<EnumValue>> enumValueListMap = enumValueGateway.getEnumValues(uuids);
        //如果发布的都没有字段
        if (enumValueListMap.isEmpty()) {
            return models;
        }
        return models.stream()
                .peek(model -> model.setEnumValues(enumValueListMap.get(model.getUuid()))).collect(Collectors.toSet());
    }

    /**
     * <h1>选项查询</h1>
     *
     * @param projectKey
     * @return {@link Set<String>}
     */
    @Override
    public Set<String> select(String projectKey) {
        assert StringUtils.isNotBlank(projectKey);
        LambdaQueryWrapper<ModelDO> lqw = new LambdaQueryWrapper<>();
        lqw.select(ModelDO::getClassName)
                .eq(ModelDO::getProjectKey, projectKey);
        List<ModelDO> list = modelMapper.selectList(lqw);
        return list.stream().map(ModelDO::getClassName).collect(Collectors.toSet());
    }

    /**
     * 删除
     *
     * @param uuid 编码
     * @return
     */
    @Override
    public Boolean delete(String uuid) {
        return modelMapper.deleteById(uuid) > 0;
    }
}
