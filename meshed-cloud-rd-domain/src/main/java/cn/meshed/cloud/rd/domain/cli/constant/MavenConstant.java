package cn.meshed.cloud.rd.domain.cli.constant;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface MavenConstant {

    /**
     * 原型组ID
     */
    String ARCHETYPE_GROUP_ID = "archetypeGroupId";
    /**
     * 原型制品ID
     */
    String ARCHETYPE_ARTIFACT_ID = "archetypeArtifactId";
    /**
     * 原型版本
     */
    String ARCHETYPE_VERSION = "archetypeVersion";
    /**
     * 组ID
     */
    String GROUP_ID = "groupId";
    /**
     * 制品ID
     */
    String ARTIFACT_ID = "artifactId";
    /**
     * 版本
     */
    String VERSION = "version";
    /**
     * 包名
     */
    String PACKAGE = "package";

    /**
     * setting 命令参数
     */
    String SETTING_PARAM_FORMAT = "--settings %s";
    /**
     * setting 命令参数
     */
    String DEFAULT_SETTING_XML = "settings.xml";

    /**
     * 原型构建命令
     */
    String MULTI_MODULE_PROJECT_DIRECTORY = "maven.multiModuleProjectDirectory";

    /**
     * 原型构建命令
     */
    String ARCHETYPE_GENERATE_ARG = "archetype:generate";

    /**
     * 构建参数
     */
    String BUILD_ARG = "-B";

    /**
     * 参数格式
     */
    String ARG_FORMAT = "-D%s=%s";
}
