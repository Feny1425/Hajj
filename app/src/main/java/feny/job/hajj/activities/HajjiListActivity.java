package feny.job.hajj.activities;

import static feny.job.hajj.custom.Data.DEATH;
import static feny.job.hajj.custom.Data.FINAL;
import static feny.job.hajj.custom.Data.MADINA;
import static feny.job.hajj.custom.Data.MAKKAH;
import static feny.job.hajj.custom.Data.MISSION;
import static feny.job.hajj.custom.Data.NOT_ARRIVED;
import static feny.job.hajj.custom.Data.NOT_COMING;
import static feny.job.hajj.custom.Data.hajjis;
import static feny.job.hajj.custom.Storage.checkChangesHajjiFromFirebaseAndSaveLocally;
import static feny.job.hajj.custom.Storage.retrieveHajjiFromFirebaseAndSaveLocally;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.client.android.Intents;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import feny.job.hajj.R;
import feny.job.hajj.Utils.DateUtils;
import feny.job.hajj.adapters.HajjiAdapter;
import feny.job.hajj.adapters.SearchAdapter;
import feny.job.hajj.custom.Hajji;
import feny.job.hajj.custom.Storage;
import feny.job.hajj.readers.CaptureAct;
import feny.job.hajj.readers.HajjiExcelExporter;

public class HajjiListActivity extends AppCompatActivity implements Storage.ChangeNotification{

    private final String TAG = "APPFHajjiListActivity";
    private AutoCompleteTextView searchEditText;
    private List<Hajji> hajjiList = new ArrayList<>();
    private ArrayList<Hajji> hajjiListFILTERED = new ArrayList<>();
    private boolean autoCheck;

    public boolean isAutoCheck() {
        return autoCheck;
    }

    private HajjiAdapter hajjiAdapter;
    private static Set<String> searchList = new HashSet<>();

    private TextView summary;
    SearchAdapter searchAdapter;

