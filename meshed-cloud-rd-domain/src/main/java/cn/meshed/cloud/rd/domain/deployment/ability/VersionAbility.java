package cn.meshed.cloud.rd.domain.deployment.ability;

import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.rd.deployment.command.VersionCmd;
import cn.meshed.cloud.rd.deployment.data.VersionDTO;
import cn.meshed.cloud.rd.deployment.query.VersionPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface VersionAbility extends IPageList<VersionPageQry, PageResponse<VersionDTO>> {

    /**
     * 发布版本
     *
     * @param versionCmd 发布版本参数
     * @return {@link Response}
     */
    Response publish(VersionCmd versionCmd);
}
