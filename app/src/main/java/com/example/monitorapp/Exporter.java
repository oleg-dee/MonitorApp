package com.example.monitorapp;

import android.content.Context;
import android.os.Build;

import com.opencsv.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Exporter {
    private Context context;

    private Map<String, String> namesFilterMap = new LinkedHashMap<String, String>() {{
        put("uid", "ID");
        put("full_name", "DETAILS");
        put("map", "MAP");
        put("hr", "HR");
        put("ci", "CI");
        put("ido2", "DO2I");
        put("ivo2", "VO2I");
        put("keo2", "O2ER");
        put("rq", "RQ");
        put("imr", "MRI");
        put("ibmr", "BMRI");
        put("itmr", "TMRI");
        put("md", "MD");
    }};

    public Exporter(Context context) {
        this.context = context;
    }

    public Map<String, String> getNamesFilterMap() {
        return namesFilterMap;
    }

    public List<String>  buildRowList(Entry currentEntry, boolean withTitles) {
        if (currentEntry == null ) {return new ArrayList<String>();}
        List<String> rowList = new ArrayList<String>();
        for (String key : namesFilterMap.keySet()) {
            for (Field field : currentEntry.getClass().getDeclaredFields()) {
                if (field.getName() == key) {
                    Object currentValue = null;
                    try {
                        currentValue = field.get(currentEntry);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (field.getName() == "timestamp") {
                        String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentValue);
                        rowList.add((withTitles ? namesFilterMap.get(key) + ": " : "" ) + formattedTimestamp);
                    } else if (field.getName() == "uid" || field.getName() == "full_name") {
                        rowList.add((withTitles ? namesFilterMap.get(key) + ": " : "" ) + currentValue.toString());
                    } else {
                        rowList.add((withTitles ? namesFilterMap.get(key) + ": " : "" ) + String.format("%.2f",currentValue));
                    }

                    break;
                }
            }
        }
        return rowList;

    }

    public File exportToCSV() throws IOException, IllegalAccessException, ExecutionException, InterruptedException {

        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "MonitorAppEntries.csv";
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);
        CSVWriter writer = null;

        List<String> rowList = new ArrayList<String>();
        Entry entry = new Entry();
        for (String key : namesFilterMap.keySet()){
            for (Field field : entry.getClass().getDeclaredFields()) {
                if (field.getName().equals(key)) {
                    rowList.add(namesFilterMap.get(key));
                    break;
                }
            }
        }

        List<String[]> data = new ArrayList<String[]>();
        data.add(rowList.toArray(new String[0]));

        EntryRepository entryRepository = new EntryRepository(context);
        List<Entry> listOfEntries = new ArrayList<Entry>();
        listOfEntries = entryRepository.getAll();
        for (Entry currentEntry : listOfEntries) {
            rowList = buildRowList(currentEntry, false);
            data.add(rowList.toArray(new String[0]));
        }

        try {
            writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filePath), "windows-1251"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String[] dataRow : data) {
            if (writer != null) {
                writer.writeNext(dataRow);
            }
        }

        if (writer != null) {
            writer.close();
        }

        return f;

    }




}
