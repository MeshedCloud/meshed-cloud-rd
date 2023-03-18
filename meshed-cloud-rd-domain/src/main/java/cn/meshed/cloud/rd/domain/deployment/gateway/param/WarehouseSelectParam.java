package cn.meshed.cloud.rd.domain.deployment.gateway.param;

import cn.meshed.cloud.rd.deployment.enums.WarehousePurposeTypeEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehouseRepoTypeEnum;
import lombok.Data;

import java.util.List;


/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class WarehouseSelectParam {

    /**
     * 项目标识
     */
    private String projectKey;

    /**
     * 仓库存储类型（代码仓库）：云效/github/gitee
     */
    private WarehouseRepoTypeEnum repoType;
    /**
     * 仓库目的类型（管理仓库）: 服务/客户端/组件/前端
     */
    private List<WarehousePurposeTypeEnum> purposeTypes;
}
