package cn.meshed.cloud.rd.wrapper.user;

import cn.meshed.cloud.iam.account.UserRpc;
import cn.meshed.cloud.iam.account.data.AccountDTO;
import cn.meshed.cloud.iam.account.data.UserDTO;
import cn.meshed.cloud.iam.account.query.AccountPageQry;
import cn.meshed.cloud.iam.account.query.UserByOneQry;
import cn.meshed.cloud.iam.account.query.UserQry;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Component
public class UserWrapper {

    @DubboReference
    private UserRpc userRpc;

    public UserDTO getUser(Long id) {
        UserByOneQry userByOneQry = new UserByOneQry();
        userByOneQry.setId(id);
        userByOneQry.setHasGrantedAuthority(false);
        SingleResponse<UserDTO> userInfo = userRpc.getUserInfo(userByOneQry);
        if (userInfo.isSuccess()) {
            return userInfo.getData();
        }
        return null;
    }

    public Map<Long, UserDTO> getUserMap(Set<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            UserQry userQry = new UserQry();
            userQry.setIds(ids);
            MultiResponse<UserDTO> userList = userRpc.getUserList(userQry);
            if (userList.isSuccess()) {
                return userList.getData()
                        .stream().collect(Collectors.toMap(UserDTO::getId, Function.identity()));
            }
        }
        return new HashMap<>();
    }

    public PageResponse<AccountDTO> list(AccountPageQry accountPageQry) {
        return userRpc.list(accountPageQry);
    }
}
