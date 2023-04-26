package cn.meshed.cloud.rd.domain.deployment;

import cn.meshed.cloud.core.IDelete;
import cn.meshed.cloud.core.IList;
import cn.meshed.cloud.rd.project.enums.ServiceModelTypeEnum;

import java.util.Set;

/**
 * <h1>版本占用信息网关</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface VersionOccupyGateway extends IDelete<Long, Boolean>, IList<Long, Set<VersionOccupy>> {

    /**
     * 批量新增
     *
     * @param versionId   版本id
     * @param type        类型
     * @param relationIds 关系id列表
     * @return 成功与否
     */
    boolean saveBatch(Long versionId, ServiceModelTypeEnum type, Set<String> relationIds);

    /**
     * 通过类型和编码查询版本占用信息
     *
     * @param type 类型
     * @param uuid 编码
     * @return {@link VersionOccupy}
     */
    VersionOccupy query(ServiceModelTypeEnum type, String uuid);
}
