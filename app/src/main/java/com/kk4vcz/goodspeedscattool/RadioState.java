package com.kk4vcz.goodspeedscattool;

import android.util.Log;
import android.widget.TextView;

import com.kk4vcz.codeplug.CATRadio;
import com.kk4vcz.codeplug.Channel;
import com.kk4vcz.codeplug.Main;
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

    static MainActivity mainActivity=null;

    public static long freqa=0, freqb=0;
    public static TextView textFreqa=null;
    public static TextView textCodeplug=null;



    static boolean connect(String driver, String path) throws IOException {
        if(conn!=null)
            return false;

        //TODO Support other radio and connection drivers.
        conn = TCPConnection.getConnection(path);
        radio=new TMD710G(conn.getInputStream(), conn.getOutputStream());
        return true;
    }
    static void disconnect() throws IOException {
        conn.close();
        conn=null;
        radio=null;
    }

    //Updates some values from the radio and writes them back to the GUI.
    public static void updateCAT() throws IOException {
        Log.v("RADIORESULT", "Getting FreqA");
        freqa=radio.getFrequency();
        Log.v("RADIORESULT", "Getting FreqB");
        freqb=radio.getFrequencyB();
    }

    static String codeplugdump=""; //TODO More than just a string.
    //Downloads the codeplug from the radio.
    public static void downloadCodeplug() throws IOException{
        codeplugdump="";
        for (int i = radio.getChannelMin(); i <= radio.getChannelMax(); i++) {
            Channel c = radio.readChannel(i);
            if (c != null) {
                Log.e("RadioState", Main.RenderChannel(c));
                codeplugdump+=Main.RenderChannel(c)+"\n";
            }
        }
    }

    //Draws that current state back to the UI fragments.  Call from any thread.
    public static void drawback(){
        mainActivity.runOnUiThread(new Runnable(){
            public void run(){
                textFreqa.setText(String.format("%d\n%d",freqa,freqb));
                if(textCodeplug!=null)
                    textCodeplug.setText(codeplugdump);
                else
                    Log.e("RADIOSTATE", "Refusing to display codeplug on a null pointer.");
            }
        });
    }
}
