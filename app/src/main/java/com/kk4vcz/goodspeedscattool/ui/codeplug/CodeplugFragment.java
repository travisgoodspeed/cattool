package com.kk4vcz.goodspeedscattool.ui.codeplug;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kk4vcz.goodspeedscattool.R;
import com.kk4vcz.goodspeedscattool.RadioState;

public class CodeplugFragment extends Fragment {

    private CodeplugViewModel galleryViewModel;
    RecyclerView codeplugList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(CodeplugViewModel.class);
        View root = inflater.inflate(R.layout.fragment_codeplug, container, false);


        //final TextView tv = root.findViewById(R.id.txtCodeplug);
        //RadioState.textCodeplug=tv;

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