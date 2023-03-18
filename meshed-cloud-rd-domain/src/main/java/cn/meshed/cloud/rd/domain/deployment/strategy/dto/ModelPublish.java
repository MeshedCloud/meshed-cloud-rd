package cn.meshed.cloud.rd.domain.deployment.strategy.dto;

import cn.meshed.cloud.rd.domain.deployment.strategy.Publish;
import cn.meshed.cloud.rd.domain.repo.Branch;
import lombok.Data;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class ModelPublish extends Publish {

    /**
     * 生成路径
     */
    private String basePath;

    /**
     * 提交分支信息
     */
    private Branch branch;

}
