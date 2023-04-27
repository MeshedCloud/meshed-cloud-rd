package cn.meshed.cloud.rd.domain.log.ability;

import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.rd.log.data.TrendDTO;
import cn.meshed.cloud.rd.log.query.TrendPageQry;
import com.alibaba.cola.dto.PageResponse;

/**
 * <h1>动态能力</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface TrendLogAbility extends IPageList<TrendPageQry, PageResponse<TrendDTO>> {
}
