package feny.job.hajj.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import feny.job.hajj.R;
import feny.job.hajj.custom.Data;

public class SettingsActivity extends AppCompatActivity {

    private final String TAG = "APPFSettingsActivity";

    private EditText editTextOfficeNumber;
    private Button buttonSave,buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextOfficeNumber = findViewById(R.id.editTextOfficeNumber);
        buttonSave = findViewById(R.id.buttonSave);
        buttonBack = findViewById(R.id.back);

        // Load saved office number if it exists
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        String savedOfficeNumber = String.valueOf(sharedPreferences.getInt("office_number", -1));
        try {
            editTextOfficeNumber.setText(savedOfficeNumber.equals("-1")?"":savedOfficeNumber);
        }
        catch (Exception e){
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }

        buttonBack.setOnClickListener(view -> {
            finish();
        });
        buttonSave.setOnClickListener(v -> {
            String newOfficeNumber = editTextOfficeNumber.getText().toString().trim();
            if (TextUtils.isEmpty(newOfficeNumber)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("office_number", -1);
                editor.apply();

                Toast.makeText(SettingsActivity.this, "Office number saved as all offices", Toast.LENGTH_SHORT).show();
            }
            else {

                // Save the new office number
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("office_number", Integer.parseInt(newOfficeNumber));
                editor.apply();
                Toast.makeText(SettingsActivity.this, "Office number saved", Toast.LENGTH_SHORT).show();

            }

            int _savedOfficeNumber = sharedPreferences.getInt("office_number", -1);
            Data.MAKTAB = _savedOfficeNumber;
        });
    }
}