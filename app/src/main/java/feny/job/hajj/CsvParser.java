package feny.job.hajj;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CsvParser {

    private static final String TAG = "CsvParser";
    public static ArrayList<Hajji> parseCsvData(String csvData) {
        Log.e(TAG, "parseCsvData: "+  csvData);
        ArrayList<Hajji> hajjiList = new ArrayList<>();
        String[] lines = csvData.split("\n");
        boolean dataStarted = false; // To track if data rows have started
        for (String line : lines) {
            // Check if data rows have started
            if (dataStarted) {
                String[] values = line.split("\t");
                if (values.length >= 13) {
                    String SI = values[0].trim();
                    String PID = values[1].trim();
                    String unit = values[2].trim();
                    String trackingNo = values[3].trim();
                    String name = values[4].trim();
                    boolean gender = values[5].trim().equalsIgnoreCase("male");
                    String passport = values[6].trim();
                    String phoneNumber = values[7].trim().replace("+", "");
                    String guide = values[8].trim();
                    String flight = values[9].trim();
                    String houseNumber = values[10].trim();
                    String roomNumber = values[11].trim();
                    String maktabNumber = values[12].trim();

                    Hajji hajji = new Hajji(SI, PID, unit, trackingNo, name, gender, passport,
                            phoneNumber, guide, flight, houseNumber, roomNumber, maktabNumber);
                    hajjiList.add(hajji);
                }
            } else {
                // Check if the line starts with "Sl." indicating the start of data rows
                if (line.trim().startsWith("Sl.")) {
                    dataStarted = true;
                }
            }
        }
        return hajjiList;
    }

}
