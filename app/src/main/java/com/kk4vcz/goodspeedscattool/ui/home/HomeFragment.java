package com.kk4vcz.goodspeedscattool.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.kk4vcz.goodspeedscattool.R;
import com.kk4vcz.goodspeedscattool.RadioState;
import com.kk4vcz.goodspeedscattool.RadioTask;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        final TextView textView = root.findViewById(R.id.text_home);
        /*
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        RadioState.textFreqa=textView;

        //Make sure the preferences are up to date before executing it in another thread.
        RadioState.updatePreferences();
        final Button connect = root.findViewById(R.id.but_connect);
        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RadioTask asyncTask=RadioTask.newCatTask();
                asyncTask.execute();
            }
        });

        //Draw the results when we load.
        RadioState.drawback(100);

        return root;
    }
}