package cn.meshed.cloud.rd.deployment.gatewayimpl.database.dataobject;

import cn.meshed.cloud.entity.BaseEntity;
import cn.meshed.cloud.rd.deployment.enums.VersionStatusEnum;
import cn.meshed.cloud.rd.deployment.enums.VersionTypeEnum;
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
 * @since 2023-03-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_version")
public class VersionDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 版本编码
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 版本来源编码（仓库编码）
     */
    private String sourceId;

    /**
     * 版本环境
     */
    private String environment;

    /**
     * 仓库版本号
     */
    private Long version;

    /**
     * 仓库所属项目key
     */
    private String projectKey;

    /**
     * 版本类型
     */
    private VersionTypeEnum type;

    /**
     * 版本状态
     */
    @TableField("`status`")
    private VersionStatusEnum status;

}
