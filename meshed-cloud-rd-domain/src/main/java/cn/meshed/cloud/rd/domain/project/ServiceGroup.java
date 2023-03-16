package cn.meshed.cloud.rd.domain.project;

import cn.hutool.core.util.StrUtil;
import cn.meshed.cloud.rd.domain.cli.utils.GenerateUtils;
import cn.meshed.cloud.rd.project.enums.ServiceTypeEnum;
import cn.meshed.cloud.utils.AssertUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import static cn.meshed.cloud.rd.domain.project.constant.ProjectConstant.INIT_VERSION;

/**
 * <h1>服务分组</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class ServiceGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    private String uuid;

    /**
     * 服务分组名称
     */
    private String name;

    /**
     * 服务分组uri
     */
    private String uri;

    /**
     * 服务映射类名
     */
    private String className;

    /**
     * 服务分组包名
     */
    private String packageName;

    /**
     * 服务分组版本号
     */
    private Long version;

    /**
     * 服务分组所属领域key
     */
    private String domainKey;

    /**
     * 模型分组所属项目key
     */
    @Setter(AccessLevel.NONE)
    private String projectKey;

    /**
     * 服务分组描述
     */
    private String description;

    /**
     * 服务类型
     */
    private ServiceTypeEnum type;

    /**
     * 服务列表
     */
    private Set<Service> services;

    public void setProjectKey(String projectKey) {
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "项目唯一标识不能为空");
        this.projectKey = StringUtils.upperCase(projectKey);
    }

    public void initServiceGroup(String groupKey, String basePackage) {
        this.className = StrUtil.upperFirst(groupKey) + this.type.getKey();
        if (ServiceTypeEnum.RPC == this.type) {
            this.uri = this.className;
        }
        this.version = INIT_VERSION;
        AssertUtils.isTrue(StringUtils.isNotBlank(this.domainKey), "领域标识不能为空");
        this.packageName = GenerateUtils.buildServicePackageName(basePackage, this.domainKey, this.className);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceGroup)) {
            return false;
        }
        ServiceGroup that = (ServiceGroup) o;
        return getClassName().equals(that.getClassName()) && getPackageName().equals(that.getPackageName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassName(), getPackageName());
    }
}
