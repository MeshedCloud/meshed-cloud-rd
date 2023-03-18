package cn.meshed.cloud.rd.deployment.gatewayimpl.strategy;

import cn.meshed.cloud.rd.codegen.Adapter;
import cn.meshed.cloud.rd.codegen.AdapterMethod;
import cn.meshed.cloud.rd.codegen.ObjectMethod;
import cn.meshed.cloud.rd.codegen.constant.RequestType;
import cn.meshed.cloud.rd.domain.cli.GenerateAdapter;
import cn.meshed.cloud.rd.domain.cli.gateway.CliGateway;
import cn.meshed.cloud.rd.domain.deployment.strategy.AbstractServicePublish;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishHandler;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishType;
import cn.meshed.cloud.rd.domain.deployment.strategy.dto.ServicePublish;
import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.ServiceGroup;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.project.enums.RequestTypeEnum;
import cn.meshed.cloud.utils.CopyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static cn.meshed.cloud.rd.domain.common.constant.Constant.SRC_PATH;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Component
public class AdapterPublishHandler extends AbstractServicePublish implements PublishHandler<ServicePublish> {


    public AdapterPublishHandler(ModelGateway modelGateway, CliGateway cliGateway) {
        super(modelGateway, cliGateway);
    }

    /**
     * 注册类型
     *
     * @return PublishType
     */
    @Override
    public PublishType getPublishType() {
        return PublishType.ADAPTER;
    }

    /**
     * 发布模型
     *
     * @param servicePublish 模型发布
     */
    @Override
    public void publish(ServicePublish servicePublish) {
        //用于提取操作数据中新增的模型对象
        Set<ServiceGroup> serviceGroups = servicePublish.getServiceGroups();
        //转换为适配器
        Set<Adapter> adapters = serviceGroups.stream().map(this::toAdapter).collect(Collectors.toSet());

        //新增接口
        getCliGateway().asyncGenerateAdapterWithPush(servicePublish.getSourceId(),
                buildGenerateAdapter(servicePublish, adapters));
    }

    @NotNull
    private GenerateAdapter buildGenerateAdapter(ServicePublish servicePublish, Set<Adapter> adapters) {
        GenerateAdapter generateAdapter = new GenerateAdapter();
        generateAdapter.setAdapters(adapters);
        generateAdapter.setBasePath(SRC_PATH);
        generateAdapter.setCommitMessage(servicePublish.getMessage());
        generateAdapter.setBranch(servicePublish.getBranch());
        return generateAdapter;
    }

    private Adapter toAdapter(ServiceGroup serviceGroup) {
        //每个服务中都记录了较为冗余的数据，所以取第一个即可
        Adapter adapter = new Adapter();
        assembleObjectDefinition(serviceGroup, adapter);
        adapter.setUri(serviceGroup.getUri());
        //没有方法可以之间返回适配器
        if (CollectionUtils.isEmpty(serviceGroup.getServices())) {
            return adapter;
        }
        Set<AdapterMethod> methods = serviceGroup.getServices().stream()
                .map(this::toAdapterMethod).collect(Collectors.toSet());
        adapter.setMethods(methods);
        adapter.addImport(scanMethodsPackage(CopyUtils.copySetProperties(methods, ObjectMethod::new)));
        return adapter;
    }

    protected AdapterMethod toAdapterMethod(Service service) {
        AdapterMethod method = new AdapterMethod();
        assembleMethod(method, service);
        method.setRequestType(convertRequestType(service.getRequestType()));
        method.setUri(service.getUri());
        return method;
    }

    private RequestType convertRequestType(RequestTypeEnum requestTypeEnum) {
        return RequestType.valueOf(requestTypeEnum.name());
    }

}
