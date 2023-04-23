package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.log.Trend;
import cn.meshed.cloud.rd.domain.project.Domain;
import cn.meshed.cloud.rd.domain.project.gateway.DomainGateway;
import cn.meshed.cloud.rd.project.command.DomainCmd;
import cn.meshed.cloud.utils.CopyUtils;
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
public class DomainCmdExe implements CommandExecute<DomainCmd, Response> {

    private final DomainGateway domainGateway;

    /**
     * @param domainCmd
     * @return
     */
    @Trend(key = "#{domainCmd.projectKey}", content = "新增领域:+#{domainCmd.key}")
    @Override
    public Response execute(DomainCmd domainCmd) {
        if (domainCmd == null || StringUtils.isEmpty(domainCmd.getKey())) {
            return ResultUtils.fail("参数异常");
        }
        if (!domainGateway.existDomainKey(domainCmd.getKey())) {
            Integer save = domainGateway.save(CopyUtils.copy(domainCmd, Domain.class));
            return ResultUtils.of(save > 0, "新增领域异常");
        }
        //已经新增，重复请求，可忽略请求,无需报错
        return ResultUtils.ok();
    }
}
