package com.kk4vcz.goodspeedscattool;


/*
 * Android is really fussy about performing any real work within the UI thread, and
 * ham radios are often slow about returning any data back to the host.  So this class
 * acts as a sort of buffer to perform transactions in the background and then later
 * fetch their results to the GUI.
 */

import android.os.AsyncTask;
import android.util.Log;


import java.io.IOException;

public class RadioTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        /* Hardcoding this until it begins to work. */

        try {
            if(RadioState.connect("d710", "192.168.1.5:54321")) {
                RadioState.updateCAT();
                RadioState.disconnect();
            }else{
                Log.e("RADIORESULT", "Ignoring duplicate connection request.");
            }
        }catch(IOException e){
            e.printStackTrace();
            Log.e("RADIORESULT", e.getMessage());
            try{
                RadioState.disconnect();
            }catch(IOException f){
                Log.e("RADIORESULT", "IOException in disconnect handler.");
            }
        }catch(NullPointerException e){
            Log.e("RADIORESULT", e.getMessage());
            try{
                RadioState.disconnect();
            }catch(IOException f){
                Log.e("RADIORESULT", "IOException in null pointer exception handler.");
            }
        }


        return String.format("VFOA=%d\n", RadioState.freqa);
    }

    @Override
    protected void onPostExecute(Object res) {
        super.onPostExecute(res);
        Log.e("RADIORESULT", (String) res);
    }
}
