package com.example.fussionv3.roomdatabase.entitas;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Catatan {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String judul;

    @ColumnInfo(name = "isi_catatan")
    public String isi;

}
