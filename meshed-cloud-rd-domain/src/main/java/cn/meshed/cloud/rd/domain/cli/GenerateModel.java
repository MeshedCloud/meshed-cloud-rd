package cn.meshed.cloud.rd.domain.cli;

import cn.meshed.cloud.rd.codegen.ObjectModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * <h1>构建模型</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GenerateModel extends GenerateCode {

    /**
     * 对象模型
     */
    private Set<ObjectModel> models;

}
