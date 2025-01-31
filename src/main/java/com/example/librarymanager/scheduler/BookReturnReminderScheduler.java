package com.example.librarymanager.scheduler;

import com.example.librarymanager.domain.dto.common.DataMailDto;
import com.example.librarymanager.domain.entity.BorrowReceipt;
import com.example.librarymanager.repository.BorrowReceiptRepository;
import com.example.librarymanager.util.SendMailUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class BookReturnReminderScheduler {

    private final SendMailUtil sendMailUtil;

    private final BorrowReceiptRepository borrowReceiptRepository;

    @Scheduled(cron = "0 0 8 * * ?") // Chạy lúc 8:00 AM mỗi ngày
    @Transactional
    public void sendReminderEmails() {
        List<BorrowReceipt> overdueRecords = borrowReceiptRepository.findRecordsByReturnDate();

        for (BorrowReceipt record : overdueRecords) {

            Map<String, Object> properties = new HashMap<>();
            properties.put("username", record.getReader().getFullName());
            properties.put("receiptNumber", record.getReceiptNumber());
            properties.put("returnDate", record.getDueDate());

            DataMailDto mailDto = new DataMailDto();
            mailDto.setTo(record.getReader().getEmail());
            mailDto.setSubject("Nhắc nhở trả sách");
            mailDto.setProperties(properties);

            CompletableFuture.runAsync(() -> {
                try {
                    sendMailUtil.sendEmailWithHTML(mailDto, "reminderEmail.html");
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
