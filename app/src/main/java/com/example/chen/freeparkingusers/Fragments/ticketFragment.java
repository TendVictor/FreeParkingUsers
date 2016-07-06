package com.example.chen.freeparkingusers.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chen.freeparkingusers.R;

/**
 * Created by chen on 16/7/4.
 */
public class ticketFragment extends BaseFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container , Bundle SavedInstanceState){
        View view = inflater.inflate(R.layout.fragment_ticket,null,false);
//        tv = (TextView) view.findViewById(R.id.tv_ticket_test);
//        tv.setText(getTitle());
        return view;
    }

}
