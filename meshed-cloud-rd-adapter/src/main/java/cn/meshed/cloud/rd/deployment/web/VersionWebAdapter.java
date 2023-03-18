package cn.meshed.cloud.rd.deployment.web;

import cn.meshed.cloud.rd.deployment.VersionAdapter;
import cn.meshed.cloud.rd.deployment.command.VersionCmd;
import cn.meshed.cloud.rd.deployment.data.VersionDTO;
import cn.meshed.cloud.rd.deployment.query.VersionPageQry;
import cn.meshed.cloud.rd.domain.deployment.ability.VersionAbility;
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
public class VersionWebAdapter implements VersionAdapter {

    private final VersionAbility versionAbility;

    /**
     * 版本列表
     *
     * @param projectKey     项目key
     * @param versionPageQry 版本分页查询
     * @return
     */
    @Override
    public PageResponse<VersionDTO> list(String projectKey, @Valid VersionPageQry versionPageQry) {
        versionPageQry.setProjectKey(projectKey);
        return versionAbility.searchPageList(versionPageQry);
    }

    /**
     * 发布版本
     *
     * @param versionCmd 发布版本参数
     * @return
     */
    @Override
    public Response publish(@Valid VersionCmd versionCmd) {
        return versionAbility.publish(versionCmd);
    }
}
