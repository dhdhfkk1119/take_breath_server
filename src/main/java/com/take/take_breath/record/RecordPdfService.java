package com.take.take_breath.record;


import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
//import com.itextpdf.layout.properties.TextAlignment;
//import com.itextpdf.layout.properties.UnitValue;
//import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.properties.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RecordPdfService {

    // ⭐ application.yml에서 upload.root-dir 설정값 주입
    @Value("${upload.root-dir}")
    private String uploadRootDir;

    // 한글 폰트 경로 (프로젝트의 resources/fonts 폴더에 넣어두세요)
    private static final String FONT_PATH = "fonts/NanumGothic.ttf";
    private static final String FONT_BOLD_PATH = "fonts/NanumGothicBold.ttf";

    // 컬러 정의
    private static final Color COLOR_PRIMARY = new DeviceRgb(52, 152, 219);    // #3498db
    private static final Color COLOR_DARK = new DeviceRgb(44, 62, 80);         // #2c3e50
    private static final Color COLOR_GRAY = new DeviceRgb(127, 140, 141);      // #7f8c8d
    private static final Color COLOR_LIGHT_BG = new DeviceRgb(236, 240, 241);  // #ecf0f1
    private static final Color COLOR_BORDER = new DeviceRgb(189, 195, 199);    // #bdc3c7

    public byte[] generatePdf(Record record, List<RecordFile> recordFiles) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4);

        Document document = new Document(pdfDoc);
        document.setMargins(56.7f, 56.7f, 56.7f, 56.7f); // 2cm margins

        // 폰트 로드 (한글 지원)
        PdfFont fontRegular = loadFont(FONT_PATH);
        PdfFont fontBold = loadFont(FONT_BOLD_PATH);

        // 1. 제목 섹션
        // addTitle(document, record.getTitle(), fontBold);

        // 1. 제목
        addTitle(document, "기록", fontBold);

        // 2. 구분선
        addDivider(document, COLOR_PRIMARY, 2f);
        document.add(new Paragraph("\n").setMarginBottom(10));

        // 3. 메타 정보 테이블
        addMetaInfoTable(document, record, recordFiles, fontBold, fontRegular);
        document.add(new Paragraph("\n").setMarginBottom(15));

        // 4-1. 제목 섹션
        addContentSection(document, "제목", record.getTitle(), fontBold, fontRegular);
        document.add(new Paragraph("\n").setMarginBottom(20));
        // 4-2. 내용 섹션
        addContentSection(document, "내용", record.getContent(), fontBold, fontRegular);
        document.add(new Paragraph("\n").setMarginBottom(20));

        // 5. 첨부 이미지 섹션
        if (recordFiles != null && !recordFiles.isEmpty()) {
            addImagesSection(document, recordFiles, fontBold, fontRegular);
        }

        // 6. 푸터
        addFooter(document, fontRegular);

        document.close();
        return baos.toByteArray();
    }

    /**
     * 폰트 로드 (fallback 포함)
     */
    private PdfFont loadFont(String fontPath) throws Exception {
        try {
            // classpath에서 폰트 로드
            return PdfFontFactory.createFont(
                    getClass().getClassLoader().getResource(fontPath).getPath(),
                    PdfEncodings.IDENTITY_H
            );
        } catch (Exception e) {
            // fallback - 시스템 폰트 사용 (Linux 서버 기준)
            try {
                return PdfFontFactory.createFont(
                        "/usr/share/fonts/truetype/nanum/NanumGothic.ttf",
                        PdfEncodings.IDENTITY_H
                );
            } catch (Exception e2) {
                // 최종 fallback - 기본 폰트 (한글 깨짐)
                return PdfFontFactory.createFont();
            }
        }
    }

    /**
     * 제목 추가
     */
    private void addTitle(Document document, String title, PdfFont font) {
        Paragraph titlePara = new Paragraph(title)
                .setFont(font)
                .setFontSize(24)
                .setFontColor(COLOR_DARK)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(titlePara);
    }

    /**
     * 구분선 추가
     */
    private void addDivider(Document document, Color color, float width) {
        Table divider = new Table(1);
        divider.setWidth(UnitValue.createPercentValue(100));

        Cell cell = new Cell()
                .setBorder(null)
                .setBorderTop(new SolidBorder(color, width))
                .setHeight(0);
        divider.addCell(cell);

        document.add(divider);
    }

    /**
     * 메타 정보 테이블 추가
     */
    private void addMetaInfoTable(Document document, Record record, List<RecordFile> files,
                                  PdfFont fontBold, PdfFont fontRegular) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Table table = new Table(new float[]{3, 14});
        table.setWidth(UnitValue.createPercentValue(100));

        // Record ID
        // addMetaRow(table, "Record ID:", "#" + record.getId(), fontBold, fontRegular);

        // 작성일
        addMetaRow(table, "작성일:",
                record.getRecordDate().toLocalDateTime().format(formatter), fontBold, fontRegular);

        // 수정일
        addMetaRow(table, "수정일:",
                record.getUpdatedDate().toLocalDateTime().format(formatter), fontBold, fontRegular);

        // 첨부 파일 수
        addMetaRow(table, "첨부 파일:",
                (files != null ? files.size() : 0) + "개", fontBold, fontRegular);

        document.add(table);
    }

    /**
     * 메타 정보 행 추가
     */
    private void addMetaRow(Table table, String label, String value,
                            PdfFont fontBold, PdfFont fontRegular) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label))
                .setFont(fontBold)
                .setFontSize(10)
                .setFontColor(COLOR_GRAY)
                .setBorder(null)
                .setPaddingBottom(8);

        Cell valueCell = new Cell()
                .add(new Paragraph(value))
                .setFont(fontRegular)
                .setFontSize(10)
                .setFontColor(COLOR_DARK)
                .setBorder(null)
                .setPaddingBottom(8);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    /**
     * 본문 섹션 추가
     */
    private void addContentSection(Document document, String title, String content,
                                   PdfFont fontBold, PdfFont fontRegular) {
        // 섹션 제목
        Paragraph sectionName = new Paragraph(title)
                .setFont(fontBold)
                .setFontSize(14)
                .setFontColor(COLOR_DARK)
                .setMarginBottom(12);
        document.add(sectionName);

        // 내용 박스
        Table contentBox = new Table(1);
        contentBox.setWidth(UnitValue.createPercentValue(100));

        Cell contentCell = new Cell()
                .add(new Paragraph(content)
                        .setFont(fontRegular)
                        .setFontSize(11)
                        .setMultipliedLeading(1.5f))
                .setBackgroundColor(COLOR_LIGHT_BG)
                .setBorder(new SolidBorder(COLOR_BORDER, 1))
                .setPadding(15);

        contentBox.addCell(contentCell);
        document.add(contentBox);
    }

    /**
     * 첨부 이미지 섹션 추가
     */
    private void addImagesSection(Document document, List<RecordFile> files,
                                  PdfFont fontBold, PdfFont fontRegular) {
        // 섹션 제목
        Paragraph sectionTitle = new Paragraph("첨부 이미지")
                .setFont(fontBold)
                .setFontSize(14)
                .setFontColor(COLOR_DARK)
                .setMarginBottom(12)
                .setMarginTop(20);
        document.add(sectionTitle);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < files.size(); i++) {
            RecordFile file = files.get(i);

            // 이미지 메타 정보
            Table metaTable = new Table(new float[]{2.5f, 14.5f});
            metaTable.setWidth(UnitValue.createPercentValue(100));

            addMetaRow(metaTable, "파일명:", file.getOriginalFileName(), fontBold, fontRegular);
            // addMetaRow(metaTable, "크기:", String.format("%,d bytes", file.getFileSize()), fontBold, fontRegular);
            addMetaRow(metaTable, "업로드:", file.getCreatedAt().toLocalDateTime().format(formatter), fontBold, fontRegular);

            document.add(metaTable);
            document.add(new Paragraph("\n").setMarginBottom(5));

            // ⭐ 실제 이미지 추가 (수정됨 - 올바른 절대 경로 구성)
            try {
                String dbFilePath = file.getFilePath(); // DB에 저장된 상대 경로 (예: /uploads/records/images/xxx.png)
                System.out.println("📁 DB에 저장된 경로: " + dbFilePath);
                System.out.println("📂 uploadRootDir 설정값: " + uploadRootDir);

                // ⭐ 올바른 절대 경로 구성
                String absolutePath;

                // uploadRootDir이 상대 경로인 경우 (예: ./uploads/)
                if (uploadRootDir.startsWith(".")) {
                    // Working directory에 상대 경로 결합
                    String baseDir = Paths.get(System.getProperty("user.dir"), uploadRootDir)
                            .normalize().toString();

                    // DB의 /uploads/... 에서 /uploads 부분 제거
                    String relativePart = dbFilePath.replaceFirst("^/uploads/", "");

                    absolutePath = Paths.get(baseDir, relativePart).normalize().toString();
                } else {
                    // uploadRootDir이 이미 절대 경로인 경우
                    String relativePart = dbFilePath.replaceFirst("^/uploads/", "");
                    absolutePath = Paths.get(uploadRootDir, relativePart).normalize().toString();
                }

                System.out.println("🔍 최종 절대 경로: " + absolutePath);
                System.out.println("📂 현재 working directory: " + System.getProperty("user.dir"));

                File imageFile = new File(absolutePath);

                if (imageFile.exists()) {
                    System.out.println("✅ 파일 존재: " + absolutePath);
                    Image img = new Image(ImageDataFactory.create(absolutePath));

                    // 이미지 크기 조절 (최대 너비: 15cm = 425pt)
                    float maxWidth = 425f;
                    if (img.getImageWidth() > maxWidth) {
                        img.scaleToFit(maxWidth, 600f);
                    }

                    // 이미지를 테두리 박스로 감싸기
                    Table imgFrame = new Table(1);
                    imgFrame.setWidth(UnitValue.createPercentValue(100));

                    Cell imgCell = new Cell()
                            .add(img)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                            .setBorder(new SolidBorder(COLOR_BORDER, 1))
                            .setBackgroundColor(new DeviceRgb(255, 255, 255))
                            .setPadding(10);

                    imgFrame.addCell(imgCell);
                    document.add(imgFrame);
                } else {
                    // 이미지 파일이 없는 경우
                    System.out.println("❌ 파일 없음: " + absolutePath);
                    System.out.println("📂 현재 working directory: " + System.getProperty("user.dir"));
                    System.out.println("💾 uploadRootDir: " + uploadRootDir);

                    Paragraph errorMsg = new Paragraph("[이미지를 로드할 수 없습니다: " + file.getFileName() + "]")
                            .setFont(fontRegular)
                            .setFontSize(10)
                            .setFontColor(new DeviceRgb(192, 57, 43))
                            .setTextAlignment(TextAlignment.CENTER);
                    document.add(errorMsg);
                }
            } catch (Exception e) {
                // 이미지 로드 실패
                System.out.println("⚠️ 이미지 로드 실패: " + e.getMessage());
                e.printStackTrace();
                Paragraph errorMsg = new Paragraph("[이미지 로드 실패: " + e.getMessage() + "]")
                        .setFont(fontRegular)
                        .setFontSize(10)
                        .setFontColor(new DeviceRgb(192, 57, 43))
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(errorMsg);
            }

            document.add(new Paragraph("\n").setMarginBottom(15));

            // 이미지 간 구분선 (마지막 이미지가 아닌 경우)
            if (i < files.size() - 1) {
                addDivider(document, COLOR_BORDER, 0.5f);
                document.add(new Paragraph("\n").setMarginBottom(15));
            }
        }
    }

    /**
     * 푸터 추가
     */
    private void addFooter(Document document, PdfFont font) {
        document.add(new Paragraph("\n").setMarginTop(30));
        addDivider(document, COLOR_LIGHT_BG, 1f);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = java.time.LocalDateTime.now().format(formatter);

        Paragraph footer = new Paragraph("Generated on " + timestamp)
                .setFont(font)
                .setFontSize(8)
                .setFontColor(new DeviceRgb(149, 165, 166))
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5);

        document.add(footer);
    }
}