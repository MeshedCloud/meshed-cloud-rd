package cn.meshed.cloud.rd.domain.project.ability;

import cn.meshed.cloud.core.ISelect;
import cn.meshed.cloud.rd.project.command.DomainCmd;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;

import java.util.Set;

/**
 * <h1>领域能力</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface DomainAbility extends ISelect<String, SingleResponse<Set<String>>> {

    /**
     * 领域统计
     *
     * @param domainCmd 项目key
     * @return {@link Response}
     */
    Response add(DomainCmd domainCmd);

    /**
     * 可用领域key
     *
     * @param key 领域key
     * @return 是否可用
     */
    Response availableKey(String key);

}
