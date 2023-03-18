package cn.meshed.cloud.rd.domain.deployment.gateway;

import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.IQuery;
import cn.meshed.cloud.rd.deployment.query.VersionPageQry;
import cn.meshed.cloud.rd.domain.deployment.Version;
import com.alibaba.cola.dto.PageResponse;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface VersionGateway extends IPageList<VersionPageQry, PageResponse<Version>>, IQuery<Long, Version> {

    /**
     * 登记版本信息
     * 注： 修改仅限于版本状态和环境信息
     *
     * @param version 版本信息
     * @return 操作后版本信息
     */
    Version registration(Version version);

    /**
     * 改变版本信息 修改仅限于版本状态和环境信息
     *
     * @param version 版本信息
     * @return 操作后版本信息
     */
    Version change(Version version);

    /**
     * 根据来源信息和版本号获取版本
     *
     * @param version 版本信息
     * @return 返回版本信息
     */
    Version getVersionBySourceIdAndVersion(Version version);

}
