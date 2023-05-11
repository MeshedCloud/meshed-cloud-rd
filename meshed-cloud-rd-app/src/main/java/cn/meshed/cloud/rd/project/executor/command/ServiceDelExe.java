package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGateway;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ServiceDelExe implements CommandExecute<String, Response> {

    private final ServiceGateway serviceGateway;

    /**
     * @param uuid
     * @return
     */
    @Transactional
    @Override
    public Response execute(String uuid) {
        AssertUtils.isTrue(StringUtils.isNotBlank(uuid), "服务编码不能为空");
        Service service = serviceGateway.query(uuid);
        AssertUtils.isTrue(service != null, "服务不存在");
        assert service != null;
        AssertUtils.isTrue(ReleaseStatusEnum.EDIT == service.getReleaseStatus(), "非编辑状态无法删除");
        return ResultUtils.of(serviceGateway.delete(uuid), "删除失败");
    }

}
