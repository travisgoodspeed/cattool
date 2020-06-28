package com.kk4vcz.goodspeedscattool;


/*
 * Android is really fussy about performing any real work within the UI thread, and
 * ham radios are often slow about returning any data back to the host.  So this class
 * acts as a sort of buffer to perform transactions in the background and then later
 * fetch their results to the GUI.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.kk4vcz.codeplug.CATRadio;
import com.kk4vcz.codeplug.RadioConnection;
import com.kk4vcz.codeplug.connections.TCPConnection;
import com.kk4vcz.codeplug.radios.kenwood.TMD710G;


import com.kk4vcz.goodspeedscattool.R;
import com.kk4vcz.goodspeedscattool.RadioTask;

import java.io.IOException;

public class RadioTask extends AsyncTask {
    long freq;
    long freqb;

    @Override
    protected Object doInBackground(Object[] objects) {
        /* Hardcoding this until it begins to work. */

        try {
            RadioConnection conn = TCPConnection.getConnection("192.168.1.5:54321");
            CATRadio radio=new TMD710G(conn.getInputStream(), conn.getOutputStream());
            for(int i=0; i<20; i++)
                radio.getID();
            freq=radio.getFrequency();
            freqb=radio.getFrequencyB();

        }catch(IOException e){
            e.printStackTrace();
            Log.e("RADIORESULT", e.getMessage());
        }

        return String.format("%d %d\n", freq, freqb);
    }

    @Override
    protected void onPostExecute(Object res) {
        super.onPostExecute(res);
        Log.e("RADIORESULT", (String) res);
    }
}
