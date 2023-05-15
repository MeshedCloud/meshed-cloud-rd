package cn.meshed.cloud.rd.deployment.gatewayimpl.strategy;

import cn.meshed.cloud.rd.domain.deployment.VersionOccupyGateway;
import cn.meshed.cloud.rd.domain.deployment.strategy.Publish;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishHandler;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishType;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.ServiceItem;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGateway;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.enums.ServiceModelTypeEnum;
import cn.meshed.cloud.rd.project.query.ModelPageQry;
import cn.meshed.cloud.rd.project.query.ServicePageQry;
import com.alibaba.cola.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ReleasePublishHandler implements PublishHandler<Publish> {

    private final VersionOccupyGateway versionOccupyGateway;
    private final ServiceGateway serviceGateway;
    private final ModelGateway modelGateway;

    /**
     * 注册类型
     *
     * @return PublishType
     */
    @Override
    public PublishType getPublishType() {
        return PublishType.RELEASE;
    }

    /**
     * 发布
     *
     * @param publishData 数据包
     */
    @Override
    public void publish(Publish publishData) {
        handleService(publishData.getVersionId(), publishData.getProjectKey());
        handleModel(publishData.getVersionId(), publishData.getProjectKey());
    }

    private void handleService(Long versionId, String projectKey) {
        ServicePageQry pageQry = new ServicePageQry();
        pageQry.setPageSize(1000);
        pageQry.setProjectKey(projectKey);
        pageQry.setReleaseStatus(ReleaseStatusEnum.SNAPSHOT);
        PageResponse<ServiceItem> pageResponse = serviceGateway.searchPageList(pageQry);
        if (CollectionUtils.isNotEmpty(pageResponse.getData())) {
            Set<String> uuid = pageResponse.getData().stream().map(ServiceItem::getUuid).collect(Collectors.toSet());
            versionOccupyGateway.saveBatch(versionId, ServiceModelTypeEnum.SERVICE, uuid);
        }
    }

    private void handleModel(Long versionId, String projectKey) {
        ModelPageQry pageQry = new ModelPageQry();
        pageQry.setPageSize(1000);
        pageQry.setProjectKey(projectKey);
        pageQry.setReleaseStatus(ReleaseStatusEnum.SNAPSHOT);
        PageResponse<Model> pageResponse = modelGateway.searchPageList(pageQry);
        if (CollectionUtils.isNotEmpty(pageResponse.getData())) {
            Set<String> uuid = pageResponse.getData().stream().map(Model::getUuid).collect(Collectors.toSet());
            versionOccupyGateway.saveBatch(versionId, ServiceModelTypeEnum.MODEL, uuid);
        }
    }
}
