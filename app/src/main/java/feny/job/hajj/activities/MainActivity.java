package feny.job.hajj.activities;

import static feny.job.hajj.custom.Data.CAN_EDIT;
import static feny.job.hajj.custom.Data.MIGRATION_2_4;
import static feny.job.hajj.custom.Data.setHajjiDB;
import static feny.job.hajj.custom.Network.checkConnectivity;
import static feny.job.hajj.custom.Storage.retrieveHajjiFromFirebaseAndSaveLocally;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import feny.job.hajj.R;
import feny.job.hajj.database.HajjiDatabase;

public class MainActivity extends AppCompatActivity {
    Button buttonHajjiList,buttonSettings,buttonUpdateData;
    private final String TAG = "APPFMainActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            new Thread(this::database).start();
        }
        catch (Exception e){
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }

        ini();
        buttons();
        if (checkConnectivity(this)) {
            try{
                Toast.makeText(this,"Checking for any data changes",Toast.LENGTH_SHORT).show();
                retrieveHajjiFromFirebaseAndSaveLocally(this);
            }
            catch (Exception e){
                Log.e(TAG, "onCreate: " + e.getMessage());
            }
        }


    }

    private void database() {
        try {
            RoomDatabase.Callback myCallBack = new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                }

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                }
            };
            try {
                HajjiDatabase dbInstance = Room.databaseBuilder(getApplicationContext(), HajjiDatabase.class, "HajjiDB")
                        .addCallback(myCallBack)
                        .addMigrations(MIGRATION_2_4)
                        .fallbackToDestructiveMigration()
                        .build();
                setHajjiDB(dbInstance);
                Log.d(TAG, "Database initialized successfully.");
            } catch (Exception e) {
                Log.e(TAG, "Database initialization error: " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "Database setup error: " + e.getMessage());
        }
    }

    private void buttons() {
        buttonUpdateData.setVisibility(CAN_EDIT?View.VISIBLE:View.GONE);
        buttonHajjiList.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HajjiListActivity.class)));

        //buttonSettings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        buttonUpdateData.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, UpdateDataActivity.class)));
    }

    private void ini() {
        buttonHajjiList = findViewById(R.id.button_hajji_list);
        buttonSettings = findViewById(R.id.button_settings);
        buttonUpdateData = findViewById(R.id.button_update_data);
    }

}