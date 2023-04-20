package cn.meshed.cloud.rd.domain.project;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * <h1>枚举数据</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class EnumValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模型ID
     */
    private String mId;

    /**
     * 枚举名称
     */
    private String name;

    /**
     * 枚举值
     */
    private Integer value;

    /**
     * 枚举扩展数据
     */
    private String ext;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnumValue)) {
            return false;
        }
        EnumValue enumValue = (EnumValue) o;
        return getName().equals(enumValue.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
