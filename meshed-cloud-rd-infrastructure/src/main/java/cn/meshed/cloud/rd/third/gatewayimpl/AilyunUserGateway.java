package cn.meshed.cloud.rd.third.gatewayimpl;

import cn.meshed.cloud.rd.domain.third.ThirdUser;
import cn.meshed.cloud.rd.domain.third.gateway.ThirdUserGateway;
import cn.meshed.cloud.utils.AssertUtils;
import com.alibaba.cola.exception.SysException;
import com.aliyun.ram20150501.Client;
import com.aliyun.ram20150501.models.CreateLoginProfileRequest;
import com.aliyun.ram20150501.models.CreateUserRequest;
import com.aliyun.ram20150501.models.CreateUserResponse;
import com.aliyun.ram20150501.models.CreateUserResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class AilyunUserGateway implements ThirdUserGateway {

    private final Client client;

    /**
     * <h1>保存对象</h1>
     *
     * @param newThirdUser 第三方用户
     * @return {@link ThirdUser}
     */
    @Override
    public ThirdUser save(ThirdUser newThirdUser) {
        //创建账号
        ThirdUser thirdUser = createThirdUser(newThirdUser);
        //开启控制台登入
        initiateThirdUserLogin(newThirdUser);
        return thirdUser;
    }


    @NotNull
    private void initiateThirdUserLogin(ThirdUser newThirdUser) {
        CreateLoginProfileRequest createLoginProfileRequest = new CreateLoginProfileRequest();
        createLoginProfileRequest.setUserName(newThirdUser.getUserName());
        AssertUtils.isTrue(StringUtils.isNotBlank(newThirdUser.getInitPassword()), "初始密码不能为空");
        createLoginProfileRequest.setPassword(newThirdUser.getInitPassword());
        createLoginProfileRequest.setPasswordResetRequired(newThirdUser.isPasswordResetRequired());
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.createLoginProfileWithOptions(createLoginProfileRequest, runtime);
            return;
        } catch (TeaException error) {
            // 如有需要，请打印 error
            Common.assertAsString(error.message);
        } catch (Exception error) {
            TeaException teaException = new TeaException(error.getMessage(), error);
            // 如有需要，请打印 error
            Common.assertAsString(teaException.message);
        }
        throw new SysException("第三方账号创建失败");
    }

    @NotNull
    private ThirdUser createThirdUser(ThirdUser newThirdUser) {
        try {

            CreateUserRequest createUserRequest = buildCreateUserRequest(newThirdUser);
            RuntimeOptions runtime = new RuntimeOptions();
            // 复制代码运行请自行打印 API 的返回值
            CreateUserResponse createUserResponse = client.createUserWithOptions(createUserRequest, runtime);
            CreateUserResponseBody responseBody = createUserResponse.getBody();
            AssertUtils.isTrue(responseBody != null, "请求失败");
            CreateUserResponseBody.CreateUserResponseBodyUser user = responseBody.getUser();
            return getThirdUser(user);
        } catch (TeaException error) {
            // 如有需要，请打印 error
            Common.assertAsString(error.message);
            throw new SysException("第三方账号创建失败:" + error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            Common.assertAsString(error.message);
            throw new SysException("第三方账号创建失败:" + error.message);
        }
    }

    @NotNull
    private ThirdUser getThirdUser(CreateUserResponseBody.CreateUserResponseBodyUser user) {
        AssertUtils.isTrue(user != null, "第三方账号创建失败");
        ThirdUser thirdUser = new ThirdUser();
        assert user != null;
        thirdUser.setUserId(user.getUserId());
        thirdUser.setUserName(user.getUserName());
        thirdUser.setDisplayName(user.getDisplayName());
        thirdUser.setEmail(user.getEmail());
        thirdUser.setMobilePhone(user.getMobilePhone());
        thirdUser.setComments(user.getComments());
        return thirdUser;
    }

    @NotNull
    private CreateUserRequest buildCreateUserRequest(ThirdUser newThirdUser) {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUserName(newThirdUser.getUserName());
        createUserRequest.setDisplayName(newThirdUser.getDisplayName());
        createUserRequest.setEmail(newThirdUser.getEmail());
        createUserRequest.setMobilePhone(newThirdUser.getMobilePhone());
        createUserRequest.setComments(newThirdUser.getComments());
        return createUserRequest;
    }
}
