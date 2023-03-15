package cn.meshed.cloud.rd.project.executor;

import cn.meshed.cloud.rd.domain.project.ability.DomainAbility;
import cn.meshed.cloud.rd.project.command.DomainCmd;
import cn.meshed.cloud.rd.project.executor.command.DomainCmdExe;
import cn.meshed.cloud.rd.project.executor.query.DomainAvailableKeyQryExe;
import cn.meshed.cloud.rd.project.executor.query.DomainSelectQryExe;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * <h1>领域能力实现</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class DomainAbilityImpl implements DomainAbility {

    private final DomainCmdExe domainCmdExe;
    private final DomainAvailableKeyQryExe domainAvailableKeyQryExe;
    private final DomainSelectQryExe domainSelectQryExe;

    /**
     * 领域统计
     *
     * @param projectKey 项目key
     * @return {@link SingleResponse < List <String>>}
     */
    @Override
    public SingleResponse<Set<String>> select(String projectKey) {
        return domainSelectQryExe.execute(projectKey);
    }

    /**
     * 领域统计
     *
     * @param domainCmd 项目key
     * @return {@link Response}
     */
    @Override
    public Response add(DomainCmd domainCmd) {
        return domainCmdExe.execute(domainCmd);
    }

    /**
     * 可用领域key
     *
     * @param key 领域key
     * @return 是否可用
     */
    @Override
    public Response availableKey(String key) {
        return domainAvailableKeyQryExe.execute(key);
    }
}
