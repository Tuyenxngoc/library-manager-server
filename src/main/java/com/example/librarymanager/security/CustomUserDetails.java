package com.example.librarymanager.security;

import com.example.librarymanager.constant.AccountStatus;
import com.example.librarymanager.constant.CardStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final String userId;

    @Getter
    private final String cardNumber;

    @Getter
    private final LocalDate expiryDate;

    @Getter
    private final AccountStatus accountStatus;

    @Getter
    private final CardStatus cardStatus;

    @JsonIgnore
    private final String username;

    @JsonIgnore
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(CustomUserDetailsBuilder builder) {
        this.userId = builder.getUserId();
        this.cardNumber = builder.getCardNumber();
        this.expiryDate = builder.getExpiryDate();
        this.accountStatus = builder.getAccountStatus();
        this.cardStatus = builder.getCardStatus();
        this.username = builder.getUsername();
        this.password = builder.getPassword();
        this.authorities = builder.getAuthorities();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
