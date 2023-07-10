package com.example.inflearnjpapart2.api;

import com.example.inflearnjpapart2.domain.Member;
import com.example.inflearnjpapart2.request.CreateMemberRequest;
import com.example.inflearnjpapart2.response.CreateMemberResponse;
import com.example.inflearnjpapart2.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

}
