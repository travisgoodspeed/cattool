package com.kk4vcz.goodspeedscattool.ui.cat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.kk4vcz.goodspeedscattool.R;
import com.kk4vcz.goodspeedscattool.RadioState;

public class CatFragment extends Fragment {

    private CatViewModel catViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        catViewModel =
                ViewModelProviders.of(this).get(CatViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cat, container, false);


        /*
        final TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */

        //Draw the results when we load.
        RadioState.drawback(100);

        return root;
    }
}