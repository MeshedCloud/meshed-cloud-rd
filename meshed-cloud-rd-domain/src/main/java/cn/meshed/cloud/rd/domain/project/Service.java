package cn.meshed.cloud.rd.domain.project;

import cn.hutool.core.util.StrUtil;
import cn.meshed.cloud.context.SecurityContext;
import cn.meshed.cloud.rd.project.enums.BaseGenericsEnum;
import cn.meshed.cloud.rd.project.enums.ModelAccessModeEnum;
import cn.meshed.cloud.rd.project.enums.ModelTypeEnum;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.enums.RequestModeEnum;
import cn.meshed.cloud.rd.project.enums.RequestTypeEnum;
import cn.meshed.cloud.rd.project.enums.ServiceAccessModeEnum;
import cn.meshed.cloud.rd.project.enums.ServiceModelStatusEnum;
import cn.meshed.cloud.rd.project.enums.ServiceTypeEnum;
import cn.meshed.cloud.utils.AssertUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.meshed.cloud.rd.domain.common.constant.Constant.DTO;
import static cn.meshed.cloud.rd.domain.common.constant.Constant.PAGE_QUERY;
import static cn.meshed.cloud.rd.domain.project.constant.ProjectConstant.INIT_VERSION;

/**
 * <h1>服务</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class Service implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    private String uuid;

    /**
     * 服务分组ID
     */
    private String groupId;

    /**
     * 服务类型
     */
    private ServiceTypeEnum type;

    /**
     * 模型分组所属项目key
     */
    @Setter(AccessLevel.NONE)
    private String projectKey;

    /**
     * 所属分组的类名
     */
    private String className;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务方法名称
     */
    @Setter(AccessLevel.NONE)
    private String method;

    /**
     * 服务uri
     */
    private String uri;

    /**
     * 服务版本号
     */
    private Long version;

    /**
     * 服务负责人ID
     */
    private Long ownerId;

    /**
     * 服务授权码，用于注册身份安全
     */
    private String identifier;

    /**
     * 服务请求参数模式
     */
    private RequestModeEnum requestMode;

    /**
     * 服务返回参数模式
     */
    private Boolean responseMerge;

    /**
     * 服务访问权限
     */
    private ServiceAccessModeEnum accessMode;

    /**
     * 服务描述
     */
    private String description;

    /**
     * 服务状态
     */
    private ServiceModelStatusEnum status;

    /**
     * 版本状态
     */
    private ReleaseStatusEnum releaseStatus;

    /**
     * 服务请求类型
     */
    private RequestTypeEnum requestType;

    /**
     * 请求参数传输列表
     */
    private Set<Field> requests;
    /**
     * 响应字段列表
     */
    private Set<Field> responses;

    public void setMethod(String method) {
        AssertUtils.isTrue(StringUtils.isNotBlank(method), "方法不能为空");
        this.method = StrUtil.lowerFirst(method);
    }

    public void setProjectKey(String projectKey) {
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "项目唯一标识不能为空");
        this.projectKey = StringUtils.upperCase(projectKey);
    }

    public void initService() {
        this.releaseStatus = ReleaseStatusEnum.EDIT;
        this.status = ServiceModelStatusEnum.DEV;
        this.version = INIT_VERSION;
        this.ownerId = SecurityContext.getOperatorUserId();
        if (ServiceTypeEnum.RPC == this.type) {
            this.uri = this.method;
            this.requestType = RequestTypeEnum.RPC;
        }
    }

    public Set<Model> handleFields(Set<Field> requests, Set<Field> responses) {
        AssertUtils.isTrue(this.requestMode != null, "请求模式不能为空");
        AssertUtils.isTrue(this.type != null, "请先设置服务类型");
        Set<Model> models = new HashSet<>();
        Model requestModel = handleRequestFields(requests);
        addModelSet(models, requestModel);
        Model responseModel = handleResponseFields(responses);
        addModelSet(models, responseModel);
        return models;
    }


    private void addModelSet(Set<Model> models, Model model) {
        if (model != null) {
            models.add(model);
        }
    }

    /**
     * 处理请求参数
     *
     * @param requests 外部传递请求参数列表
     * @return 新增模型 or null
     */
    private Model handleRequestFields(Set<Field> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            return null;
        }
        //参数策略: 不进行任何合并，支持多参数
        if (RequestModeEnum.MULTIPLE == this.requestMode) {
            this.requests = requests;
            return null;
        }
        String name = this.name + "请求参数";
        //其他模式均需要合并，路径参数依旧支持独立（合并中依旧包含路径参数）
        Model model = buildBaseModel(requests, name);
        if (RequestModeEnum.PAGE == this.requestMode) {
            model.setType(getModelTypeEnum(RequestModeEnum.PAGE.name()));
            model.setSuperClass(PAGE_QUERY);
        } else {
            model.setType(getModelTypeEnum("REQUEST"));
            model.setSuperClass(DTO);
        }
        String classNamePrefix = StrUtil.upperFirst(
                this.className.replaceAll(this.type.getKey(), "")
        );
        model.initModel(classNamePrefix + StrUtil.upperFirst(this.method));

        Set<Field> fields = requests.stream().filter(Objects::nonNull)
                .filter(field -> field.getGeneric() == BaseGenericsEnum.PATH_VARIABLE).collect(Collectors.toSet());
        //组装成字段
        Field field = buildBaseField(name, model.getClassName());
        //如果请求模式是JSON，字段同样设置成JSON，否则无泛型
        if (RequestModeEnum.JSON == requestMode) {
            field.setGeneric(BaseGenericsEnum.JSON);
        } else {
            field.setGeneric(BaseGenericsEnum.NONE);
        }
        //添加到
        fields.add(field);
        this.requests = fields;

        return model;
    }

    private ModelTypeEnum getModelTypeEnum(String strategy) {
        if (StringUtils.isBlank(strategy)) {
            return ModelTypeEnum.DTO;
        }
        switch (strategy) {
            case "PAGE":
                return this.type == ServiceTypeEnum.API ? ModelTypeEnum.PAGE_PARAM : ModelTypeEnum.PAGE_REQUEST;
            case "REQUEST":
                return this.type == ServiceTypeEnum.API ? ModelTypeEnum.PARAM : ModelTypeEnum.REQUEST;
            case "RESPONSE":
                return this.type == ServiceTypeEnum.API ? ModelTypeEnum.VO : ModelTypeEnum.RESPONSE;
            default:
                return ModelTypeEnum.DTO;
        }
    }

    private Field buildBaseField(String name, String className) {
        Field field = new Field();
        field.setExplain(name);
        field.setFieldType(className);
        field.setFieldName(StrUtil.lowerFirst(field.getFieldType()));
        return field;
    }

    private Model handleResponseFields(Set<Field> responses) {

        if (CollectionUtils.isEmpty(responses)) {
            return null;
        }
        if (!this.responseMerge) {
            //响应参数为单参数(不合并)且参数只有一个，如果存在多个需要友善提醒
            AssertUtils.isTrue(responses.size() == 1, "无需合并的参数不能大于1个");
        }

        if (!this.responseMerge && responses.size() == 1) {
            this.responses = responses;
            return null;
        }
        String name = this.name + "返回参数";
        //其他模式均需要合并，路径参数依旧支持独立（合并中依旧包含路径参数）
        Model model = buildBaseModel(responses, name);
        model.setType(getModelTypeEnum("RESPONSE"));
        model.setSuperClass(DTO);
        String classNamePrefix = StrUtil.upperFirst(
                this.className.replaceAll(this.type.getKey(), "")
        );
        model.initModel(classNamePrefix + StrUtil.upperFirst(this.method));
        //组装成字段
        Field field = buildBaseField(name, model.getClassName());
        field.setGeneric(BaseGenericsEnum.NONE);
        this.responses = Collections.singleton(field);
        return model;
    }


    private Model buildBaseModel(Set<Field> fields, String modelName) {
        Model model = new Model();
        model.setFields(fields);
        model.setName(modelName);
        model.setDescription(modelName);
        //非页面新增，也不需要出现在其他模型的选项中
        model.setAccessMode(ModelAccessModeEnum.PRIVATE);
        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Service)) {
            return false;
        }
        Service service = (Service) o;
        return getMethod().equals(service.getMethod()) && getGroupId().equals(service.getGroupId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMethod(), getGroupId());
    }
}
