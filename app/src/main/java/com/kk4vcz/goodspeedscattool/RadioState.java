package com.kk4vcz.goodspeedscattool;

import android.widget.TextView;

import com.kk4vcz.codeplug.CATRadio;
import com.kk4vcz.codeplug.RadioConnection;
import com.kk4vcz.codeplug.connections.TCPConnection;
import com.kk4vcz.codeplug.radios.kenwood.TMD710G;
import com.kk4vcz.goodspeedscattool.ui.cat.CatFragment;
import com.kk4vcz.goodspeedscattool.ui.cat.CatViewModel;
import com.kk4vcz.goodspeedscattool.ui.codeplug.CodeplugFragment;

import java.io.IOException;

/*
 * This class managed the radio connection and other variables.
 */

public class RadioState {
    static RadioConnection conn=null;
    static CATRadio radio=null;

    static CatFragment catfragment=null;
    static CodeplugFragment codeplugFragment=null;
    static MainActivity mainActivity=null;

    public static long freqa=0, freqb=0;
    public static TextView textFreqa=null;


    static void connect(String driver, String path) throws IOException {
        //TODO Support other radio and connection drivers.
        conn = TCPConnection.getConnection("192.168.1.5:54321");
        radio=new TMD710G(conn.getInputStream(), conn.getOutputStream());
    }
    static void disconnect() throws IOException {
        conn.close();
    }

    //Updates some values from the radio and writes them back to the GUI.
    public static void update() throws IOException {
        freqa=radio.getFrequency();
        freqb=radio.getFrequencyB();

        drawback();
    }

    //Draws that current state back to the UI fragments.  Call from any thread.
    private static void drawback(){
        mainActivity.runOnUiThread(new Runnable(){
            public void run(){
                textFreqa.setText(String.format("%d\n%d",freqa,freqb));
            }
        });
    }
}
