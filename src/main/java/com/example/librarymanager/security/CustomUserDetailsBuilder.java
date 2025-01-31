package com.example.librarymanager.security;

import com.example.librarymanager.constant.AccountStatus;
import com.example.librarymanager.constant.CardStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;

@Getter
@Setter
public class CustomUserDetailsBuilder {

    private String userId;

    private String cardNumber;

    private LocalDate expiryDate;

    private AccountStatus accountStatus;

    private CardStatus cardStatus;

    private String username;

    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetailsBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public CustomUserDetailsBuilder cardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public CustomUserDetailsBuilder expiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public CustomUserDetailsBuilder accountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
        return this;
    }

    public CustomUserDetailsBuilder cardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
        return this;
    }

    public CustomUserDetailsBuilder username(String username) {
        this.username = username;
        return this;
    }

    public CustomUserDetailsBuilder password(String password) {
        this.password = password;
        return this;
    }

    public CustomUserDetailsBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
        return this;
    }

    public CustomUserDetails build() {
        return new CustomUserDetails(this);
    }
}
