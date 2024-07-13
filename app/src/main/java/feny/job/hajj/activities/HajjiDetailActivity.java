package feny.job.hajj.activities;

import static feny.job.hajj.custom.Data.CAN_EDIT;
import static feny.job.hajj.custom.Data.DEATH;
import static feny.job.hajj.custom.Data.FINAL;
import static feny.job.hajj.custom.Data.MADINA;
import static feny.job.hajj.custom.Data.MAKKAH;
import static feny.job.hajj.custom.Data.MISSION;
import static feny.job.hajj.custom.Data.NOT_ARRIVED;
import static feny.job.hajj.custom.Data.NOT_COMING;
import static feny.job.hajj.custom.Storage.uploadHajjiToFireBase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import feny.job.hajj.custom.Data;
import feny.job.hajj.custom.Hajji;
import feny.job.hajj.R;

public class HajjiDetailActivity extends AppCompatActivity {
    static final String TAG = "APPFHajjiDetailActivity";
    Gson gson;
    Hajji hajji;
    TextView nameTextView;
    TextView passportTextView;
    TextView visaTextView;
    TextView unitTextView;
    TextView genderTextView;
    TextView guideTextView;
    TextView flightTextView;
    TextView houseNumberTextView;
    TextView roomNumberTextView;
    TextView maktabNumberTextView;
    TextView stateTextView;
    TextView serialTextView;
    TextView busTextView;
    TextView pidTextView;

    TextView codeTextView;

    CheckBox Makkah,Ill;


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
        pidTextView = findViewById(R.id.pidTextView);
        visaTextView = findViewById(R.id.visaTextView);
        unitTextView = findViewById(R.id.unitTextView);
        genderTextView = findViewById(R.id.genderTextView);
        guideTextView = findViewById(R.id.guideTextView);
        flightTextView = findViewById(R.id.flightTextView);
        houseNumberTextView = findViewById(R.id.houseNumberTextView);
        roomNumberTextView = findViewById(R.id.roomNumberTextView);
        maktabNumberTextView = findViewById(R.id.maktabNumberTextView);
        stateTextView = findViewById(R.id.stateTextView);
        serialTextView = findViewById(R.id.serialTextView);
        busTextView = findViewById(R.id.busTextView);
        codeTextView = findViewById(R.id.codeTextView);
        Makkah = findViewById(R.id.makkah);
        Ill = findViewById(R.id.ill);
        Makkah.setClickable(CAN_EDIT);
        Ill.setClickable(CAN_EDIT);

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
            pidTextView.setText("PID: " + hajji.getPID());
            visaTextView.setText("Visa: " + hajji.getVisa());
            codeTextView.setText("Code: " + hajji.getCode());
            unitTextView.setText("Unit: " + hajji.getUnit()+ "  ("+ Data.hajjis.getUnitCount(hajji.getUnit()) +")");
            genderTextView.setText("Gender: " + (hajji.isGender() ? "Male" : "Female"));
            guideTextView.setText("Guide: " + hajji.getGuide());
            flightTextView.setText("Flight: " + hajji.getFlight());
            houseNumberTextView.setText("House Number: " + hajji.getHouseNumber());
            roomNumberTextView.setText("Room Number: " + hajji.getRoomNumber());
            maktabNumberTextView.setText("Maktab Number: " + hajji.getMaktabNumber());
            stateTextView.setText("State: " + hajji.getStateName());
            serialTextView.setText("Serial: " + hajji.getSerial());
            busTextView.setText("Bus: " + hajji.getBus() + "  ("+ Data.hajjis.getBusCount(hajji.getFlight(),hajji.getBus()) +")");
            Makkah.setChecked(hajji.isCameToMakkah());
            Ill.setChecked(hajji.isPatient());
            // Set other TextViews with Hajji details
        }
    }

    private void edit() {
        stateTextView.setOnLongClickListener(view -> {
                if(checkConnectivity()){
                    hajji.nextState();
                    stateTextView.setText("State: " + hajji.getStateName());
                    state();
                    uploadHajjiToFireBase(hajji,this);
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
        Makkah.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                hajji.setCameToMakkah(b);
                uploadHajjiToFireBase(hajji,HajjiDetailActivity.this);

            }
        });
        Ill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                hajji.setPatient(b);
                uploadHajjiToFireBase(hajji,HajjiDetailActivity.this);

            }
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
        HajjiListActivity.resetSearches();
        HajjiListActivity.addSearch("maktab: "+hajji.getMaktabNumber());
        sendData();
    }


    private void filterHajjisByHouse() {
        HajjiListActivity.resetSearches();
        HajjiListActivity.addSearch("house: "+hajji.getHouseNumber());
        sendData();
    }

    private void filterHajjisByFlight() {
        HajjiListActivity.resetSearches();
        HajjiListActivity.addSearch("flight: "+hajji.getFlight());
        sendData();
    }

    private void filterHajjisByGuide() {
        HajjiListActivity.resetSearches();
        HajjiListActivity.addSearch("guide: "+hajji.getGuide());
        sendData();
    }

    // Inside HajjiDetailActivity
    private void filterHajjisByUnit() {
        HajjiListActivity.resetSearches();
        HajjiListActivity.addSearch("unit: "+hajji.getUnit());
        sendData();
    }
    private void filterHajjisByBus() {
        HajjiListActivity.resetSearches();
        HajjiListActivity.addSearch("flight: "+hajji.getFlight());
        HajjiListActivity.addSearch("bus: "+hajji.getBus());
        sendData();
    }

    private void sendData() {
        finish();
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
                        uploadHajjiToFireBase(hajji,this);
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
                        uploadHajjiToFireBase(hajji,this);
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

