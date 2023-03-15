package cn.meshed.cloud.rd.project.gatewayimpl;

import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.ServiceGroup;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGroupGateway;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.ServiceDO;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.ServiceGroupDO;
import cn.meshed.cloud.rd.project.gatewayimpl.database.mapper.ServiceGroupMapper;
import cn.meshed.cloud.rd.project.gatewayimpl.database.mapper.ServiceMapper;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import com.alibaba.cola.exception.SysException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1>服务网关</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ServiceGroupGatewayImpl implements ServiceGroupGateway {

    private final ServiceMapper serviceMapper;
    private final ServiceGroupMapper serviceGroupMapper;
    private final ServiceGateway serviceGateway;

    /**
     * <h1>保存对象</h1>
     *
     * @param serviceGroup 服务分组
     * @return {@link String} uuid
     */
    @Override
    public ServiceGroup save(ServiceGroup serviceGroup) {
        AssertUtils.isTrue(serviceGroup != null, "分组参数不能为空");
        assert serviceGroup != null;
        AssertUtils.isTrue(StringUtils.isBlank(serviceGroup.getUuid()), "分组暂不支持修改");
        if (existGroupClassName(serviceGroup.getProjectKey(), serviceGroup.getClassName())) {
            throw new SysException("同一个控制器中不允许同名服务");
        }
        ServiceGroupDO serviceGroupDO = CopyUtils.copy(serviceGroup, ServiceGroupDO.class);
        AssertUtils.isTrue(serviceGroupMapper.insert(serviceGroupDO) > 0, "新增分组失败");
        return CopyUtils.copy(serviceGroupDO, ServiceGroup.class);
    }

    /**
     * <h1>选项查询</h1>
     *
     * @param projectKey 项目唯一标识
     * @return {@link Set<ServiceGroup>}
     */
    @Override
    public Set<ServiceGroup> select(String projectKey) {
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "项目唯一标识不能为空");
        LambdaQueryWrapper<ServiceGroupDO> lqw = new LambdaQueryWrapper<>();
        lqw.select(ServiceGroupDO::getName, ServiceGroupDO::getClassName, ServiceGroupDO::getType,
                ServiceGroupDO::getUuid, ServiceGroupDO::getProjectKey);
        lqw.eq(ServiceGroupDO::getProjectKey, projectKey);
        return CopyUtils.copySetProperties(serviceGroupMapper.selectList(lqw), ServiceGroup::new);
    }

    /**
     * <h1>分页搜索</h1>
     *
     * @param uuids
     * @return {@link List<ServiceGroup>}
     */
    @Override
    public List<ServiceGroup> searchList(Set<String> uuids) {
        LambdaQueryWrapper<ServiceGroupDO> lqw = new LambdaQueryWrapper<>();
        lqw.in(ServiceGroupDO::getUuid, uuids);
        return CopyUtils.copyListProperties(serviceGroupMapper.selectList(lqw), ServiceGroup::new);
    }

    /**
     * 查询
     *
     * @param uuid 参数
     * @return {@link ServiceGroup}
     */
    @Override
    public ServiceGroup query(String uuid) {
        AssertUtils.isTrue(StringUtils.isNotBlank(uuid), "服务分组编码不能为空");
        LambdaQueryWrapper<ServiceGroupDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ServiceGroupDO::getUuid, uuid);
        return CopyUtils.copy(serviceGroupMapper.selectOne(lqw), ServiceGroup.class);
    }

    /**
     * 判断分组类名是否已经存在
     *
     * @param projectKey 项目唯一标识
     * @param className  类名
     * @return 存在情况
     */
    @Override
    public boolean existGroupClassName(String projectKey, String className) {
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "归属系统标识编码不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(className), "服务类名编码不能为空");
        LambdaQueryWrapper<ServiceGroupDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ServiceGroupDO::getProjectKey, projectKey)
                .eq(ServiceGroupDO::getClassName, className);
        return serviceGroupMapper.selectCount(lqw) > 0;
    }

    /**
     * 查询项目的待发布服务详情列表
     * 注：服务发布需要重建所属这个控制器全部方法，需要将同分组的服务方法一并查询出来
     *
     * @param projectKey 项目key
     * @return 详情列表
     */
    @Override
    public Set<ServiceGroup> waitPublishListByProject(String projectKey) {
        Set<String> groupList = waitPublishGroupList(projectKey);
        if (CollectionUtils.isEmpty(groupList)) {
            return Collections.emptySet();
        }
        List<ServiceGroupDO> serviceGroups = getServiceGroups(groupList);
        if (CollectionUtils.isEmpty(serviceGroups)) {
            return Collections.emptySet();
        }
        Set<String> uuids = serviceGroups.stream().map(ServiceGroupDO::getUuid).collect(Collectors.toSet());
        //查询出所有的服务，并分类出各个分组的服务
        Set<Service> services = serviceGateway.listByUuids(uuids);
        Map<String, Set<Service>> serviceMap = services.stream()
                .collect(Collectors.groupingBy(Service::getGroupId, Collectors.toSet()));

        return serviceGroups.stream().map(serviceGroupDO ->
                toServiceGroup(serviceGroupDO, serviceMap.get(serviceGroupDO.getUuid()))).collect(Collectors.toSet());
    }

    private ServiceGroup toServiceGroup(ServiceGroupDO serviceGroupDO, Set<Service> services) {
        ServiceGroup serviceGroup = CopyUtils.copy(serviceGroupDO, ServiceGroup.class);
        serviceGroup.setServices(services);
        return serviceGroup;
    }

    @Nullable
    private List<ServiceGroupDO> getServiceGroups(Set<String> groupList) {
        LambdaQueryWrapper<ServiceGroupDO> lqw = new LambdaQueryWrapper<>();
        lqw.in(ServiceGroupDO::getUuid, groupList);
        return serviceGroupMapper.selectList(lqw);
    }


    private Set<String> waitPublishGroupList(String projectKey) {
        LambdaQueryWrapper<ServiceDO> lqw = new LambdaQueryWrapper<>();
        lqw.select(ServiceDO::getGroupId)
                .eq(ServiceDO::getProjectKey, projectKey)
                .eq(ServiceDO::getReleaseStatus, ReleaseStatusEnum.PROCESSING);
        List<ServiceDO> serviceList = serviceMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(serviceList)) {
            return Collections.emptySet();
        }
        return serviceList.stream().map(ServiceDO::getGroupId).collect(Collectors.toSet());
    }
}
