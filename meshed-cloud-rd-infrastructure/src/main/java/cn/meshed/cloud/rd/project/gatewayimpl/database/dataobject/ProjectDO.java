package cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject;

import cn.meshed.cloud.entity.BaseEntity;
import cn.meshed.cloud.rd.project.enums.ProjectAccessModeEnum;
import cn.meshed.cloud.rd.project.enums.ProjectStatusEnum;
import cn.meshed.cloud.rd.project.enums.ProjectTypeEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 项目
 * </p>
 *
 * @author by Vincent Vic
 * @since 2023-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_project")
public class ProjectDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String uuid;

    /**
     * 第三方关联ID
     */
    private Long thirdId;

    /**
     * 项目key
     */
    @TableField("`key`")
    private String key;

    /**
     * identity
     */
    private String identity;

    /**
     * 项目名称
     */
    @TableField("`name`")
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
    @TableField("`status`")
    private ProjectStatusEnum status;


}
