package cn.meshed.cloud.rd.log.gatewayimpl;

import cn.meshed.cloud.rd.domain.log.TrendLog;
import cn.meshed.cloud.rd.domain.log.gateway.TrendLogGateway;
import cn.meshed.cloud.rd.log.gatewayimpl.database.dataobject.TrendLogDO;
import cn.meshed.cloud.rd.log.gatewayimpl.database.mapper.TrendLogMapper;
import cn.meshed.cloud.rd.log.query.TrendPageQry;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.PageUtils;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
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
public class TrendLogGatewayImpl implements TrendLogGateway {

    private final TrendLogMapper trendLogMapper;

    /**
     * @param trendLog
     * @return
     */
    @Override
    public Boolean save(TrendLog trendLog) {
        if (StringUtils.isBlank(trendLog.getProjectKey())) {
            return false;
        }
        TrendLogDO trendLogDO = CopyUtils.copy(trendLog, TrendLogDO.class);
        return trendLogMapper.insert(trendLogDO) > 0;
    }

    /**
     * <h1>分页列表</h1>
     *
     * @param pageQry 分页查询 {@link TrendPageQry}
     * @return {@link PageResponse<TrendLog>}
     */
    @Override
    public PageResponse<TrendLog> searchPageList(TrendPageQry pageQry) {
        Page<Object> page = PageUtils.startPage(pageQry);
        LambdaQueryWrapper<TrendLogDO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(pageQry.getProjectKey()), TrendLogDO::getProjectKey, pageQry.getProjectKey());
        return PageUtils.of(trendLogMapper.selectList(lqw), page, TrendLog::new);
    }
}
