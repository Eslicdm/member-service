package com.eslirodrigues.service_app_java.repository;

import com.eslirodrigues.service_app_java.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findAllByManagerId(Long managerId);
}