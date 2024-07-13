package feny.job.hajj.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import feny.job.hajj.custom.Hajji;

@Database(entities = {Hajji.class},version = 7)
public abstract class HajjiDatabase extends RoomDatabase {

    public abstract HajjiDAO getHajjiDAO();
}
