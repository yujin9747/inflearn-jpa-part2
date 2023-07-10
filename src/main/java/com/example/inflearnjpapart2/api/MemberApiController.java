package com.example.inflearnjpapart2.api;

import com.example.inflearnjpapart2.domain.Address;
import com.example.inflearnjpapart2.domain.Member;
import com.example.inflearnjpapart2.dto.MemberDTO;
import com.example.inflearnjpapart2.request.CreateMemberRequest;
import com.example.inflearnjpapart2.request.UpdateMemberRequest;
import com.example.inflearnjpapart2.response.CreateMemberResponse;
import com.example.inflearnjpapart2.response.Result;
import com.example.inflearnjpapart2.response.UpdateMemberResponse;
import com.example.inflearnjpapart2.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@ResponseBody // data를 json이나 xml로 바꿔서 보내준다.
@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    // @RequestBody : JSON을 자바 객체로 변환해준다.
    // v1 문제점 : 엔티티를 DTO로 사용하면 큰 장애를 일으킬 수 있다. => api 스팩이 쉽게 변할 수 있음.
    // v1은 장점보다 위험성이 더 크다.
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // v2 : 엔티티 대신에 DTO를 사용한다.
    // v2의 장점 : 엔티티가 변해도 api 스팩에 영향을 주지 않는다.
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        Address address = new Address();
        address.setCity(request.getCity());
        address.setStreet(request.getStreet());
        address.setZipcode(request.getZipcode());
        member.setAddress(address);

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
//        return new UpdateMemberResponse(id, request.getName()); update와 query를 분리하자.

        // update된 결과를 db에서 query를 통해 다시 가져온다. -> 유지보수성 증대
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @GetMapping("/api/v1/members")
    public List<Member> findMemberV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result findMemberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDTO> collect = findMembers.stream().map(m -> new MemberDTO(m.getName())).collect(Collectors.toList());
        return new Result(collect.size(), collect ); // json 배열타입으로 나가면 유연성이 많이 떨어지므로 한번 감싸서 보내준다.
    }
}
