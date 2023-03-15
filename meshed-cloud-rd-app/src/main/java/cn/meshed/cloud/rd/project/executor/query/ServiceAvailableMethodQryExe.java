package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.dto.ShowType;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGateway;
import cn.meshed.cloud.rd.project.query.ServiceAvailableMethodQry;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * <h1>查询方法是否可用</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ServiceAvailableMethodQryExe implements QueryExecute<ServiceAvailableMethodQry, Response> {

    private final ServiceGateway serviceGateway;

    /**
     * <h1>查询执行器</h1>
     *
     * @param serviceAvailableMethodQry 执行器 {@link ServiceAvailableMethodQry}
     * @return {@link Response}
     */
    @Override
    public Response execute(ServiceAvailableMethodQry serviceAvailableMethodQry) {
        AssertUtils.isTrue(StringUtils.isNotBlank(serviceAvailableMethodQry.getGroupId()), "分组编码不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(serviceAvailableMethodQry.getMethod()), "方法名称不能为空");
        return ResultUtils.of(!serviceGateway.existMethodName(serviceAvailableMethodQry.getGroupId(),
                serviceAvailableMethodQry.getMethod()), "方法名称已经存在", ShowType.SILENT);
    }
}
