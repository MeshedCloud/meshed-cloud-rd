package cn.meshed.cloud.rd.domain.deployment.strategy;

import cn.meshed.cloud.rd.codegen.ObjectDefinition;
import cn.meshed.cloud.rd.codegen.ObjectField;
import cn.meshed.cloud.rd.codegen.ObjectMethod;
import cn.meshed.cloud.rd.codegen.ObjectParameter;
import cn.meshed.cloud.rd.codegen.ObjectResponse;
import cn.meshed.cloud.rd.codegen.constant.ParameterType;
import cn.meshed.cloud.rd.domain.cli.gateway.CliGateway;
import cn.meshed.cloud.rd.domain.project.Field;
import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.ServiceGroup;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.project.enums.BaseGenericsEnum;
import cn.meshed.cloud.rd.project.enums.RequestModeEnum;
import com.alibaba.cola.exception.SysException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.meshed.cloud.rd.domain.common.constant.Constant.PAGE_RESPONSE;
import static cn.meshed.cloud.rd.domain.common.constant.Constant.RESPONSE;
import static cn.meshed.cloud.rd.domain.common.constant.Constant.SINGLE_RESPONSE;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public abstract class AbstractServicePublish {

    private final ModelGateway modelGateway;

    private final CliGateway cliGateway;

    /**
     * 转换成生成所用的对象字段
     *
     * @param field 字段
     * @return ObjectField
     */
    protected ObjectField toObjectField(Field field) {
        ObjectField objectField = new ObjectField();
        objectField.setName(field.getFieldName());
        objectField.setType(field.getFieldType());
        objectField.setExplain(field.getExplain());
        objectField.setExample(field.getMock());
        if (field.getGeneric() != null && field.getGeneric() != BaseGenericsEnum.NONE) {
            objectField.setGeneric(field.getGeneric().getExt());
        }
        objectField.setAnnotationJson(field.getRule());
        objectField.setNonNull(field.getNonNull());
        return objectField;
    }

    /**
     * 字段列表导包
     *
     * @param fields 字段列表
     * @return 包列表
     */
    protected Set<String> fieldImport(Set<Field> fields) {
        Set<String> classNames = fields.stream().map(Field::getFieldType).collect(Collectors.toSet());
        return modelGateway.scanPackageNameByClassNames(classNames);
    }

    /**
     * 单个类名导包
     *
     * @param className
     * @return
     */
    protected Set<String> classNameImport(String className) {
        if (StringUtils.isBlank(className)) {
            return Collections.emptySet();
        }
        return modelGateway.scanPackageNameByClassNames(Collections.singleton(className));
    }

    /**
     * 多个个类名导包
     *
     * @param classNames
     * @return
     */
    protected Set<String> classNamesImport(Set<String> classNames) {
        if (CollectionUtils.isEmpty(classNames)) {
            return Collections.emptySet();
        }
        return modelGateway.scanPackageNameByClassNames(classNames);
    }

    protected void assembleMethod(ObjectMethod method, Service service) {
        method.setName(service.getMethod());
        method.setExplain(service.getName());
        method.setDescription(service.getDescription());
        //参数处理
        //返回参数处理
        handleResponse(method, service);
        //请求参数处理
        handleRequest(method, service);

    }

    protected Set<String> scanMethodsPackage(Set<ObjectMethod> methods) {
        if (CollectionUtils.isEmpty(methods)) {
            return Collections.emptySet();
        }

        Set<String> classNames = new HashSet<>();
        methods.forEach(method -> {
            if (CollectionUtils.isNotEmpty(method.getParameters())) {
                Set<String> classNameSet = method.getParameters().stream()
                        .map(ObjectParameter::getType).collect(Collectors.toSet());
                classNames.addAll(classNameSet);

            }
            ObjectResponse objectResponse = method.getObjectResponse();
            if (objectResponse != null && StringUtils.isNotBlank(objectResponse.getDataType())) {
                classNames.add(objectResponse.getDataType());
            }
        });

        return classNamesImport(classNames);
    }

    protected void assembleObjectDefinition(ServiceGroup serviceGroup, ObjectDefinition objectDefinition) {
        objectDefinition.setClassName(serviceGroup.getClassName());
        objectDefinition.setAuthor(getAuthor());
        objectDefinition.setDescription(serviceGroup.getDescription());
        objectDefinition.setPackageName(serviceGroup.getPackageName());
        objectDefinition.setExplain(serviceGroup.getName());
        objectDefinition.setVersion(serviceGroup.getVersion().toString());
    }

    /**
     * 获取作者
     *
     * @return
     */
    protected String getAuthor() {
        return "Meshed Cloud 研发平台";
    }

    private void handleRequest(ObjectMethod method, Service service) {
        Set<Field> requests = service.getRequests();

        if (CollectionUtils.isEmpty(requests)) {
            return;
        }
        //处理字段
        Set<ObjectParameter> objectFields = requests.stream().filter(Objects::nonNull)
                .map(this::toObjectParameter).collect(Collectors.toSet());
        method.setParameters(objectFields);
    }

    private ObjectParameter toObjectParameter(Field field) {
        ObjectParameter parameter = new ObjectParameter();
        parameter.setExplain(field.getExplain());
        parameter.setName(field.getFieldName());
        parameter.setType(field.getFieldType());
        //默认无
        parameter.setParameterType(ParameterType.NONE);
        if (field.getGeneric() == BaseGenericsEnum.PATH_VARIABLE) {
            parameter.setParameterType(ParameterType.PATH_VARIABLE);
        } else if (field.getGeneric() == BaseGenericsEnum.JSON) {
            parameter.setParameterType(ParameterType.REQUEST_BODY);
        }
        return parameter;
    }

    private void handleResponse(ObjectMethod method, Service service) {
        ObjectResponse objectResponse = new ObjectResponse();
        Set<Field> responses = service.getResponses();
        //没有返回参数的时候直接采用无参数据体
        if (CollectionUtils.isEmpty(responses)) {
            objectResponse.setGeneric(RESPONSE);
        } else if (responses.size() == 1) {
            //仅仅一个的时候直接返回一个数据，不进行数据包装，直接返回
            Field field = responses.iterator().next();
            //存在业务泛型设置，分页不建议使用泛型，但是不限制业务
            if (field.getGeneric() != BaseGenericsEnum.NONE) {
                objectResponse.setSubGeneric(field.getGeneric().getExt());
            }
            //设置类型
            objectResponse.setDataType(field.getFieldType());
        } else {
            throw new SysException("系统逻辑异常：响应参数多个不符合业务逻辑");
        }
        //如果是分页的情况下顶层泛型采用分页数据体，否则简单数据体返回
        if (service.getRequestMode() == RequestModeEnum.PAGE) {
            objectResponse.setGeneric(PAGE_RESPONSE);
        } else {
            objectResponse.setGeneric(SINGLE_RESPONSE);
        }

        method.setObjectResponse(objectResponse);
    }
}
