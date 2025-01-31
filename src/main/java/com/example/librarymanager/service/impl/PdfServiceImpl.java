package com.example.librarymanager.service.impl;

import com.example.librarymanager.domain.dto.request.CreateBorrowReceiptRequestDto;
import com.example.librarymanager.domain.dto.request.CreateReaderCardsRequestDto;
import com.example.librarymanager.domain.entity.*;
import com.example.librarymanager.service.PdfService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static Font normalFontSmall;
    private static Font normalFontMedium;
    private static Font normalFontLarge;
    private static Font boldFontSmall;
    private static Font boldFontMedium;
    private static Font boldFontLarge;
    private static Font italicFontSmall;
    private static Font italicFontMedium;
    private static Font italicFontLarge;
    private static Font boldItalicFontSmall;
    private static Font boldItalicFontMedium;
    private static Font boldItalicFontLarge;
    private static Font headerFont;
    private static Font subHeaderFont;

    static {
        String FONT_PATH = "src/main/resources/fonts/NotoSans-Regular.ttf";

        try {
            BaseFont baseFont = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            // Font thông thường
            normalFontSmall = new Font(baseFont, 10, Font.NORMAL);
            normalFontMedium = new Font(baseFont, 12, Font.NORMAL);
            normalFontLarge = new Font(baseFont, 14, Font.NORMAL);

            // Font đậm
            boldFontSmall = new Font(baseFont, 10, Font.BOLD);
            boldFontMedium = new Font(baseFont, 12, Font.BOLD);
            boldFontLarge = new Font(baseFont, 14, Font.BOLD);

            // Font nghiêng
            italicFontSmall = new Font(baseFont, 10, Font.ITALIC);
            italicFontMedium = new Font(baseFont, 12, Font.ITALIC);
            italicFontLarge = new Font(baseFont, 14, Font.ITALIC);

            // Font vừa đậm vừa nghiêng
            boldItalicFontSmall = new Font(baseFont, 10, Font.BOLDITALIC);
            boldItalicFontMedium = new Font(baseFont, 12, Font.BOLDITALIC);
            boldItalicFontLarge = new Font(baseFont, 14, Font.BOLDITALIC);

            // Font tiêu đề
            headerFont = new Font(baseFont, 16, Font.BOLD);
            subHeaderFont = new Font(baseFont, 14, Font.BOLD);
        } catch (DocumentException | IOException e) {
            log.error("Failed to create font: {}", FONT_PATH);
        }
    }

    @Override
    public byte[] createReaderCard(CreateReaderCardsRequestDto requestDto, List<Reader> readers) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            int cardsPerPage = 8;
            int totalReaders = readers.size();
            int totalPages = (int) Math.ceil((double) totalReaders / cardsPerPage);

            for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
                PdfPTable mainTable = new PdfPTable(2);
                mainTable.setWidthPercentage(100);
                mainTable.setSpacingBefore(10f);
                mainTable.setSpacingAfter(10f);

                int lop = Math.min((pageIndex + 1) * cardsPerPage, totalReaders);
                for (int i = pageIndex * cardsPerPage; i < lop; i++) {
                    Reader reader = readers.get(i);
                    PdfPTable cardContainer = new PdfPTable(1);
                    cardContainer.setWidthPercentage(100);

                    PdfPCell headerCell = new PdfPCell();
                    headerCell.addElement(createParagraph(requestDto.getManagementUnit(), headerFont, Element.ALIGN_CENTER));
                    headerCell.addElement(createParagraph(requestDto.getSchoolName(), headerFont, Element.ALIGN_CENTER));
                    headerCell.setBorder(Rectangle.NO_BORDER);
                    cardContainer.addCell(headerCell);

                    PdfPTable cardContentTable = new PdfPTable(2);
                    cardContentTable.setWidthPercentage(100);
                    cardContentTable.setWidths(new int[]{1, 2});

                    PdfPTable avatarTable = createAvatarTable(writer, reader);
                    PdfPCell avatarCell = new PdfPCell(avatarTable);
                    avatarCell.setPadding(5);
                    avatarCell.setBorder(Rectangle.NO_BORDER);
                    cardContentTable.addCell(avatarCell);

                    PdfPCell infoCell = createInfoCell(requestDto, reader);
                    cardContentTable.addCell(infoCell);

                    PdfPCell cardCell = new PdfPCell(cardContentTable);
                    cardCell.setPadding(5);
                    cardCell.setBorder(Rectangle.NO_BORDER);
                    cardContainer.addCell(cardCell);

                    mainTable.addCell(cardContainer);
                }

                if (lop % 2 == 1) {
                    mainTable.completeRow();
                }

                document.add(mainTable);

                // Add new page except for the last page
                if (pageIndex < totalPages - 1) {
                    document.newPage();
                }
            }

            document.close();
        } catch (DocumentException | IOException e) {
            log.error("Error creating PDF: {}", e.getMessage(), e);
        }

        return outputStream.toByteArray();
    }

    private PdfPTable createAvatarTable(PdfWriter writer, Reader reader) throws IOException, DocumentException {
        PdfPTable avatarTable = new PdfPTable(1);
        avatarTable.setWidthPercentage(100);

        PdfPCell avatarImageCell = new PdfPCell();
        avatarImageCell.setFixedHeight(90);
        String avatarUrl = reader.getAvatar();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Image avatarImage = Image.getInstance(avatarUrl);
            avatarImage.scaleToFit(75, 75);
            avatarImage.setAlignment(Element.ALIGN_CENTER);
            avatarImageCell.addElement(avatarImage);
            avatarImageCell.setBorder(Rectangle.NO_BORDER);
        } else {
            avatarImageCell.setBorder(Rectangle.BOX);
        }
        avatarTable.addCell(avatarImageCell);

        PdfPCell barcodeCell = new PdfPCell();
        barcodeCell.setPaddingTop(10);
        barcodeCell.setBorder(Rectangle.NO_BORDER);

        Barcode128 barcode = new Barcode128();
        barcode.setCode(reader.getCardNumber());
        Image barcodeImage = barcode.createImageWithBarcode(writer.getDirectContent(), null, null);
        barcodeImage.setAlignment(Element.ALIGN_CENTER);
        barcodeImage.scalePercent(80);
        barcodeCell.addElement(barcodeImage);

        avatarTable.addCell(barcodeCell);

        return avatarTable;
    }

    private PdfPCell createInfoCell(CreateReaderCardsRequestDto requestDto, Reader reader) {
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);

        infoCell.addElement(createParagraph("THẺ THƯ VIỆN", boldFontMedium, Element.ALIGN_CENTER));
        infoCell.addElement(new Paragraph(String.format("Họ và tên: %s", reader.getFullName()), normalFontMedium));
        infoCell.addElement(new Paragraph(String.format("Loại thẻ: %s", reader.getCardType().getDisplayName()), normalFontMedium));
        infoCell.addElement(new Paragraph(String.format("Ngày sinh: %s", reader.getDateOfBirth() != null ? reader.getDateOfBirth().format(formatter) : "Không có"), normalFontMedium));
        infoCell.addElement(new Paragraph(String.format("Ngày hết hạn: %s", reader.getExpiryDate() != null ? reader.getExpiryDate().format(formatter) : "Không có"), normalFontMedium));
        infoCell.addElement(createParagraph("Ban giám hiệu", normalFontMedium, Element.ALIGN_CENTER));
        infoCell.addElement(createParagraph(requestDto.getPrincipalName().toUpperCase(), italicFontMedium, Element.ALIGN_CENTER));

        return infoCell;
    }

    private Paragraph createParagraph(String text, Font font, int alignment) {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(alignment);
        return paragraph;
    }

    @Override
    public byte[] createReceipt(User user, CreateBorrowReceiptRequestDto requestDto, List<BorrowReceipt> borrowReceipts) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            int receiptsPerPage = 2;
            int totalBorrowReceipts = borrowReceipts.size();
            int totalPages = (int) Math.ceil((double) totalBorrowReceipts / receiptsPerPage);

            for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
                PdfPTable mainTable = new PdfPTable(2);
                mainTable.setWidthPercentage(100);
                mainTable.setSpacingBefore(10f);
                mainTable.setSpacingAfter(10f);

                int lop = Math.min((pageIndex + 1) * receiptsPerPage, totalBorrowReceipts);
                for (int i = pageIndex * receiptsPerPage; i < lop; i++) {
                    BorrowReceipt borrowReceipt = borrowReceipts.get(i);
                    PdfPCell receiptContainer = new PdfPCell();
                    receiptContainer.setPadding(10);
                    receiptContainer.setBorder(Rectangle.BOX);
                    receiptContainer.setBorderColor(BaseColor.GRAY);

                    //Tiêu đề
                    Paragraph title = new Paragraph(requestDto.getSchoolName().toUpperCase(), boldFontLarge);
                    title.setAlignment(Element.ALIGN_CENTER);
                    receiptContainer.addElement(title);

                    Paragraph receiptInfo = new Paragraph("PHIẾU MƯỢN TÀI LIỆU", normalFontLarge);
                    receiptInfo.setAlignment(Element.ALIGN_CENTER);
                    receiptContainer.addElement(receiptInfo);

                    receiptContainer.addElement(new Paragraph("\n"));

                    Paragraph receiptNumber = new Paragraph(String.format("Số phiếu: %s", borrowReceipt.getReceiptNumber()), normalFontMedium);
                    receiptNumber.setAlignment(Element.ALIGN_RIGHT);
                    receiptContainer.addElement(receiptNumber);

                    // Thông tin người mượn
                    Paragraph readerName = new Paragraph(String.format("Họ và tên: %s", borrowReceipt.getReader().getFullName()), normalFontMedium);
                    readerName.setAlignment(Element.ALIGN_LEFT);
                    receiptContainer.addElement(readerName);

                    Paragraph readerCard = new Paragraph(String.format("Mã số thẻ: %s", borrowReceipt.getReader().getCardNumber()), normalFontMedium);
                    readerCard.setAlignment(Element.ALIGN_LEFT);
                    receiptContainer.addElement(readerCard);

                    Paragraph borrowDate = new Paragraph(String.format("Ngày mượn: %s", borrowReceipt.getBorrowDate().format(formatter)), normalFontMedium);
                    borrowDate.setAlignment(Element.ALIGN_LEFT);
                    receiptContainer.addElement(borrowDate);

                    Paragraph dueDate = new Paragraph(String.format("Ngày hẹn trả: %s", borrowReceipt.getDueDate().format(formatter)), normalFontMedium);
                    dueDate.setAlignment(Element.ALIGN_LEFT);
                    receiptContainer.addElement(dueDate);

                    receiptContainer.addElement(new Paragraph("\n"));

                    Paragraph documentListLabel = new Paragraph("Danh sách tài liệu:", normalFontMedium);
                    documentListLabel.setAlignment(Element.ALIGN_LEFT);
                    receiptContainer.addElement(documentListLabel);

                    // Danh sách sách mượn
                    PdfPTable bookTable = new PdfPTable(3);
                    bookTable.setWidthPercentage(100);
                    bookTable.setSpacingBefore(10f);
                    bookTable.setWidths(new float[]{1, 6, 3});

                    // Header bảng
                    PdfPCell headerSTT = new PdfPCell(new Phrase("STT", boldFontSmall));
                    headerSTT.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerSTT.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    bookTable.addCell(headerSTT);

                    PdfPCell headerTitle = new PdfPCell(new Phrase("Tên sách", boldFontSmall));
                    headerTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    bookTable.addCell(headerTitle);

                    PdfPCell headerCode = new PdfPCell(new Phrase("Mã sách", boldFontSmall));
                    headerCode.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerCode.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    bookTable.addCell(headerCode);

                    // Dữ liệu sách mượn
                    List<BookBorrow> bookBorrows = borrowReceipt.getBookBorrows();
                    for (int k = 0; k < bookBorrows.size(); k++) {
                        BookBorrow bookBorrow = bookBorrows.get(k);
                        Book book = bookBorrow.getBook();

                        PdfPCell sttCell = new PdfPCell(new Phrase(String.valueOf(k + 1), boldFontMedium));
                        sttCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        bookTable.addCell(sttCell);

                        bookTable.addCell(new PdfPCell(new Phrase(book.getBookDefinition().getTitle(), normalFontMedium)));
                        bookTable.addCell(new PdfPCell(new Phrase(book.getBookCode(), normalFontMedium)));
                    }
                    receiptContainer.addElement(bookTable);

                    // Chữ ký
                    PdfPTable signatureTable = new PdfPTable(2);
                    signatureTable.setWidthPercentage(100);

                    PdfPCell borrowerLabelCell = new PdfPCell(new Phrase("Người mượn", normalFontMedium));
                    borrowerLabelCell.setBorder(Rectangle.NO_BORDER);
                    borrowerLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    signatureTable.addCell(borrowerLabelCell);

                    PdfPCell issuerLabelCell = new PdfPCell(new Phrase("Người lập phiếu", normalFontMedium));
                    issuerLabelCell.setBorder(Rectangle.NO_BORDER);
                    issuerLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    signatureTable.addCell(issuerLabelCell);

                    PdfPCell borrowerSignatureCell = new PdfPCell(new Phrase("", normalFontMedium));
                    borrowerSignatureCell.setBorder(Rectangle.NO_BORDER);
                    borrowerSignatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    signatureTable.addCell(borrowerSignatureCell);

                    PdfPCell issuerSignatureCell = new PdfPCell(new Phrase(user.getFullName(), normalFontMedium));
                    issuerSignatureCell.setBorder(Rectangle.NO_BORDER);
                    issuerSignatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    signatureTable.addCell(issuerSignatureCell);

                    receiptContainer.addElement(signatureTable);

                    mainTable.addCell(receiptContainer);
                }

                if (lop % 2 == 1) {
                    mainTable.completeRow();
                }

                document.add(mainTable);

                // Add new page except for the last page
                if (pageIndex < totalPages - 1) {
                    document.newPage();
                }
            }

            document.close();
        } catch (DocumentException e) {
            log.error("Error creating PDF: {}", e.getMessage(), e);
        }

        return outputStream.toByteArray();
    }

    @Override
    public byte[] createReceiptWithFourPerPage(CreateBorrowReceiptRequestDto requestDto) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            int receiptsPerPage = 4;
            int totalBorrowReceipts = 4;
            int totalPages = (int) Math.ceil((double) totalBorrowReceipts / receiptsPerPage);

            for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
                PdfPTable mainTable = new PdfPTable(2);
                mainTable.setWidthPercentage(100);
                mainTable.setSpacingBefore(10f);
                mainTable.setSpacingAfter(10f);

                int lop = Math.min((pageIndex + 1) * receiptsPerPage, totalBorrowReceipts);
                for (int i = pageIndex * receiptsPerPage; i < lop; i++) {
                    PdfPCell receiptContainer = new PdfPCell();
                    receiptContainer.setPadding(10);
                    receiptContainer.setBorder(Rectangle.BOX);
                    receiptContainer.setBorderColor(BaseColor.GRAY);

                    //Tiêu đề
                    Paragraph title = new Paragraph(requestDto.getSchoolName().toUpperCase(), boldFontLarge);
                    title.setAlignment(Element.ALIGN_CENTER);
                    receiptContainer.addElement(title);

                    Paragraph receiptInfo = new Paragraph("PHIẾU MƯỢN TÀI LIỆU", normalFontLarge);
                    receiptInfo.setAlignment(Element.ALIGN_CENTER);
                    receiptContainer.addElement(receiptInfo);

                    receiptContainer.addElement(new Paragraph("\n"));

                    Paragraph receiptNumber = new Paragraph(adjustLength("Số phiếu:", 20), normalFontMedium);
                    receiptNumber.setAlignment(Element.ALIGN_RIGHT);
                    receiptContainer.addElement(receiptNumber);

                    // Thông tin người mượn
                    Paragraph readerName = new Paragraph(adjustLength("Họ và tên:", 60), normalFontMedium);
                    readerName.setAlignment(Element.ALIGN_LEFT);
                    receiptContainer.addElement(readerName);

                    Paragraph readerCard = new Paragraph(adjustLength("Mã số thẻ:", 60), normalFontMedium);
                    readerCard.setAlignment(Element.ALIGN_LEFT);
                    receiptContainer.addElement(readerCard);

                    Paragraph borrowDate = new Paragraph(adjustLength("Ngày mượn:", 60), normalFontMedium);
                    borrowDate.setAlignment(Element.ALIGN_LEFT);
                    receiptContainer.addElement(borrowDate);

                    Paragraph dueDate = new Paragraph(adjustLength("Ngày hẹn trả:", 60), normalFontMedium);
                    dueDate.setAlignment(Element.ALIGN_LEFT);
                    receiptContainer.addElement(dueDate);

                    receiptContainer.addElement(new Paragraph("\n"));

                    Paragraph documentListLabel = new Paragraph("Danh sách tài liệu:", normalFontMedium);
                    documentListLabel.setAlignment(Element.ALIGN_LEFT);
                    receiptContainer.addElement(documentListLabel);

                    // Danh sách sách mượn
                    PdfPTable bookTable = new PdfPTable(3);
                    bookTable.setWidthPercentage(100);
                    bookTable.setSpacingBefore(10f);
                    bookTable.setWidths(new float[]{1, 6, 3});

                    // Header bảng
                    PdfPCell headerSTT = new PdfPCell(new Phrase("STT", boldFontSmall));
                    headerSTT.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerSTT.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    bookTable.addCell(headerSTT);

                    PdfPCell headerTitle = new PdfPCell(new Phrase("Tên sách", boldFontSmall));
                    headerTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerTitle.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    bookTable.addCell(headerTitle);

                    PdfPCell headerCode = new PdfPCell(new Phrase("Mã sách", boldFontSmall));
                    headerCode.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerCode.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    bookTable.addCell(headerCode);

                    // Dữ liệu sách mượn
                    for (int k = 0; k < 4; k++) {
                        PdfPCell sttCell = new PdfPCell(new Phrase(String.valueOf(k + 1), boldFontMedium));
                        sttCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        bookTable.addCell(sttCell);

                        bookTable.addCell(new PdfPCell(new Phrase("", normalFontMedium)));
                        bookTable.addCell(new PdfPCell(new Phrase("", normalFontMedium)));
                    }
                    receiptContainer.addElement(bookTable);

                    // Chữ ký
                    PdfPTable signatureTable = new PdfPTable(2);
                    signatureTable.setWidthPercentage(100);

                    PdfPCell borrowerLabelCell = new PdfPCell(new Phrase("Người mượn", normalFontMedium));
                    borrowerLabelCell.setBorder(Rectangle.NO_BORDER);
                    borrowerLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    signatureTable.addCell(borrowerLabelCell);

                    PdfPCell issuerLabelCell = new PdfPCell(new Phrase("Người lập phiếu", normalFontMedium));
                    issuerLabelCell.setBorder(Rectangle.NO_BORDER);
                    issuerLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    signatureTable.addCell(issuerLabelCell);

                    PdfPCell borrowerSignatureCell = new PdfPCell(new Phrase(" ", normalFontMedium));
                    borrowerSignatureCell.setBorder(Rectangle.NO_BORDER);
                    borrowerSignatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    signatureTable.addCell(borrowerSignatureCell);

                    PdfPCell issuerSignatureCell = new PdfPCell(new Phrase(" ", normalFontMedium));
                    issuerSignatureCell.setBorder(Rectangle.NO_BORDER);
                    issuerSignatureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    signatureTable.addCell(issuerSignatureCell);

                    receiptContainer.addElement(signatureTable);

                    mainTable.addCell(receiptContainer);
                }

                document.add(mainTable);

                // Add new page except for the last page
                if (pageIndex < totalPages - 1) {
                    document.newPage();
                }
            }

            document.close();
        } catch (DocumentException e) {
            log.error("Error creating PDF: {}", e.getMessage(), e);
        }

        return outputStream.toByteArray();
    }

    private String adjustLength(String input, int targetLength) {
        if (input.length() >= targetLength) {
            return input.substring(0, targetLength);
        } else {
            StringBuilder result = new StringBuilder(input);
            while (result.length() < targetLength) {
                result.append(".");
            }
            return result.toString();
        }
    }

    @Override
    public byte[] createPdfFromBooks(List<Book> books) {
        return new byte[0];
    }

    @Override
    public byte[] createLabelType1Pdf(List<Book> books) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            int labelsPerRow = 6;
            int totalLabels = books.size();

            PdfPTable table = new PdfPTable(labelsPerRow);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);

            float[] columnWidths = new float[labelsPerRow];
            Arrays.fill(columnWidths, 1f);
            table.setWidths(columnWidths);

            for (int i = 0; i < totalLabels; i++) {
                Book book = books.get(i);

                PdfPTable labelTable = new PdfPTable(1);
                labelTable.setWidthPercentage(100);

                PdfPCell nameCell = new PdfPCell(new Phrase("HAUI", boldFontMedium));
                nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                nameCell.setBorder(Rectangle.BOX);
                labelTable.addCell(nameCell);

                ClassificationSymbol classificationSymbol = book.getBookDefinition().getClassificationSymbol();
                PdfPCell khplCell = new PdfPCell(new Phrase(classificationSymbol != null ? classificationSymbol.getCode() : "KHPL", boldFontMedium));
                khplCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                khplCell.setBorder(Rectangle.BOX);
                labelTable.addCell(khplCell);

                PdfPCell sdkcbCell = new PdfPCell(new Phrase(book.getBookCode(), boldFontMedium));
                sdkcbCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                sdkcbCell.setBorder(Rectangle.BOX);
                labelTable.addCell(sdkcbCell);

                PdfPCell barcodeCell = new PdfPCell();
                barcodeCell.setBorder(Rectangle.BOX);
                barcodeCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                Barcode128 barcode = new Barcode128();
                barcode.setCode(book.getBookCode());
                barcode.setFont(null);
                barcode.setBarHeight(30f);
                barcode.setX(1f);

                Image barcodeImage = barcode.createImageWithBarcode(writer.getDirectContent(), null, null);
                barcodeImage.scalePercent(60);
                barcodeImage.setAlignment(Image.ALIGN_CENTER);
                barcodeCell.addElement(barcodeImage);
                labelTable.addCell(barcodeCell);

                table.addCell(labelTable);
            }

            int remainingCells = labelsPerRow - (totalLabels % labelsPerRow);
            if (remainingCells != labelsPerRow) {
                for (int i = 0; i < remainingCells; i++) {
                    PdfPCell emptyCell = new PdfPCell();
                    emptyCell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(emptyCell);
                }
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            log.error("Error creating LabelType1 PDF: {}", e.getMessage(), e);
        }

        return outputStream.toByteArray();
    }

    @Override
    public byte[] createLabelType2Pdf(List<Book> books) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            int labelsPerRow = 5;
            int totalLabels = books.size();

            PdfPTable table = new PdfPTable(labelsPerRow);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);

            float[] columnWidths = new float[labelsPerRow];
            Arrays.fill(columnWidths, 1f);
            table.setWidths(columnWidths);

            for (int i = 0; i < totalLabels; i++) {
                Book book = books.get(i);

                PdfPTable labelTable = new PdfPTable(2);
                labelTable.setWidths(new int[]{1, 3});
                labelTable.setWidthPercentage(100);

                PdfPCell sdkcbCell = new PdfPCell(new Phrase(book.getBookCode(), boldFontMedium));
                sdkcbCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                sdkcbCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                sdkcbCell.setBorder(Rectangle.BOX);
                sdkcbCell.setRowspan(4);
                sdkcbCell.setRotation(90);
                labelTable.addCell(sdkcbCell);

                PdfPCell khksCell = new PdfPCell(new Phrase("KHKS", boldFontMedium));
                khksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                khksCell.setBorder(Rectangle.BOX);
                labelTable.addCell(khksCell);

                ClassificationSymbol classificationSymbol = book.getBookDefinition().getClassificationSymbol();
                PdfPCell khplCell = new PdfPCell(new Phrase(classificationSymbol != null ? classificationSymbol.getCode() : "KHPL", boldFontMedium));
                khplCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                khplCell.setBorder(Rectangle.BOX);
                labelTable.addCell(khplCell);

                String code = book.getBookDefinition().getBookCode();
                PdfPCell khtsCell = new PdfPCell(new Phrase(code != null && !code.isEmpty() ? code : "KHTS", boldFontMedium));
                khtsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                khtsCell.setBorder(Rectangle.BOX);
                labelTable.addCell(khtsCell);

                PdfPCell barcodeCell = new PdfPCell();
                barcodeCell.setBorder(Rectangle.BOX);
                barcodeCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                Barcode128 barcode = new Barcode128();
                barcode.setCode(book.getBookCode());
                barcode.setFont(null);
                barcode.setBarHeight(30f);
                barcode.setX(1f);

                Image barcodeImage = barcode.createImageWithBarcode(writer.getDirectContent(), null, null);
                barcodeImage.scalePercent(60);
                barcodeImage.setAlignment(Image.ALIGN_CENTER);
                barcodeCell.addElement(barcodeImage);
                labelTable.addCell(barcodeCell);

                table.addCell(labelTable);
            }

            int remainingCells = labelsPerRow - (totalLabels % labelsPerRow);
            if (remainingCells != labelsPerRow) {
                for (int i = 0; i < remainingCells; i++) {
                    PdfPCell emptyCell = new PdfPCell();
                    emptyCell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(emptyCell);
                }
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            log.error("Error creating LabelType2 PDF: {}", e.getMessage(), e);
        }

        return outputStream.toByteArray();
    }

    @Override
    public byte[] createBookListPdf(List<Book> books) {
        return new byte[0];
    }

    @Override
    public byte[] createOverdueListPdf(List<BorrowReceipt> borrowReceipts) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Tiêu đề
            Paragraph title = new Paragraph("DANH SÁCH MƯỢN QUÁ HẠN CHƯA TRẢ", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Ngày tháng
            Paragraph date = new Paragraph("Đến ngày: " + LocalDate.now().format(formatter), boldFontMedium);
            date.setAlignment(Element.ALIGN_CENTER);
            date.setSpacingBefore(10);
            date.setSpacingAfter(20);
            document.add(date);

            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            float[] columnWidths = {0.5f, 2f, 2f, 2f, 2f, 3f, 2f, 2f, 1.5f};
            table.setWidths(columnWidths);

            String[] headers = {"STT", "Họ và tên", "Số điện thoại", "Số ĐKCB", "Nhan đề", "Tác giả", "Ngày mượn", "Ngày hẹn trả", "Số ngày quá hạn"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, boldFontSmall));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            int index = 1;
            for (BorrowReceipt receipt : borrowReceipts) {
                Reader reader = receipt.getReader();
                LocalDate borrowDate = receipt.getBorrowDate();
                LocalDate dueDate = receipt.getDueDate();

                for (BookBorrow bookBorrow : receipt.getBookBorrows()) {
                    PdfPCell cell;
                    cell = new PdfPCell(new Phrase(String.valueOf(index++), normalFontSmall));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    table.addCell(cell);

                    table.addCell(new PdfPCell(new Phrase(reader.getFullName(), normalFontSmall)));

                    table.addCell(new PdfPCell(new Phrase(reader.getPhoneNumber(), normalFontSmall)));

                    String bookCode = bookBorrow.getBook().getBookCode();
                    table.addCell(new PdfPCell(new Phrase(bookCode, normalFontSmall)));

                    String bookTitle = bookBorrow.getBook().getBookDefinition().getTitle();
                    table.addCell(new PdfPCell(new Phrase(bookTitle, normalFontSmall)));

                    String authors = bookBorrow.getBook().getBookDefinition().getBookAuthors().stream()
                            .map(BookAuthor::getAuthor)
                            .map(Author::getFullName)
                            .collect(Collectors.joining(", "));
                    table.addCell(new PdfPCell(new Phrase(authors, normalFontSmall)));

                    cell = new PdfPCell(new Phrase(borrowDate.toString(), normalFontSmall));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(dueDate.toString(), normalFontSmall));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    table.addCell(cell);

                    long overdueDays = DAYS.between(dueDate, LocalDate.now());
                    cell = new PdfPCell(new Phrase(String.valueOf(overdueDays), normalFontSmall));
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                    table.addCell(cell);
                }
            }

            // Thêm bảng vào tài liệu
            document.add(table);

            // Chữ ký
            Paragraph signature = new Paragraph("Hà Nội, ngày " +
                    LocalDate.now().getDayOfMonth() + " tháng " +
                    LocalDate.now().getMonthValue() + " năm " +
                    LocalDate.now().getYear() + "\nCán bộ thư viện", italicFontMedium);
            signature.setSpacingBefore(20);
            signature.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature);

        } catch (DocumentException e) {
            log.error("Error while creating PDF document", e);
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }
}
