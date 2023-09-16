package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3ByOrderByAgeAsc();

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id,m.username,t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username);

    Member findMemberByUsername(String username);

    Optional<Member> findOptionalByUsername(String username);

    //query를 counting 할때조차 조인되어 있는 team까지 불러오면서 카운팅하면 성능이
    //매우 느려질 수 있으므로, 나눠서 쿼리를 짠다.
    @Query(value = "select m from Member m left join m.team t"
            , countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    //modifying이 없으면 일반적인 조회에서 처럼 getSingleResult를 실행하게 되기때문에
    //modifying을 적어주어서 excuteUpdate()를 실행하게 해준다.
    //@Modifying(clearAutomatically = true)은
    // db를 update한후 자동으로 영속성 컨텍스트를 clear 해준다.
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    List<Member> findByUsername(String username);
}
