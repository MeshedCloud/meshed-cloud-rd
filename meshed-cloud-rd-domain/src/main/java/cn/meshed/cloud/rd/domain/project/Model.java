package cn.meshed.cloud.rd.domain.project;

import cn.hutool.core.util.StrUtil;
import cn.meshed.cloud.context.SecurityContext;
import cn.meshed.cloud.rd.project.enums.ModelAccessModeEnum;
import cn.meshed.cloud.rd.project.enums.ModelTypeEnum;
import cn.meshed.cloud.rd.project.enums.ProjectAccessModeEnum;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.enums.ServiceModelStatusEnum;
import cn.meshed.cloud.utils.AssertUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import static cn.meshed.cloud.rd.domain.project.constant.ProjectConstant.INIT_VERSION;
import static cn.meshed.cloud.rd.domain.project.constant.ProjectConstant.MODEL_PACKAGE_NAME_FORMAT;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Model implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    private String uuid;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型类名
     */
    private String className;

    /**
     * 模型包名
     */
    private String packageName;

    /**
     * 模型版本号
     */
    private Long version;

    /**
     * 模型负责人ID
     */
    private Long ownerId;

    /**
     * 模型父类
     */
    private String superClass;

    /**
     * 模型所属领域key
     */
    private String domainKey;

    /**
     * 模型所属项目key
     */
    @Setter(AccessLevel.NONE)
    private String projectKey;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目类型
     */
    private ModelTypeEnum type;

    /**
     * 模型级别
     */
    private ModelAccessModeEnum accessMode;

    /**
     * 项目状态
     */
    private ServiceModelStatusEnum status;

    /**
     * 版本状态
     */
    private ReleaseStatusEnum releaseStatus;

    /**
     * 相应字段列表
     */
    private Set<Field> fields;
    /**
     * 相应枚举信息列表，仅在枚举类型
     */
    private Set<EnumValue> enumValues;

    public void setProjectKey(String projectKey) {
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "项目唯一标识不能为空");
        this.projectKey = StringUtils.upperCase(projectKey);
    }

    public void initModel(Project project, String classNamePrefix) {
        this.accessMode = convertorAccessMode(project.getAccessMode());
        initModel(classNamePrefix);
        buildPackageName(project.getBasePackage());
    }

    public void initModel(String classNamePrefix) {
        this.className = StrUtil.upperFirst(classNamePrefix) + this.type.getExt();
        this.releaseStatus = ReleaseStatusEnum.EDIT;
        this.status = ServiceModelStatusEnum.DEV;
        this.version = INIT_VERSION;
        this.ownerId = SecurityContext.getUserId();
    }

    /**
     * 包名生成
     * 基础包名.领域名称.子包名.类名
     * 基础包名：项目的基础包名
     * 领域名称：项目内部的领域划分
     * 子包名： 具体包名
     * 类名：模型实体类名
     *
     * @param basePackage
     * @return
     */
    public void buildPackageName(String basePackage) {
        String sunPackageName = null;
        if (ModelTypeEnum.PAGE_PARAM == this.type) {
            sunPackageName = ModelTypeEnum.PARAM.name().toLowerCase();
        }
        if (ModelTypeEnum.PAGE_REQUEST == this.type) {
            sunPackageName = ModelTypeEnum.REQUEST.name().toLowerCase();
        } else {
            sunPackageName = this.type.name().toLowerCase();
        }

        this.packageName = String.format(MODEL_PACKAGE_NAME_FORMAT, basePackage, domainKey, sunPackageName, this.className);
    }

    /**
     * 转换访问权限
     * 当前进支持公共库的组件公开使用
     *
     * @param accessMode 授权模式
     * @return
     */
    private ModelAccessModeEnum convertorAccessMode(ProjectAccessModeEnum accessMode) {
        if (accessMode == ProjectAccessModeEnum.PUBLIC) {
            return ModelAccessModeEnum.PUBLIC;
        }
        return ModelAccessModeEnum.PROTECTED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Model)) {
            return false;
        }
        Model model = (Model) o;
        return getClassName().equals(model.getClassName()) && getPackageName().equals(model.getPackageName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassName(), getPackageName());
    }
}
