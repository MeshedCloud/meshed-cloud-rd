package cn.meshed.cloud.rd.deployment.gatewayimpl.strategy;

import cn.meshed.cloud.rd.domain.deployment.VersionOccupyGateway;
import cn.meshed.cloud.rd.domain.deployment.strategy.AsyncPublishStrategy;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishHandler;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishType;
import cn.meshed.cloud.rd.domain.deployment.strategy.dto.ServicePublish;
import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.ServiceGroup;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGroupGateway;
import cn.meshed.cloud.rd.project.enums.ServiceModelTypeEnum;
import cn.meshed.cloud.rd.project.enums.ServiceTypeEnum;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ServicePublishHandler implements PublishHandler<ServicePublish> {

    private final ServiceGroupGateway serviceGroupGateway;
    private final AsyncPublishStrategy asyncPublishStrategy;
    private final VersionOccupyGateway versionOccupyGateway;

    /**
     * 注册类型
     *
     * @return PublishType
     */
    @Override
    public PublishType getPublishType() {
        return PublishType.SERVICE;
    }

    /**
     * 发布服务
     *
     * @param servicePublish 服务发布
     */
    @Override
    public void publish(ServicePublish servicePublish) {
        String projectKey = servicePublish.getProjectKey();
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "项目key不允许为空");
        Set<ServiceGroup> serviceGroups = serviceGroupGateway.waitPublishListByProject(projectKey);
        if (CollectionUtils.isEmpty(serviceGroups)) {
            return;
        }
        //记录占用
        Set<String> serviceUuids = serviceGroups.stream()
                .flatMap(serviceGroup -> serviceGroup.getServices().stream())
                .map(Service::getUuid).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        versionOccupyGateway.saveBatch(servicePublish.getVersionId(), ServiceModelTypeEnum.SERVICE, serviceUuids);
        //服务分类适配器和rpc等
        Map<ServiceTypeEnum, Set<ServiceGroup>> serviceTypeMap = serviceGroups.stream()
                .collect(Collectors.groupingBy(ServiceGroup::getType, Collectors.toSet()));
        serviceTypeMap.forEach((key, value) -> {
            if (CollectionUtils.isNotEmpty(value)) {
                ServicePublish publish = CopyUtils.copy(servicePublish, ServicePublish.class);
                publish.setServiceGroups(value);
                PublishType publishType = convertPublishType(key);
                if (publishType != null) {
                    asyncPublishStrategy.asyncPublish(publishType, publish);
                } else {
                    log.warn("【服务发布】存在服务类型{}未适配发布类型", key);
                }
            }
        });
    }

    private PublishType convertPublishType(ServiceTypeEnum serviceType) {
        if (ServiceTypeEnum.RPC == serviceType) {
            return PublishType.RPC;
        } else if (ServiceTypeEnum.API == serviceType) {
            return PublishType.ADAPTER;
        }
        return null;
    }
}
