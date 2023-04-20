package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.iam.account.data.UserDTO;
import cn.meshed.cloud.rd.domain.project.Member;
import cn.meshed.cloud.rd.domain.project.gateway.MemberGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectMemberGateway;
import cn.meshed.cloud.rd.project.data.MemberDTO;
import cn.meshed.cloud.rd.project.query.MemberPageQry;
import cn.meshed.cloud.rd.wrapper.user.UserWrapper;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
public class MemberPageQryExe implements QueryExecute<MemberPageQry, PageResponse<MemberDTO>> {

    private final MemberGateway memberGateway;
    private final ProjectMemberGateway projectMemberGateway;
    private final UserWrapper userWrapper;

    /**
     * <h1>查询执行器</h1>
     *
     * @param pageQry 分页参数 {@link MemberPageQry}
     * @return {@link PageResponse<MemberDTO>}
     */
    @Override
    public PageResponse<MemberDTO> execute(MemberPageQry pageQry) {
        PageResponse<Member> pageResponse = memberGateway.searchPageList(pageQry);
        if (pageResponse.isEmpty()) {
            return ResultUtils.copyPage(pageResponse, MemberDTO::new);
        }
        Set<Long> uidSet = pageResponse.getData().stream().map(Member::getUid).collect(Collectors.toSet());
        Map<Long, UserDTO> userMap = userWrapper.getUserMap(uidSet);
        Set<Long> present = new HashSet<>();
        if (pageQry != null && pageQry.getQueryDisabled() != null && pageQry.getQueryDisabled()) {
            present = projectMemberGateway.filterPresentByUserId(uidSet);
        }
        Set<Long> finalPresent = present;
        List<MemberDTO> list = pageResponse.getData().stream()
                .map(member -> toMemberDTO(member, userMap.get(member.getUid()),
                        finalPresent.contains(member.getUid())))
                .collect(Collectors.toList());

        return PageResponse.of(list, pageResponse.getTotalPages(), pageQry.getPageSize(), pageQry.getPageIndex());
    }

    private MemberDTO toMemberDTO(Member member, UserDTO userDTO, boolean disabled) {
        MemberDTO memberDTO = null;
        if (userDTO != null) {
            memberDTO = CopyUtils.copy(userDTO, MemberDTO.class);
        } else {
            memberDTO = new MemberDTO();
            memberDTO.setName("已注销");
        }
        memberDTO.setId(member.getId());
        memberDTO.setUid(member.getUid());
        memberDTO.setThirdUid(member.getThirdUid());
        memberDTO.setDisabled(disabled);
        return memberDTO;
    }
}