    public static List<String> getSearchList() {
        return new ArrayList<>(searchList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hajji_list);
        checkChangesHajjiFromFirebaseAndSaveLocally(HajjiListActivity.this);

        try {
            searchEditText = findViewById(R.id.searchEditText);
            summary = findViewById(R.id.data_summary);

            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            RecyclerView searchRecyclerView = findViewById(R.id.searchRecycleView);
            hajjis.resetCheck();
            hajjiAdapter = new HajjiAdapter(this);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            searchRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            searchAdapter = new SearchAdapter(this);
            searchRecyclerView.setAdapter(searchAdapter);
            recyclerView.setAdapter(hajjiAdapter);
            retrieve();
            getSummary();

            ((CheckBox) findViewById(R.id.autoCheck)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    autoCheck = b;
                    if (!b) hajjis.resetCheck();
                    hajjiAdapter.notifyDataSetChanged();
                }
            });
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
                }
            });
            searchEditText.setOnLongClickListener(view -> {
                scanCode();
                return false;
            });
        }
        catch (Exception e){
            Log.e(TAG,"Create: " + e.getMessage());
        }
    }

    public static void addSearch(String s){
        if(searchList.contains(s)) return;
        searchList.add(s);
    }
    public static void resetSearches(){
        searchList = new HashSet<>();
    }

    public void removeSearch(String s){
        searchList.remove(s);
        retrieve();
        getSummary();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void removeSearches(){
        searchList = new HashSet<>();
        searchAdapter.notifyDataSetChanged();
        retrieve();
        getSummary();
    }
    @SuppressLint("NotifyDataSetChanged")
    private void retrieve() {
        try {
            // Perform non-UI operations in a background thread
            new Thread(() -> {
                try {
                    hajjiListFILTERED = new ArrayList<>(hajjis.getHajjis());
                    hajjiList = new ArrayList<>(hajjiListFILTERED);
                    String[] searches = SearchesSuggest();

                    // Switch to the main thread to perform UI updates
                    runOnUiThread(() -> {
                        try {
                            ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searches);
                            searchEditText.setAdapter(searchAdapter);

                            if (hajjis.isEmpty() || hajjiAdapter == null) return;
                            if (!searchList.isEmpty()) {
                                for (String search : searchList) {
                                    filter(search + "-");
                                }
                            } else {
                                hajjiAdapter.filterList(new ArrayList<>(hajjis.getHajjis()));
                                hajjiAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "retrieve (UI update): " + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "retrieve (background thread): " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "retrieve: " + e.getMessage());
        }
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
                searches[i] = suggestions.get(i-arraySearch.length)+"-";
            }
        }
        return searches;
    }
    public Set<String> getAllSuggestions() {
        Set<String> allSuggestions = new HashSet<>(); // Preallocate estimated size
        hajjiList.parallelStream().forEach(hajji -> {
            String[] attributes = {
                    "flight: " + hajji.getFlight(),
                    "house: " + hajji.getHouseNumber(),
                    "bus: " + hajji.getBus(),
                    "state: " + hajji.getStateName()
            };

            for (String attribute : attributes) {
                allSuggestions.add(attribute);
                allSuggestions.add("not: " + attribute);
            }
        });



        allSuggestions.add("state: isCameToMakkah");
        allSuggestions.add("not: state: isCameToMakkah");

        return allSuggestions;
    }



    @SuppressLint("NotifyDataSetChanged")
    private void filter(String searchText) {
        try {
            List<Hajji> filteredList = new ArrayList<>();
            boolean neg = searchText.toLowerCase().startsWith("not:");
            searchText = searchText.replace("not:","");
            for (Hajji hajji : hajjiList) {

                if (searchText.toLowerCase().contains("maktab:")) {
                    String search = searchText.replace("maktab:", "").replace(" ", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getMaktabNumber()).contains(search)) {
                        filteredList.add(hajji);
                    }
                } else if (searchText.toLowerCase().contains("unit:")) {
                    String search = searchText.replace("unit:", "").replace(" ", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getUnit()).contains(search)) {
                        filteredList.add(hajji);
                    }
                } else if (searchText.toLowerCase().contains("flight:")) {
                    String search = searchText.replace("flight:", "").replace(" ", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getFlight()).contains(search)) {
                        filteredList.add(hajji);
                    }
                } else if (searchText.toLowerCase().contains("guide:")) {
                    String search = searchText.replace("guide:", "").replace(" ", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getGuide()).contains(search)) {
                        filteredList.add(hajji);
                    }
                } else if (searchText.toLowerCase().contains("name:")) {
                    String search = searchText.replace("name:", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getName()).contains(search)) {
                        filteredList.add(hajji);
                    }
                } else if (searchText.toLowerCase().contains("passport:")) {
                    String search = searchText.replace("passport:", "").replace(" ", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getPassport()).contains(search)) {
                        filteredList.add(hajji);
                    }
                } else if (searchText.toLowerCase().contains("serial:")) {
                    String search = searchText.replace("serial:", "").replace(" ", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getSerial()).equals(search)) {
                        filteredList.add(hajji);
                    }
                } else if (searchText.toLowerCase().contains("bus:")) {
                    String search = searchText.replace("bus:", "").replace(" ", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getBus()).equals(  search)) {
                        filteredList.add(hajji);
                    }
                } else if (searchText.toLowerCase().contains("house:")) {
                    String search = searchText.replace("house:", "").replace(" ", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getHouseNumber()).contains(search)) {
                        filteredList.add(hajji);
                    }
                } else if (searchText.toLowerCase().contains("state:")) {
                    String search = searchText.replace("state:", "").replace(" ", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getStateName()).toLowerCase().replace(" ","").contains(search.toLowerCase())) {
                        filteredList.add(hajji);
                    }
                    else if (neg ^ (hajji.isCameToMakkah() && search.toLowerCase().contains("iscametomakkah"))) {
                        filteredList.add(hajji);
                    }
                } else if (searchText.toLowerCase().contains("visa:")) {
                    String search = searchText.replace("visa:", "").replace(" ", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getVisa()).toLowerCase().replace(" ","").contains(search.toLowerCase())) {
                        filteredList.add(hajji);
                    }
                } else if (searchText.toLowerCase().contains("pid:")) {
                    String search = searchText.replace("pid:", "").replace(" ", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getPID()).toLowerCase().replace(" ","").contains(search.toLowerCase())) {
                        filteredList.add(hajji);
                    }
                } else if (searchText.toLowerCase().contains("code:")) {
                    String search = searchText.replace("code:", "").replace(" ", "").replace("-", "");
                    if (neg ^ String.valueOf(hajji.getCode()).toLowerCase().replace(" ","").contains(search.toLowerCase())) {
                        filteredList.add(hajji);
                    }
                } else if (hajji.getPassport().toLowerCase().contains(searchText.toLowerCase().replace(" ", ""))) {
                    filteredList.add(hajji);
                }

            }
            hajjiAdapter.filterList(filteredList);
            hajjiAdapter.notifyDataSetChanged();
            if (searchText.contains("-")) {
                String text = searchText.split("-")[0];
                addSearch((neg?"not: ":"")+text);
                searchAdapter.notifyDataSetChanged();
                hajjiList = filteredList;
                searchEditText.setText("");
                getSummary();

                String[] searches = SearchesSuggest();
                ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searches);
                searchEditText.setAdapter(searchAdapter);
            }
            hajjiListFILTERED = new ArrayList<>(filteredList);

        }
        catch (Exception e){
            Log.e(TAG, "filter: " + e.getMessage() );
        }
    }
    // Method to check if a Hajji object with the given passport number is already saved locally



    private void scanCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Volume up to flash on");
        intentIntegrator.setBeepEnabled(false); // Disable beep sound
        intentIntegrator.setOrientationLocked(false); // Allow orientation changes during scanning
        intentIntegrator.setCaptureActivity(CaptureAct.class); // Use custom CaptureActivity if necessary

        // Specify barcode formats and hints
        intentIntegrator.addExtra(Intents.Scan.BARCODE_IMAGE_ENABLED, true); // Enable capturing barcode images
        intentIntegrator.addExtra(Intents.Scan.PROMPT_MESSAGE, "Scan a barcode"); // Customize prompt message
        intentIntegrator.addExtra(Intents.Scan.CAMERA_ID, Camera.CameraInfo.CAMERA_FACING_BACK); // Use back-facing camera
        // Enable autofocus
        // Use manual focus mode
        // Set manual focus mode
        intentIntegrator.addExtra("SCAN_FOCUS_MODE", "MANUAL");
        intentIntegrator.addExtra("SCAN_FOCUS_DISTANCE", 100.0); // Set desired focus distance
        



        // Initiate barcode scanning
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result.getContents() != null){
            int length = result.getContents().length();
            String resultS = result.getContents();
            if(resultS.contains("=")){
                resultS = resultS.replace("=","");
                String flight = resultS.split(",")[0];
                String bus = resultS.split(",")[1];
                removeSearches();
                addSearch("flight: " + flight);
                addSearch("bus: " + bus);
                retrieve();
            }
            else if(Character.isAlphabetic(resultS.charAt(0))){
                Hajji hajji = hajjis.getHajjiByPassport(resultS);
                if(hajji != null) {
                    if(autoCheck){
                        hajjiAdapter.highlightHajji(hajji);
                        return;
                    }
                    Gson gson = new Gson();
                    // Launch detail activity with Hajji data
                    Intent intent = new Intent(this, HajjiDetailActivity.class);
                    intent.putExtra("hajji", gson.toJson(hajji));
                    startActivity(intent);
                }
                else {
                    searchEditText.setText("passport:"+resultS);
                }
            }
            else if(length < 5){

                String res = resultS.replaceFirst("^0+(?!$)", "");
                Hajji hajji = hajjis.getHajjiBySerial(res);
                if(hajji != null) {
                    if(autoCheck){
                        hajjiAdapter.highlightHajji(hajji);
                        return;
                    }
                    Gson gson = new Gson();
                    // Launch detail activity with Hajji data
                    Intent intent = new Intent(this, HajjiDetailActivity.class);
                    intent.putExtra("hajji", gson.toJson(hajji));
                    startActivity(intent);
                }
                else {
                    searchEditText.setText("serial:"+res);
                }
            }
            else if(length == 8){
                Hajji hajji = hajjis.getHajjiByCode(resultS);
                if(hajji != null) {
                    if(autoCheck){
                        hajjiAdapter.highlightHajji(hajji);
                        return;
                    }
                    Gson gson = new Gson();
                    // Launch detail activity with Hajji data
                    Intent intent = new Intent(this, HajjiDetailActivity.class);
                    intent.putExtra("hajji", gson.toJson(hajji));
                    startActivity(intent);
                }
                else {
                    searchEditText.setText("code: "+resultS);
                }
            }
            else{
                Hajji hajji = hajjis.getHajjiByVisa(resultS);
                if(hajji != null) {
                    if(autoCheck){
                        hajjiAdapter.highlightHajji(hajji);
                        return;
                    }
                    Gson gson = new Gson();
                    // Launch detail activity with Hajji data
                    Intent intent = new Intent(this, HajjiDetailActivity.class);
                    intent.putExtra("hajji", gson.toJson(hajji));
                    startActivity(intent);
                }
                else {
                    searchEditText.setText("visa:"+resultS);
                }
            }
        }
    }
    public void addCSV(View view) {
        showOptionsDialog();
    }
    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        String[] options = new String[]{"Export XLSX"}; // Customize the options as needed
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    String name = "Hajj_" + DateUtils.getCurrentDateTime() + "_exported.xlsx";
                    if (HajjiExcelExporter.exportToExcel(HajjiListActivity.this, hajjiListFILTERED, name)) {
                        Toast.makeText(HajjiListActivity.this, "Done File in\n/Documents/" + name, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HajjiListActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
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
    }

    private void getSummary(){
        int[] states = new int[7];
        int[] gender = new int[2];
        for(Hajji hajji : hajjiList){
            states[hajji.getState()]++;
            gender[hajji.isGender()?0:1]++;
        }
        String summaryText = "NotArrived: "+states[NOT_ARRIVED] + ", Not Coming: " + states[NOT_COMING]+", Deaths: "+states[DEATH]+
                ",\nMakkah: "+states[MAKKAH]+", Madina: "+states[MADINA]+", Mission: "+states[MISSION]+ ",Final: "+states[FINAL]+
                "\nMale: " + gender[0] + ",Female: " + gender[1]+
                ",\nTotal: "+ hajjiList.size() +" - " + states[NOT_COMING] +" - " + states[DEATH] + " = " + (hajjiList.size()-states[NOT_COMING]-states[DEATH]);
        try {
            summary.setText(summaryText);
        }
        catch (Exception e){
            Log.e(TAG, "getSummary: " + e.getMessage() );
        }
    }


    @Override
    public void dataChanged() {
        retrieve();
    }
}
