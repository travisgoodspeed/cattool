package com.kk4vcz.goodspeedscattool;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

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
import com.kk4vcz.goodspeedscattool.ui.codeplug.CodeplugViewAdapter;
import com.kk4vcz.goodspeedscattool.ui.codeplug.EditFragment;
import com.kk4vcz.goodspeedscattool.ui.codeplug.QueryFragment;

import java.io.IOException;

/*
 * This class manages the radio connection and other variables.  Many of its methods can't be
 * called from the GUI thread, so use RadioTask tasks to take care of scheduling the real work.
 */

public class RadioState {
    static RadioConnection conn=null;
    static CATRadio radio=null;

    static public MainActivity mainActivity=null;

    //Local copies of radio variables save us from unneeded comm delays.
    public static long freqa=0, freqb=0;
    public static ChirpCSV csvradio=new ChirpCSV();
    public static int index=0; //Currently selected channel, for GUI use one.

    //GUI elements can only be written in the GUI thread.
    public static TextView textFreqa=null;
    //public static TextView textCodeplug=null; //Removed for new codeplug view.
    public static ProgressBar progressBar=null;
    public static CodeplugViewAdapter codeplugViewAdapter=null;


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

        if(pref_conn.equals("tcp")) {
            conn = TCPConnection.getConnection(String.format("%s:%d", pref_hostname, pref_port));
        }else if(pref_conn.equals("bt")) {
            conn = BTConnection.getConnection(pref_btdev);
        }else{
            Log.e("RadioState", String.format("Unknown connection type: %s", pref_conn));
            return false;
        }

        if(conn==null){
            //We attempted to connect, but it didn't work out.
            drawbackstring("Failed to connect. Check radio and settings.");
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
                //TODO better CAT.
                try {
                    if(textFreqa!=null)
                        textFreqa.setText(String.format("%d\n%d", freqa, freqb));

                    //This updates the codeplug view.
                    if (codeplugViewAdapter != null)
                        codeplugViewAdapter.notifyDataSetChanged();


                    progressBar.setProgress(progress);
                    progressBar.setVisibility(progress < 100 ? View.VISIBLE : View.INVISIBLE);
                }catch(NullPointerException e){
                    Log.e("RadioState", "Null pointer when drawing the frequencies.");
                }
            }
        });
    }
    //Draws that current state back to the UI fragments.  Call from any thread.
    public static void drawback(){
        mainActivity.runOnUiThread(new Runnable(){
            public void run(){
                //This updates the codeplug view.
                if(codeplugViewAdapter!=null)
                    codeplugViewAdapter.notifyDataSetChanged();
            }
        });
    }


    //Draws that current state back to the UI fragments.  Call from any thread.
    public static void drawbackstring(final String message){
        mainActivity.runOnUiThread(new Runnable(){
            public void run(){
                try {
                    View view=mainActivity.findViewById(android.R.id.content);

                    //This updates the codeplug view.
                    if(codeplugViewAdapter!=null)
                        codeplugViewAdapter.notifyDataSetChanged();

                    if (view != null) {
                        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }
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

    //Shows the channel editor for a given channel number.
    public static void showEditor(final int index){
        mainActivity.runOnUiThread(new Runnable(){
            public void run(){
                EditFragment edit=new EditFragment(index);

                // A better solution is to display the fragment as as a Dialog.
                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();

                // The device is using a large layout, so show the fragment as a dialog
                edit.show(fragmentManager, "dialog");


                /* For some stupid bug, this overlays on the lower image, and I don't know why.
                // Show the fragment full screen.
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                // For a little polish, specify a transition animation
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                // To make it fullscreen, use the 'content' root view as the container
                // for the fragment, which is always the root view for the activity
                //transaction.add(android.R.id.content, edit).addToBackStack(null).commit();
                transaction.replace(android.R.id.content, edit).addToBackStack(null).commit();
                */
            }
        });
    }

    //Shows the query fragment to import from a databse source.
    public static void showQueryImport(final int index){
        mainActivity.runOnUiThread(new Runnable(){
            public void run(){
                QueryFragment query=new QueryFragment(index);

                // A better solution is to display the fragment as as a Dialog.
                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();

                // The device is using a large layout, so show the fragment as a dialog
                query.show(fragmentManager, "dialog");
            }
        });
    }

}
