package cn.meshed.cloud.rd.project.gatewayimpl;

import cn.meshed.cloud.rd.domain.project.EnumValue;
import cn.meshed.cloud.rd.domain.project.gateway.EnumValueGateway;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.EnumValueDO;
import cn.meshed.cloud.rd.project.gatewayimpl.database.mapper.EnumValueMapper;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class EnumValueGatewayImpl implements EnumValueGateway {

    private final EnumValueMapper enumValueMapper;

    /**
     * 得到枚举值列表
     *
     * @param mId 模型ID
     * @return {@link List < EnumValue >}
     */
    @Override
    public Set<EnumValue> getEnumValues(String mId) {
        AssertUtils.isTrue(StringUtils.isNotBlank(mId), "模型ID不能为空");
        LambdaQueryWrapper<EnumValueDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(EnumValueDO::getMId, mId);
        List<EnumValueDO> enumValues = enumValueMapper.selectList(lqw);
        return CopyUtils.copySetProperties(enumValues, EnumValue::new);
    }

    /**
     * 得到枚举值列表
     *
     * @param mIds 模型ID列表
     * @return {@link Map <String,List<EnumValue>>}
     */
    @Override
    public Map<String, Set<EnumValue>> getEnumValues(Set<String> mIds) {
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(mIds), "模型ID不能为空");
        LambdaQueryWrapper<EnumValueDO> lqw = new LambdaQueryWrapper<>();
        lqw.in(EnumValueDO::getMId, mIds);
        List<EnumValueDO> enumValues = enumValueMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(enumValues)) {
            return new HashMap<>(0);
        }
        //转换进行分组
        List<EnumValue> values = CopyUtils.copyListProperties(enumValues, EnumValue::new);
        return values.stream().collect(Collectors.groupingBy(EnumValue::getMId, Collectors.toSet()));
    }

    /**
     * <h1>批量保存对象</h1>
     * 删除旧数据新增新数据
     *
     * @param enumValues 枚举对象列表
     * @return {@link Boolean}
     */
    @Override
    public Boolean saveBatch(Set<EnumValue> enumValues) {
        if (CollectionUtils.isEmpty(enumValues)) {
            return false;
        }
        Set<String> mIds = enumValues.stream()
                .map(EnumValue::getMId).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        //先删除列表
        delEnumValueByModelIds(mIds);
        List<EnumValueDO> enumValueList = CopyUtils.copyListProperties(enumValues, EnumValueDO::new);
        //新增保存
        return enumValueMapper.insertBatch(enumValueList) > 0;
    }

    /**
     * 根据模型ID列表批量删除枚举值信息
     *
     * @param mIds 模型ID列表
     */
    private void delEnumValueByModelIds(Set<String> mIds) {
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(mIds), "模型ID列表不能为空");
        LambdaQueryWrapper<EnumValueDO> lqw = new LambdaQueryWrapper<>();
        lqw.in(EnumValueDO::getMId, mIds);
        //由于新增和修改均会执行删除逻辑，无需确认是否删除
        enumValueMapper.delete(lqw);
    }
}
