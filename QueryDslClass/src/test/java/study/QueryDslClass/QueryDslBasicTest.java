package study.QueryDslClass;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.QueryDslClass.entity.Member;
import study.QueryDslClass.entity.Team;

import static org.assertj.core.api.Assertions.assertThat;
import static study.QueryDslClass.entity.QMember.member;

@SpringBootTest
@Transactional
public class QueryDslBasicTest {

    @Autowired
    EntityManager em;
    JPAQueryFactory query;



    @BeforeEach
    public void before() {
        query = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();
    }

    @Test
    public void startJPQL(){
        Member result = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(result.getUsername()).isEqualTo("member1");
    }
    
    @Test
    public void startQuerydsl() throws Exception {
        Member result = query
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(result.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() throws Exception {
        Member result = query.selectFrom(member)
                .where(member.username.eq("member1"),
                        member.age.eq(10))
                .fetchOne();

        assertThat(result.getUsername()).isEqualTo("member1");
        assertThat(result.getAge()).isEqualTo(10);
    }
}