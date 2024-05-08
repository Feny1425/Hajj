package feny.job.hajj.activities;

import static feny.job.hajj.Data.DEATH;
import static feny.job.hajj.Data.FINAL;
import static feny.job.hajj.Data.MADINA;
import static feny.job.hajj.Data.MAKKAH;
import static feny.job.hajj.Data.MISSION;
import static feny.job.hajj.Data.NOT_ARRIVED;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import feny.job.hajj.CaptureAct;
import feny.job.hajj.Hajji;
import feny.job.hajj.HajjiAdapter;
import feny.job.hajj.HajjiExcelExporter;
import feny.job.hajj.R;
import feny.job.hajj.Utils.DateUtils;

public class HajjiListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AutoCompleteTextView searchEditText;
    private List<Hajji> hajjiList = new ArrayList<>();
    private List<Hajji> hajjiListFILTERED = new ArrayList<>();
    private HajjiAdapter hajjiAdapter;

    private TextView summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hajji_list);

        recyclerView = findViewById(R.id.recyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        summary = findViewById(R.id.data_summary);


        if (checkConnectivity()) {
            Toast.makeText(this,"Checking for any data changes",Toast.LENGTH_SHORT).show();
            retrieveHajjiFromFirebaseAndSaveLocally(this);
        }
        else retrieve();

        hajjiAdapter = new HajjiAdapter(this, hajjiList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(hajjiAdapter);
        getSummary();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(searchEditText.getText().toString());
            }
        });
        searchEditText.setOnLongClickListener(view -> {
            scanCode();
            return false;
        });
    }



    private void retrieve() {
        hajjiList = retrieveHajjiListFromLocalStorage(this);
        hajjiListFILTERED = retrieveHajjiListFromLocalStorage(this);
        if (hajjiList.size() > 0) {

            String hajjisJson = getIntent().getStringExtra("hajjisJson");

            // Convert JSON string back to a list of Hajji objects using Gson
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Hajji>>() {
            }.getType();
            List<Hajji> hajjiList2 = gson.fromJson(hajjisJson, listType);
            if (hajjiList2 != null) hajjiList = hajjiList2;
        }
        String[] searches = SearchesSuggest();
        ArrayAdapter searchAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, searches);
        searchEditText.setAdapter(searchAdapter);

    }

    @NonNull
    private String[] SearchesSuggest() {
        String[] arraySearch = getResources().getStringArray(R.array.search);
        List<String> suggestions = new ArrayList<>(getAllSuggestions());
        String[] searches = new String[arraySearch.length+suggestions.size()];
        for(int i = 0; i < searches.length; i++){
            if(i < arraySearch.length){
                searches[i] = arraySearch[i];
            }
            else {
                searches[i] = suggestions.get(i-arraySearch.length);
            }
        }
        return searches;
    }
    public Set<String> getAllSuggestions(){
        Set<String> suggestions = new HashSet<>();
        for(Hajji hajji : hajjiList){
            suggestions.add("flight: " + hajji.getFlight());
            suggestions.add("passport: " + hajji.getPassport());
            suggestions.add("house: " + hajji.getHouseNumber());
            suggestions.add("guide: " + hajji.getGuide());
            suggestions.add("name: " + hajji.getName());
            suggestions.add("unit: " + hajji.getUnit());
            suggestions.add("maktab: " + hajji.getUnit());
        }
        return suggestions;
    }
    public static ArrayList<Hajji> retrieveHajjiListFromLocalStorage(Context context) {
        ArrayList<Hajji> hajjiList = new ArrayList<>();

        // Retrieve Hajji list from local storage (e.g., SharedPreferences)
        // Here, we assume you have stored the Hajji objects as JSON strings in SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("hajji_prefs", MODE_PRIVATE);
        Gson gson = new Gson();
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = entry.getValue().toString();
            // Deserialize JSON string to Hajji object
            Hajji hajji = gson.fromJson(json, Hajji.class);
            // Add Hajji object to the list
            hajjiList.add(hajji);
        }
        hajjiList.sort(Comparator.comparingInt(Hajji::getSerial));
        return hajjiList;
    }


    private void filter(String searchText) {
        List<Hajji> filteredList = new ArrayList<>();
        for (Hajji hajji : hajjiList) {
            // Implement your filtering logic based on name, passport, group, guide, flight, unit, maktab

            if (searchText.toLowerCase().contains("maktab:")) {
                String search = searchText.replace("maktab:", "").replace(" ", "");
                if (String.valueOf(hajji.getMaktabNumber()).contains(search)) {
                    filteredList.add(hajji);
                }
            } else if (searchText.toLowerCase().contains("unit:")) {
                String search = searchText.replace("unit:", "").replace(" ", "");
                if (String.valueOf(hajji.getUnit()).contains(search)) {
                    filteredList.add(hajji);
                }
            } else if (searchText.toLowerCase().contains("flight:")) {
                String search = searchText.replace("flight:", "").replace(" ", "");
                if (String.valueOf(hajji.getFlight()).contains(search)) {
                    filteredList.add(hajji);
                }
            } else if (searchText.toLowerCase().contains("guide:")) {
                String search = searchText.replace("guide:", "").replace(" ", "");
                if (String.valueOf(hajji.getGuide()).contains(search)) {
                    filteredList.add(hajji);
                }
            } else if (searchText.toLowerCase().contains("name:")) {
                String search = searchText.replace("name:", "");
                if (String.valueOf(hajji.getName()).contains(search)) {
                    filteredList.add(hajji);
                }
            } else if (searchText.toLowerCase().contains("passport:")) {
                String search = searchText.replace("passport:", "").replace(" ", "");
                if (String.valueOf(hajji.getPassport()).contains(search)) {
                    filteredList.add(hajji);
                }
            } else if (searchText.toLowerCase().contains("serial:")) {
                String search = searchText.replace("serial:", "");
                if (String.valueOf(hajji.getSerial()).contains(search)) {
                    filteredList.add(hajji);
                }
            }else if (searchText.toLowerCase().contains("bus:")) {
                String search = searchText.replace("bus:", "");
                if (String.valueOf(hajji.getBus()).contains(search)) {
                    filteredList.add(hajji);
                }
            }else if (searchText.toLowerCase().contains("house:")) {
                String search = searchText.replace("house:", "");
                if (String.valueOf(hajji.getHouseNumber()).contains(search)) {
                    filteredList.add(hajji);
                }
            } else if (hajji.getPassport().toLowerCase().contains(searchText.toLowerCase().replace(" ", ""))) {
                filteredList.add(hajji);
            }
        }
        hajjiAdapter.filterList(filteredList);
        hajjiListFILTERED = filteredList;
    }

    private void retrieveHajjiFromFirebaseAndSaveLocally(Context context) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hajji");

        // Attach ValueEventListener to retrieve data from Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method will be called once with the initial data from Firebase
                List<Hajji> hajjiList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve Hajji object from DataSnapshot
                    Hajji hajji = snapshot.getValue(Hajji.class);
                    hajjiList.add(hajji);
                }
                // Save Hajji objects locally in a background thread
                saveHajjisLocallyInBackground(context, hajjiList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("Firebase", "Error retrieving Hajji from Firebase: " + databaseError.getMessage());
            }
        });
    }

    private void saveHajjisLocallyInBackground(Context context, List<Hajji> hajjiList) {
        for (Hajji hajji : hajjiList) {
            saveHajjiLocally(context, hajji);
        }
        hajjiList = retrieveHajjiListFromLocalStorage(this);
        if (hajjiList.size() > 0) {

            String hajjisJson = getIntent().getStringExtra("hajjisJson");

            // Convert JSON string back to a list of Hajji objects using Gson
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Hajji>>() {
            }.getType();
            List<Hajji> hajjiList2 = gson.fromJson(hajjisJson, listType);
            if (hajjiList2 != null) hajjiList = hajjiList2;

            hajjiAdapter = new HajjiAdapter(this, hajjiList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(hajjiAdapter);
            Toast.makeText(this,"Done Checking for data changes",Toast.LENGTH_SHORT).show();
        }
    }

    private void saveHajjiLocally(Context context, Hajji hajji) {
        if(hajji == null) return;
        SharedPreferences sharedPreferences = context.getSharedPreferences("hajji_prefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Serialize Hajji object to JSON
        Gson gson = new Gson();
        String json = gson.toJson(hajji);

        // Save JSON string in SharedPreferences with passport number as key
        editor.putString(hajji.getPassport(), json);
        editor.apply();

    }

    // Method to check if a Hajji object with the given passport number is already saved locally

    public boolean checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.isConnected();
    }

    private void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result -> {
       if(result.getContents() != null){
           int length = result.getContents().length();
           if(length > 7){
               searchEditText.setText("passport:"+result.getContents());
           }
           else {
               searchEditText.setText("serial:"+result.getContents());
           }
       }
    });

    public void addCSV(View view) {
        showOptionsDialog();
    }
    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        String[] options = {"Update XLSX", "Export XLSX"}; // Customize the options as needed
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if(checkConnectivity()) {
                            Intent intent = new Intent(HajjiListActivity.this, ImportXLSX.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(HajjiListActivity.this,"No internet Connection",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        String name = "Hajj_"+ DateUtils.getCurrentDateTime() +"_exported.xlsx";
                        if(HajjiExcelExporter.exportToExcel(HajjiListActivity.this,hajjiListFILTERED, name)){
                            Toast.makeText(HajjiListActivity.this,"Done File in\n/Documents/"+name,Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(HajjiListActivity.this,"error",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        // Handle Option 3
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieve();
        getSummary();
        hajjiAdapter.filterList(hajjiList);
        filter(searchEditText.getText().toString());
    }

    private void getSummary(){
        int[] states = new int[6];
        for(Hajji hajji : hajjiList){
            states[hajji.getState()]++;
        }
        String summaryText = "NotArrived: "+states[NOT_ARRIVED]+
                ",\nMakkah: "+states[MAKKAH]+", Madina: "+states[MADINA]+", Mission: "+states[MISSION]+", Deaths: "+states[DEATH]+", Final: "+states[FINAL]+
                ",\nTotal: "+hajjiList.size();
        summary.setText(summaryText);
    }
}
