package feny.job.hajj.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import feny.job.hajj.custom.Hajji;

@Dao
public interface HajjiDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addHajji(Hajji hajji);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addHajjis(List<Hajji> hajjis);

    @Update
    public void updateHajji(Hajji hajji);
    @Delete
    public void deleteHajji(Hajji hajji);
    @Query("select * from hajji")
    public List<Hajji> getAllHajjis();
    @Query("select * from hajji where passport==:passport")
    public Hajji getHajji(String passport);
    @Query("select * from hajji where visa==:visa")
    public Hajji getHajji(long visa);
}
