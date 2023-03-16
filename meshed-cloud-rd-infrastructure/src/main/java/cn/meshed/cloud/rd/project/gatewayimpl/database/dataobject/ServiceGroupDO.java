package cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject;

import cn.meshed.cloud.entity.BaseEntity;
import cn.meshed.cloud.rd.project.enums.ServiceTypeEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author by Vincent Vic
 * @since 2023-03-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_service_group")
public class ServiceGroupDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String uuid;

    /**
     * 服务分组名称
     */
    @TableField("`name`")
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
    private String projectKey;

    /**
     * 服务分组描述
     */
    private String description;

    /**
     * 服务类型
     */
    private ServiceTypeEnum type;


}
