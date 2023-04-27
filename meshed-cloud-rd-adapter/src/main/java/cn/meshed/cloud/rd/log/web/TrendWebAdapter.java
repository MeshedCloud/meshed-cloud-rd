package cn.meshed.cloud.rd.log.web;

import cn.meshed.cloud.rd.domain.log.ability.TrendLogAbility;
import cn.meshed.cloud.rd.log.TrendAdapter;
import cn.meshed.cloud.rd.log.data.TrendDTO;
import cn.meshed.cloud.rd.log.query.TrendPageQry;
import com.alibaba.cola.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
public class TrendWebAdapter implements TrendAdapter {

    private final TrendLogAbility trendLogAbility;

    /**
     * 列表
     *
     * @param projectKey 项目唯一标识
     * @param pageQry    分页参数
     * @return {@link PageResponse<TrendDTO>}
     */
    @Override
    public PageResponse<TrendDTO> list(String projectKey, @Valid TrendPageQry pageQry) {
        pageQry.setProjectKey(projectKey);
        return trendLogAbility.searchPageList(pageQry);
    }
}
