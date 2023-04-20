package cn.meshed.cloud.rd.domain.project.gateway;

import cn.meshed.cloud.core.IBatchSave;
import cn.meshed.cloud.rd.domain.project.EnumValue;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <h1>枚举值网关</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface EnumValueGateway extends IBatchSave<Set<EnumValue>, Boolean> {

    /**
     * 得到枚举值列表
     *
     * @param mId 模型ID
     * @return {@link List<EnumValue>}
     */
    Set<EnumValue> getEnumValues(String mId);

    /**
     * 得到枚举值列表
     *
     * @param mIds 模型ID列表
     * @return {@link Map<String,List<EnumValue>>}
     */
    Map<String, Set<EnumValue>> getEnumValues(Set<String> mIds);
}
