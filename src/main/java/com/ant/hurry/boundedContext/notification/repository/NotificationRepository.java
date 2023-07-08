package com.ant.hurry.boundedContext.notification.repository;

import com.ant.hurry.boundedContext.member.entity.Member;
import com.ant.hurry.boundedContext.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select n from Notification n join fetch n.requester r join fetch n.helper h " +
            "where r.id  = :memberId or h.id = :memberId order by n.createdAt desc ")
    List<Notification> findAllByMemberId(@Param("memberId") Long memberId);

    @Query("select count(n.id) from Notification n where (n.requester = :member or n.helper = :member) and n.readDate = null")
    int countByMemberAndReadDateIsNull(@Param("member") Member member);

}
