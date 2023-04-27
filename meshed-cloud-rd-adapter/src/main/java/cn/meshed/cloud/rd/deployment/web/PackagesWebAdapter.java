package cn.meshed.cloud.rd.deployment.web;

import cn.meshed.cloud.rd.deployment.PackagesAdapter;
import cn.meshed.cloud.rd.deployment.command.PackagesCmd;
import cn.meshed.cloud.rd.deployment.data.PackagesDTO;
import cn.meshed.cloud.rd.deployment.query.PackagesPageQry;
import cn.meshed.cloud.rd.domain.deployment.ability.PackagesAbility;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
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
public class PackagesWebAdapter implements PackagesAdapter {

    private final PackagesAbility packagesAbility;

    /**
     * 列表
     *
     * @param packagesPageQry 制品分页查询
     * @return {@link PageResponse <PackagesDTO>}
     */
    @Override
    public PageResponse<PackagesDTO> list(@Valid PackagesPageQry packagesPageQry) {
        return packagesAbility.searchPageList(packagesPageQry);
    }

    /**
     * 保存
     *
     * @param packagesCmd 新增制品
     * @return {@link Response}
     */
    @Override
    public Response save(PackagesCmd packagesCmd) {
        return packagesAbility.save(packagesCmd);
    }
}
