package cn.meshed.cloud.rd.domain.project.gateway;

import cn.meshed.cloud.core.IList;
import cn.meshed.cloud.core.IQuery;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.core.ISelect;
import cn.meshed.cloud.rd.domain.project.ServiceGroup;

import java.util.List;
import java.util.Set;

/**
 * <h1>服务分组网关</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface ServiceGroupGateway extends ISave<ServiceGroup, ServiceGroup>, ISelect<String, Set<ServiceGroup>>,
        IQuery<String, ServiceGroup>, IList<Set<String>, List<ServiceGroup>> {

    /**
     * 判断分组类名是否已经存在
     *
     * @param projectKey 项目唯一标识
     * @param className  类名
     * @return 存在情况
     */
    boolean existGroupClassName(String projectKey, String className);

    /**
     * 查询项目的待发布服务详情列表（适配器和RPC不区分）
     * 查询待发布涉及的组，查询组内容出来重新构建
     *
     * @param projectKey 项目key
     * @return 详情列表
     */
    Set<ServiceGroup> waitPublishListByProject(String projectKey);

}
