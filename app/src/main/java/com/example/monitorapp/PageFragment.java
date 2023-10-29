package com.example.monitorapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

public class PageFragment extends Fragment {

    private int pageNumber;

    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    public PageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int res = R.layout.fragment_inv;
        if (pageNumber == 0) {
            res = R.layout.fragment_inv;
        } else if (pageNumber == 1) {
            res = R.layout.fragment_noninv;
        } else if (pageNumber == 2) {
            res = R.layout.fragment_mininv;
        } else if (pageNumber == 3) {
            res = R.layout.fragment_entries;
        }
        View result = inflater.inflate(res, container, false);
        return result;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_simplified);
        if (checkBox != null){
                checkBox.setOnCheckedChangeListener(( buttonView, isChecked) ->
                {
                    if (isChecked) {
                        Common c = new Common(getContext());
                        Calculator calculator = new Calculator();
                        double peco2 = c.getdouble(((EditText) view.findViewById(R.id.peco2)).getText().toString());
                        double paco2 = peco2 / calculator.getPeco2paco2Coeff();
                        String s = String.format(getResources().getConfiguration().locale, "%15.2f", paco2);
                        ((TextView) view.findViewById(R.id.paco2)).setText(s.trim());
                        ((TextView) view.findViewById(R.id.paco2)).setEnabled(false);
                    }
                    else {
                        ((TextView) view.findViewById(R.id.paco2)).setEnabled(true);
                    }
                });
        }

        SwitchCompat onOffSwitch = (SwitchCompat) view.findViewById(R.id.add_option);
            if (onOffSwitch != null) {
                onOffSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                {
                    //Log.v("Switch State=", "" + isChecked);
                    FragmentContainerView MyFragment = (FragmentContainerView) view.findViewById(R.id.add_option_fragment_container_view);
                    if (MyFragment != null)
                    {
                        MyFragment.setAlpha(isChecked? 1 : 0);
                    }

                });
            }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (pageNumber==3) {
            View view = getView();
            if (view != null) {
                Button btn1 = view.findViewById(R.id.moveToServer);
                if (btn1 != null) {
                    btn1.setEnabled(States.getInstance().isServerOnline);
                }
                Button btn2 = view.findViewById(R.id.retrieveFromServer);
                if (btn2 != null) {
                    btn2.setEnabled(States.getInstance().isServerOnline);
                }
            }
        }
    }
}