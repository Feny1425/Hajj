package feny.job.hajj;

import java.util.List;
import java.util.Objects;

public class Data {

    public final static boolean CAN_EDIT = true;

    public final static int NOT_ARRIVED = 0;
    public final static int MAKKAH = 1;
    public final static int MADINA = 2;
    public final static int DEATH = 3;
    public final static int FINAL = 4;
    public final static int MISSION = 5;
    public final static int NOT_COMING = 6;

    public static Hajji getHajjiByPassport(List<Hajji> hajjis, String Passport){
        for(Hajji hajj : hajjis){
            if(hajj.getPassport().equals(Passport)) return hajj;
        }
        return null;
    }
    public static Hajji getHajjiBySerial(List<Hajji> hajjis, String Serial){
        for(Hajji hajj : hajjis){
            if(String.valueOf(hajj.getSerial()).equals(Serial)) return hajj;
        }
        return null;
    }
}
