package com.example.librarymanager.domain.dto.response;

import com.example.librarymanager.constant.CommonConstant;
import com.example.librarymanager.domain.entity.Log;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LogResponseDto {

    private final long id;

    private final String feature;

    private final String event;

    private final String content;

    private final String user;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstant.PATTERN_DATE_TIME)
    private final LocalDateTime timestamp; // Thời gian hành động

    public LogResponseDto(Log log) {
        this.id = log.getId();
        this.feature = log.getFeature();
        this.event = log.getEvent();
        this.content = log.getContent();
        this.timestamp = log.getTimestamp();
        this.user = log.getUser().getUsername();
    }
}
