package cn.meshed.cloud.rd.project.executor.query;

import cn.hutool.core.util.StrUtil;
import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.dto.ShowType;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGroupGateway;
import cn.meshed.cloud.rd.project.query.ServiceByClassNameQry;
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
public class ServiceByClassNameQryExe implements QueryExecute<ServiceByClassNameQry, Response> {

    private final ServiceGroupGateway serviceGroupGateway;

    /**
     * <h1>查询执行器</h1>
     *
     * @param serviceByClassNameQry 执行器 {@link ServiceByClassNameQry}
     * @return {@link Response}
     */
    @Override
    public Response execute(ServiceByClassNameQry serviceByClassNameQry) {
        AssertUtils.isTrue(StringUtils.isNotBlank(serviceByClassNameQry.getProjectKey()), "项目唯一标识不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(serviceByClassNameQry.getControl()), "分组名称不能为空");
        AssertUtils.isTrue(serviceByClassNameQry.getType() != null, "分组类型不能为空");
        //组装成类名
        String className = StrUtil.upperFirst(serviceByClassNameQry.getControl())
                + serviceByClassNameQry.getType().getKey();
        return ResultUtils.of(!serviceGroupGateway
                .existGroupClassName(serviceByClassNameQry.getProjectKey(), className), "类名存在", ShowType.SILENT);
    }
}
