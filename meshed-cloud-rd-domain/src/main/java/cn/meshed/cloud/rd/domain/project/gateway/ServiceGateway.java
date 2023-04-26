package cn.meshed.cloud.rd.domain.project.gateway;

import cn.meshed.cloud.core.IDelete;
import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.IQuery;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.ServiceItem;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.enums.ServiceModelStatusEnum;
import cn.meshed.cloud.rd.project.query.ServicePageQry;
import com.alibaba.cola.dto.PageResponse;

import java.util.Set;

/**
 * <h1>服务网关</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface ServiceGateway extends ISave<Service, String>, IQuery<String, Service>,
        IPageList<ServicePageQry, PageResponse<ServiceItem>>, IDelete<String, Boolean> {

    /**
     * 判断服务处理器类中是否存在查询的方法
     *
     * @param groupId 分组ID
     * @param method  方法名称
     * @return
     */
    boolean existMethodName(String groupId, String method);

    /**
     * 判断服务处理器类中是否存在查询的方法
     *
     * @param groupId 分组ID
     * @param uri     uri
     * @param uuid    服务ID 可为空
     * @return
     */
    boolean existUri(String groupId, String uri, String uuid);

    /**
     * 查询项目的待发布服务详情列表
     * 注：服务发布需要重建所属这个控制器全部方法，需要将同分组的服务方法一并查询出来
     *
     * @param groupIds 分组ID列表
     * @return 服务列表
     */
    Set<Service> listByGroupIds(Set<String> groupIds);

    /**
     * 更新状态
     *
     * @param uuid          编码
     * @param status        状态
     * @param releaseStatus 发行状态
     * @return 成功与否
     */
    boolean updateStatus(String uuid, ServiceModelStatusEnum status, ReleaseStatusEnum releaseStatus);

    /**
     * 批量更新状态
     *
     * @param uuids         编码列表
     * @param status        状态
     * @param releaseStatus 发行状态
     * @return 成功与否
     */
    boolean batchUpdateStatus(Set<String> uuids, ServiceModelStatusEnum status, ReleaseStatusEnum releaseStatus);

    /**
     * 服务检查合法性
     *
     * @param uuids
     * @return
     */
    boolean checkLegal(String uuids);
}
