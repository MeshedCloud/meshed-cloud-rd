package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.dto.ShowType;
import cn.meshed.cloud.rd.domain.project.gateway.DomainGateway;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class DomainAvailableKeyQryExe implements QueryExecute<String, Response> {

    private final DomainGateway domainGateway;

    /**
     * @param projectKey
     * @return
     */
    @Override
    public Response execute(String projectKey) {
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "项目唯一标识不能为空");
        return ResultUtils.of(!domainGateway.existDomainKey(projectKey), "领域标识已经存在", ShowType.SILENT);
    }
}
