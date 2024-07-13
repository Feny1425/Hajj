package feny.job.hajj.activities;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import static feny.job.hajj.custom.Storage.uploadHajjisToFirebaseAndSaveLocally;
import static feny.job.hajj.readers.XLSXReader.readXLSXFile;
import static feny.job.hajj.readers.XLSXReader.readXLSXFileAddVisa;
import static feny.job.hajj.readers.XLSXReader.readXLSXFileSB;
import static feny.job.hajj.readers.XLSXReader.readXLSXFileUpdateGender;
import static feny.job.hajj.readers.XLSXReader.readXLSXFileUpdateHajjiCode;
import static feny.job.hajj.readers.XLSXReader.readXLSXFileUpdateState;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.TextView;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;

import feny.job.hajj.R;

public class UpdateDataActivity extends AppCompatActivity {

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
            context.setText("");
            openFile(101);
        });
        findViewById(R.id.addSerialAndBus).setOnClickListener(view -> {
            context.setText("");
            openFile(105);
        });
        findViewById(R.id.updateState).setOnClickListener(view -> {
            context.setText("");
            openFile(110);
        });
        findViewById(R.id.AddVisa).setOnClickListener(view -> {
            context.setText("");
            openFile(115);
        });
        findViewById(R.id.updateGender).setOnClickListener(view -> {
            context.setText("");
            openFile(120);
        });
        findViewById(R.id.AddCode).setOnClickListener(view -> {
            context.setText("");
            openFile(125);
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

            ActivityCompat.requestPermissions(UpdateDataActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri fileUri = data.getData();

            try {
                switch (requestCode) {
                    case 101:
                        uploadHajjisToFirebaseAndSaveLocally(readXLSXFile(getApplicationContext(), fileUri), this, status -> context.setText(status));
                        break;
                    case 105:
                        uploadHajjisToFirebaseAndSaveLocally(readXLSXFileSB(getApplicationContext(), fileUri), this, status -> context.setText(status));
                        break;
                    case 110:
                        uploadHajjisToFirebaseAndSaveLocally(readXLSXFileUpdateState(getApplicationContext(), fileUri), this, status -> context.setText(status));
                        break;
                    case 115:
                        uploadHajjisToFirebaseAndSaveLocally(readXLSXFileAddVisa(getApplicationContext(), fileUri), this, status -> context.setText(status));
                        break;
                    case 120:
                        uploadHajjisToFirebaseAndSaveLocally(readXLSXFileUpdateGender(getApplicationContext(), fileUri), this, status -> context.setText(status));
                        break;
                    case 125:
                        uploadHajjisToFirebaseAndSaveLocally(readXLSXFileUpdateHajjiCode(getApplicationContext(), fileUri), this, status -> context.setText(status));
                        break;
                }
            } catch (IOException | InvalidFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }




}
