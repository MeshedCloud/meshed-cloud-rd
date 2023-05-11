package cn.meshed.cloud.rd.deployment.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.deployment.command.PackagesCmd;
import cn.meshed.cloud.rd.deployment.enums.PackagesTypeEnum;
import cn.meshed.cloud.rd.domain.deployment.Packages;
import cn.meshed.cloud.rd.domain.deployment.gateway.PackagesGateway;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class PackagesCmdExe implements CommandExecute<PackagesCmd, Response> {

    private final PackagesGateway packagesGateway;
    private final ProjectGateway projectGateway;

    /**
     * <h1>执行器</h1>
     *
     * @param packagesCmd 执行器 {@link PackagesCmd}
     * @return {@link Response}
     */
    @Override
    public Response execute(PackagesCmd packagesCmd) {

        AssertUtils.isTrue(StringUtils.isNotBlank(packagesCmd.getGroupId()), "分组不能为空");
        String key = packagesCmd.getGroupId().substring(packagesCmd.getGroupId().lastIndexOf("."));

        if (StringUtils.isNotBlank(key)) {
            key = key.toUpperCase();
            Project project = projectGateway.queryByKey(key);
            if (project == null) {
                key = "UNKNOWN";
            }
        }

        Packages packages = CopyUtils.copy(packagesCmd, Packages.class);
        packages.setProjectKey(key);
        packages.setType(PackagesTypeEnum.MAVEN);

        return ResultUtils.of(packagesGateway.save(packages), "保存失败");
    }
}
