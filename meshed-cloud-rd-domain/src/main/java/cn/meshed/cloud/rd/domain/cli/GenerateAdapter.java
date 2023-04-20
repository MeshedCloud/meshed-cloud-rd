package cn.meshed.cloud.rd.domain.cli;

import cn.meshed.cloud.rd.codegen.Adapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * <h1>生成适配器服务接口</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GenerateAdapter extends GenerateCode {

    /**
     * 适配器接口
     */
    private Set<Adapter> adapters;

}
