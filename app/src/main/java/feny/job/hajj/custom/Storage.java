package feny.job.hajj.custom;

import static feny.job.hajj.custom.Data.getHajjiDB;
import static feny.job.hajj.custom.Data.hajjis;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import feny.job.hajj.activities.HajjiListActivity;
import feny.job.hajj.readers.UploadCallback;

public class Storage {
static final String TAG = "APPFStorage";
    public static boolean uploadHajjiToFireBase(Hajji hajji, Context context) {
        AtomicBoolean done = new AtomicBoolean(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hajji");


        String passportNumber = hajji.getPassport();

        // Push creates a unique key for each hajji based on passport number
        databaseReference.child(passportNumber).setValue(hajji)
                .addOnSuccessListener(aVoid -> {
                    // Data uploaded successfully
                    Log.d("Firebase", "Hajji uploaded successfully");
                    saveHajjiLocally(context, hajji);
                    done.set(true);
                })
                .addOnFailureListener(e -> {
                    // Failed to upload data
                    Log.e("Firebase", "Error uploading Hajji: " + e.getMessage());
                });
        return done.get();
    }
    public static void uploadHajjisToFirebaseAndSaveLocally(ArrayList<Hajji> hajjiList, Context context, UploadCallback callback) {
        if (hajjiList.size() == 0) {
            if (callback != null) {
                callback.onUploadComplete("List is empty");
            }
            return;
        }

        new HajjiUploader(context, callback).execute(hajjiList);
    }
    private static void saveHajjiLocally(Context context, Hajji hajji) {
        try{
                getHajjiDB().getHajjiDAO().addHajji(hajji);
                hajjis.setHajjis(getHajjiDB().getHajjiDAO().getAllHajjis(),null);
        }
        catch (Exception e){
            Log.e(TAG,"saveHajjiLocally: " + e.getMessage());
        }
    }
    private static void saveHajjisLocally(Context context, List<Hajji> hajji) {
        try{
           new Thread(()->{
               getHajjiDB().getHajjiDAO().addHajjis(hajji);
               hajjis.setHajjis(getHajjiDB().getHajjiDAO().getAllHajjis(),null);
           }).start();
        }
        catch (Exception e){
            Log.e(TAG,"saveHajjiLocally: " + e.getMessage());
        }
    }

    public static void checkChangesHajjiFromFirebaseAndSaveLocally(final HajjiListActivity context) {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hajji");

            // Attach ValueEventListener to retrieve data from Firebase
            databaseReference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Count the number of Hajji objects
                    int hajjiCount = (int) dataSnapshot.getChildrenCount();
                    if (hajjiCount == 0) {
                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Initialize the CountDownLatch
                    CountDownLatch latch = new CountDownLatch(hajjiCount);

                    List<Hajji> hajjiList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Retrieve Hajji object from DataSnapshot
                        Hajji hajji = snapshot.getValue(Hajji.class);
                        if (hajji != null) {
                            hajjiList.add(hajji);
                            latch.countDown();  // Count down the latch
                        } else {
                            latch.countDown();  // Count down even if hajji is null to avoid hanging
                        }
                    }

                    // Wait for all threads to complete
                    new Thread(() -> {
                        try {
                            latch.await();  // Wait until the latch reaches zero
                            // Show the toast message on the main thread
                            ((Activity) context).runOnUiThread(() -> {

                                        ChangeNotification changeNotification = context;
                                        hajjis.setHajjis(hajjiList, changeNotification);
                                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();

                                    }
                            );
                        } catch (InterruptedException e) {
                            Log.e("Firebase", "Error waiting for latch: " + e.getMessage());
                        }
                    }).start();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            Log.e(TAG, "retrieveHajjiFromFirebaseAndSaveLocally: " + e.getMessage() );
        }
    }
    public static void retrieveHajjiFromFirebaseAndSaveLocally(final Context context) {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hajji");

            // Attach ValueEventListener to retrieve data from Firebase
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Count the number of Hajji objects
                    int hajjiCount = (int) dataSnapshot.getChildrenCount();
                    if (hajjiCount == 0) {
                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Initialize the CountDownLatch
                    CountDownLatch latch = new CountDownLatch(hajjiCount);

                    List<Hajji> hajjiList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Retrieve Hajji object from DataSnapshot
                        Hajji hajji = snapshot.getValue(Hajji.class);
                        if (hajji != null) {
                            hajjiList.add(hajji);
                            latch.countDown();  // Count down the latch
                        } else {
                            latch.countDown();  // Count down even if hajji is null to avoid hanging
                        }
                    }

                    // Wait for all threads to complete
                    new Thread(() -> {
                        try {
                            latch.await();  // Wait until the latch reaches zero
                            // Show the toast message on the main thread
                            ((Activity) context).runOnUiThread(() -> {
                                        saveHajjisLocally(context, hajjiList);
                                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();

                                    }
                            );
                        } catch (InterruptedException e) {
                            Log.e("Firebase", "Error waiting for latch: " + e.getMessage());
                        }
                    }).start();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                    Log.e("Firebase", "Error retrieving Hajji from Firebase: " + databaseError.getMessage());
                }
            });

        }
        catch (Exception e){
            Log.e(TAG, "retrieveHajjiFromFirebaseAndSaveLocally: " + e.getMessage() );
        }
    }

    private static class SaveHajjiTask extends AsyncTask<ArrayList<Hajji>, Void, Void> {
        private Context context;

        SaveHajjiTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(ArrayList<Hajji>... params) {
            ArrayList<Hajji> hajjiList = params[0];
            for (Hajji hajji : hajjiList) {
                saveHajjiLocally(context, hajji);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //context.updateAdapter();
            Toast.makeText(context,"done checking",Toast.LENGTH_SHORT).show();
        }
    }


    public static class HajjiUploader extends AsyncTask<ArrayList<Hajji>, Void, String> {
        private Context context;
        private UploadCallback callback;

        public HajjiUploader(Context context, UploadCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(ArrayList<Hajji>... params) {
            ArrayList<Hajji> hajjiList = params[0];
            if (hajjiList.size() == 0) return "list empty";

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hajji");

            // Create a map to store hajji objects with passport number as key
            Map<String, Hajji> hajjiMap = new HashMap<>();

            // Populate the map with hajji objects, using passport number as key
            for (Hajji hajji : hajjiList) {
                // Use passport number as the key
                String passportNumber = hajji.getPassport();
                saveHajjiLocally(context, hajji);

                // Only add the hajji object to the map if it doesn't already exist with the same passport number
                if (!hajjiMap.containsKey(passportNumber)) {
                    hajjiMap.put(passportNumber, hajji);
                }
            }

            // Atomic integer to track the number of remaining uploads
            AtomicInteger remainingUploads = new AtomicInteger(hajjiMap.size());

            for (Map.Entry<String, Hajji> entry : hajjiMap.entrySet()) {
                String passportNumber = entry.getKey();
                Hajji hajji = entry.getValue();

                // Push creates a unique key for each hajji based on passport number
                databaseReference.child(passportNumber).setValue(hajji)
                        .addOnSuccessListener(aVoid -> {
                            // Data uploaded successfully
                            Log.d("Firebase", "Hajji uploaded successfully");
                            if (remainingUploads.decrementAndGet() == 0) {
                                synchronized (HajjiUploader.this) {
                                    HajjiUploader.this.notify();
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Failed to upload data
                            Log.e("Firebase", "Error uploading Hajji: " + e.getMessage());
                            if (remainingUploads.decrementAndGet() == 0) {
                                synchronized (HajjiUploader.this) {
                                    HajjiUploader.this.notify();
                                }
                            }
                        });
            }

            synchronized (this) {
                while (remainingUploads.get() > 0) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Log.e("Firebase", "Upload interrupted: " + e.getMessage());
                        return "Upload interrupted: " + e.getMessage();
                    }
                }
            }

            return "uploaded";
        }

        @Override
        protected void onPostExecute(String result) {
            if (callback != null) {
                callback.onUploadComplete(result);
            }
        }

    }

    public interface ChangeNotification{
        public void dataChanged();
    }
}
