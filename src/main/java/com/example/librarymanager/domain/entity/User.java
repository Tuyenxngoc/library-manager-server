package com.example.librarymanager.domain.entity;

import com.example.librarymanager.constant.AccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "UN_USER_USERNAME", columnNames = "username"),
                @UniqueConstraint(name = "UN_USER_EMAIL", columnNames = "email")
        })
public class User {

    @Id
    @UuidGenerator
    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private String id;  // ID người dùng (UUID)

    @Column(name = "username", nullable = false)
    private String username;  // Tên đăng nhập

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;  // Mật khẩu

    @Column(name = "expiry_date")
    private LocalDate expiryDate;  // Ngày hết hạn tài khoản

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;  // Trạng thái tài khoản

    @Column(name = "full-name")
    private String fullName;  // Họ tên đầy đủ của người dùng

    @Column(name = "position")
    private String position;  // Chức vụ của người dùng

    @Column(name = "email", nullable = false)
    private String email;  // Địa chỉ email

    @Column(name = "phone-number", nullable = false)
    private String phoneNumber;  // Số điện thoại

    @Column(name = "address")
    private String address;  // Địa chỉ của người dùng

    @Column(name = "note")
    private String note;  // Ghi chú về người dùng

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Log> logs = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_group_id", foreignKey = @ForeignKey(name = "FK_USER_USER_GROUP_ID"), referencedColumnName = "user_group_id", nullable = false)
    @JsonIgnore
    private UserGroup userGroup;

    public User(String userId) {
        this.id = userId;
    }

}
