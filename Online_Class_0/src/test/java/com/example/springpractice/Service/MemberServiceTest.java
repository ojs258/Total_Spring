package com.example.springpractice.Service;

import com.example.springpractice.domain.Member;
import com.example.springpractice.repository.MemoryMemberRepository;
import com.example.springpractice.repository.MemoryMemberRepositoryTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {

    MemberService memberService;
    MemoryMemberRepository memberRepository;

    @BeforeEach
    // DI로 구현되어있는 상위 class에 하위 class인 테스트 구현체에서
    // 메소드가 실행 될 때마다 객체를 생성해서 주입해주는 형태
    public void beforeEach(){
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }

    // 테스트 구현체 메소드가 실행될 때 마다 그 후에
    @AfterEach
    public void afterEach(){
        memberRepository.clearStore();
        // store.clear()를 호출해 repository값을 clear해준다.
    }

    @Test
    void 회원가입() {
        //given
        Member member = new Member();
        member.setName("hello");
        //hello라는 이름을 가진 멤버 객체를 하나 생성

        //when
        Long saveId = memberService.join(member);
        // join서비스 메소드를 호출해서 위에서 만든 멤버객체를 저장해줌
        // 리턴되는 값은 방금 저장된 객체의 id(saveId)

        //then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
        // saveId를 매개로 일치하는 객체를 찾아와서 위에 만든 멤버 객체와 이름이 같은지 확인
        // 같으면 True를 반환 => Success
    }

    @Test
    public void 중복_회원_예외(){
        //given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        //when
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> memberService.join(member2));
        // IllegalStateException이 발생하면(중복회원이 발견되면)
        // 해당 에러메세지를 지역변수 e에 반환

        //then
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        //when구문에서 반환된 에러메세지가 "이미 존재하는 회원입니다."와 동일한지 확인

       /* try {
            memberService.join(member2);
            fail();

        }
        //then
        catch (IllegalStateException e){
            assertThat(e.getMessage()).isEqualTo("이미존재하는 회원입니다.");
        }
        */


    }
}