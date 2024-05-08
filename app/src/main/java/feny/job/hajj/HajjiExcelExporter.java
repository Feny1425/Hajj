package feny.job.hajj;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class HajjiExcelExporter {

    public static boolean exportToExcel(Context context, List<Hajji> hajjiList, String fileName) {
        // Create a new workbook
        Workbook workbook = new XSSFWorkbook();
        // Create a sheet
        Sheet sheet = workbook.createSheet("Hajji Data");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Serial");
        headerRow.createCell(1).setCellValue("PID");
        headerRow.createCell(2).setCellValue("Unit");
        headerRow.createCell(3).setCellValue("Tracking No");
        headerRow.createCell(4).setCellValue("Name");
        headerRow.createCell(5).setCellValue("Gender");
        headerRow.createCell(6).setCellValue("Passport");
        headerRow.createCell(7).setCellValue("Guide");
        headerRow.createCell(8).setCellValue("Flight");
        headerRow.createCell(9).setCellValue("House Number");
        headerRow.createCell(10).setCellValue("Room Number");
        headerRow.createCell(11).setCellValue("Bus Number");
        headerRow.createCell(12).setCellValue("State");

        // Populate data rows
        int rowNum = 1;
        for (Hajji hajji : hajjiList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(hajji.getSerial());
            row.createCell(1).setCellValue(hajji.getPID());
            row.createCell(2).setCellValue(hajji.getUnit());
            row.createCell(3).setCellValue(hajji.getTrackingNo());
            row.createCell(4).setCellValue(hajji.getName());
            row.createCell(5).setCellValue(hajji.isGender() ? "Male" : "Female");
            row.createCell(6).setCellValue(hajji.getPassport());
            row.createCell(7).setCellValue(hajji.getGuide());
            row.createCell(8).setCellValue(hajji.getFlight());
            row.createCell(9).setCellValue(hajji.getHouseNumber());
            row.createCell(10).setCellValue(hajji.getRoomNumber());
            row.createCell(11).setCellValue(hajji.getBus());
            row.createCell(12).setCellValue(hajji.getStateName());
        }

        // Save workbook to file
        try {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(directory, fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            return true;
        } catch (IOException e) {
            Log.e("HajjiExcelExporter", "Error exporting Hajji data to Excel", e);
            return false;
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                Log.e("HajjiExcelExporter", "Error closing workbook", e);
            }
        }
    }
}
