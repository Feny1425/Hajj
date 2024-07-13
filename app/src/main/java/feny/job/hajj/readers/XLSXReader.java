package feny.job.hajj.readers;


import static feny.job.hajj.custom.Data.hajjis;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import feny.job.hajj.Utils.FileUtils;
import feny.job.hajj.custom.Hajji;
import feny.job.hajj.custom.HajjiList;

public class XLSXReader {
static String TAG = "XLSXReader";
    public static ArrayList<Hajji> readXLSXFileUpdateState(Context context, Uri uri) throws IOException, InvalidFormatException {


        File file = FileUtils.getFileFromUri(context, uri);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<Hajji> updatedHajjis = new ArrayList<>();
        for (Row row : sheet) {
            try {
                if (row.getRowNum() < 1) continue;
                Hajji hajji = hajjis.getHajjiByPassport(row.getCell(1).getStringCellValue());
                if(hajji == null) continue;
                Cell cell = row.getCell(0);
                if (cell.getCellType() == CellType.NUMERIC) {
                    int value = (int) cell.getNumericCellValue();
                    hajji.setState(value);
                } else {
                    String value = cell.getStringCellValue();
                    hajji.setState(Integer.parseInt(value));
                    // Handle the case where the cell does not contain a numeric value
                }
                updatedHajjis.add(hajji);
            }
            catch (Exception e){
                Log.e(TAG,"readXLSXFileUpdateState :" + e.getMessage());
            }
        }
        workbook.close();
        return updatedHajjis;
    }

