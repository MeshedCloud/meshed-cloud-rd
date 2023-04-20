package cn.meshed.cloud.rd.cli.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.cli.executor.query.ArchetypeTemplateQry;
import cn.meshed.cloud.rd.domain.cli.Archetype;
import cn.meshed.cloud.rd.domain.cli.Artifact;
import cn.meshed.cloud.rd.domain.cli.BuildArchetype;
import cn.meshed.cloud.rd.domain.cli.Skeleton;
import cn.meshed.cloud.rd.domain.cli.gateway.CliGateway;
import cn.meshed.cloud.rd.domain.repo.Branch;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.SysException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static cn.meshed.cloud.rd.domain.repo.constant.RepoConstant.MASTER;
import static cn.meshed.cloud.rd.domain.repo.constant.RepoConstant.WORKSPACE;


/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class SkeletonCmdExe implements CommandExecute<Skeleton, SingleResponse<String>> {

    private final CliGateway cliGateway;
    private final ArchetypeTemplateQry archetypeTemplateQry;


    /**
     * <h1>执行器</h1>
     *
     * @param skeleton 执行器 {@link Skeleton}
     * @return {@link Response}
     */
    @Override
    public SingleResponse<String> execute(Skeleton skeleton) {
        //校验
        skeleton.verification();
        //获取原型模板
        Archetype archetype = archetypeTemplateQry.execute(skeleton.getEngineTemplate());
        if (archetype == null) {
            return ResultUtils.fail("模板不存在");
        }
        Artifact artifact = new Artifact(skeleton.getBasePackage(), skeleton.getRepositoryName(), true);
        artifact.addExtended("domain", "examples");
        artifact.addExtended("projectKey", skeleton.getProjectKey());
        try {
            //构建原型并推送到物理仓库
            String branch = cliGateway.archetypeWithPush(skeleton.getRepositoryId(),
                    new BuildArchetype(archetype, artifact, new Branch(WORKSPACE, MASTER)));
            if (StringUtils.isNotBlank(branch)) {
                return ResultUtils.fail("原型不存在任何代码");
            }
            return ResultUtils.of(branch);
        } catch (SysException sysException) {
            return ResultUtils.fail(sysException.getMessage());
        }
    }
}
