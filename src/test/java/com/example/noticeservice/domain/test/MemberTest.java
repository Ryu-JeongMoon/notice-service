package com.example.noticeservice.domain.test;

import com.example.noticeservice.config.JpaTestConfig;
import com.example.noticeservice.test.Member;
import com.example.noticeservice.test.Team;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Slf4j
@DataJpaTest
@Import(JpaTestConfig.class)
class MemberTest {

  @Autowired
  EntityManager em;

  @Test
  @DisplayName("연관관계의 이해")
  void em() {
    Member member1 = Member.builder().name("panda1").build();
    Member member2 = Member.builder().name("panda2").build();

    Team team1 = Team.builder().name("bear1").build();
    Team team2 = Team.builder().name("bear2").build();

    member1.changeTeam(team1);
    member2.changeTeam(team2);

    log.info("team1 = {}", team1);
    log.info("team2 = {}", team2);

    em.persist(member1);
    em.persist(member2);

    em.flush();
    em.clear();

    Member findMember = em.find(Member.class, member1.getId());
    Member referenceMember = em.getReference(Member.class, member1.getId());

    log.info("findMember = {}", findMember);
    log.info("referenceMember = {}", referenceMember);
    log.info("(findMember == referenceMember) = {}", (findMember == referenceMember));
  }
}