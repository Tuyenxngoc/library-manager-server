package com.example.librarymanager.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(name = "feature", nullable = false, length = 100)
    private String feature; // Chức năng được thao tác

    @Column(name = "event", nullable = false, length = 100)
    private String event; // Sự kiện (Truy cập, Thêm, Xóa, v.v.)

    @Column(name = "content", nullable = false)
    private String content; // Nội dung mô tả chi tiết

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp; // Thời gian hành động

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_LOG_USER_ID"), referencedColumnName = "user_id", nullable = false)
    @JsonIgnore
    private User user;

}
