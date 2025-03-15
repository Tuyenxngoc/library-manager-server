package com.example.librarymanager.repository;

import com.example.librarymanager.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    List<Notification> findByUser_Id(String userId);

    Optional<Notification> findByIdAndUser_Id(Long notificationId, String userId);
}
