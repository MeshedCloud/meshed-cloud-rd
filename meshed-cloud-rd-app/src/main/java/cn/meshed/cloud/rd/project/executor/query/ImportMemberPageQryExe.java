package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.iam.account.data.AccountDTO;
import cn.meshed.cloud.iam.account.enums.AccountStatusEnum;
import cn.meshed.cloud.iam.account.query.AccountPageQry;
import cn.meshed.cloud.rd.domain.project.gateway.MemberGateway;
import cn.meshed.cloud.rd.project.data.MemberDTO;
import cn.meshed.cloud.rd.project.query.ImportMemberPageQry;
import cn.meshed.cloud.rd.wrapper.user.UserWrapper;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ImportMemberPageQryExe implements QueryExecute<ImportMemberPageQry, PageResponse<MemberDTO>> {

    private final UserWrapper userWrapper;
    private final MemberGateway memberGateway;

    /**
     * @param pageQry
     * @return
     */
    @Override
    public PageResponse<MemberDTO> execute(ImportMemberPageQry pageQry) {
        AccountPageQry accountPageQry = new AccountPageQry();
        accountPageQry.setStatus(AccountStatusEnum.VALID);
        accountPageQry.setPageIndex(pageQry.getPageIndex());
        accountPageQry.setPageSize(pageQry.getPageSize());
        PageResponse<AccountDTO> pageResponse = userWrapper.list(accountPageQry);
        if (pageResponse.isEmpty()) {
            return ResultUtils.copyPage(pageResponse, MemberDTO::new);
        }
        Set<Long> uidSet = pageResponse.getData().stream().map(AccountDTO::getId).collect(Collectors.toSet());
        Set<Long> present = memberGateway.filterPresentByUserId(uidSet);
        List<MemberDTO> list = pageResponse.getData().stream()
                .map(accountDTO -> toMemberDTO(accountDTO, present.contains(accountDTO.getId()))).collect(Collectors.toList());

        return PageResponse.of(list, pageResponse.getTotalCount(), pageQry.getPageSize(), pageQry.getPageIndex());
    }

    private MemberDTO toMemberDTO(AccountDTO accountDTO, boolean disabled) {
        MemberDTO dto = CopyUtils.copy(accountDTO, MemberDTO.class);
        dto.setUid(accountDTO.getId());
        dto.setDisabled(disabled);
        return dto;
    }
}
