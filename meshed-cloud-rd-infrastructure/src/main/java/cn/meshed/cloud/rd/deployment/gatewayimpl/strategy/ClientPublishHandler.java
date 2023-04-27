package cn.meshed.cloud.rd.deployment.gatewayimpl.strategy;

import cn.meshed.cloud.rd.domain.deployment.strategy.AsyncPublishStrategy;
import cn.meshed.cloud.rd.domain.deployment.strategy.Publish;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishHandler;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishType;
import cn.meshed.cloud.rd.domain.deployment.strategy.dto.ModelPublish;
import cn.meshed.cloud.rd.domain.deployment.strategy.dto.ServicePublish;
import cn.meshed.cloud.rd.domain.repo.Branch;
import cn.meshed.cloud.rd.domain.repo.gateway.RepositoryGateway;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static cn.meshed.cloud.rd.domain.common.constant.Constant.SRC_PATH;
import static cn.meshed.cloud.rd.domain.repo.constant.RepoConstant.DEVELOP;
import static cn.meshed.cloud.rd.domain.repo.constant.RepoConstant.WORKSPACE;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ClientPublishHandler implements PublishHandler<Publish> {

    private final AsyncPublishStrategy asyncPublishStrategy;
    private final RepositoryGateway repositoryGateway;

    /**
     * 注册类型
     *
     * @return PublishType
     */
    @Override
    public PublishType getPublishType() {
        return PublishType.CLIENT;
    }

    /**
     * 发布客户端
     *
     * @param publish 客户端发布数据包
     */
    @Override
    public void publish(Publish publish) {
        String projectKey = publish.getProjectKey();
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "项目key不允许为空");
        Branch branch = new Branch(WORKSPACE, DEVELOP);
        repositoryGateway.rebuildBranch(publish.getSourceId(), branch);
        //发布模型
        ModelPublish modelPublish = CopyUtils.copy(publish, ModelPublish.class);
        modelPublish.setBasePath(SRC_PATH);
        modelPublish.setBranch(branch);
        asyncPublishStrategy.asyncPublish(PublishType.MODEL, modelPublish);
        //发布枚举 （枚举是特殊模型，可复用模型信息）
        asyncPublishStrategy.asyncPublish(PublishType.ENUM, modelPublish);
        //发布服务
        ServicePublish servicePublish = CopyUtils.copy(publish, ServicePublish.class);
        servicePublish.setBasePath(SRC_PATH);
        servicePublish.setBranch(branch);
        asyncPublishStrategy.asyncPublish(PublishType.SERVICE, servicePublish);
    }
}
