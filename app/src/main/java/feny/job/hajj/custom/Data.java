package feny.job.hajj.custom;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import feny.job.hajj.custom.Hajji;
import feny.job.hajj.database.HajjiDatabase;

public class Data {

    private final static String TAG = "APPFData";
    public final static boolean CAN_EDIT = true;

    public final static int NOT_ARRIVED = 0;
    public final static int MAKKAH = 1;
    public final static int MADINA = 2;
    public final static int DEATH = 3;
    public final static int FINAL = 4;
    public final static int MISSION = 5;
    public final static int NOT_COMING = 6;

    public static int MAKTAB = 7;

    public static HajjiDatabase getHajjiDB() {
        return hajjiDB;
    }

    public static List<Hajji> databaseHajji = new ArrayList<>();
    public static void setHajjiDB(HajjiDatabase hajjidb) {
        try {
            hajjiDB = hajjidb;
            databaseHajji = hajjidb.getHajjiDAO().getAllHajjis();
            hajjis.setHajjis(hajjidb.getHajjiDAO().getAllHajjis(),null);
        }
        catch (Exception e){
            Log.e(TAG, "setHajjiDB: " + e.getMessage());
        }
    }
    public static final Migration MIGRATION_2_4 = new Migration(2, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Perform destructive migration by dropping existing tables
            database.execSQL("DROP TABLE IF EXISTS hajji");
            // Recreate the hajji table with the updated schema
            database.execSQL("CREATE TABLE IF NOT EXISTS hajji ("
                    + "passport TEXT PRIMARY KEY NOT NULL,"
                    + "visa INTEGER,"
                    + "pid INTEGER,"
                    + "unit INTEGER,"
                    + "tracking_no TEXT,"
                    + "name TEXT,"
                    + "guide INTEGER,"
                    + "flight TEXT,"
                    + "house_number INTEGER,"
                    + "room_number INTEGER,"
                    + "maktab_number INTEGER,"
                    + "code INTEGER,"
                    + "state INTEGER,"
                    + "serial INTEGER,"
                    + "bus INTEGER,"
                    + "came_to_makkah INTEGER,"
                    + "patient INTEGER,"
                    + "gender INTEGER,"
                    + "checked INTEGER)");
        }
    };

    public static HajjiDatabase hajjiDB;
    public static HajjiList hajjis = new HajjiList();
}
