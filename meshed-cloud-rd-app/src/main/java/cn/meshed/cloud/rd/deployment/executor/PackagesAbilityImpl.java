package cn.meshed.cloud.rd.deployment.executor;

import cn.meshed.cloud.rd.deployment.command.PackagesCmd;
import cn.meshed.cloud.rd.deployment.data.PackagesDTO;
import cn.meshed.cloud.rd.deployment.executor.command.PackagesCmdExe;
import cn.meshed.cloud.rd.deployment.executor.query.PackagesPageQryExe;
import cn.meshed.cloud.rd.deployment.query.PackagesPageQry;
import cn.meshed.cloud.rd.domain.deployment.ability.PackagesAbility;
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
public class PackagesAbilityImpl implements PackagesAbility {

    private final PackagesPageQryExe packagesPageQryExe;
    private final PackagesCmdExe packagesCmdExe;

    /**
     * <h1>分页列表</h1>
     *
     * @param pageQry 分页查询 {@link PackagesPageQry}
     * @return {@link PageResponse<PackagesDTO>}
     */
    @Override
    public PageResponse<PackagesDTO> searchPageList(PackagesPageQry pageQry) {
        return packagesPageQryExe.execute(pageQry);
    }

    /**
     * <h1>保存对象</h1>
     *
     * @param packagesCmd
     * @return {@link Response}
     */
    @Override
    public Response save(PackagesCmd packagesCmd) {
        return packagesCmdExe.execute(packagesCmd);
    }
}
