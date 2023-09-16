package study.datajpa.repository;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    EntityManager em;

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

        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 15);

        assertThat(members.get(0).getUsername()).isEqualTo("memberA");
        assertThat(members.get(0).getAge()).isEqualTo(20);
        assertThat(members.size()).isEqualTo(1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 10);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 10);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernames = memberRepository.findUsernameList();
        for (String s : usernames) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Member m1 = new Member("AAA", 10);
        memberRepository.save(m1);

        Team team = new Team("teamA");
        teamRepository.save(team);

        m1.changeTeam(team);

        List<MemberDto> memberDtos = memberRepository.findMemberDto();

        for (MemberDto memberDto : memberDtos) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 10);
        Member m3 = new Member("CCC", 10);
        Member m4 = new Member("DDD", 10);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);

        List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        assertThat(members.get(0).getUsername()).isEqualTo("AAA");
        assertThat(members.get(1).getUsername()).isEqualTo("BBB");
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 10);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //collection으로 받을 경우에는 값이 일치하는 코드가 없다면 빈 List를 줘서 null이 아니지만
        //단건으로 받을때는 일치하는 경우가 없다면 null로 반환한다. 주의!
        List<Member> members = memberRepository.findListByUsername("AAA");
        Member member = memberRepository.findMemberByUsername("AAA");
        Optional<Member> findMember = memberRepository.findOptionalByUsername("asdfsdf");
        assertThat(members.get(0).getUsername()).isEqualTo("AAA");
        assertThat(members.get(1).getUsername()).isEqualTo("BBB");
    }


    @Test
    public void paging() {

        for (int i = 0; i < 10; i++) {
            memberRepository.save(new Member("member" + i, 10));
        }

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        //Slice는 size+1만큼 조회를 해와서 다음 페이지가 있다 없다를 알려준다.
        //Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        //page를 dto로 변환
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        List<Member> content = page.getContent();
        Long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(10);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(4);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {
        for (int i = 1; i <= 5; i++) {
            memberRepository.save(new Member("member" + i, i * 10));
        }
        int resultCount = memberRepository.bulkAgePlus(30);

        AssertionsForClassTypes.assertThat(resultCount).isEqualTo(3);

        //member5에 값은 무엇일까?? DB에 나이를 1씩 업데이트 했으므로 51일까??
        //답은 50이다. bulkAgePlus()가 DB에 바로 업데이트를 했지만
        // 영속성 컨텍스트를 거치지 않았기 때문에 영속성 컨텍스는 값이 50으로 남아 있었다.
        //따라서, member5에 이름을 갖는 Member 엔티티를 가져오려고 했는데 영속성컨텍스트에
        //값이 있었기 때문에 50이라는 값이 리턴된 것이다.
        //이걸 해결하기 위해서는, EntityManger에서 영속성 컨텍스트를 flush로 commit 해주고
        // clear로 영속성 컨텍스트를 날려주어야 한다.
        //영속성 컨텍스트를 날려주었을때 db를 다시 조회하기 때문이다.
        //spring data jpa에서는 @Modifying(clearAutomatically = true) 해주면됨
//        em.flush();
//        em.clear();
        List<Member> result = memberRepository.findByUsername("member5");

        assertThat(result.get(0).getAge()).isEqualTo(51);

    }

    @Test
    public void findMemberLazy(){
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB= new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",10,teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();



    }
}