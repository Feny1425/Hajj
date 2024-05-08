package feny.job.hajj;

import java.util.List;
import java.util.Objects;

public class Data {
    public final static int NOT_ARRIVED = 0;
    public final static int MAKKAH = 1;
    public final static int MADINA = 2;
    public final static int DEATH = 3;
    public final static int FINAL = 4;
    public final static int MISSION = 5;

    public static Hajji getHajjiByPassport(List<Hajji> hajjis, String Passport){
        for(Hajji hajj : hajjis){
            if(Objects.equals(hajj.getPassport(), Passport)) return hajj;
        }
        return null;
    }
}
