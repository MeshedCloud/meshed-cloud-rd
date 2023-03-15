package cn.meshed.cloud.rd.project.web;

import cn.meshed.cloud.rd.domain.project.ability.DomainAbility;
import cn.meshed.cloud.rd.project.DomainAdapter;
import cn.meshed.cloud.rd.project.command.DomainCmd;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * <h1>领域Web适配器</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
public class DomainWebAdapter implements DomainAdapter {

    private final DomainAbility domainAbility;

    /**
     * 领域统计
     *
     * @param projectKey 项目key
     * @return {@link SingleResponse<List<String>>}
     */
    @Override
    public SingleResponse<Set<String>> select(String projectKey) {
        return domainAbility.select(projectKey);
    }

    /**
     * 领域统计
     *
     * @param domainCmd 项目key
     * @return {@link Response}
     */
    @Override
    public Response add(@Valid DomainCmd domainCmd) {
        return domainAbility.add(domainCmd);
    }

    /**
     * 检查领域标识是否可用
     *
     * @param key 领域key
     * @return {@link Response}
     */
    @Override
    public Response availableKey(String key) {
        return domainAbility.availableKey(key);
    }
}
