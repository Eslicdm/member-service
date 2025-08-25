package com.eslirodrigues.service_app_java.service;

import com.eslirodrigues.service_app_java.dto.CreateMemberRequest;
import com.eslirodrigues.service_app_java.entity.Member;
import com.eslirodrigues.service_app_java.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getAllMembersByManagerId(Long managerId) {
        return memberRepository.findAllByManagerId(managerId);
    }

    public Member createMember(
            Long managerId,
            CreateMemberRequest request
    ) {
        Member member = new Member();
        member.setName(request.name());
        member.setEmail(request.email());
        member.setBirthDate(request.birthDate());
        member.setPhoto(request.photo());
        member.setServiceType(request.serviceType());
        member.setManagerId(managerId);

        return memberRepository.save(member);
    }
}