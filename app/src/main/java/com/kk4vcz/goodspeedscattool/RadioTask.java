package com.kk4vcz.goodspeedscattool;


/*
 * Android is really fussy about performing any real work within the UI thread, and
 * ham radios are often slow about returning any data back to the host.  So this class
 * acts as a sort of buffer to perform transactions in the background and then later
 * fetch their results to the GUI.
 *
 * In general, we try to queue up background tasks to update RadioState, while
 */

import android.os.AsyncTask;
import android.util.Log;


import java.io.IOException;

public class RadioTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        /* Hardcoding this until it begins to work. */

        try {
            /* The current behavior is to tear down the socket and reconnect for each request.
             * This works reasonably well for TCP, but it might not work well for RFCOMM,
             * in which case we'll move to longer lived connections.
             */
            Log.v("RADIORESULT", "Connecting");
            if(RadioState.connect()) {
                Log.v("RADIORESULT", "Connected, grabbing CAT.");
                RadioState.updateCAT();
                RadioState.drawback();
                Log.v("RADIORESULT", "Grabbing codeplug.");
                RadioState.downloadCodeplug();
                RadioState.drawback();
                Log.v("RADIORESULT", "Disconnecting.");
                RadioState.disconnect();
                RadioState.drawback();
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
