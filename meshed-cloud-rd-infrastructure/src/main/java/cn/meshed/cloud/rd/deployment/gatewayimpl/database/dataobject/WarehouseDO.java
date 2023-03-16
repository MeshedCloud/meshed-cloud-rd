package cn.meshed.cloud.rd.deployment.gatewayimpl.database.dataobject;

import cn.meshed.cloud.entity.BaseEntity;
import cn.meshed.cloud.rd.deployment.enums.WarehouseAccessModeEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehousePurposeTypeEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehouseRelationEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehouseRepoTypeEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehouseStatusEnum;
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
 * @since 2023-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_warehouse")
public class WarehouseDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String uuid;

    /**
     * repositoryId
     */
    private String repoId;

    /**
     * 中文名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 仓库名称
     */
    private String repoName;

    /**
     * 仓库存储类型（代码仓库）：云效/github/gitee
     */
    private WarehouseRepoTypeEnum repoType;

    /**
     * 仓库地址
     */
    private String repoUrl;

    /**
     * 仓库目的类型（管理仓库）: 服务/客户端/组件/前端
     */
    private WarehousePurposeTypeEnum purposeType;

    /**
     * 仓库系统关系：自建/登记/导入
     */
    private WarehouseRelationEnum relation;

    /**
     * 仓库版本号
     */
    private Long version;

    /**
     * 仓库负责人ID
     */
    private Long ownerId;

    /**
     * 仓库访问模式
     */
    private WarehouseAccessModeEnum accessMode;

    /**
     * 仓库所属项目key
     */
    private String projectKey;

    /**
     * 仓库描述
     */
    private String description;

    /**
     * 仓库状态
     */
    @TableField("`status`")
    private WarehouseStatusEnum status;


}
