package com.ant.hurry.boundedContext.notification.repository;

import com.ant.hurry.boundedContext.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
