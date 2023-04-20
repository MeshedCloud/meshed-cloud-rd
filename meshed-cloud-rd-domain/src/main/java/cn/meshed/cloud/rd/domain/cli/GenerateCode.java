package cn.meshed.cloud.rd.domain.cli;

import cn.meshed.cloud.rd.domain.repo.Branch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>构建模型</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class GenerateCode {

    /**
     * 生成路径
     */
    private String basePath;

    /**
     * 提交信息
     */
    private String commitMessage;

    /**
     * 提交分支信息
     */
    private Branch branch;
}
