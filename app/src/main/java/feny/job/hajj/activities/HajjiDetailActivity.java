package feny.job.hajj.activities;

import static feny.job.hajj.Data.CAN_EDIT;
import static feny.job.hajj.Data.DEATH;
import static feny.job.hajj.Data.FINAL;
import static feny.job.hajj.Data.MADINA;
import static feny.job.hajj.Data.MAKKAH;
import static feny.job.hajj.Data.MISSION;
import static feny.job.hajj.Data.NOT_ARRIVED;
import static feny.job.hajj.Data.NOT_COMING;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import feny.job.hajj.Hajji;
import feny.job.hajj.R;

public class HajjiDetailActivity extends AppCompatActivity {
    Gson gson;
    Hajji hajji;
    TextView nameTextView;
    TextView passportTextView;
    TextView unitTextView;
    TextView genderTextView;
    TextView phoneNumberTextView;
    TextView guideTextView;
    TextView flightTextView;
    TextView houseNumberTextView;
    TextView roomNumberTextView;
    TextView maktabNumberTextView;
    TextView stateTextView;
    TextView serialTextView;
    TextView busTextView;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hajji_detail);

        gson = new Gson();
        // Retrieve Hajji object from intent
        // Retrieve JSON string from intent extras
        String hajjiJson = getIntent().getStringExtra("hajji");

        // Deserialize JSON string into Hajji object using Gson
        hajji = gson.fromJson(hajjiJson, Hajji.class);

        // Display Hajji details in TextViews
        nameTextView = findViewById(R.id.nameTextView);
        passportTextView = findViewById(R.id.passportTextView);
        unitTextView = findViewById(R.id.unitTextView);
        genderTextView = findViewById(R.id.genderTextView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);
        guideTextView = findViewById(R.id.guideTextView);
        flightTextView = findViewById(R.id.flightTextView);
        houseNumberTextView = findViewById(R.id.houseNumberTextView);
        roomNumberTextView = findViewById(R.id.roomNumberTextView);
        maktabNumberTextView = findViewById(R.id.maktabNumberTextView);
        stateTextView = findViewById(R.id.stateTextView);
        serialTextView = findViewById(R.id.serialTextView);
        busTextView = findViewById(R.id.busTextView);

        state();

        if(CAN_EDIT)
            edit();


        unitTextView.setOnClickListener(view -> filterHajjisByUnit());
        guideTextView.setOnClickListener(view -> filterHajjisByGuide());
        flightTextView.setOnClickListener(view -> filterHajjisByFlight());
        houseNumberTextView.setOnClickListener(view -> filterHajjisByHouse());
        maktabNumberTextView.setOnClickListener(view -> filterHajjisByMaktab());
        busTextView.setOnClickListener(view -> filterHajjisByBus());


        if (hajji != null) {
            nameTextView.setText("Name: " + hajji.getName());
            passportTextView.setText("Passport: " + hajji.getPassport());
            unitTextView.setText("Unit: " + hajji.getUnit());
            genderTextView.setText("Gender: " + (hajji.isGender() ? "Male" : "Female"));
            phoneNumberTextView.setText("Phone Number: " + hajji.getPhoneNumber());
            guideTextView.setText("Guide: " + hajji.getGuide());
            flightTextView.setText("Flight: " + hajji.getFlight());
            houseNumberTextView.setText("House Number: " + hajji.getHouseNumber());
            roomNumberTextView.setText("Room Number: " + hajji.getRoomNumber());
            maktabNumberTextView.setText("Maktab Number: " + hajji.getMaktabNumber());
            stateTextView.setText("State: " + hajji.getStateName());
            serialTextView.setText("Serial: " + hajji.getSerial());
            busTextView.setText("Bus: " + hajji.getBus());
            // Set other TextViews with Hajji details
        }
    }

    private void edit() {
        stateTextView.setOnLongClickListener(view -> {
                if(checkConnectivity()){
                    hajji.nextState();
                    stateTextView.setText("State: " + hajji.getStateName());
                    state();
                    uploadHajjiToFirebaseAndSaveLocally();
            }
            else {
                Toast.makeText(this,"No internet Connection",Toast.LENGTH_LONG).show();
            }
                return false;
            });
        serialTextView.setOnLongClickListener(view -> {showSerialNumberInputDialog();
            return false;
        });
        busTextView.setOnLongClickListener(view -> {showBusNumberInputDialog();
            return false;
        });
    }

    private void state() {
        switch (hajji.getState()){
            case NOT_COMING :
            case DEATH:
                stateTextView.setTextColor(Color.RED); break;
            case NOT_ARRIVED: stateTextView.setTextColor(Color.GRAY); break;
            case MAKKAH: stateTextView.setTextColor(Color.WHITE); break;
            case MADINA: stateTextView.setTextColor(Color.LTGRAY); break;
            case FINAL:  stateTextView.setTextColor(Color.GREEN); break;
            case MISSION:  stateTextView.setTextColor(Color.BLUE); break;
        }
    }

    private void filterHajjisByMaktab() {
        HajjiListActivity.addSearch("maktab: "+hajji.getMaktabNumber());
        sendData();
    }


    private void filterHajjisByHouse() {
        HajjiListActivity.addSearch("house: "+hajji.getHouseNumber());
        sendData();
    }

    private void filterHajjisByFlight() {
        HajjiListActivity.addSearch("flight: "+hajji.getFlight());
        sendData();
    }

    private void filterHajjisByGuide() {
        HajjiListActivity.addSearch("guide: "+hajji.getGuide());
        sendData();
    }

    // Inside HajjiDetailActivity
    private void filterHajjisByUnit() {
        HajjiListActivity.addSearch("unit: "+hajji.getUnit());
        sendData();
    }
    private void filterHajjisByBus() {
        HajjiListActivity.addSearch("flight: "+hajji.getFlight());
        HajjiListActivity.addSearch("bus: "+hajji.getBus());
        sendData();
    }

    private void sendData() {
        finish();
    }



    private void uploadHajjiToFirebaseAndSaveLocally() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hajji");

        String passportNumber = hajji.getPassport();

        // Push creates a unique key for each hajji based on passport number
        databaseReference.child(passportNumber).setValue(hajji)
                .addOnSuccessListener(aVoid -> {
                    // Data uploaded successfully
                    Log.d("Firebase", "Hajji uploaded successfully");
                    Toast.makeText(this,"Hajji state changed successfully",Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to upload data
                    Log.e("Firebase", "Error uploading Hajji: " + e.getMessage());
                    Toast.makeText(this,"Error uploading Hajji: ",Toast.LENGTH_LONG).show();
                });
        saveHajjiLocally(this,hajji);
    }

    // Method to save Hajji locally
    // Method to save Hajji locally
    private void saveHajjiLocally(Context context, Hajji hajji) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("hajji_prefs", Context.MODE_PRIVATE);

        // Check if a Hajji object with the same passport number already exists
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Serialize Hajji object to JSON
            Gson gson = new Gson();
            String json = gson.toJson(hajji);

            // Save JSON string in SharedPreferences with passport number as key
            editor.putString(hajji.getPassport(), json);
            editor.apply();
    }
    private void showSerialNumberInputDialog() {
        if(!checkConnectivity()){
            Toast.makeText(this,"No internet Connection",Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_input_number, null);
        EditText editTextNumber = dialogView.findViewById(R.id.editTextNumber);

        builder.setView(dialogView)
                .setTitle("Enter Number")
                .setPositiveButton("OK", (dialog, which) -> {
                    String input = editTextNumber.getText().toString().trim();
                    // Handle the user input (e.g., validate the number)
                    if (!input.isEmpty()) {
                        hajji.setSerial(Integer.parseInt(input));
                        serialTextView.setText("Serial: " + hajji.getSerial());
                        uploadHajjiToFirebaseAndSaveLocally();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    } private void showBusNumberInputDialog() {
        if(!checkConnectivity()){
            Toast.makeText(this,"No internet Connection",Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_input_number, null);
        EditText editTextNumber = dialogView.findViewById(R.id.editTextNumber);
        ((TextView)dialogView.findViewById(R.id.label_dialog)).setText("Bus Number");

        builder.setView(dialogView)
                .setTitle("Enter Number")
                .setPositiveButton("OK", (dialog, which) -> {
                    String input = editTextNumber.getText().toString().trim();
                    // Handle the user input (e.g., validate the number)
                    if (!input.isEmpty()) {
                        hajji.setBus(Integer.parseInt(input));
                        busTextView.setText("Bus: " + hajji.getBus());
                        uploadHajjiToFirebaseAndSaveLocally();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public boolean checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.isConnected();
    }
    public void Back(View view) {
        finish();
    }
}

