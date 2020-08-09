package com.kk4vcz.goodspeedscattool.ui.codeplug;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
    //Target index for loading results.
    int index=0;

    //Parameters.
    String loc="";
    int distance=25;
    long band=0;

    //This stores the results of the query.
    Radio results=null;

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
        editLocation =root.findViewById(R.id.editLocation);
        editDistance =root.findViewById(R.id.editDistance);
        radiogroupBand = root.findViewById(R.id.radiogroupBand);

        //Apply the listeners.
        buttonFetch.setOnClickListener(this);
        buttonApply.setOnClickListener(this);

        //Apply is disabled until the channels are ready.
        buttonApply.setEnabled(false);

        //Apply some strings.
        textQueryInsertLocation.setText("Insert at memory "+index+".");

        return root;
    }



    //Fetch the results from the server.  Must occur in a background thread.
    void fetch() throws IOException {
        RadioAPI api=new RepeaterBook();
        results=api.queryProximity(loc, distance, band);
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
    EditText editLocation, editDistance;
    RadioGroup radiogroupBand;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonFetch:
                //Load the parameters.
                loc=editLocation.getText()+"";
                distance=Integer.parseInt(editDistance.getText()+"");
                switch(radiogroupBand.getCheckedRadioButtonId()){
                    case R.id.radioAll:
                    default:
                        band=0;
                        break;
                    case R.id.radio10M:
                        band=28000000;
                        break;
                    case R.id.radio6M:
                        band=53000000;
                        break;
                    case R.id.radio4M:
                        band=70000000;
                        break;
                    case R.id.radio2M:
                        band=144000000;
                        break;
                    case R.id.radio125M:
                        band=220000000;
                        break;
                    case R.id.radio70CM:
                        band=440000000;
                        break;
                    case R.id.radio33CM:
                        band=915000000;
                        break;
                    case R.id.radio9CM:
                        band=1200000000;
                        break;
                }

                //Perform the query.
                new QueryTask().execute(editLocation.getText()+"");
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
        protected Long doInBackground(String... asdf) {
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