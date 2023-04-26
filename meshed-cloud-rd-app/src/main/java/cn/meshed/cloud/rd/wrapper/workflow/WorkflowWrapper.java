package cn.meshed.cloud.rd.wrapper.workflow;

import cn.meshed.cloud.dto.SecurityEvent;
import cn.meshed.cloud.rd.project.config.WorkflowProperties;
import cn.meshed.cloud.stream.EventInject;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.workflow.flow.FlowRpc;
import cn.meshed.cloud.workflow.flow.command.InitiateCmd;
import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.SysException;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class WorkflowWrapper {

    private final WorkflowProperties workflowProperties;
    @DubboReference
    private FlowRpc flowRpc;

    @EventInject
    public String initiate(String key, SecurityEvent securityEvent) {
        AssertUtils.isTrue(StringUtils.isNotBlank(key), "流程标识不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(workflowProperties.getTenantId()), "流程配置缺失：Tenant");
        InitiateCmd initiateCmd = new InitiateCmd();
        initiateCmd.setTenantId(workflowProperties.getTenantId());
        initiateCmd.setKey(key);
        if (securityEvent != null) {
            initiateCmd.setParam(JSONObject.parseObject(JSONObject.toJSONString(securityEvent), Map.class));
        }

        SingleResponse<String> response = flowRpc.initiate(initiateCmd);
        if (!response.isSuccess()) {
            throw new SysException(response.getErrMessage());
        }
        return response.getData();
    }
}
