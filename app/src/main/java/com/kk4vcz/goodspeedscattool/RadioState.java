package com.kk4vcz.goodspeedscattool;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.kk4vcz.codeplug.CATRadio;
import com.kk4vcz.codeplug.Channel;
import com.kk4vcz.codeplug.Main;
import com.kk4vcz.codeplug.RadioConnection;
import com.kk4vcz.codeplug.connections.TCPConnection;
import com.kk4vcz.codeplug.radios.kenwood.THD72;
import com.kk4vcz.codeplug.radios.kenwood.THD74;
import com.kk4vcz.codeplug.radios.kenwood.TMD710G;
import com.kk4vcz.codeplug.radios.other.ChirpCSV;
import com.kk4vcz.codeplug.radios.yaesu.FT991A;

import java.io.IOException;

/*
 * This class manages the radio connection and other variables.  Many of its methods can't be
 * called from the GUI thread, so use RadioTask tasks to take care of scheduling the real work.
 */

public class RadioState {
    static RadioConnection conn=null;
    static CATRadio radio=null;

    static MainActivity mainActivity=null;

    //Local copies of radio variables save us from unneeded comm delays.
    public static long freqa=0, freqb=0;
    public static ChirpCSV csvradio=new ChirpCSV();

    //GUI elements can only be written in the GUI thread.
    public static TextView textFreqa=null;
    //public static TextView textCodeplug=null; //Removed for new codeplug view.
    public static ProgressBar progressBar=null;


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
        }else if(pref_conn.equals("bt")) {
            conn = BTConnection.getConnection(pref_btdev);
        }else{
            Log.e("RadioState", String.format("Unknown connection type: %s", pref_conn));
            return false;
        }

        if(pref_device.equals("d74")){
            radio=new THD74(conn.getInputStream(), conn.getOutputStream());
        }else if(pref_device.equals("d72")){
            radio=new THD72(conn.getInputStream(), conn.getOutputStream());
        }else if(pref_device.equals("d710")) {
            radio = new TMD710G(conn.getInputStream(), conn.getOutputStream());
        }else if(pref_device.equals("991a")) {
            radio = new FT991A(conn.getInputStream(), conn.getOutputStream());
        }else{
            Log.e("RadioState", String.format("Unknown radio type: %s", pref_device));
            return false;
        }


        return true;
    }
    static void disconnect() throws IOException {
        //If we've failed to connect, conn will already be null.
        if(conn!=null)
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


    //Downloads the codeplug from the radio.
    public static void downloadCodeplug() throws IOException{
        int count=0;

        RadioState.drawbackstring(String.format("Downloading channels from radio."));
        for (int i = radio.getChannelMin(); i <= radio.getChannelMax(); i++) {
            Channel c = radio.readChannel(i);
            csvradio.deleteChannel(i); //Might rewrite it in just a jiffy.
            if (c != null) {
                Log.v("RadioStateDownload", Main.RenderChannel(c));
                csvradio.writeChannel(i, c);
                count++;
            }
            if(i%10==0){
                RadioState.drawback(i/10);
            }
        }
        RadioState.drawback(100);
        RadioState.drawbackstring(String.format("Downloaded %d channels.",count));
    }
    //Uploads the codeplug to the radio.
    public static void uploadCodeplug() throws IOException{
        int count=0;
        RadioState.drawbackstring(String.format("Uploading channels to radio."));
        for (int i = radio.getChannelMin(); i <= radio.getChannelMax(); i++) {
            Channel c = csvradio.readChannel(i);
            if (c != null) {
                Log.v("RadioStateUpload", Main.RenderChannel(c));
                radio.writeChannel(i, c);
                count++;
            }
            if(i%10==0){
                RadioState.drawback(i/10);
            }
        }
        RadioState.drawback(100);
        RadioState.drawbackstring(String.format("Uploaded %d channels.",count));
    }
    //Erases the radio's channels.
    public static void eraseTargetCodeplug() throws IOException{
        for (int i = radio.getChannelMin(); i <= radio.getChannelMax(); i++) {
            Log.v("RadioStateEraseTargetChannel", "Erasing "+i);
            radio.deleteChannel(i);

            if(i%10==0){
                RadioState.drawback(i/10);
            }
        }
        RadioState.drawback(100);
        RadioState.drawbackstring("Erased radio's channels.");
    }
    //Erases the local channels.
    public static void eraseLocalCodeplug() throws IOException {
        for (int i = 0; i < 1000; i++) {
            csvradio.deleteChannel(i);
        }
        RadioState.drawback(100);
        RadioState.drawbackstring("Erased phone's channels.");
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
    public static void drawback(final int progress){
        mainActivity.runOnUiThread(new Runnable(){
            public void run(){
                textFreqa.setText(String.format("%d\n%d",freqa,freqb));

                /* This was the old codeplug view, just a textfile.  Now replaced.
                if(textCodeplug!=null) {
                    String codeplugdump="";

                    try {
                        for (int i = csvradio.getChannelMin(); i <= csvradio.getChannelMax(); i++) {
                            Channel ch = csvradio.readChannel(i);
                            if(ch!=null) {
                                int index=ch.getIndex();
                                String name=ch.getName();
                                if(name==null)
                                    name="";
                                double frequency=ch.getRXFrequency()/1000000.0;

                                //Split dir as one letter.
                                String splitdir=ch.getSplitDir();
                                if(splitdir.equals("split"))
                                    splitdir="s";
                                if(splitdir.equals("off"))
                                    splitdir=" ";

                                //Tone mode and Tone.
                                String tonemode=ch.getToneMode();
                                if(tonemode.equals("tone"))
                                    tonemode="t";
                                String tone=String.format("%2s%05.1f", tonemode, ch.getToneFreq()/10.0);

                                if(tonemode.equals("dcs"))
                                    tone=String.format("dcs %03d",ch.getDTCSCode());
                                if(tonemode.equals(""))
                                    tone="";



                                codeplugdump += String.format("%03d %17s %03.03f%1s %8s\n", index, name, frequency, splitdir, tone);
                            }
                        }
                    }catch(IOException e){

                    }

                    textCodeplug.setText(codeplugdump);
                }else
                    Log.e("RADIOSTATE", "Refusing to display codeplug on a null pointer.");
                 */
                progressBar.setProgress(progress);
                progressBar.setVisibility(progress<100 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }



    //Draws that current state back to the UI fragments.  Call from any thread.
    public static void drawbackstring(final String message){
        mainActivity.runOnUiThread(new Runnable(){
            public void run(){
                try {
                    View view=mainActivity.findViewById(android.R.id.content);
                    if (view != null)
                        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                }catch(IllegalArgumentException e){
                    /* Sometimes the View isn't the current view, causing the snackbar creation
                     * to fail.  No biggie, because this is just an informational message, but
                     * we need to catch the exception to prevent a crash.
                     */
                    Log.e("RadioState","drawbackString", e);
                }
            }
        });
    }
}
