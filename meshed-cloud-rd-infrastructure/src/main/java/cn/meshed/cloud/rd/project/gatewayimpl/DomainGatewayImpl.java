package cn.meshed.cloud.rd.project.gatewayimpl;

import cn.meshed.cloud.rd.domain.project.Domain;
import cn.meshed.cloud.rd.domain.project.gateway.DomainGateway;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.DomainDO;
import cn.meshed.cloud.rd.project.gatewayimpl.database.mapper.DomainMapper;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
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
public class DomainGatewayImpl implements DomainGateway {

    private final DomainMapper domainMapper;

    /**
     * 新增
     *
     * @param domain
     * @return
     */
    @Transactional
    @Override
    public Integer save(Domain domain) {
        assert domain != null;
        AssertUtils.isTrue(!existDomainKey(domain.getKey()), "领域已存在");
        DomainDO domainDO = CopyUtils.copy(domain, DomainDO.class);
        int insert = domainMapper.insert(domainDO);
        AssertUtils.isTrue(insert > 0, "新增失败");
        return domainDO.getId();
    }

    /**
     * 查询
     *
     * @param projectKey
     * @return
     */
    @Override
    public Set<String> select(String projectKey) {
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "参数请求错误");
        LambdaQueryWrapper<DomainDO> lqw = new LambdaQueryWrapper<>();
        lqw.select(DomainDO::getKey).eq(DomainDO::getProjectKey, projectKey);
        List<DomainDO> domainDOList = domainMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(domainDOList)) {
            return Collections.emptySet();
        }
        return domainDOList.stream().map(DomainDO::getKey).collect(Collectors.toSet());
    }

    /**
     * 判断key是否在领域中存在
     *
     * @param key key
     * @return key
     */
    @Override
    public boolean existDomainKey(String key) {
        assert StringUtils.isNotBlank(key);
        LambdaQueryWrapper<DomainDO> lqw = new LambdaQueryWrapper<>();
        lqw.select(DomainDO::getId).eq(DomainDO::getKey, key);
        return domainMapper.selectCount(lqw) > 0;
    }


}
