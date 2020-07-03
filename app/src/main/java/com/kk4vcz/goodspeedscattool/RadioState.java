package com.kk4vcz.goodspeedscattool;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import com.kk4vcz.codeplug.CATRadio;
import com.kk4vcz.codeplug.Channel;
import com.kk4vcz.codeplug.Main;
import com.kk4vcz.codeplug.RadioConnection;
import com.kk4vcz.codeplug.connections.TCPConnection;
import com.kk4vcz.codeplug.radios.kenwood.THD72;
import com.kk4vcz.codeplug.radios.kenwood.THD74;
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


    //Preferences are managed in the GUI, then fetched after begin updated.
    public static SharedPreferences preferences=null;
    static String pref_conn; //tcp or bt
    static String pref_btdev; //BT mac address.
    static String pref_device; //Device type.
    static String pref_hostname; //TCP hostname
    static int pref_port; //TCP port.



    static boolean connect() throws IOException {
        if(conn!=null)
            return false;

        //TODO Support bluetooth drivers.
        if(pref_conn.equals("tcp")) {
            conn = TCPConnection.getConnection(String.format("%s:%d", pref_hostname, pref_port));
        }else{
            Log.e("RadioState", String.format("Unknown connection type: %s", pref_conn));
            return false;
        }

        if(pref_device.equals("d74")){
            radio=new THD74(conn.getInputStream(), conn.getOutputStream());
        }else if(pref_device.equals("d72")){
            radio=new THD72(conn.getInputStream(), conn.getOutputStream());
        }else if(pref_device.equals("d710")){
            radio=new TMD710G(conn.getInputStream(), conn.getOutputStream());
        }else{
            Log.e("RadioState", String.format("Unknown radio type: %s", pref_device));
            return false;
        }


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

    //Updates the preferences.
    public static void updatePreferences(){
        /* Here we grab the strings from the app's preferences, so that we know what to connect
         * to when it comes time for that.
         */

        pref_conn=preferences.getString("conn", "tcp");
        pref_btdev=preferences.getString("btdevices", "");
        pref_hostname=preferences.getString("hostname", "");
        pref_port=Integer.parseInt(preferences.getString("port", "54321"));
        pref_device=preferences.getString("radio", "d74");

        Log.v("RadioState",
                String.format("conn=%s, btdev=%s, hostname=%s, port=%d, device=%s",
                        pref_conn, pref_btdev, pref_hostname, pref_port, pref_device
                ));
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
