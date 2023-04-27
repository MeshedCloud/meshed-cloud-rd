package cn.meshed.cloud.rd.log.executor;

import cn.meshed.cloud.rd.domain.log.ability.TrendLogAbility;
import cn.meshed.cloud.rd.log.data.TrendDTO;
import cn.meshed.cloud.rd.log.executor.query.TrendPageQryExe;
import cn.meshed.cloud.rd.log.query.TrendPageQry;
import com.alibaba.cola.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class TrendLogAbilityImpl implements TrendLogAbility {

    private final TrendPageQryExe trendPageQryExe;

    /**
     * <h1>分页列表</h1>
     *
     * @param pageQry 分页查询 {@link TrendPageQry}
     * @return {@link PageResponse<TrendDTO>}
     */
    @Override
    public PageResponse<TrendDTO> searchPageList(TrendPageQry pageQry) {
        return trendPageQryExe.execute(pageQry);
    }
}