    public static ArrayList<Hajji> readXLSXFileAddVisa(Context context, Uri uri) throws IOException, InvalidFormatException {


        File file = FileUtils.getFileFromUri(context, uri);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<Hajji> updatedHajjis = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() < 1) continue;
            Hajji hajji = hajjis.getHajjiByPassport(row.getCell(1).getStringCellValue());
            if(hajji == null) continue;
            Cell cell = row.getCell(0);
            if (cell.getCellType() == CellType.NUMERIC) {
                long value = (long) cell.getNumericCellValue();
                hajji.setVisa(value);
            } else {
                String value = cell.getStringCellValue();
                hajji.setVisa(Long.parseLong(value));
                // Handle the case where the cell does not contain a numeric value
            }
            updatedHajjis.add(hajji);
        }
        workbook.close();
        return updatedHajjis;
    }
    public static ArrayList<Hajji> readXLSXFileUpdateGender(Context context, Uri uri) throws IOException, InvalidFormatException {


        File file = FileUtils.getFileFromUri(context, uri);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<Hajji> updatedHajjis = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() < 1) continue;
            Hajji hajji = hajjis.getHajjiByPassport(row.getCell(1).getStringCellValue());
            if(hajji == null) continue;
            Cell cell = row.getCell(0);
            hajji.setGender(cell.getStringCellValue().equals("male"));
            updatedHajjis.add(hajji);
        }
        workbook.close();
        return updatedHajjis;
    }
    public static ArrayList<Hajji> readXLSXFileUpdateHajjiCode(Context context, Uri uri) throws IOException, InvalidFormatException {


        File file = FileUtils.getFileFromUri(context, uri);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<Hajji> updatedHajjis = new ArrayList<>();
        for (Row row : sheet) {
            int rowNum = row.getRowNum();
            if(row.getCell(0) == null)
                break;
            if (sheet.getFirstRowNum() == rowNum)
                continue;

            Hajji hajji = hajjis.getHajjiByPassport(row.getCell(1).getStringCellValue());
            if(hajji == null)
                continue;
            Cell cell = row.getCell(0);
            if (cell.getCellType() == CellType.NUMERIC) {
                int value = (int) cell.getNumericCellValue();
                hajji.setCode(value);
            } else {
                String value = cell.getStringCellValue();
                hajji.setCode(Integer.parseInt(value));
                // Handle the case where the cell does not contain a numeric value
            }
            updatedHajjis.add(hajji);
        }
        workbook.close();
        return updatedHajjis;
    }
    public static ArrayList<Hajji> readXLSXFileSB(Context context, Uri uri) throws IOException, InvalidFormatException {


        File file = FileUtils.getFileFromUri(context, uri);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<Hajji> updatedHajjis = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() < 1) continue;
            Hajji hajji = hajjis.getHajjiByPassport(row.getCell(2).getStringCellValue());
            if(hajji == null) continue;
            for (int i = 0; i < 2; i++) { // Assuming 13 columns in the row
                Cell cell = row.getCell(i);
                if (cell != null) {
                    switch (i) {
                        case 0:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                int value = (int) cell.getNumericCellValue();
                                hajji.setSerial(value);
                            } else {
                                String value = cell.getStringCellValue();
                                hajji.setSerial(Integer.parseInt(value));
                                // Handle the case where the cell does not contain a numeric value
                            }
                            break;
                        case 1:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                int value = (int) cell.getNumericCellValue();
                                hajji.setBus(value);
                            } else {
                                String value = cell.getStringCellValue();
                                hajji.setBus(Integer.parseInt(value));
                                // Handle the case where the cell does not contain a numeric value
                            }
                            break;
                    }
                }
            }
            updatedHajjis.add(hajji);
        }
        workbook.close();
        return updatedHajjis;
    }

    // Update your method to use Context to get the file path
    public static ArrayList<Hajji> readXLSXFile(Context context, Uri uri) throws IOException, InvalidFormatException {

        File file = FileUtils.getFileFromUri(context, uri);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<Hajji> hajjis = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() < 2) continue;
            Hajji hajji = new Hajji();
            for (int i = 0; i < 13; i++) { // Assuming 13 columns in the row
                Cell cell = row.getCell(i);
                if (cell != null) {
                    switch (i) {
                        case 0:
                            //hajji.setSI(String.valueOf((int)cell.getNumericCellValue()));
                            break;
                        case 1:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                int value = (int) cell.getNumericCellValue();
                                hajji.setPID(value);
                            } else {
                                String value = cell.getStringCellValue();
                                hajji.setPID(Integer.parseInt(value));
                                // Handle the case where the cell does not contain a numeric value
                            }
                            break;
                        case 2:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                int value = (int) cell.getNumericCellValue();
                                hajji.setUnit(value);
                            } else {
                                String value = cell.getStringCellValue();
                                hajji.setUnit(Integer.parseInt(value));
                                // Handle the case where the cell does not contain a numeric value
                            }
                            break;
                        case 3:
                            hajji.setTrackingNo(cell.getStringCellValue());
                            break;
                        case 4:
                            hajji.setName(cell.getStringCellValue());
                            break;
                        case 5:
                            hajji.setGender(cell.getStringCellValue().equalsIgnoreCase("male"));
                            break;
                        case 6:
                            hajji.setPassport(cell.getStringCellValue());
                            break;
                        case 8:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                int guideValue = (int) cell.getNumericCellValue();
                                hajji.setGuide(guideValue);
                            } else {
                                String guideValue = cell.getStringCellValue();
                                if(guideValue.equals(""))
                                    hajji.setGuide(0);
                                else
                                    hajji.setGuide(Integer.parseInt(guideValue));
                                // Handle the case where the cell does not contain a numeric value
                            }


                            break;
                        case 9:
                            hajji.setFlight(cell.getStringCellValue());
                            break;
                        case 10:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                int value = (int) cell.getNumericCellValue();
                                hajji.setHouseNumber(value);
                            } else {
                                String value = cell.getStringCellValue();
                                hajji.setHouseNumber(Integer.parseInt(value));
                                // Handle the case where the cell does not contain a numeric value
                            }
                            break;
                        case 11:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                int value = (int) cell.getNumericCellValue();
                                hajji.setRoomNumber(value);
                            } else {
                                String value = cell.getStringCellValue();
                                hajji.setRoomNumber(Integer.parseInt(value));
                                // Handle the case where the cell does not contain a numeric value
                            }
                            break;
                        case 12:
                            if (cell.getCellType() == CellType.NUMERIC) {
                                int value = (int) cell.getNumericCellValue();
                                hajji.setMaktabNumber(value);
                            } else {
                                String value = cell.getStringCellValue();
                                hajji.setMaktabNumber(Integer.parseInt(value));
                                // Handle the case where the cell does not contain a numeric value
                            }
                            break;
                    }
                }
            }
            if(hajji.getGuide() == 0){
                hajji.setGuide(hajji.getPID());
            }
            hajjis.add(hajji);
        }
        HajjiList.PIDAccording(hajjis);
        HajjiList.GuideAccording(hajjis);
        double guide = -1;
        int bus = 0;
        for(Hajji hajji : hajjis){
            if(hajji.getGuide() != guide){
                bus++;
                guide = hajji.getGuide();
            }
            hajji.setBus(bus);
        }
        workbook.close();
        return hajjis;
    }


}
