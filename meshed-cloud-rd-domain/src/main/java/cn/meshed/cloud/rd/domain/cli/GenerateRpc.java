package cn.meshed.cloud.rd.domain.cli;

import cn.meshed.cloud.rd.codegen.Rpc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * <h1>生成RPC服务接口</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GenerateRpc extends GenerateCode {

    /**
     * rpc列表
     */
    private Set<Rpc> rpcList;

}
