package cn.meshed.cloud.rd.domain.cli;

import cn.meshed.cloud.utils.AssertUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static cn.meshed.cloud.rd.domain.project.constant.ProjectConstant.INIT_VERSION;

/**
 * <h1>制品信息</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class Artifact {

    /**
     * 分组ID
     */
    private String groupId;

    /**
     * 制品ID
     */
    private String artifactId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 扩展信息
     */
    @Setter(AccessLevel.NONE)
    private Map<String, String> extendedMap;

    public Artifact() {
        this.version = String.valueOf(INIT_VERSION);
        this.extendedMap = new HashMap<>();
    }

    public Artifact(String groupId, String artifactId) {
        this.groupId = groupId;
        this.packageName = groupId;
        this.artifactId = artifactId;
        //todo 版本转换工具
        this.version = String.valueOf(INIT_VERSION);
        this.extendedMap = new HashMap<>();
    }

    public Artifact(String groupId, String artifactId, boolean snapshot) {
        this(groupId, artifactId);
        if (snapshot) {
            this.enableSnapshot();
        }
    }

    /**
     * 设置快照
     */
    public void enableSnapshot() {
        AssertUtils.isTrue(StringUtils.isNotBlank(this.version), "版本不存在/版本配置需要在设置快照前面");
        this.version = this.version + "-SNAPSHOT";
    }

    /**
     * 添加扩展信息
     *
     * @param key
     * @param value
     */
    public void addExtended(String key, String value) {
        AssertUtils.isTrue(StringUtils.isNotBlank(key), "key不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(value), "value不能为空");
        if (this.extendedMap == null) {
            this.extendedMap = new HashMap<>();
        }
        this.extendedMap.put(key, value);
    }

    /**
     * 校验
     */
    public void verification() {
        AssertUtils.isTrue(StringUtils.isNotBlank(this.groupId), "分组ID");
        AssertUtils.isTrue(StringUtils.isNotBlank(this.artifactId), "制品库ID");
        //如果未配置默认为初代快照版本
        if (StringUtils.isBlank(this.version)) {
            this.version = String.valueOf(INIT_VERSION);
            this.enableSnapshot();
        }
        //包名如果不存在则认为同分组ID
        if (StringUtils.isBlank(this.packageName)) {
            this.packageName = this.groupId;
        }
    }
}
