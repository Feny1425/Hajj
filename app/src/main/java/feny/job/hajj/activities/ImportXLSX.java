package feny.job.hajj.activities;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import static feny.job.hajj.Data.getHajjiByPassport;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import feny.job.hajj.Hajji;
import feny.job.hajj.R;
import feny.job.hajj.Utils.FileUtils;

public class ImportXLSX extends AppCompatActivity {

    private Uri fileUri;
    private TextView context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_csv_activity);
        start();
    }

    private void start() {
        context = ((TextView)findViewById(R.id.context));
        findViewById(R.id.back).setOnClickListener(view -> {
            finish();
        });
        findViewById(R.id.btnViewSDCard).setOnClickListener(view -> {
            openFile(101);
        });
        findViewById(R.id.addSerialAndBus).setOnClickListener(view -> {
            openFile(105);
        });
        findViewById(R.id.updateState).setOnClickListener(view -> {
            openFile(110);
        });
    }

    private void openFile(int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(Environment.isExternalStorageManager()){
                Intent intent = new Intent()
                        .setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        .setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, "Select a file"), i);

            }
            else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package",getPackageName(),null);
                startActivity(intent);

            }
        }
        else {
            Intent intent = new Intent();
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.putExtra(Intent.EXTRA_AUTO_LAUNCH_SINGLE_CHOICE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);

            ActivityCompat.requestPermissions(ImportXLSX.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && data != null){
            fileUri = data.getData();


            try {
                uploadHajjiToFirebaseAndSaveLocally(readXLSXFile(getApplicationContext(), fileUri),this);
            } catch (IOException | InvalidFormatException e) {
                throw new RuntimeException(e);
            }
        }
        if(requestCode == 105 && data != null){
            fileUri = data.getData();


            try {
                uploadHajjiToFirebaseAndSaveLocally(readXLSXFileSB(getApplicationContext(), fileUri),this);
            } catch (IOException | InvalidFormatException e) {
                throw new RuntimeException(e);
            }

        }
        if(requestCode == 110 && data != null){
            fileUri = data.getData();


            try {
                uploadHajjiToFirebaseAndSaveLocally(readXLSXFileUpdateState(getApplicationContext(), fileUri),this);
            } catch (IOException | InvalidFormatException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public ArrayList<Hajji> readXLSXFileUpdateState(Context context, Uri uri) throws IOException, InvalidFormatException {


        File file = FileUtils.getFileFromUri(context, uri);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<Hajji> hajjis = HajjiListActivity.retrieveHajjiListFromLocalStorage(this);
        ArrayList<Hajji> updatedHajjis = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() < 1) continue;
            Hajji hajji = getHajjiByPassport(hajjis,row.getCell(1).getStringCellValue());
            if(hajji == null) continue;
            Cell cell = row.getCell(0);
            hajji.setState((int)cell.getNumericCellValue());
            updatedHajjis.add(hajji);
        }
        workbook.close();
        return updatedHajjis;
    }
    public ArrayList<Hajji> readXLSXFileSB(Context context, Uri uri) throws IOException, InvalidFormatException {


        File file = FileUtils.getFileFromUri(context, uri);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<Hajji> hajjis = HajjiListActivity.retrieveHajjiListFromLocalStorage(this);
        ArrayList<Hajji> updatedHajjis = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() < 1) continue;
            Hajji hajji = getHajjiByPassport(hajjis,row.getCell(2).getStringCellValue());
            if(hajji == null) continue;
            for (int i = 0; i < 2; i++) { // Assuming 13 columns in the row
                Cell cell = row.getCell(i);
                if (cell != null) {
                    switch (i) {
                        case 0:
                            hajji.setSerial((int)cell.getNumericCellValue());
                            break;
                        case 1:
                            hajji.setBus((int)cell.getNumericCellValue());
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
    public ArrayList<Hajji> readXLSXFile(Context context, Uri uri) throws IOException, InvalidFormatException {

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
                            hajji.setSI(String.valueOf((int)cell.getNumericCellValue()));
                            break;
                        case 1:
                            hajji.setPID(cell.getStringCellValue());
                            break;
                        case 2:
                            hajji.setUnit(cell.getStringCellValue());
                            break;
                        case 3:
                            hajji.setTrackingNo(cell.getStringCellValue());
                            break;
                        case 4:
                            hajji.setName(cell.getStringCellValue());
                            break;
                        case 5:
                            hajji.setGender(cell.getStringCellValue().toLowerCase().equals("male"));
                            break;
                        case 6:
                            hajji.setPassport(cell.getStringCellValue());
                            break;
                        case 8:
                            hajji.setGuide(cell.getStringCellValue());
                            break;
                        case 9:
                            hajji.setFlight(cell.getStringCellValue());
                            break;
                        case 10:
                            hajji.setHouseNumber(cell.getStringCellValue());
                            break;
                        case 11:
                            hajji.setRoomNumber(cell.getStringCellValue());
                            break;
                        case 12:
                            hajji.setMaktabNumber(cell.getStringCellValue());
                            break;
                    }
                }
            }
            hajjis.add(hajji);
        }
        workbook.close();
        return hajjis;
    }


    public void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }


    private void uploadHajjiToFirebaseAndSaveLocally(ArrayList<Hajji> hajjiList, Context context) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hajji");


        // Create a map to store hajji objects with passport number as key
        Map<String, Hajji> hajjiMap = new HashMap<>();

        // Populate the map with hajji objects, using passport number as key
        for (Hajji hajji : hajjiList) {
            // Use passport number as the key
            String passportNumber = hajji.getPassport();
            saveHajjiLocally(context,hajji);

            // Only add the hajji object to the map if it doesn't already exist with the same passport number
            if (!hajjiMap.containsKey(passportNumber)) {
                hajjiMap.put(passportNumber, hajji);
            }
        }

        // Upload hajji objects to Firebase and save locally
        for (Map.Entry<String, Hajji> entry : hajjiMap.entrySet()) {
            String passportNumber = entry.getKey();
            Hajji hajji = entry.getValue();

            // Push creates a unique key for each hajji based on passport number
            databaseReference.child(passportNumber).setValue(hajji)
                    .addOnSuccessListener(aVoid -> {
                        // Data uploaded successfully
                        Log.d("Firebase", "Hajji uploaded successfully");
                        this.context.setText("Hajji uploaded successfully");
                    })
                    .addOnFailureListener(e -> {
                        // Failed to upload data
                        Log.e("Firebase", "Error uploading Hajji: " + e.getMessage());
                        this.context.setText("Error uploading Hajji: " + e.getMessage());
                    });
        }
    }

    // Method to save Hajji locally
    // Method to save Hajji locally
    private void saveHajjiLocally(Context context, Hajji hajji) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("hajji_prefs", Context.MODE_PRIVATE);

        // Check if a Hajji object with the same passport number already exists
        if (!isHajjiSavedLocally(sharedPreferences, hajji.getPassport())) {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Serialize Hajji object to JSON
            Gson gson = new Gson();
            String json = gson.toJson(hajji);

            // Save JSON string in SharedPreferences with passport number as key
            editor.putString(hajji.getPassport(), json);
            editor.apply();
        } else {
            // Hajji object with the same passport number already exists, do not save
            Log.d("LocalSave", "Hajji with passport " + hajji.getPassport() + " already exists locally, skipping save.");
        }
    }

    // Method to check if a Hajji object with the given passport number is already saved locally
    private boolean isHajjiSavedLocally(SharedPreferences sharedPreferences, String passportNumber) {
        return sharedPreferences.contains(passportNumber);
    }


}
