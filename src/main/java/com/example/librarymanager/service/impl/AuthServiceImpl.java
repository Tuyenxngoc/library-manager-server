package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.AccountStatus;
import com.example.librarymanager.constant.CardStatus;
import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.SuccessMessage;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.common.DataMailDto;
import com.example.librarymanager.domain.dto.request.auth.*;
import com.example.librarymanager.domain.dto.response.auth.CurrentUserLoginResponseDto;
import com.example.librarymanager.domain.dto.response.auth.LoginResponseDto;
import com.example.librarymanager.domain.dto.response.auth.TokenRefreshResponseDto;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.entity.User;
import com.example.librarymanager.exception.BadRequestException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.exception.UnauthorizedException;
import com.example.librarymanager.repository.ReaderRepository;
import com.example.librarymanager.repository.UserRepository;
import com.example.librarymanager.security.CustomUserDetails;
import com.example.librarymanager.security.UserDetailsFactory;
import com.example.librarymanager.security.jwt.JwtTokenProvider;
import com.example.librarymanager.service.AuthService;
import com.example.librarymanager.service.EmailRateLimiterService;
import com.example.librarymanager.service.JwtBlacklistService;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.util.JwtUtil;
import com.example.librarymanager.util.RandomPasswordUtil;
import com.example.librarymanager.util.SendMailUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    private static final String TAG = "Xác thực";

    AuthenticationManager authenticationManager;

    JwtTokenProvider jwtTokenProvider;

    JwtBlacklistService jwtBlacklistService;

    EmailRateLimiterService emailRateLimiterService;

    MessageSource messageSource;

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    SendMailUtil sendMailUtil;

    ReaderRepository readerRepository;

    LogService logService;

    @Override
    public LoginResponseDto readerLogin(ReaderLoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCardNumber(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            if (customUserDetails.getCardNumber() == null) {
                throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_USERNAME_PASSWORD);
            }

            if (customUserDetails.getCardStatus() != CardStatus.ACTIVE) {
                throw new UnauthorizedException(ErrorMessage.Auth.ERR_ACCOUNT_NOT_ACTIVE);
            }

            if (customUserDetails.getExpiryDate() != null && customUserDetails.getExpiryDate().isBefore(LocalDate.now())) {
                throw new UnauthorizedException(ErrorMessage.Auth.ERR_ACCOUNT_EXPIRED);
            }

            String accessToken = jwtTokenProvider.generateToken(customUserDetails, false);
            String refreshToken = jwtTokenProvider.generateToken(customUserDetails, true);

            return new LoginResponseDto(
                    accessToken,
                    refreshToken
            );
        } catch (AuthenticationException e) {
            throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_USERNAME_PASSWORD);
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.ERR_EXCEPTION_GENERAL);
        }
    }

    @Override
    public LoginResponseDto adminLogin(AdminLoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            if (customUserDetails.getUserId() == null) {
                throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_USERNAME_PASSWORD);
            }

            if (customUserDetails.getAccountStatus() != AccountStatus.ACTIVATED) {
                throw new UnauthorizedException(ErrorMessage.Auth.ERR_ACCOUNT_NOT_ACTIVE);
            }

            if (customUserDetails.getExpiryDate() != null && customUserDetails.getExpiryDate().isBefore(LocalDate.now())) {
                throw new UnauthorizedException(ErrorMessage.Auth.ERR_ACCOUNT_EXPIRED);
            }

            String accessToken = jwtTokenProvider.generateToken(customUserDetails, false);
            String refreshToken = jwtTokenProvider.generateToken(customUserDetails, true);

            logService.createLog(TAG, "Đăng nhập", request.getUsername() + " đăng nhập vào hệ thống", customUserDetails.getUserId());

            return new LoginResponseDto(
                    accessToken,
                    refreshToken
            );
        } catch (AuthenticationException e) {
            throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_USERNAME_PASSWORD);
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.ERR_EXCEPTION_GENERAL);
        }
    }

    @Override
    public CommonResponseDto logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String accessToken = JwtUtil.extractTokenFromRequest(request);
        String refreshToken = JwtUtil.extractRefreshTokenFromRequest(request);

        if (accessToken != null) {
            // Lưu accessToken vào blacklist
            jwtBlacklistService.blacklistAccessToken(accessToken);
        }

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken) && jwtTokenProvider.isRefreshToken(refreshToken)) {
            // Lưu refreshToken vào blacklist
            jwtBlacklistService.blacklistRefreshToken(refreshToken);
        }

        SecurityContextLogoutHandler logout = new SecurityContextLogoutHandler();
        logout.logout(request, response, authentication);

        String message = messageSource.getMessage(SuccessMessage.Auth.LOGOUT, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public TokenRefreshResponseDto refresh(TokenRefreshRequestDto request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken) ||
                !jwtTokenProvider.isRefreshToken(refreshToken) ||
                jwtBlacklistService.isTokenBlocked(refreshToken)) {
            throw new BadRequestException(ErrorMessage.Auth.ERR_INVALID_REFRESH_TOKEN);
        }

        String userId = jwtTokenProvider.extractSubjectFromJwt(refreshToken);
        if (userId != null) {
            User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException(ErrorMessage.Auth.ERR_INVALID_REFRESH_TOKEN));
            CustomUserDetails userDetails = UserDetailsFactory.fromUser(user);

            String newAccessToken = jwtTokenProvider.generateToken(userDetails, false);
            String newRefreshToken = jwtTokenProvider.generateToken(userDetails, true);

            jwtBlacklistService.blacklistRefreshToken(refreshToken);

            return new TokenRefreshResponseDto(newAccessToken, newRefreshToken);
        }

        String cardNumber = jwtTokenProvider.extractClaimCardNumber(refreshToken);
        if (cardNumber != null) {
            Reader reader = readerRepository.findByCardNumber(cardNumber).orElseThrow(() -> new BadRequestException(ErrorMessage.Auth.ERR_INVALID_REFRESH_TOKEN));
            CustomUserDetails userDetails = UserDetailsFactory.fromReader(reader);

            String newAccessToken = jwtTokenProvider.generateToken(userDetails, false);
            String newRefreshToken = jwtTokenProvider.generateToken(userDetails, true);

            jwtBlacklistService.blacklistRefreshToken(refreshToken);

            return new TokenRefreshResponseDto(newAccessToken, newRefreshToken);
        }

        throw new BadRequestException(ErrorMessage.Auth.ERR_INVALID_REFRESH_TOKEN);
    }

    @Override
    public CommonResponseDto adminForgetPassword(AdminForgetPasswordRequestDto requestDto) {
        User user = userRepository.findByUsernameAndEmail(requestDto.getUsername(), requestDto.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ACCOUNT));

        // Kiểm tra giới hạn thời gian gửi email
        if (emailRateLimiterService.isMailLimited(requestDto.getEmail())) {
            String message = messageSource.getMessage(ErrorMessage.User.RATE_LIMIT, null, LocaleContextHolder.getLocale());
            return new CommonResponseDto(message);
        }

        String newPassword = RandomPasswordUtil.random();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", requestDto.getUsername());
        properties.put("newPassword", newPassword);
        sendEmail(user.getEmail(), "Lấy lại mật khẩu", properties, "forgetPassword.html");

        emailRateLimiterService.setMailLimit(requestDto.getEmail(), 1, TimeUnit.MINUTES);

        String message = messageSource.getMessage(SuccessMessage.User.FORGET_PASSWORD, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public CommonResponseDto adminChangePassword(ChangePasswordRequestDto requestDto, String username) {
        if (!requestDto.getPassword().equals(requestDto.getRepeatPassword())) {
            throw new BadRequestException(ErrorMessage.INVALID_REPEAT_PASSWORD);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_USERNAME, username));

        boolean isCorrectPassword = passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword());
        if (!isCorrectPassword) {
            throw new BadRequestException(ErrorMessage.Auth.ERR_INCORRECT_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);

        Map<String, Object> properties = new HashMap<>();
        properties.put("currentTime", new Date());
        sendEmail(user.getEmail(), "Đổi mật khẩu thành công", properties, "changePassword.html");

        String message = messageSource.getMessage(SuccessMessage.User.CHANGE_PASSWORD, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public CommonResponseDto forgetPassword(ReaderForgetPasswordRequestDto requestDto) {
        Reader reader = readerRepository.findByCardNumberAndEmail(requestDto.getCardNumber(), requestDto.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ACCOUNT));

        // Kiểm tra giới hạn thời gian gửi email
        if (emailRateLimiterService.isMailLimited(requestDto.getEmail())) {
            String message = messageSource.getMessage(ErrorMessage.User.RATE_LIMIT, null, LocaleContextHolder.getLocale());
            return new CommonResponseDto(message);
        }

        String newPassword = RandomPasswordUtil.random();

        reader.setPassword(passwordEncoder.encode(newPassword));
        readerRepository.save(reader);

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", requestDto.getCardNumber());
        properties.put("newPassword", newPassword);
        sendEmail(reader.getEmail(), "Lấy lại mật khẩu", properties, "forgetPassword.html");

        emailRateLimiterService.setMailLimit(requestDto.getEmail(), 1, TimeUnit.MINUTES);

        String message = messageSource.getMessage(SuccessMessage.User.FORGET_PASSWORD, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public CommonResponseDto changePassword(ChangePasswordRequestDto requestDto, String cardNumber) {
        if (!requestDto.getPassword().equals(requestDto.getRepeatPassword())) {
            throw new BadRequestException(ErrorMessage.INVALID_REPEAT_PASSWORD);
        }

        Reader reader = readerRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Reader.ERR_NOT_FOUND_CARD_NUMBER, cardNumber));

        boolean isCorrectPassword = passwordEncoder.matches(requestDto.getOldPassword(), reader.getPassword());
        if (!isCorrectPassword) {
            throw new BadRequestException(ErrorMessage.Auth.ERR_INCORRECT_PASSWORD);
        }

        reader.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        readerRepository.save(reader);

        Map<String, Object> properties = new HashMap<>();
        properties.put("currentTime", new Date());
        sendEmail(reader.getEmail(), "Đổi mật khẩu thành công", properties, "changePassword.html");

        String message = messageSource.getMessage(SuccessMessage.User.CHANGE_PASSWORD, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    private void sendEmail(String to, String subject, Map<String, Object> properties, String templateName) {
        DataMailDto mailDto = new DataMailDto();
        mailDto.setTo(to);
        mailDto.setSubject(subject);
        mailDto.setProperties(properties);

        CompletableFuture.runAsync(() -> {
            try {
                sendMailUtil.sendEmailWithHTML(mailDto, templateName);
            } catch (MessagingException e) {
                log.error("Failed to send email to [{}] with subject [{}]. Error: {}", to, subject, e.getMessage(), e);
            } catch (Exception e) {
                log.error("Unexpected error while sending email to [{}]. Error: {}", to, e.getMessage(), e);
            }
        });
    }

    @Override
    public CurrentUserLoginResponseDto getCurrentUser(CustomUserDetails userDetails) {
        if (userDetails.getUserId() != null) {
            User user = userRepository.findById(userDetails.getUserId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, userDetails.getUserId()));

            return CurrentUserLoginResponseDto.create(user);
        } else if (userDetails.getCardNumber() != null) {
            Reader reader = readerRepository.findByCardNumber(userDetails.getCardNumber())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.Reader.ERR_NOT_FOUND_CARD_NUMBER, userDetails.getCardNumber()));

            return CurrentUserLoginResponseDto.create(reader);
        }
        return null;
    }
}
