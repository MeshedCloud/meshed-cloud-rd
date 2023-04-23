package cn.meshed.cloud.rd.cli.gatewayimpl.strategy;

import cn.meshed.cloud.rd.domain.cli.Archetype;
import cn.meshed.cloud.rd.domain.cli.Artifact;
import cn.meshed.cloud.rd.domain.cli.BuildArchetype;
import cn.meshed.cloud.rd.domain.cli.EngineTemplate;
import cn.meshed.cloud.rd.domain.cli.Skeleton;
import cn.meshed.cloud.rd.domain.cli.gateway.CliGateway;
import cn.meshed.cloud.rd.domain.cli.strategy.SkeletonEngine;
import cn.meshed.cloud.rd.domain.cli.strategy.SkeletonType;
import cn.meshed.cloud.rd.domain.repo.Branch;
import cn.meshed.cloud.utils.AssertUtils;
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
public class MavenSkeleton implements SkeletonEngine {

    private final CliGateway cliGateway;

    /**
     * 脚手架类型
     *
     * @return SkeletonType
     */
    @Override
    public SkeletonType getType() {
        return SkeletonType.MAVEN;
    }

    /**
     * 构建
     *
     * @param skeleton 数据包
     */
    @Override
    public void build(Skeleton skeleton) {
//校验
        skeleton.verification();
        //获取原型模板
        Archetype archetype = getArchetype(skeleton.getEngineTemplate());
        if (archetype == null) {
            throw new SysException("模板不存在");
        }
        Artifact artifact = new Artifact(skeleton.getBasePackage(), skeleton.getRepositoryName(), true);
        artifact.addExtended("domain", "examples");
        artifact.addExtended("projectKey", skeleton.getProjectKey());
        //构建原型并推送到物理仓库
        String branch = cliGateway.archetypeWithPush(skeleton.getRepositoryId(),
                new BuildArchetype(archetype, artifact, new Branch(WORKSPACE, MASTER)));
        if (StringUtils.isBlank(branch)) {
            throw new SysException("原型不存在任何代码");
        }
    }

    private Archetype getArchetype(EngineTemplate engineTemplate) {
        AssertUtils.isTrue(engineTemplate != null, "模板不存在");
        Archetype archetype = new Archetype();
        assert engineTemplate != null;
        archetype.setArchetypeArtifactId(engineTemplate.getId());
        archetype.setArchetypeGroupId(engineTemplate.getOrigin());
        archetype.setArchetypeVersion(engineTemplate.getTag());
        return archetype;
    }


}
