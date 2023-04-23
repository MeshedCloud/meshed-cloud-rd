package cn.meshed.cloud.rd.domain.cli;

import cn.meshed.cloud.utils.AssertUtils;
import lombok.Data;

/**
 * <h1>脚手架信息</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class Skeleton {

    /**
     * 引擎模板 具体的模板
     */
    private EngineTemplate engineTemplate;
    /**
     * 基本包名
     */
    private String basePackage;
    /**
     * 仓库名称
     */
    private String repositoryName;
    /**
     * 仓库ID
     */
    private String repositoryId;
    /**
     * 项目key
     */
    private String projectKey;

    /**
     * 校验
     */
    public void verification() {
        AssertUtils.isTrue(this.engineTemplate != null, "引擎模板不存在");
    }
}
