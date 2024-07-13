package feny.job.hajj.custom;


import static feny.job.hajj.custom.Data.MAKTAB;
import static feny.job.hajj.custom.Data.databaseHajji;
import static feny.job.hajj.custom.Data.getHajjiDB;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import feny.job.hajj.database.HajjiDAO;

public class HajjiList {

    private final String TAG = "APP, HajjiList";
    List<Hajji> hajjis = new ArrayList<>();

    public HajjiList() {

    }

    public void setHajjis(List<Hajji> hajjis, Storage.ChangeNotification changeNotification) {
        this.hajjis = hajjis;
        new Thread(()->{
            for(Hajji h : databaseHajji){
                if(databaseHajji.size() == this.hajjis.size()) break;
                boolean con = false;
                for (Hajji hh : this.hajjis){
                    if(h.getPassport().equals(hh.getPassport())){
                        con = true;
                        break;
                    }
                }
                if(con) continue;
                getHajjiDB().getHajjiDAO().deleteHajji(h);
                databaseHajji = getHajjiDB().getHajjiDAO().getAllHajjis();
            }
            if(changeNotification != null) changeNotification.dataChanged();
        }).start();
    }

    public List<Hajji> getHajjis() {
        try {
            return hajjis;
        }
        catch (Exception e){
            Log.e(TAG, "getHajjis: " + e.getMessage() );
            return new ArrayList<>();
        }
    }

    public boolean isEmpty(){
        return hajjis.isEmpty();
    }


    public Hajji getHajjiByPassport(String Passport){
        for(Hajji hajji : hajjis){
            if(Passport.equals(hajji.getPassport())){
                return hajji;
            }
        }
        return null;
    }
    public Hajji getHajjiByVisa(String Visa) {
        for(Hajji hajji : hajjis){
            if(Visa.equals(String.valueOf(hajji.getVisa()))){
                return hajji;
            }
        }
        return null;
    }
    public Hajji getHajjiByCode(String Code){
        for(Hajji hajj : hajjis){
            if(String.valueOf(hajj.getCode()).equals(Code)) return hajj;
        }
        return null;
    }
    public Hajji getHajjiBySerial(String Serial){
        for(Hajji hajj : hajjis){
            if(String.valueOf(hajj.getSerial()).equals(Serial)) return hajj;
        }
        return null;
    }

    public int getBusCount(String flight, int bus){
        int count  = 0;
        for(Hajji hajj : hajjis){
            if(String.valueOf(hajj.getFlight()).equals(flight) && hajj.getBus() == bus) count++;
        }
        return count;
    }
    public int getUnitCount(int unit){
        int count  = 0;
        for(Hajji hajj : hajjis){
            if(hajj.getUnit() == unit) count++;
        }
        return count;
    }
    public static void PIDAccording(ArrayList<Hajji> hajjis) {
        hajjis.sort(Comparator.comparingInt(Hajji::getPID));
    }
    public static void GuideAccording(ArrayList<Hajji> hajjis) {
        hajjis.sort(Comparator.comparingInt(Hajji::getGuide));
    }

    public void resetCheck() {
        for(Hajji hajji : hajjis){
            hajji.setChecked(false);
        }
    }
}
