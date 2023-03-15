package cn.meshed.cloud.rd.domain.project.gateway;

import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.core.ISelect;
import cn.meshed.cloud.rd.domain.project.Domain;

import java.util.Set;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface DomainGateway extends ISelect<String, Set<String>>, ISave<Domain, Integer> {

    /**
     * 判断key是否在领域中存在
     *
     * @param key key
     * @return key
     */
    boolean existDomainKey(String key);

}
