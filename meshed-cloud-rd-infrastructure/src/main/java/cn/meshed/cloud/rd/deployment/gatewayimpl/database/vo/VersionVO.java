package cn.meshed.cloud.rd.deployment.gatewayimpl.database.vo;

import cn.meshed.cloud.rd.deployment.enums.VersionStatusEnum;
import cn.meshed.cloud.rd.deployment.enums.VersionTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
public class VersionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;
    /**
     * 版本审批关联表
     */
    private String flowId;
    /**
     * 版本来源编码（仓库编码）
     */
    private String sourceId;
    /**
     * 版本仓库项目名称
     */
    private String name;
    /**
     * 版本实体项目全称代号
     */
    private String versionName;
    /**
     * 环境
     */
    private String environments;
    /**
     * 版本
     */
    private Long version;
    /**
     * 版本类型
     */
    private VersionTypeEnum type;
    /**
     * 版本状态
     */
    private VersionStatusEnum status;

}
