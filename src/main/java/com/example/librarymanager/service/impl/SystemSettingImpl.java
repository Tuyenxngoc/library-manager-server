package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.EventConstants;
import com.example.librarymanager.constant.SuccessMessage;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.request.LibraryConfigRequestDto;
import com.example.librarymanager.domain.dto.request.LibraryInfoRequestDto;
import com.example.librarymanager.domain.dto.request.LibraryRulesRequestDto;
import com.example.librarymanager.domain.dto.response.LibraryConfigResponseDto;
import com.example.librarymanager.domain.dto.response.LibraryInfoResponseDto;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.service.SystemSettingService;
import com.example.librarymanager.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemSettingImpl implements SystemSettingService {
    private static final String LIBRARY_RULES_FILE_PATH = "data/library_rules.txt";
    private static final String LIBRARY_CONFIG_FILE_PATH = "data/library_config.txt";
    private static final String LIBRARY_INFO_FILE_PATH = "data/library_info.txt";

    private static final String TAG = "Thiết lập hệ thống";

    private final LogService logService;

    private final MessageSource messageSource;

    private final UploadFileUtil uploadFileUtil;

    @Override
    public CommonResponseDto updateLibraryRules(LibraryRulesRequestDto requestDto, String userId) {
        try {
            File file = new File(LIBRARY_RULES_FILE_PATH);

            if (!file.exists()) {
                file.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(requestDto.getContent());
            }
        } catch (IOException ignored) {
        }

        logService.createLog(TAG, EventConstants.EDIT, "Cập nhật nội quy thư viện ngày: " + LocalDateTime.now(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public String getLibraryRules() {
        File file = new File(LIBRARY_RULES_FILE_PATH);

        if (!file.exists()) {
            return "Library rules file not found.";
        }

        try {
            return new String(Files.readAllBytes(Paths.get(LIBRARY_RULES_FILE_PATH)));
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading library rules: " + e.getMessage();
        }
    }

    @Override
    public LibraryConfigResponseDto getLibraryConfig() {
        File file = new File(LIBRARY_CONFIG_FILE_PATH);

        if (!file.exists()) {
            return new LibraryConfigResponseDto(10, 30, 5, 3, 7);
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            int rowsPerPage = 10, reservationTime = 30, maxBorrowLimit = 5, maxRenewalTimes = 3, maxRenewalDays = 7;

            for (String line : lines) {
                if (line.startsWith("rowsPerPage=")) {
                    rowsPerPage = Integer.parseInt(line.split("=")[1]);
                } else if (line.startsWith("reservationTime=")) {
                    reservationTime = Integer.parseInt(line.split("=")[1]);
                } else if (line.startsWith("maxBorrowLimit=")) {
                    maxBorrowLimit = Integer.parseInt(line.split("=")[1]);
                } else if (line.startsWith("maxRenewalTimes=")) {
                    maxRenewalTimes = Integer.parseInt(line.split("=")[1]);
                } else if (line.startsWith("maxRenewalDays=")) {
                    maxRenewalDays = Integer.parseInt(line.split("=")[1]);
                }
            }

            return new LibraryConfigResponseDto(rowsPerPage, reservationTime, maxBorrowLimit, maxRenewalTimes, maxRenewalDays);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading library config file.");
        }
    }

    @Override
    public CommonResponseDto updateLibraryConfig(LibraryConfigRequestDto requestDto, String userId) {
        File file = new File(LIBRARY_CONFIG_FILE_PATH);

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("rowsPerPage=" + requestDto.getRowsPerPage() + "\n");
                writer.write("reservationTime=" + requestDto.getReservationTime() + "\n");
                writer.write("maxBorrowLimit=" + requestDto.getMaxBorrowLimit() + "\n");
                writer.write("maxRenewalTimes=" + requestDto.getMaxRenewalTimes() + "\n");
                writer.write("maxRenewalDays=" + requestDto.getMaxRenewalDays() + "\n");
            }

            logService.createLog(TAG, EventConstants.EDIT, "Cập nhật cấu hình thư viện: " + LocalDateTime.now(), userId);

            String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
            return new CommonResponseDto(message);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating library config file.");
        }
    }

    @Override
    public LibraryInfoResponseDto getLibraryInfo() {
        File file = new File(LIBRARY_INFO_FILE_PATH);

        if (!file.exists()) {
            return null;
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            LibraryInfoResponseDto responseDto = new LibraryInfoResponseDto();

            for (String line : lines) {
                String[] parts = line.split("=", 2);
                if (parts.length < 2) continue;

                String key = parts[0].trim();
                String value = parts[1].trim();

                switch (key) {
                    case "librarySymbol":
                        responseDto.setLibrarySymbol(value);
                        break;
                    case "libraryName":
                        responseDto.setLibraryName(value);
                        break;
                    case "address":
                        responseDto.setAddress(value);
                        break;
                    case "postalCode":
                        responseDto.setPostalCode(value);
                        break;
                    case "countryCode":
                        responseDto.setCountryCode(value);
                        break;
                    case "provinceCity":
                        responseDto.setProvinceCity(value);
                        break;
                    case "educationOffice":
                        responseDto.setEducationOffice(value);
                        break;
                    case "school":
                        responseDto.setSchool(value);
                        break;
                    case "phoneNumber":
                        responseDto.setPhoneNumber(value);
                        break;
                    case "alternatePhoneNumber":
                        responseDto.setAlternatePhoneNumber(value);
                        break;
                    case "faxNumber":
                        responseDto.setFaxNumber(value);
                        break;
                    case "email":
                        responseDto.setEmail(value);
                        break;
                    case "pageTitle":
                        responseDto.setPageTitle(value);
                        break;
                    case "motto":
                        responseDto.setMotto(value);
                        break;
                    case "logo":
                        responseDto.setLogo(value);
                        break;
                    case "introduction":
                        responseDto.setIntroduction(value);
                        break;
                    default:
                        break;
                }
            }
            return responseDto;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading library info file.");
        }
    }

    @Override
    public CommonResponseDto updateLibraryInfo(LibraryInfoRequestDto requestDto, MultipartFile logo, String userId) {
        uploadFileUtil.checkImageIsValid(logo);
        File file = new File(LIBRARY_INFO_FILE_PATH);
        String logoFilePath = null;

        try {
            // Lưu logo nếu có
            if (logo != null && !logo.isEmpty()) {
                logoFilePath = uploadFileUtil.uploadFile(logo);
                if (file.exists()) {
                    List<String> lines = Files.readAllLines(file.toPath());
                    for (String line : lines) {
                        if (line.startsWith("logo=")) {
                            String oldLogoUrl = line.split("=", 2)[1].trim();
                            if (!oldLogoUrl.isEmpty()) {
                                uploadFileUtil.destroyFileWithUrl(oldLogoUrl);
                            }
                            break;
                        }
                    }
                }
            }

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("librarySymbol=" + requestDto.getLibrarySymbol() + "\n");
                writer.write("libraryName=" + requestDto.getLibraryName() + "\n");
                writer.write("address=" + requestDto.getAddress() + "\n");
                writer.write("postalCode=" + requestDto.getPostalCode() + "\n");
                writer.write("countryCode=" + requestDto.getCountryCode() + "\n");
                writer.write("provinceCity=" + requestDto.getProvinceCity() + "\n");
                writer.write("educationOffice=" + requestDto.getEducationOffice() + "\n");
                writer.write("school=" + requestDto.getSchool() + "\n");
                writer.write("phoneNumber=" + requestDto.getPhoneNumber() + "\n");
                writer.write("alternatePhoneNumber=" + requestDto.getAlternatePhoneNumber() + "\n");
                writer.write("faxNumber=" + requestDto.getFaxNumber() + "\n");
                writer.write("email=" + requestDto.getEmail() + "\n");
                writer.write("pageTitle=" + requestDto.getPageTitle() + "\n");
                writer.write("motto=" + requestDto.getMotto() + "\n");
                writer.write("logo=" + (logoFilePath != null ? logoFilePath : "") + "\n");
                writer.write("introduction=" + requestDto.getIntroduction() + "\n");
            }

            logService.createLog(TAG, EventConstants.EDIT, "Cập nhật thông tin thư viện: " + LocalDateTime.now(), userId);

            String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
            return new CommonResponseDto(message);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating library info.");
        }
    }
}
