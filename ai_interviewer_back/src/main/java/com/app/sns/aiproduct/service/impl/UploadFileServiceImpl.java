package com.app.sns.aiproduct.service.impl;

import com.app.sns.aiproduct.constant.ServiceCodeEnum;
import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.service.UploadFileService;
import com.app.sns.aiproduct.web.JsonResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UploadFileServiceImpl implements UploadFileService {
    @Override
    public JsonResult<StringBuilder> uploadFile(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();
        StringBuilder textContent = new StringBuilder();
        if (fileName.contains(".xlsx")) {
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                // Assuming there is only one sheet
                Sheet sheet = workbook.getSheetAt(0);

                // Iterate through each row in the sheet
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        // Append cell value to textContent
                        switch (cell.getCellType()) {
                            case STRING:
                                textContent.append(cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    textContent.append(cell.getDateCellValue());
                                } else {
                                    textContent.append(cell.getNumericCellValue());
                                }
                                break;
                            case BOOLEAN:
                                textContent.append(cell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                textContent.append(cell.getCellFormula());
                                break;
                            default:
                                textContent.append("");
                                break;
                        }
                        textContent.append("\t"); // Use tab as delimiter
                    }
                    textContent.append("\n"); // New line for each row
                }
                return JsonResult.ok(textContent);
            }
        } else if (fileName.contains(".pdf")){

            try {

                // 加载 PDF 文件
                PDDocument document = PDDocument.load(file.getInputStream());

                // 获取 PDF 文件的内容
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);
                textContent.append(text);

                // 关闭文档
                document.close();

                return JsonResult.ok(textContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (fileName.contains(".docx")||fileName.contains(".doc")){
            try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
                // 读取段落
                List<XWPFParagraph> paragraphs = document.getParagraphs();
                for (XWPFParagraph paragraph : paragraphs) {
                    textContent.append(paragraph.getText()).append("\n");
                }

                // 读取表格
                List<XWPFTable> tables = document.getTables();
                for (XWPFTable table : tables) {
                    for (XWPFTableRow row : table.getRows()) {
                        row.getTableCells().forEach(cell -> textContent.append(cell.getText()).append("\t"));
                        textContent.append("\n");
                    }
                }
                return JsonResult.ok(textContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            throw new ServiceException(ServiceCodeEnum.ERR_FILE_EMPTY);
        }

        return null;

    }
}

