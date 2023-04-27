package cn.meshed.cloud.rd.domain.deployment.gateway;

import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.rd.deployment.query.PackagesPageQry;
import cn.meshed.cloud.rd.domain.deployment.Packages;
import com.alibaba.cola.dto.PageResponse;

/**
 * <h1>制品</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface PackagesGateway extends ISave<Packages, Boolean>, IPageList<PackagesPageQry, PageResponse<Packages>> {
}
