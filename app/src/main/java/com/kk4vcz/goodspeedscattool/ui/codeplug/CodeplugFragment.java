package com.kk4vcz.goodspeedscattool.ui.codeplug;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kk4vcz.goodspeedscattool.R;
import com.kk4vcz.goodspeedscattool.RadioState;

public class CodeplugFragment extends Fragment {
    RecyclerView codeplugList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_codeplug, container, false);

        codeplugList = (RecyclerView) root.findViewById(R.id.codeplugList);
        setupRecyclerView();

        //Draw the results when we load.
        RadioState.drawback(100);

        return root;
    }

    private void setupRecyclerView() {
        CodeplugViewAdapter adapter = new CodeplugViewAdapter();
        codeplugList.setHasFixedSize(true);
        codeplugList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        codeplugList.setAdapter(adapter);
    }
}