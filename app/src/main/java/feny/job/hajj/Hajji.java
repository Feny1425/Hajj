package feny.job.hajj;

import static feny.job.hajj.Data.DEATH;
import static feny.job.hajj.Data.FINAL;
import static feny.job.hajj.Data.MADINA;
import static feny.job.hajj.Data.MAKKAH;
import static feny.job.hajj.Data.MISSION;
import static feny.job.hajj.Data.NOT_ARRIVED;
import static feny.job.hajj.Data.NOT_COMING;

public class Hajji {
    String SI;
    String PID;
    String Unit;
    String TrackingNo;
    String Name;
    boolean Gender;
    String Passport;
    String PhoneNumber;
    String Guide;
    String Flight;
    String HouseNumber;
    String RoomNumber;
    String MaktabNumber;

    int State = NOT_ARRIVED;

    int serial = 0;
    int bus = 0;

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

    public void setSI(String SI) {
        this.SI = SI;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public void setUnit(String unit) {
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

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public void setGuide(String guide) {
        Guide = guide;
    }

    public void setFlight(String flight) {
        Flight = flight;
    }

    public void setHouseNumber(String houseNumber) {
        HouseNumber = houseNumber;
    }

    public void setRoomNumber(String roomNumber) {
        RoomNumber = roomNumber;
    }

    public void setMaktabNumber(String maktabNumber) {
        MaktabNumber = maktabNumber;
    }

    public Hajji() {
        // Default constructor required by Firebase
    }
    public Hajji(String SI, String PID, String unit, String trackingNo, String name,
                 boolean gender, String passport, String phoneNumber, String guide,
                 String flight, String houseNumber, String roomNumber, String maktabNumber) {
        this.SI = SI;
        this.PID = PID;
        Unit = unit;
        TrackingNo = trackingNo;
        Name = name;
        Gender = gender;
        Passport = passport;
        PhoneNumber = phoneNumber;
        Guide = guide;
        Flight = flight;
        HouseNumber = houseNumber;
        RoomNumber = roomNumber;
        MaktabNumber = maktabNumber;
    }

    public String getSI() {
        return SI;
    }

    public String getPID() {
        return PID;
    }

    public String getUnit() {
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

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getGuide() {
        return Guide;
    }

    public String getFlight() {
        return Flight;
    }

    public String getHouseNumber() {
        return HouseNumber;
    }

    public String getRoomNumber() {
        return RoomNumber;
    }

    public String getMaktabNumber() {
        return MaktabNumber;
    }

    @Override
    public String toString() {
        return "Hajji{" +
                "SI=" + SI +
                ", PID=" + PID +
                ", Unit=" + Unit +
                ", TrackingNo='" + TrackingNo + '\'' +
                ", Name='" + Name + '\'' +
                ", Gender=" + Gender +
                ", Passport='" + Passport + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", Guide='" + Guide + '\'' +
                ", Flight='" + Flight + '\'' +
                ", HouseNumber='" + HouseNumber + '\'' +
                ", RoomNumber='" + RoomNumber + '\'' +
                ", MaktabNumber='" + MaktabNumber + '\'' +
                '}';
    }
}
