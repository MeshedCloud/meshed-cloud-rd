package cn.meshed.cloud.rd.domain.deployment.ability;

import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.rd.deployment.command.PackagesCmd;
import cn.meshed.cloud.rd.deployment.data.PackagesDTO;
import cn.meshed.cloud.rd.deployment.query.PackagesPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;

/**
 * <h1>制品库</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface PackagesAbility extends IPageList<PackagesPageQry, PageResponse<PackagesDTO>>, ISave<PackagesCmd, Response> {
}
