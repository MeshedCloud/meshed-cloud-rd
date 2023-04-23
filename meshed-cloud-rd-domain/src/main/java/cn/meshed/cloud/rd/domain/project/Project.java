package cn.meshed.cloud.rd.domain.project;

import cn.meshed.cloud.context.SecurityContext;
import cn.meshed.cloud.rd.project.enums.ProjectAccessModeEnum;
import cn.meshed.cloud.rd.project.enums.ProjectStatusEnum;
import cn.meshed.cloud.rd.project.enums.ProjectTypeEnum;
import cn.meshed.cloud.utils.AssertUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import static cn.meshed.cloud.rd.domain.project.constant.ProjectConstant.INIT_BASE_PACKAGE;
import static cn.meshed.cloud.rd.domain.project.constant.ProjectConstant.INIT_VERSION;
import static cn.meshed.cloud.rd.domain.project.constant.ProjectConstant.PROJECT_PACKAGE_NAME_FORMAT;

/**
 * <h1>项目</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    private String uuid;

    /**
     * identity
     */
    private String identity;

    /**
     * 第三方关联ID
     */
    private Long thirdId;

    /**
     * 项目key
     */
    @Setter(AccessLevel.NONE)
    private String key;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目版本号
     */
    private Long version;

    /**
     * 项目基本包名
     */
    private String basePackage;

    /**
     * 项目负责人ID
     */
    private Long ownerId;

    /**
     * 项目级别
     */
    private ProjectAccessModeEnum accessMode;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目类型
     */
    private ProjectTypeEnum type;

    /**
     * 项目状态
     */
    private ProjectStatusEnum status;

    public void setKey(String key) {
        AssertUtils.isTrue(StringUtils.isNotBlank(key), "项目唯一标识不能为空");
        this.key = StringUtils.upperCase(key);
    }

    public void initProject() {
        this.basePackage = buildBasePackageName();
        this.key = this.key.toUpperCase();
        this.version = INIT_VERSION;
        this.ownerId = SecurityContext.getUserId();
        this.status = ProjectStatusEnum.APPLY;
    }

    /**
     * 包名生成
     * 域名反转.大领域名称
     *
     * @return 包名
     */
    private String buildBasePackageName() {
        return String.format(PROJECT_PACKAGE_NAME_FORMAT, INIT_BASE_PACKAGE, this.key).toLowerCase();
    }
}
