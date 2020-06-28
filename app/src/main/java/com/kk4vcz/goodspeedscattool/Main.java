package com.kk4vcz.goodspeedscattool;


/*
 * Android is really fussy about performing any real work within the UI thread, and
 * ham radios are often slow about returning any data back to the host.  So this class
 * acts as a sort of buffer to perform transactions in the background and then later
 * fetch their results to the GUI.
 */

import android.os.AsyncTask;

import com.kk4vcz.codeplug.CATRadio;
import com.kk4vcz.codeplug.RadioConnection;
import com.kk4vcz.codeplug.connections.TCPConnection;
import com.kk4vcz.codeplug.radios.kenwood.TMD710G;

import java.io.IOException;

public class Main extends AsyncTask {
    long freq;
    long freqb;

    @Override
    protected Object doInBackground(Object[] objects) {
        /* Hardcoding this until it begins to work. */

        try {
            RadioConnection conn = TCPConnection.getConnection("localhost:54321");
            CATRadio radio=new TMD710G(conn.getInputStream(), conn.getOutputStream());
            freq=radio.getFrequency();
            freqb=radio.getFrequencyB();


        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
