package com.example.chen.freeparkingusers.Fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chen.freeparkingusers.R;

/**
 * Created by chen on 16/7/4.
 */
public class TicketFragment extends BaseFragment {

    private View rootView = null;

    private SwipeRefreshLayout swipeRefreshLayout = null;
    private RecyclerView recyclerView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket, null, false);
        initViewAndEvents(view);
        return view;
    }

    private void initViewAndEvents(View view) {

        recyclerView = $(view,R.id.rv_ticket);
        swipeRefreshLayout = $(view,R.id.sl_ticket);

    }

    private <T extends View> T $(View v, int id) {
        return (T) v.findViewById(id);
    }
}
