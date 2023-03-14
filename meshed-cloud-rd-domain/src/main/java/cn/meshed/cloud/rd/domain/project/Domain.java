package cn.meshed.cloud.rd.domain.project;

import cn.meshed.cloud.utils.AssertUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * <h1>领域</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Domain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;

    /**
     * 领域唯一值
     */
    private String key;

    /**
     * 领域名称
     */
    private String name;

    /**
     * 所属项目key
     */
    private String projectKey;


    public void setKey(String key) {
        AssertUtils.isTrue(StringUtils.isNotBlank(key), "领域唯一标识不能为空");
        this.key = StringUtils.upperCase(key);
    }

    public void setProjectKey(String projectKey) {
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "项目唯一标识不能为空");
        this.projectKey = StringUtils.upperCase(projectKey);
    }

}
