package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        // Given
        Member member = new Member();
        member.setName("lim");

        // When
        Long saveId = memberService.join(member);

        // Then
        // em.flush();
        assertEquals(member, memberRepository.findOne(saveId));
    }


    @Test
    public void 중복_회원_예외() throws Exception {
        // Given
        Member member = new Member();
        member.setName("lim");

        Member member2 = new Member();
        member2.setName("lim");

        // When
        memberService.join(member);
        memberService.join(member2); // 중복

        // Then
        fail("예외가 발생해야 한다.");
    }
}
