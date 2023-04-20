package cn.meshed.cloud.rd.domain.cli.gateway;

import cn.meshed.cloud.rd.domain.cli.BuildArchetype;
import cn.meshed.cloud.rd.domain.cli.GenerateAdapter;
import cn.meshed.cloud.rd.domain.cli.GenerateEnum;
import cn.meshed.cloud.rd.domain.cli.GenerateModel;
import cn.meshed.cloud.rd.domain.cli.GenerateRpc;
import com.alibaba.cola.exception.SysException;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface CliGateway {

    /**
     * 原型构建
     *
     * @param buildArchetype 构建信息
     * @return 工作路径
     * @throws SysException 构建异常信息
     */
    String buildArchetype(BuildArchetype buildArchetype) throws SysException;

    /**
     * 原型构建并推送仓库
     *
     * @param repositoryId   仓库ID
     * @param buildArchetype 构建信息
     * @return 分支
     * @throws SysException
     */
    String archetypeWithPush(String repositoryId, BuildArchetype buildArchetype) throws SysException;

    /**
     * 异步生成模型并推送
     *
     * @param repositoryId  仓库ID
     * @param generateModel 生成模型
     */
    void asyncGenerateModelWithPush(String repositoryId, GenerateModel generateModel);

    /**
     * 异步生成枚举并推送
     *
     * @param repositoryId 仓库ID
     * @param generateEnum 生成枚举
     */
    void asyncGenerateEnumWithPush(String repositoryId, GenerateEnum generateEnum);

    /**
     * 异步生成服务并推送
     *
     * @param repositoryId    仓库ID
     * @param generateAdapter 生成适配器服务接口
     */
    void asyncGenerateAdapterWithPush(String repositoryId, GenerateAdapter generateAdapter);

    /**
     * 异步生成服务并推送
     *
     * @param repositoryId 仓库ID
     * @param generateRpc  生成RPC服务接口
     */
    void asyncGenerateRpcWithPush(String repositoryId, GenerateRpc generateRpc);

}
