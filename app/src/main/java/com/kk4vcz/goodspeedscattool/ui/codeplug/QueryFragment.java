package com.kk4vcz.goodspeedscattool.ui.codeplug;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kk4vcz.codeplug.Main;
import com.kk4vcz.codeplug.Radio;
import com.kk4vcz.codeplug.RadioAPI;
import com.kk4vcz.codeplug.api.RepeaterBook;
import com.kk4vcz.goodspeedscattool.R;
import com.kk4vcz.goodspeedscattool.RadioState;

import java.io.IOException;

/**
 * This fragment fetches results from RepeaterBook's servers.
 */
public class QueryFragment extends DialogFragment implements View.OnClickListener {
    int index=0;

    public QueryFragment(int index) {
        this.index=index;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_query, container, false);

        //Grab the widgets.
        buttonFetch=root.findViewById(R.id.buttonFetch);
        buttonApply=root.findViewById(R.id.buttonApply);
        textQueryInsertLocation=root.findViewById(R.id.textQueryInsertLocation);
        textLocation=root.findViewById(R.id.textLocation);

        //Apply the listeners.
        buttonFetch.setOnClickListener(this);
        buttonApply.setOnClickListener(this);

        //Apply is disabled until the channels are ready.
        buttonApply.setEnabled(false);

        //Apply some strings.
        textQueryInsertLocation.setText("Insert at memory "+index+".");

        return root;
    }

    //This stores the results of the query.
    Radio results=null;

    //Fetch the results from the server.  Must occur in a background thread.
    void fetch() throws IOException {
        RadioAPI api=new RepeaterBook();
        results=api.queryProximity(textLocation.getText()+"", 25, 0);
    }

    //Apply them to the appropriate index.
    void apply(){
        try {
            Main.CopyChannels(RadioState.csvradio, index, results);
            RadioState.drawbackstring("Loaded channels.");
        }catch(IOException e){
            Log.e("QUERY", "apple()", e);
            RadioState.drawbackstring("Error loading channels.");
        }
    }


    //Local pointers to widgets.
    Button buttonFetch, buttonApply;
    TextView textQueryInsertLocation;
    EditText textLocation;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonFetch:
                new QueryTask().execute(textLocation.getText()+"");
                break;
            case R.id.buttonApply:
                apply();
                getDialog().dismiss();
                break;
            default:
                Log.e("QUERY", "Unhandled click event.");
                break;
        }
    }


    private class QueryTask extends AsyncTask<String, Integer, Long> {
        protected Long doInBackground(String... loc) {
            try {
                results=null;
                fetch();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return 0L;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
            buttonApply.setEnabled(results!=null);
        }
    }

}