package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.entity.Notification;

import java.util.List;

public interface NotificationService {
    Notification createNotification(String userId, String title, String message);

    List<Notification> getUserNotifications(String userId);

    CommonResponseDto markAsRead(Long notificationId, String userId);

    CommonResponseDto deleteNotification(Long notificationId, String userId);
}
