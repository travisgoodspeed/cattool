package com.kk4vcz.goodspeedscattool.ui.codeplug;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kk4vcz.goodspeedscattool.R;
import com.kk4vcz.goodspeedscattool.RadioState;

import java.io.IOException;

public class CodeplugFragment extends Fragment {
    RecyclerView codeplugList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_codeplug, container, false);

        codeplugList = (RecyclerView) root.findViewById(R.id.codeplugList);
        setupRecyclerView();

        //Draw the results when we load.
        RadioState.drawback(100);

        //This fragment handles the context menu, but CodeplugViewAdapter handles the setting the RadioState index.
        registerForContextMenu(codeplugList);

        return root;
    }

    private void setupRecyclerView() {
        CodeplugViewAdapter adapter = new CodeplugViewAdapter();
        codeplugList.setHasFixedSize(true);
        codeplugList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        codeplugList.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            switch (item.getTitle().toString()) {

                case "Delete":
                    RadioState.csvradio.deleteChannel(RadioState.index);
                    RadioState.drawbackstring("Deleted local memory " + RadioState.index + ".");
                    break;
                case "Tune":
                case "Edit":
                case "Duplicate":
                case "Move":
                case "M->V":
                default:
                    RadioState.drawbackstring("TODO: "+item.getTitle());
            }

        }catch(IOException e){
            Log.e("CODEPLUG", "Error handling the context menu.", e);
        }

        return true;
    }
}