package cn.meshed.cloud.rd.project.executor.query;

import cn.hutool.core.util.StrUtil;
import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.dto.ShowType;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGroupGateway;
import cn.meshed.cloud.rd.project.query.ServiceAvailableClassQry;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * <h1>查询类是否存在</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ServiceGroupAvailableClassQryExe implements QueryExecute<ServiceAvailableClassQry, Response> {

    private final ServiceGroupGateway serviceGroupGateway;

    /**
     * <h1>查询执行器</h1>
     *
     * @param serviceAvailableClassQry 执行器 {@link ServiceAvailableClassQry}
     * @return {@link Response}
     */
    @Override
    public Response execute(ServiceAvailableClassQry serviceAvailableClassQry) {
        AssertUtils.isTrue(StringUtils.isNotBlank(serviceAvailableClassQry.getProjectKey()), "项目唯一标识不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(serviceAvailableClassQry.getKey()), "分组标识不能为空");
        AssertUtils.isTrue(serviceAvailableClassQry.getType() != null, "分组类型不能为空");
        //组装成类名
        String className = StrUtil.upperFirst(serviceAvailableClassQry.getKey())
                + serviceAvailableClassQry.getType().getExt();

        return ResultUtils.of(!serviceGroupGateway
                .existGroupClassName(serviceAvailableClassQry.getProjectKey(), className), "分组标识已经存在", ShowType.SILENT);
    }
}
