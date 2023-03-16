package cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject;

import cn.meshed.cloud.entity.BaseEntity;
import cn.meshed.cloud.rd.project.enums.ModelAccessModeEnum;
import cn.meshed.cloud.rd.project.enums.ModelTypeEnum;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.enums.ServiceModelStatusEnum;
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
 * @since 2023-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_model")
public class ModelDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String uuid;

    /**
     * 模型名称
     */
    @TableField("`name`")
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
    @TableField("`status`")
    private ServiceModelStatusEnum status;

    /**
     * 版本状态
     */
    private ReleaseStatusEnum releaseStatus;


}
