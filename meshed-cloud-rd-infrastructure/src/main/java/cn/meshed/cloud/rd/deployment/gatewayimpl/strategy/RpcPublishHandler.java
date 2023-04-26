package cn.meshed.cloud.rd.deployment.gatewayimpl.strategy;

import cn.meshed.cloud.rd.codegen.ObjectMethod;
import cn.meshed.cloud.rd.codegen.Rpc;
import cn.meshed.cloud.rd.domain.cli.GenerateRpc;
import cn.meshed.cloud.rd.domain.cli.gateway.CliGateway;
import cn.meshed.cloud.rd.domain.deployment.strategy.AbstractServicePublish;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishHandler;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishType;
import cn.meshed.cloud.rd.domain.deployment.strategy.dto.ServicePublish;
import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.ServiceGroup;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
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
public class RpcPublishHandler extends AbstractServicePublish implements PublishHandler<ServicePublish> {


    public RpcPublishHandler(ModelGateway modelGateway, CliGateway cliGateway) {
        super(modelGateway, cliGateway);
    }

    /**
     * 注册类型
     *
     * @return PublishType
     */
    @Override
    public PublishType getPublishType() {
        return PublishType.RPC;
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
        Set<Rpc> rpcList = serviceGroups.stream().map(this::toRpc).collect(Collectors.toSet());

        if (CollectionUtils.isNotEmpty(rpcList)) {
            //新增接口
            getCliGateway().asyncGenerateRpcWithPush(servicePublish.getSourceId(),
                    buildGenerateRpc(servicePublish, rpcList));
        }
    }

    @NotNull
    private GenerateRpc buildGenerateRpc(ServicePublish servicePublish, Set<Rpc> rpcList) {
        GenerateRpc generateRpc = new GenerateRpc();
        generateRpc.setRpcList(rpcList);
        generateRpc.setBasePath(SRC_PATH);
        generateRpc.setCommitMessage(servicePublish.getMessage());
        generateRpc.setBranch(servicePublish.getBranch());
        return generateRpc;
    }

    private Rpc toRpc(ServiceGroup serviceGroup) {
        //每个服务中都记录了较为冗余的数据，所以取第一个即可
        Rpc rpc = new Rpc();
        assembleObjectDefinition(serviceGroup, rpc);
        //没有方法可以之间返回适配器
        if (CollectionUtils.isEmpty(serviceGroup.getServices())) {
            return rpc;
        }
        Set<ObjectMethod> methods = serviceGroup.getServices().stream()
                .map(this::toObjectMethod).collect(Collectors.toSet());
        rpc.setMethods(methods);
        rpc.addImport(scanMethodsPackage(CopyUtils.copySetProperties(methods, ObjectMethod::new)));
        return rpc;
    }

    protected ObjectMethod toObjectMethod(Service service) {
        ObjectMethod method = new ObjectMethod();
        assembleMethod(method, service);
        return method;
    }

}
