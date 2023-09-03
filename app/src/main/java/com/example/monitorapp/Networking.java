package com.example.monitorapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Networking {

    private final String endpointURL = "http://ec2-54-197-15-129.compute-1.amazonaws.com:8080/monitorapp/entries"; //"http://10.0.2.2:8080/monitorapp/entries"
    private final String apiKey = "$2a$12$GZ8y/zt9wBS2A7QK/zMsQuPkFxo1GcRXjhTS.q/2HSukY.ZmnNcNC";
    private final int retrieveCount = 5;

    private List<Entry> entryList;

    public Networking() {
        entryList = new ArrayList<>();
    }

    private JSONArray serializeEntries(List<Entry> entryList) {
        JSONArray jsonArray = new JSONArray();
        for (Entry entry : entryList) {
            JSONObject jsonObject = new JSONObject();
            Field[] fields = entry.getClass().getDeclaredFields();
            try {
                for (Field field : fields) {
                    try {
                        if (field.getName().equals("uid")) {
                            jsonObject.put("clientRecId", field.get(entry));
                            continue;
                        }
                        jsonObject.put(field.getName(), field.get(entry));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                jsonObject.put("userId", "oleg.dee@gmail.com");
                jsonObject.put("appId", BuildConfig.APPLICATION_ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }

        return jsonArray;

    }

    private List<Entry> deserializeEntries(String json) {
        Gson g = new Gson();
        List<Entry> entriesList = Arrays.asList(g.fromJson(json,  Entry[].class));
        return entriesList;
    }


    public void syncToTheServer(Context context, boolean deleteAfterSync) {
        EntryRepository entryRepository = new EntryRepository(context);
        entryList.clear();
        try {
            entryList = entryRepository.getAllNotSynced();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (entryList.isEmpty()) {
            if (deleteAfterSync) {
                entryRepository.clearAllEntries();
            }
            return;
        }

        JSONArray jsonArray = serializeEntries(entryList);

        System.out.println("Calling the server endpoint to SYNC..");
        AndroidNetworking.post(endpointURL)
//                .addPathParameter("pageNumber", "0")
//                .addQueryParameter("limit", "3")
                .addJSONArrayBody(jsonArray)
                .addHeaders("Accept", "application/json")
                .addHeaders("x-api-key", apiKey)
//                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        States.getInstance().isServerOnline = true;
                        System.out.println("Got the response: " + response);
                        for(Entry entry : entryList) {
                            entry.synced = 1;
                            entryRepository.update(entry);
                        }

                        if (deleteAfterSync) {

                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    entryList.clear();
                                    try {
                                        entryList = entryRepository.getAllNotSynced();
                                    } catch (ExecutionException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if  (!entryList.isEmpty()) {
                                        try {
                                            throw new Exception("There are remain not synced entries after successful sync to the server!");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    entryList.clear();
                                    entryRepository.clearAllEntries();

                                }
                            }, 200);

                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        States.getInstance().isServerOnline = false;
                        System.out.println("Got an error: " + anError);
                    }
                });
//                .getAsJSONArray(new JSONArrayRequestListener() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        // do anything with response
//                    }
//                    @Override
//                    public void onError(ANError error) {
//                        // handle error
//                    }
//                });

    }

    public void retrieveFromTheServer(Context context) {
        EntryRepository entryRepository = new EntryRepository(context);

        System.out.println("Calling the server endpoint to RETRIEVE..");
        AndroidNetworking.get(endpointURL)
                .addQueryParameter("count", String.valueOf(retrieveCount))
//                .addJSONArrayBody(jsonArray)
                .addHeaders("Accept", "application/json")
                .addHeaders("x-api-key", apiKey)
//                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        States.getInstance().isServerOnline = true;
                        System.out.println("Got the response: " + response);
                        List<Entry> entryList = new ArrayList<>(deserializeEntries(response));
                        for (Entry entry : entryList) {
                            entry.synced = 1;
                        }

                        List<Entry> currentEntryList;
                        List<Entry> entryListToRemove = new ArrayList<>();
                        try {
                            currentEntryList = entryRepository.getAll();
                            HashSet<Integer> uids = new HashSet<>();
                            for (Entry curEntry : currentEntryList) {
                                uids.add(curEntry.uid);
                            };
                            for (Entry entry : entryList) {
                                if (uids.contains(entry.clientRecId)){
                                    entryListToRemove.add(entry);
                                }
                            }
                            entryList.removeAll(entryListToRemove);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (entryList.size()>0) {
                            entryRepository.insertAll(Arrays.asList(entryList.toArray(new Entry[0])));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        States.getInstance().isServerOnline = false;
                        System.out.println("Got an error: " + anError);
                    }
                });

    }


}
