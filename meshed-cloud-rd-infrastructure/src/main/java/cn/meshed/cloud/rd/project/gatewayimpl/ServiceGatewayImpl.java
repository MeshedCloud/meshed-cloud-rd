package cn.meshed.cloud.rd.project.gatewayimpl;

import cn.meshed.cloud.rd.domain.project.Field;
import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.constant.RelevanceTypeEnum;
import cn.meshed.cloud.rd.domain.project.gateway.FieldGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGateway;
import cn.meshed.cloud.rd.project.convertor.ServiceConvertor;
import cn.meshed.cloud.rd.project.enums.RequestModeEnum;
import cn.meshed.cloud.rd.project.enums.ServiceTypeEnum;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.ServiceDO;
import cn.meshed.cloud.rd.project.gatewayimpl.database.mapper.ServiceMapper;
import cn.meshed.cloud.rd.project.query.ServicePageQry;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.exception.SysException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.meshed.cloud.rd.domain.common.constant.Constant.BASE_JAVA_TYPES;

/**
 * <h1>服务网关实现</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ServiceGatewayImpl implements ServiceGateway {

    private final ServiceMapper serviceMapper;
    private final ModelGateway modelGateway;
    private final FieldGateway fieldGateway;
    private final List<RelevanceTypeEnum> groupTypes = new ArrayList<RelevanceTypeEnum>(2) {{
        add(RelevanceTypeEnum.REQUEST);
        add(RelevanceTypeEnum.RESPONSE);
    }};


    /**
     * <h1>分页搜索</h1>
     *
     * @param param@return {@link PageResponse<Service>}
     */
    @Override
    public PageResponse<Service> searchPageList(ServicePageQry param) {
        return null;
    }

    /**
     * 查询
     *
     * @param uuid uuid
     * @return {@link Service}
     */
    @Override
    public Service query(String uuid) {
        Service service = CopyUtils.copy(queryByUuid(uuid), Service.class);
        if (service == null) {
            return null;
        }
        Set<Field> fields = fieldGateway.listByService(uuid);
        Map<RelevanceTypeEnum, Set<Field>> fieldMap = fields.stream()
                .collect(Collectors.groupingBy(Field::getRelevanceType, Collectors.toSet()));
        service.setRequests(fieldMap.get(RelevanceTypeEnum.REQUEST));
        service.setResponses(fieldMap.get(RelevanceTypeEnum.RESPONSE));

        //解包处理
        unpackingProcessing(service);
        return service;
    }

    /**
     * 服务参数解构
     *
     * @param service
     */
    private void unpackingProcessing(Service service) {
        //判断请求参数是否需要解包
        //非多参数模式都需要解包且请求参数字段存在
        Set<Field> requests = service.getRequests();
        if (RequestModeEnum.MULTIPLE != service.getRequestMode() && CollectionUtils.isNotEmpty(requests)) {
            Set<String> classNames = requests.stream().map(Field::getFieldType)
                    .filter(fieldType -> !BASE_JAVA_TYPES.contains(fieldType)).collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(classNames)) {
                throw new SysException("【解构】字段类型异常");
            }
            Set<String> modelUuids = modelGateway.selectUuidListByClassNames(classNames);
            if (CollectionUtils.isNotEmpty(modelUuids)) {
                service.setRequests(fieldGateway.listByModels(modelUuids));
            }
        }
        //判断响应参数是否需要解包
        //响应参数为合并的情况下需要解包且响应参数字段存在
        Set<Field> responses = service.getResponses();
        if (service.getResponseMerge() && CollectionUtils.isNotEmpty(responses)) {
            //合并情况下仅一个参数且是对象类型
            Field field = responses.iterator().next();
            Set<String> uuids = modelGateway.selectUuidListByClassNames(Collections.singleton(field.getFieldType()));
            if (CollectionUtils.isNotEmpty(uuids)) {
                service.setResponses(fieldGateway.listByModel(uuids.iterator().next()));
            }
        }
    }

    /**
     * <h1>保存对象</h1>
     *
     * @param service
     * @return {@link String}
     */
    @Override
    public String save(Service service) {
        if (service == null) {
            return null;
        }
        AssertUtils.isTrue(!existMethodName(service.getGroupId(), service.getMethod()), "方法名称重复");
        if (ServiceTypeEnum.API == service.getType()) {
            AssertUtils.isTrue(!existUri(service.getGroupId(), service.getUri()), "URI重复");
        }

        ServiceDO serviceDO = ServiceConvertor.toEntity(service, queryByUuid(service.getUuid()));
        //保存服务
        if (StringUtils.isEmpty(serviceDO.getUuid())) {
            //判断服务新增是否成功
            AssertUtils.isTrue(serviceMapper.insert(serviceDO) > 0, "服务新增失败");

        } else {
            //更新服务
            //判断服务新增是否成功
            AssertUtils.isTrue(serviceMapper.updateById(serviceDO) > 0, "服务更新失败");
        }

        //保存字段
        String uuid = serviceDO.getUuid();
        saveFields(RelevanceTypeEnum.REQUEST, uuid, service.getRequests());
        saveFields(RelevanceTypeEnum.RESPONSE, uuid, service.getResponses());
        //返回uuid
        return uuid;
    }

    @NotNull
    private void saveFields(RelevanceTypeEnum groupType, String uuid, Set<Field> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        fields = fields.stream().peek(field -> {
            field.setRelevanceId(uuid);
            field.setRelevanceType(groupType);
        }).collect(Collectors.toSet());

        AssertUtils.isTrue(fieldGateway.saveBatch(groupType, fields), "字段保存失败");
    }


    /**
     * @param uuid
     * @return
     */
    private ServiceDO queryByUuid(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            return null;
        }
        return serviceMapper.selectById(uuid);
    }

    /**
     * 判断服务处理器类中是否存在查询的方法
     *
     * @param groupId 分组编码
     * @param method  方法名称
     * @return
     */
    @Override
    public boolean existMethodName(String groupId, String method) {
        AssertUtils.isTrue(StringUtils.isNotBlank(groupId), "分组ID不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(method), "方法名称不能为空");
        LambdaQueryWrapper<ServiceDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ServiceDO::getGroupId, groupId)
                .eq(ServiceDO::getMethod, method);
        //项目的控制器类的方法具有唯一性
        return serviceMapper.selectCount(lqw) > 0;
    }

    /**
     * 判断服务处理器类中是否存在查询的方法
     *
     * @param groupId 分组ID
     * @param uri     uri
     * @return
     */
    @Override
    public boolean existUri(String groupId, String uri) {
        AssertUtils.isTrue(StringUtils.isNotBlank(groupId), "分组ID不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(uri), "路径参数不能为空");
        LambdaQueryWrapper<ServiceDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ServiceDO::getGroupId, groupId)
                .eq(ServiceDO::getUri, uri);
        //项目的控制器类的方法具有唯一性
        return serviceMapper.selectCount(lqw) > 0;
    }

    /**
     * 查询项目的待发布服务详情列表
     * 注：服务发布需要重建所属这个控制器全部方法，需要将同分组的服务方法一并查询出来
     *
     * @param uuids uuids
     * @return 服务列表
     */
    @Override
    public Set<Service> listByUuids(Set<String> uuids) {
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(uuids), "uuid列表不能为空");
        LambdaQueryWrapper<ServiceDO> lqw = new LambdaQueryWrapper<>();
        lqw.in(ServiceDO::getGroupId, uuids);
        Set<Service> services = CopyUtils.copySetProperties(serviceMapper.selectList(lqw), Service::new);
        if (CollectionUtils.isEmpty(services)) {
            return services;
        }
        uuids = services.stream().map(Service::getUuid).collect(Collectors.toSet());
        Set<Field> fields = fieldGateway.listByServices(uuids);
        if (CollectionUtils.isNotEmpty(fields)) {
            Map<String, Set<Field>> fieldMap = fields.stream()
                    .collect(Collectors.groupingBy(Field::getRelevanceId, Collectors.toSet()));
            services.forEach(service -> {
                Set<Field> serviceFields = fieldMap.get(service.getUuid());
                if (CollectionUtils.isNotEmpty(serviceFields)) {
                    Map<RelevanceTypeEnum, Set<Field>> relevanceTypeMap = serviceFields.stream()
                            .collect(Collectors.groupingBy(Field::getRelevanceType, Collectors.toSet()));
                    service.setRequests(relevanceTypeMap.get(RelevanceTypeEnum.REQUEST));
                    service.setResponses(relevanceTypeMap.get(RelevanceTypeEnum.RESPONSE));
                }

            });
        }

        return services;
    }

}
