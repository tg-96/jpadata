package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testMember() {
        Member member = new Member("MemberA");
        Member saveMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(saveMember.getId()).get();
        assertEquals(findMember.getId(), member.getId());
        assertEquals(findMember.getUsername(), member.getUsername());
        assertEquals(findMember, member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("Member1");
        Member member2 = new Member("Member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        count = memberRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void findByUserNameAndAgeGreaterThan() {
        Member m1 = new Member("memberA", 10);
        Member m2 = new Member("memberC", 20);
        Member m3 = new Member("memberB", 5);
        Member m4 = new Member("memberF", 8);
        Member m5 = new Member("memberD", 2);

        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);
        memberRepository.save(m5);

        List<Member> members2 = memberRepository.findTop3ByOrderByAgeAsc();
        for (Member member : members2) {
            System.out.println(member);
        }

//        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 15);

//        assertThat(members.get(0).getUsername()).isEqualTo("memberA");
//        assertThat(members.get(0).getAge()).isEqualTo(20);
//        assertThat(members.size()).isEqualTo(1);
    }

}