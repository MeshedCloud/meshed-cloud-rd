package cn.meshed.cloud.rd.domain.deployment;

import cn.meshed.cloud.context.SecurityContext;
import cn.meshed.cloud.rd.deployment.enums.WarehouseAccessModeEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehousePurposeTypeEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehouseRelationEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehouseRepoTypeEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehouseStatusEnum;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.project.enums.ProjectAccessModeEnum;
import cn.meshed.cloud.utils.AssertUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import static cn.meshed.cloud.rd.domain.project.constant.ProjectConstant.INIT_VERSION;

/**
 * <h1>仓库</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class Warehouse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    private String uuid;

    /**
     * repositoryId
     */
    private String repoId;

    /**
     * 中文名称
     */
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
    private WarehouseStatusEnum status;

    public static Warehouse buildImportWarehouse(Project project, String repoUrl, String name, String description,
                                                 WarehousePurposeTypeEnum purposeType) {
        Warehouse warehouse = new Warehouse();
        warehouse.setAccessMode(warehouse.convertorAccessMode(project.getAccessMode()));
        warehouse.setName(name);
        warehouse.setProjectKey(project.getKey());
        warehouse.setDescription(description);
        warehouse.setOwnerId(SecurityContext.getUserId());
        warehouse.setRelation(WarehouseRelationEnum.IMPORT);
        warehouse.setPurposeType(purposeType);
        warehouse.setRepoUrl(repoUrl);
        //提取仓库名称
        warehouse.setRepoName(getRepoNameByUrl(repoUrl));
        //提取仓库类型
        warehouse.setRepoType(getRepoTypeByUrl(repoUrl));
        warehouse.setVersion(INIT_VERSION);
        warehouse.setStatus(WarehouseStatusEnum.NORMAL);

        return warehouse;
    }

    /**
     * 提取仓库类型
     *
     * @param repoUrl 仓库地址 ip无法提取，仅支持常见几种
     * @return 仓库类型
     */
    private static WarehouseRepoTypeEnum getRepoTypeByUrl(String repoUrl) {
        if (StringUtils.isEmpty(repoUrl)) {
            return null;
        }
        for (WarehouseRepoTypeEnum repoTypeEnum : WarehouseRepoTypeEnum.values()) {
            if (repoUrl.contains(repoTypeEnum.getExt())) {
                return repoTypeEnum;
            }
        }
        return WarehouseRepoTypeEnum.UNKNOWN;
    }

    /**
     * 提取仓库名字
     *
     * @param repoUrl 仓库地址
     * @return 仓库名字
     */
    private static String getRepoNameByUrl(String repoUrl) {
        if (StringUtils.isEmpty(repoUrl)) {
            return null;
        }
        int lastIndexOf = repoUrl.lastIndexOf("/");
        AssertUtils.isTrue(lastIndexOf > 0, "仓库地址异常");
        return repoUrl.substring(lastIndexOf).replaceAll("\\.git", "");
    }

    public void initNewWarehouse(ProjectAccessModeEnum accessMode) {
        this.status = WarehouseStatusEnum.INIT;
        this.accessMode = convertorAccessMode(accessMode);
        this.repoType = WarehouseRepoTypeEnum.CODEUP;
        this.ownerId = SecurityContext.getUserId();
        this.relation = WarehouseRelationEnum.BUILD;
        this.version = INIT_VERSION;

    }

    /**
     * 转换访问权限
     * 当前核心为私有，其他为企业内部可见
     *
     * @param accessMode
     * @return
     */
    public WarehouseAccessModeEnum convertorAccessMode(ProjectAccessModeEnum accessMode) {
        if (accessMode == ProjectAccessModeEnum.CORE) {
            return WarehouseAccessModeEnum.PRIVATE;
        }
        return WarehouseAccessModeEnum.PUBLIC;
    }

}
