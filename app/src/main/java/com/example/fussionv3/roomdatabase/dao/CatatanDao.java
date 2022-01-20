package com.example.fussionv3.roomdatabase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.fussionv3.roomdatabase.entitas.Catatan;

import java.util.List;

@Dao
public interface CatatanDao {

    @Query("SELECT * FROM catatan")
    List<Catatan> getAll();

    @Insert
    void insertAll(Catatan... catatans);

    @Query("UPDATE Catatan SET judul=:judul, isi_catatan=:isi_catatan WHERE id=:id")
    void update(int id, String judul, String isi_catatan);

    @Query("SELECT * FROM Catatan WHERE id=:id")
    Catatan get(int id);

    @Delete
    void delete(Catatan kontak);

}
