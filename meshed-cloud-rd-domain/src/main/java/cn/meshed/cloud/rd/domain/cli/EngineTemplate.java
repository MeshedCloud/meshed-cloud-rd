package cn.meshed.cloud.rd.domain.cli;

import lombok.Data;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class EngineTemplate {

    /**
     * id 仓库名称/制品名称
     */
    private String id;

    /**
     * origin 仓库地址/制品分组
     */
    private String origin;

    /**
     * tag 仓库分支/制品版本
     */
    private String tag;

    /**
     * MAVEN制品/GIT_TEMPLATE仓库模板
     */
    private EngineTemplateType type;

    public static enum EngineTemplateType {
        /**
         * MAVEN制品
         */
        MAVEN,
        /**
         * GIT_TEMPLATE仓库模板
         */
        GIT_TEMPLATE
    }
}
