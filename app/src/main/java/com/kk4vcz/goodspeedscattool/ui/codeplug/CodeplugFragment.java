package com.kk4vcz.goodspeedscattool.ui.codeplug;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kk4vcz.goodspeedscattool.R;
import com.kk4vcz.goodspeedscattool.RadioState;

public class CodeplugFragment extends Fragment {

    private CodeplugViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(CodeplugViewModel.class);
        View root = inflater.inflate(R.layout.fragment_codeplug, container, false);


        /*
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        //final RecyclerView rv = root.findViewById(R.id.memlist);
        final TextView tv = root.findViewById(R.id.txtCodeplug);
        RadioState.textCodeplug=tv;

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This will control the connection someday.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                RadioState.drawback();
            }
        });

        //Draw the results when we load.
        RadioState.drawback();

        return root;
    }
}