package com.example.monitorapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity
public class Entry {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "synced"  )
    public int synced;

    @Ignore
    public int clientRecId; //this field is used when record gets deserialized from the server response

    @ColumnInfo(name = "entry_type"  )
    public int entry_type;
    @ColumnInfo(name = "timestamp"  )
    public double timestamp;

    @ColumnInfo(name = "full_name"  )
    public String full_name;
    @ColumnInfo(name = "sex"        )
    public String sex;
    @ColumnInfo(name = "age"        )
    public double age;
    @ColumnInfo(name = "pb"         )
    public double pb;
    @ColumnInfo(name = "weight"     )
    public double weight;
    @ColumnInfo(name = "height"     )
    public double height;
    @ColumnInfo(name = "hb"         )
    public double hb;
    @ColumnInfo(name = "sbp"        )
    public double sbp;
    @ColumnInfo(name = "dbp"        )
    public double dbp;
    @ColumnInfo(name = "hr"         )
    public double hr;
    @ColumnInfo(name = "co"         )
    public double co;
    @ColumnInfo(name = "sao2"       )
    public double sao2;
    @ColumnInfo(name = "svo2"       )
    public double svo2;
    @ColumnInfo(name = "ve"         )
    public double ve;
    @ColumnInfo(name = "peco2"      )
    public double peco2;
    @ColumnInfo(name = "rr"         )
    public double rr;
    @ColumnInfo(name = "fio2"       )
    public double fio2;
    @ColumnInfo(name = "feo2"       )
    public double feo2;
    @ColumnInfo(name = "s"          )
    public double s;
    @ColumnInfo(name = "map"        )
    public double map;
    @ColumnInfo(name = "svK"        )
    public double svK;
    @ColumnInfo(name = "sv"         )
    public double sv;
    @ColumnInfo(name = "svri"       )
    public double svri;
    @ColumnInfo(name = "ci"         )
    public double ci;
    @ColumnInfo(name = "do2"        )
    public double do2;
    @ColumnInfo(name = "ido2"       )
    public double ido2;
    @ColumnInfo(name = "keo2"       )
    public double keo2;
    @ColumnInfo(name = "vo2"        )
    public double vo2;
    @ColumnInfo(name = "ivo2"       )
    public double ivo2;
    @ColumnInfo(name = "veco2"      )
    public double veco2;
    @ColumnInfo(name = "rq"         )
    public double rq;
    @ColumnInfo(name = "mr"         )
    public double mr;
    @ColumnInfo(name = "imr"        )
    public double imr;
    @ColumnInfo(name = "ibmr"       )
    public double ibmr;
    @ColumnInfo(name = "valv"       )
    public double valv;
    @ColumnInfo(name = "vd"         )
    public double vd;
    @ColumnInfo(name = "vdve"       )
    public double vdve;
    @ColumnInfo(name = "paco2"      )
    public double paco2;
    @ColumnInfo(name = "co_measured")
    public double co_measured;
    @ColumnInfo(name = "ph"         )
    public double ph;
    @ColumnInfo(name = "hco3"       )
    public double hco3;
    @ColumnInfo(name = "tmr"        )
    public double tmr;
    @ColumnInfo(name = "itmr"       )
    public double itmr;
    @ColumnInfo(name = "md"       )
    public double md;
}
