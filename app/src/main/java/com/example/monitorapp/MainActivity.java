package com.example.monitorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private int requiredToLoadEntry = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String appNameWithVersion = getString(R.string.app_full_title) + " " + BuildConfig.VERSION_NAME;
        this.setTitle(appNameWithVersion);

        AndroidNetworking.initialize(getApplicationContext());

        Networking net = new Networking();
        net.syncToTheServer(getApplicationContext(), false);

        ViewPager2 pager = (ViewPager2) findViewById(R.id.pager);
        FragmentStateAdapter pageAdapter = new MyFragmentStateAdapter(this);
        pager.setAdapter(pageAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {

            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                String tabLabelText = "";
                if (position == 0) {
                    tabLabelText = getResources().getString(R.string.inv_option);
                }
                if (position == 1) {
                    tabLabelText = getResources().getString(R.string.noninv_option);
                }
                if (position == 2) {
                    tabLabelText = getResources().getString(R.string.mininv_option);
                }
                if (position == 3) {
                    tabLabelText = getResources().getString(R.string.entries_table_title);
                }
                tab.setText(tabLabelText);
            }
        });
        tabLayoutMediator.attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            // called when tab selected
                //System.out.println("selected");

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (requiredToLoadEntry > -1) {
                            try {
                                loadEntry(requiredToLoadEntry);
                            } catch (ExecutionException | InterruptedException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 1500);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            // called when tab unselected
                //System.out.println("unselected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            // called when a tab is reselected
                //System.out.println("reselected");
            }
        });

    }

    public void transferToTheServer(View view) {
        Networking net = new Networking();
        net.syncToTheServer(getApplicationContext(), true);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentEntriesTable fragmentEntriesTable = (FragmentEntriesTable) getSupportFragmentManager().getFragments().get(3).getChildFragmentManager().getFragments().get(0);
                fragmentEntriesTable.onResume();
            }
        }, 1000);

    }

    public void retrieveFromTheServer(View view) {
        Networking net = new Networking();
        net.retrieveFromTheServer(getApplicationContext());

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentEntriesTable fragmentEntriesTable = (FragmentEntriesTable) getSupportFragmentManager().getFragments().get(3).getChildFragmentManager().getFragments().get(0);
                fragmentEntriesTable.onResume();
            }
        }, 1000);

    }

    public void deleteAllEntriesLocally(View view) {
        EntryRepository entryRepository = new EntryRepository(getApplicationContext());
        List<Entry> entryList = new ArrayList<>();
        try {
            entryList = entryRepository.getAllNotSynced();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if  (!entryList.isEmpty()) {
            System.out.println("There are remain not synced entries after successful sync to the server!");
            return;
        }

        entryRepository.clearAllEntries();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentEntriesTable fragmentEntriesTable = (FragmentEntriesTable) getSupportFragmentManager().getFragments().get(3).getChildFragmentManager().getFragments().get(0);
                fragmentEntriesTable.onResume();
            }
        }, 1000);

    }



    public void loadEntry(int uid) throws ExecutionException, InterruptedException, IllegalAccessException {
        EntryRepository entryRepository = new EntryRepository(getApplicationContext());
        Entry entry = uid > 0 ? entryRepository.getById(uid) : entryRepository.getLast();
        if (entry == null){ return;}

        if (entry.entry_type == 0 ||
                entry.entry_type == 1 ||
                entry.entry_type == 2) {
            if (entry.entry_type != ((ViewPager2) findViewById(R.id.pager)).getCurrentItem())
            {
                TabLayout tabhost = (TabLayout) findViewById(R.id.tab_layout);
                tabhost.getTabAt(entry.entry_type).select();
                requiredToLoadEntry = uid;
                return;
            }

        }

        if (requiredToLoadEntry > -1) {
            requiredToLoadEntry = -1;};

        List<String> rowList = new ArrayList<String>();
        for (Field field : entry.getClass().getDeclaredFields()) {
            Object currentValue = field.get(entry);
            if (field.getName().equals("timestamp") ||
                    field.getName().equals("uid") ||
                    field.getName().equals("entry_type")) {
                continue;
            } else {
                int resID = getResources().getIdentifier(field.getName(), "id", "com.example.monitorapp");

                    TextView textView = ((TextView) findViewById(resID));
                    if (textView != null)
                    {
                        if (field.getType().isPrimitive()) {
                            setTextValue(resID, Double.valueOf(currentValue.toString()));
                        }
                        else
                        {
                            textView.setText(currentValue.toString().trim());
                        }
                    }
                    else {
                        //no such field at this tab
                        continue;
                    }

            }
        }
    }

    public void loadLast(View view) {
        try {
            loadEntry(0);
        } catch (ExecutionException | InterruptedException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void exportAll (View view) {

        //creating the export file
        File file = null;
        Exporter exporter = new Exporter(this);
        try {
            file = exporter.exportToCSV();
        } catch (IOException | IllegalAccessException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        //sending the email
        Emailer emailer = new Emailer(this);
        emailer.setFile(file);
        emailer.send();

    }

    public void setTextValue(int id, double val) {
        String s = String.format(getResources().getConfiguration().locale,"%15.2f", val);
        ((TextView) findViewById(id)).setText(s.trim());
    }

    public void calculate(View view) {
        ViewPager2 vp = (ViewPager2) findViewById(R.id.pager);
        int currentPage = vp.getCurrentItem();
        //страница 0 - инвазивный
        //страница 1 - неинвазивный
        //страница 2 - малоинвазивный

        Common c = new Common(this);

        Entry e = new Entry();
        e.entry_type = currentPage;
        e.full_name = "";
        e.sex = "";

        e.pb = c.getdouble(((EditText) findViewById(R.id.pb)).getText().toString());
        e.weight = c.getdouble(((EditText) findViewById(R.id.weight)).getText().toString());
        e.height = c.getdouble(((EditText) findViewById(R.id.height)).getText().toString());
        e.hb = c.getdouble(((EditText) findViewById(R.id.hb)).getText().toString());
        e.sbp = c.getdouble(((EditText) findViewById(R.id.sbp)).getText().toString());
        e.dbp = c.getdouble(((EditText) findViewById(R.id.dbp)).getText().toString());
        e.hr = c.getdouble(((EditText) findViewById(R.id.hr)).getText().toString());
        e.ve = c.getdouble(((EditText) findViewById(R.id.ve)).getText().toString());
        if ((currentPage == 1)||(currentPage == 2)) {
            e.fio2 = c.getdouble(((EditText) findViewById(R.id.fio2)).getText().toString());
            e.feo2 = c.getdouble(((EditText) findViewById(R.id.feo2)).getText().toString());
        }
        e.peco2 = c.getdouble(((EditText) findViewById(R.id.peco2)).getText().toString());
        if ((currentPage == 0)||(currentPage == 2)) {
            e.paco2 = c.getdouble(((EditText) findViewById(R.id.paco2)).getText().toString());
        }
        e.full_name = ((EditText) findViewById(R.id.full_name)).getText().toString();
        e.sex = ((EditText) findViewById(R.id.sex)).getText().toString();
        e.age = c.getdouble(((EditText) findViewById(R.id.age)).getText().toString());

        e.co_measured = c.getdouble(((EditText) findViewById(R.id.co_measured)).getText().toString());
        e.sao2 = c.getdouble(((EditText) findViewById(R.id.sao2)).getText().toString());
        if (currentPage == 0) {
            e.svo2 = c.getdouble(((EditText) findViewById(R.id.svo2)).getText().toString());
        }
        if ((currentPage == 0)||(currentPage == 2)) {
            e.ph = c.getdouble(((EditText) findViewById(R.id.ph)).getText().toString());
            e.hco3 = c.getdouble(((EditText) findViewById(R.id.hco3)).getText().toString());
        }

        Calculator calculator = new Calculator();
        e = calculator.calculate(e);

        //updating values
        setTextValue(R.id.s, e.s);
        if ((currentPage == 0)||(currentPage == 2)) {
            setTextValue(R.id.valv, e.valv);
            setTextValue(R.id.vd, e.vd);
            setTextValue(R.id.vdve, e.vdve);
        }

        if ((currentPage == 1)||(currentPage == 2)) {
            setTextValue(R.id.vo2, e.vo2);
        }
        setTextValue(R.id.ivo2, e.ivo2);
        if ((currentPage == 1)||(currentPage == 2)) {
            setTextValue(R.id.veco2, e.veco2);
        }
        setTextValue(R.id.rq, e.rq);
        if ((currentPage == 1)||(currentPage == 2)) {
            setTextValue(R.id.mr, e.mr);
        }
        setTextValue(R.id.imr, e.imr);
        setTextValue(R.id.ibmr, e.ibmr);

        if ((currentPage == 0)||(currentPage == 2)) {
            setTextValue(R.id.tmr, e.tmr);
            setTextValue(R.id.itmr, e.itmr);
        }

        setTextValue(R.id.ci, e.ci);
        if ((currentPage == 1)||(currentPage == 2)) {
            setTextValue(R.id.do2, e.do2);
        }
        setTextValue(R.id.ido2, e.ido2);
        if ((currentPage == 1)||(currentPage == 2)) {
            setTextValue(R.id.svo2, e.svo2);
        }
        setTextValue(R.id.keo2, e.keo2);

        setTextValue(R.id.map, e.map);
        setTextValue(R.id.sv, e.sv);
        setTextValue(R.id.co, e.co);
        setTextValue(R.id.ci, e.ci);
        setTextValue(R.id.svri, e.svri);

        if ((currentPage == 0)||(currentPage == 2)) {
            setTextValue(R.id.md, e.md);
        }

        //updating the DB
        e.timestamp   = System.currentTimeMillis();
        EntryRepository entryRepository = new EntryRepository(getApplicationContext());
        entryRepository.insert(e);

    }
}