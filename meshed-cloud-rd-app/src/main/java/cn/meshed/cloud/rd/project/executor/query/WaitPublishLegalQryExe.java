package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.project.Field;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.ServiceGroup;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGroupGateway;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class WaitPublishLegalQryExe implements QueryExecute<String, Response> {

    private final ServiceGroupGateway serviceGroupGateway;
    private final ServiceGateway serviceGateway;
    private final ModelGateway modelGateway;

    /**
     * 查询等待发布是否合法
     *
     * @param projectKey 项目唯一KEY
     * @return {@link Response}
     */
    @Override
    public Response execute(String projectKey) {
        Set<ServiceGroup> serviceGroups = serviceGroupGateway.waitPublishListByProject(projectKey);
        Set<Model> models = modelGateway.waitPublishModelListByProject(projectKey);
        Set<Model> enums = modelGateway.waitPublishEnumListByProject(projectKey);
        models.addAll(enums);
        if (CollectionUtils.isEmpty(serviceGroups) && CollectionUtils.isEmpty(models)) {
            return ResultUtils.fail("不存在待发布模型和服务");
        }
        Set<String> classNames = new HashSet<>();
        if (CollectionUtils.isNotEmpty(serviceGroups)) {
            Set<String> groupIds = serviceGroups.stream().map(ServiceGroup::getUuid).collect(Collectors.toSet());
            Set<Service> services = serviceGateway.listByGroupIds(groupIds);
            if (CollectionUtils.isNotEmpty(services)) {
                Set<String> set = services.stream()
                        .filter(Objects::nonNull)
                        .map(this::toField)
                        .flatMap(Collection::stream)
                        .map(Field::getFieldType).collect(Collectors.toSet());
                classNames.addAll(set);
            }
        }
        if (CollectionUtils.isNotEmpty(models)) {
            Set<String> set = models.stream().map(Model::getClassName).collect(Collectors.toSet());
            classNames.addAll(set);
        }
        return ResultUtils.of(modelGateway.checkLegalByClassNames(classNames), "类名存在");
    }

    private List<Field> toField(Service service) {
        List<Field> fields = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(service.getRequests())) {
            fields.addAll(service.getRequests());
        }
        if (CollectionUtils.isNotEmpty(service.getResponses())) {
            fields.addAll(service.getResponses());
        }
        return fields;
    }
}
