package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

//spring data jpa와 연도시키기 위해서는 인터페이스 명은 상관없고
//구현체 이름을 repository명+Impl을 꼭 맞춰줘야 한다.
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
