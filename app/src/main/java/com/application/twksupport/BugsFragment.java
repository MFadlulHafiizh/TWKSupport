package com.application.twksupport;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.twksupport.adapter.RecycleViewAdapter;
import com.application.twksupport.model.BugsData;

import java.util.ArrayList;
import java.util.List;

public class BugsFragment extends Fragment {
    View view;
    private RecyclerView rvBugs;
    private List<BugsData> listBugs;

    public BugsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listBugs = new ArrayList<>();
        listBugs.add(new BugsData("high", "Cant update item", "on proccess"));
        listBugs.add(new BugsData("low", "Error 500", "Reported"));
        listBugs.add(new BugsData("middle", "CRUD Error", "on proccess"));
        listBugs.add(new BugsData("low", "Cant add new item", "Reported"));
        listBugs.add(new BugsData("high", "Cant update item", "Reported"));
        listBugs.add(new BugsData("middle", "Error 404 Not Found", "on proccess"));
        listBugs.add(new BugsData("middle", "Cant update item", "on proccess"));
        listBugs.add(new BugsData("high", "Cant upload photo", "on proccess"));
        listBugs.add(new BugsData("low", "Cant update item", "Reported"));
        listBugs.add(new BugsData("middle", "Cant update item", "on proccess"));
        listBugs.add(new BugsData("high", "Error 500", "Reported"));
        listBugs.add(new BugsData("low", "Cant update item", "on proccess"));
        listBugs.add(new BugsData("low", "Error 404 Not Found", "on proccess"));
        listBugs.add(new BugsData("high", "Cant update item", "on proccess"));
        listBugs.add(new BugsData("high", "Cant update item", "on proccess"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bugs, container, false);
        rvBugs = (RecyclerView) view.findViewById(R.id.rv_bugs);
        RecycleViewAdapter rvAdapter = new RecycleViewAdapter(getContext(), listBugs);
        rvBugs.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvBugs.setAdapter(rvAdapter);
        return view;
    }
}