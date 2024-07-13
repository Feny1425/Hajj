package feny.job.hajj.readers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import feny.job.hajj.custom.Hajji;
import feny.job.hajj.custom.HajjiList;

public class HajjiExcelExporter {

    public static boolean exportToExcel(Context context, ArrayList<Hajji> hajjiList, String fileName){
        HajjiList.PIDAccording(hajjiList);

        // Create a new workbook
        Workbook workbook = new XSSFWorkbook();
        // Create a sheet
        Sheet sheet = workbook.createSheet("Hajji Data");
        int i = 0;
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(i++).setCellValue("SI.");
        headerRow.createCell(i++).setCellValue("Serial");
        headerRow.createCell(i++).setCellValue("PID");
        headerRow.createCell(i++).setCellValue("Unit");
        headerRow.createCell(i++).setCellValue("Tracking No");
        headerRow.createCell(i++).setCellValue("Name");
        headerRow.createCell(i++).setCellValue("Gender");
        headerRow.createCell(i++).setCellValue("Passport");
        headerRow.createCell(i++).setCellValue("Visa");
        headerRow.createCell(i++).setCellValue("Guide");
        headerRow.createCell(i++).setCellValue("Flight");
        headerRow.createCell(i++).setCellValue("House Number");
        headerRow.createCell(i++).setCellValue("Room Number");
        headerRow.createCell(i++).setCellValue("Bus Number");
        headerRow.createCell(i++).setCellValue("State");

        // Populate data rows
        int rowNum = 1;
        i = 0;
        for (Hajji hajji : hajjiList) {
            Row row = sheet.createRow(rowNum);
            row.createCell(i++).setCellValue(rowNum++);
            row.createCell(i++).setCellValue(hajji.getSerial());
            row.createCell(i++).setCellValue(hajji.getPID());
            row.createCell(i++).setCellValue(hajji.getUnit());
            row.createCell(i++).setCellValue(hajji.getTrackingNo());
            row.createCell(i++).setCellValue(hajji.getName());
            row.createCell(i++).setCellValue(hajji.isGender() ? "Male" : "Female");
            row.createCell(i++).setCellValue(hajji.getPassport());
            row.createCell(i++).setCellValue(hajji.getVisa());
            row.createCell(i++).setCellValue(hajji.getGuide());
            row.createCell(i++).setCellValue(hajji.getFlight());
            row.createCell(i++).setCellValue(hajji.getHouseNumber());
            row.createCell(i++).setCellValue(hajji.getRoomNumber());
            row.createCell(i++).setCellValue(hajji.getBus());
            row.createCell(i).setCellValue(hajji.getStateName());
            i = 0;
        }

        // Save workbook to file
        try {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/hajj");
            if (!directory.exists()) {
                directory.mkdirs(); // Create the directory if it does not exist
            }
            File file = new File(directory, fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();

            // Open the file
            openFile(context, file);

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
    private static void openFile(Context context, File file) {
        try {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "Open file with"));
        } catch (Exception e) {
            Log.e("HajjiExcelExporter", "Error opening file", e);
        }
    }
}
