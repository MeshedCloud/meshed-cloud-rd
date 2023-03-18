package cn.meshed.cloud.rd.deployment.executor;

import cn.meshed.cloud.rd.deployment.command.VersionCmd;
import cn.meshed.cloud.rd.deployment.data.VersionDTO;
import cn.meshed.cloud.rd.deployment.executor.command.VersionCmdExe;
import cn.meshed.cloud.rd.deployment.executor.query.VersionPageQryExe;
import cn.meshed.cloud.rd.deployment.query.VersionPageQry;
import cn.meshed.cloud.rd.domain.deployment.ability.VersionAbility;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
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
public class VersionAbilityImpl implements VersionAbility {

    private final VersionPageQryExe versionPageQryExe;
    private final VersionCmdExe versionCmdExe;

    /**
     * 版本列表
     *
     * @param versionPageQry 版本分页查询
     * @return
     */
    @Override
    public PageResponse<VersionDTO> searchPageList(VersionPageQry versionPageQry) {
        return versionPageQryExe.execute(versionPageQry);
    }

    /**
     * 发布版本
     *
     * @param versionCmd 发布版本参数
     * @return {@link Response}
     */
    @Override
    public Response publish(VersionCmd versionCmd) {
        return versionCmdExe.execute(versionCmd);
    }
}
