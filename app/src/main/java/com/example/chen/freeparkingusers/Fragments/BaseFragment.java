package com.example.chen.freeparkingusers.Fragments;

import android.support.v4.app.Fragment;
import android.widget.TextView;

/**
 * Created by chen on 16/7/4.
 */
public class BaseFragment extends Fragment {
    protected String title;
    protected int Resid;
    protected TextView tv;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconId() {
        return Resid;
    }

    public void setIconId(int Resid) {
        this.Resid = Resid;
    }
}
