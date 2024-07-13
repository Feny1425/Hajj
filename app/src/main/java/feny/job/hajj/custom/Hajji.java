package feny.job.hajj.custom;

import static feny.job.hajj.custom.Data.DEATH;
import static feny.job.hajj.custom.Data.FINAL;
import static feny.job.hajj.custom.Data.MADINA;
import static feny.job.hajj.custom.Data.MAKKAH;
import static feny.job.hajj.custom.Data.MISSION;
import static feny.job.hajj.custom.Data.NOT_ARRIVED;
import static feny.job.hajj.custom.Data.NOT_COMING;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "hajji")
public class Hajji{
    @ColumnInfo(name = "passport")
    @PrimaryKey
    @NonNull
    private String Passport;
    @ColumnInfo(name = "visa")
    long Visa;
    @ColumnInfo(name = "pid")
    private int PID;
    @ColumnInfo(name = "unit")
    private int Unit;
    @ColumnInfo(name = "tracking_no")
    private String TrackingNo;
    @ColumnInfo(name = "name")
    private String Name;
    @ColumnInfo(name = "guide")
    private int Guide;
    @ColumnInfo(name = "flight")
    private String Flight;
    @ColumnInfo(name = "house_number")
    private int HouseNumber;
    @ColumnInfo(name = "room_number")
    private int RoomNumber;
    @ColumnInfo(name = "maktab_number")
    private int MaktabNumber;
    @ColumnInfo(name = "code")
    private int Code;
    @ColumnInfo(name = "state")
    int State = NOT_ARRIVED;
    @ColumnInfo(name = "serial")
    int serial = 0;
    @ColumnInfo(name = "bus")
    int bus = 0;


    @ColumnInfo(name = "came_to_makkah")
    boolean cameToMakkah;
    @ColumnInfo(name = "patient")
    boolean patient;
    @ColumnInfo(name = "gender")
    boolean Gender = false;
    private boolean checked;
    private boolean ourOffice;

    public boolean isOurOffice() {
        return ourOffice;
    }

    public void setOurOffice(boolean ourOffice) {
        this.ourOffice = ourOffice;
    }

    public Hajji() {
        // Default constructor required by Firebase
    }
    @Ignore
    public Hajji(String passport, long visa, int PID, int unit, String trackingNo, String name, int guide, String flight, int houseNumber, int roomNumber, int maktabNumber, int code, int state, int serial, int bus, boolean cameToMakkah, boolean patient, boolean gender) {
        Passport = passport;
        Visa = visa;
        this.PID = PID;
        Unit = unit;
        TrackingNo = trackingNo;
        Name = name;
        Guide = guide;
        Flight = flight;
        HouseNumber = houseNumber;
        RoomNumber = roomNumber;
        MaktabNumber = maktabNumber;
        Code = code;
        State = state;
        this.serial = serial;
        this.bus = bus;
        this.cameToMakkah = cameToMakkah;
        this.patient = patient;
        Gender = gender;

    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        this.Code = code;
    }
    public boolean isPatient() {
        return patient;
    }

    public void setPatient(boolean patient) {
        this.patient = patient;
    }

    public boolean isCameToMakkah() {
        return cameToMakkah;
    }

    public void setCameToMakkah(boolean cameToMakkah) {
        this.cameToMakkah = cameToMakkah;
    }

    public long getVisa() {
        return Visa;
    }

    public void setVisa(long visa) {
        Visa = visa;
    }


    public int getBus() {
        return bus;
    }

    public void setBus(int bus) {
        this.bus = bus;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        if(state == MAKKAH) setCameToMakkah(true);
        this.State = state;
    }
    public void nextState(){
        if(State == 6){
            State = 0;
        }
        else {
            State++;
        }
    }
    public String getStateName(){
        switch (State){
            default:
                return "Not Arrived";
            case MAKKAH:
                return "Makkah";
            case MADINA:
                return "Madina";
            case DEATH:
                return "Death";
            case FINAL:
                return "Final";
            case MISSION:
                return "Mission";
            case NOT_COMING:
                return "Not Coming";
        }

    }


    public void setPID(int PID) {
        this.PID = PID;
    }

    public void setUnit(int unit) {
        Unit = unit;
    }

    public void setTrackingNo(String trackingNo) {
        TrackingNo = trackingNo;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setGender(boolean gender) {
        Gender = gender;
    }


    public void setPassport(String passport) {
        Passport = passport;
    }


    public void setGuide(int guide) {
        Guide = guide;
    }

    public void setFlight(String flight) {
        Flight = flight;
    }

    public void setHouseNumber(int houseNumber) {
        HouseNumber = houseNumber;
    }

    public void setRoomNumber(int roomNumber) {
        RoomNumber = roomNumber;
    }

    public void setMaktabNumber(int maktabNumber) {
        MaktabNumber = maktabNumber;
    }


    public int getPID() {
        return PID;
    }

    public int getUnit() {
        return Unit;
    }

    public String getTrackingNo() {
        return TrackingNo;
    }

    public String getName() {
        return Name;
    }

    public boolean isGender() {
        return Gender;
    }

    public String getPassport() {
        return Passport;
    }

    public int getGuide() {
        return Guide;
    }

    public String getFlight() {
        return Flight;
    }

    public int getHouseNumber() {
        return HouseNumber;
    }

    public int getRoomNumber() {
        return RoomNumber;
    }

    public int getMaktabNumber() {
        return MaktabNumber;
    }

    @Override
    public String toString() {
        return "Hajji{" +
                ", PID=" + PID +
                ", Unit=" + Unit +
                ", TrackingNo='" + TrackingNo + '\'' +
                ", Name='" + Name + '\'' +
                ", Gender=" + Gender +
                ", Passport='" + Passport + '\'' +
                ", Guide='" + Guide + '\'' +
                ", Flight='" + Flight + '\'' +
                ", HouseNumber='" + HouseNumber + '\'' +
                ", RoomNumber='" + RoomNumber + '\'' +
                ", MaktabNumber='" + MaktabNumber + '\'' +
                '}';
    }



}
