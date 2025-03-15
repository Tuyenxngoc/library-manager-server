package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.SuccessMessage;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.entity.Notification;
import com.example.librarymanager.domain.entity.User;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.NotificationRepository;
import com.example.librarymanager.repository.UserRepository;
import com.example.librarymanager.service.NotificationService;
import com.example.librarymanager.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final MessageUtil messageUtil;

    private Notification getEntity(Long notificationId, String userId) {
        return notificationRepository.findByIdAndUser_Id(notificationId, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Notification.ERR_NOT_FOUND_ID, notificationId));
    }

    @Override
    public Notification createNotification(String userId, String title, String message) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUser_Id(userId);
    }

    @Override
    public CommonResponseDto markAsRead(Long notificationId, String userId) {
        Notification notification = getEntity(notificationId, userId);

        notification.setRead(true);
        notificationRepository.save(notification);

        String message = messageUtil.getMessage(SuccessMessage.UPDATE);
        return new CommonResponseDto(message);
    }

    @Override
    public CommonResponseDto deleteNotification(Long notificationId, String userId) {
        Notification notification = getEntity(notificationId, userId);

        notificationRepository.delete(notification);

        String message = messageUtil.getMessage(SuccessMessage.DELETE);
        return new CommonResponseDto(message);
    }
}
