package com.kk4vcz.goodspeedscattool.ui.codeplug;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;


import com.kk4vcz.codeplug.CSVChannel;
import com.kk4vcz.codeplug.Channel;
import com.kk4vcz.goodspeedscattool.R;
import com.kk4vcz.goodspeedscattool.RadioState;

import java.io.IOException;


/* This fragment is used to display a codeplug memory entry, allowing the user to edit it and
   eventually save it back to the local CSVRadio.  GUI programming sucks, and I'm not very good
   at it, so kindly rewrite this class if you find the time.

   --Travis
 */

public class EditFragment extends DialogFragment implements View.OnClickListener {
    Channel ch;

    //These are the widgets that show the tones.
    TextView name;
    TextView rxfreq;
    TextView txfreq;
    Button shiftbutton;
    TextView tone;
    Button tonebutton;
    Button modebutton;


    //Show the current channel settings.
    public void showChannel(){
        //This configures them to show the right values.
        name.setText(ch.getName());
        rxfreq.setText(String.format("%f", ch.getRXFrequency()/1000000.0));
        tonebutton.setText(ch.getToneMode());
        modebutton.setText(ch.getMode());
        switch(ch.getToneMode()){
            case "tone":
                tone.setEnabled(true);
                tone.setText(getToneString());
                break;
            case "ct":
                tone.setEnabled(true);
                tone.setText(getToneString());
                break;
            case "dcs":
                tone.setEnabled(true);
                tone.setText(String.format("%03d",ch.getDTCSCode()));
                break;
            case "":
                tone.setEnabled(false);
                tone.setText("");
                break;
            default:
                Log.e("EDIT", "Unknown tone mode: "+ch.getToneMode());
                break;
        }
        shiftbutton.setText(ch.getSplitDir());
        shiftbutton.setEnabled(true);
        switch(ch.getSplitDir()){
            case "+":
            case "-":
                long diff = java.lang.Math.abs(ch.getTXFrequency()-ch.getRXFrequency());
                txfreq.setText(String.format("%f", diff/1000000.0));
                txfreq.setEnabled(true);
                break;
            case "split":
                txfreq.setText(String.format("%f", ch.getTXFrequency()/1000000.0));
                txfreq.setEnabled(true);
                break;
            case "simplex":
            case "":
                txfreq.setText(String.format("%f", ch.getRXFrequency()/1000000.0));
                txfreq.setEnabled(false);
                shiftbutton.setText("simplex");
                break;
            default:
                Log.e("EDIT", "Unknown split dir: "+ch.getToneMode());
                break;
        }


        //Digital modes get weird, so we handle them as special cases.
        switch(ch.getMode()){
            case "DSTAR":
                tone.setText(ch.getURCALL());
                tone.setEnabled(true);
                tonebutton.setText("URCALL");
                break;
        }
    }

    //Parses a long frequency from a string.
    public static long freq2long(String freq) throws NumberFormatException{
        return (long) (Double.parseDouble(freq) * 1000000.0);
    }

    //Parses a long frequency from a string.
    public static String freq2str(long freq){
        return String.format("%f", freq/1000000.0);
    }

    //Apply the settings to the local copy of the channel.
    public void applyChannel(){
        /* The buttons are applied through their own functions, but we need to write all
           modified strings back to from their textfields here.
         */

        //Name and RX frequency are easy.
        ch.setName(name.getText().toString());
        try {
            ch.setRXFrequency(freq2long(rxfreq.getText().toString()));
        }catch(NumberFormatException e){
            Log.e("EDIT", "Illegal RX Frequency", e);
        }

        //The shift and the tone are trickier, because they depend upon the mode.
        //TODO Shift
        //TODO Tone
    }

    //Next tone mode.
    public void nextTone(){
        //Digital modes don't support alternate tone modes.
        switch(ch.getToneMode()){
            case "DSTAR":
            case "P25":
            case "DMR":
                return;
        }

        //For the rest of them, we cycle through the list as each button is pressed.
        switch(ch.getToneMode()){
            case "tone":
                ch.setToneMode("ct");
                break;
            case "ct":
                ch.setToneMode("dcs");
                break;
            case "dcs":
                ch.setToneMode("");
                break;
            case "":
                ch.setToneMode("tone");
                break;
            default:
                Log.e("EDIT", "Unknown tone mode: "+ch.getToneMode());
                ch.setToneMode("");
                break;
        }
    }

    //Next mode.
    public void nextMode(){
        //TODO: Include all of FM, FMN, FMW, AM, USB, LSB, USB-D, LSB-D, DMR, P25, DSTAR, CW, R-CW, etc
        switch(ch.getMode()){
            case "FM":
                ch.setMode("FMN");
                break;
            case "FMN"://Correct string.
            case "NFM"://Incorrect, but common string.
                ch.setMode("AM");
                break;
            case "AM":
                ch.setMode("USB");
                break;
            case "USB":
                ch.setMode("LSB");
                break;
            case "LSB":
                ch.setMode("USB-D");
                break;
            case "USB-D":
                ch.setMode("LSB-D");
                break;
            case "LSB-D":
                ch.setMode("DMR");
                break;
            case "DMR":
                ch.setMode("P25");
                break;
            case "P25":
                ch.setMode("DSTAR");
                break;
            case "DSTAR":
                ch.setMode("CW");
                break;
            case "CW":
                ch.setMode("R-CW");
                break;
            case "R-CW":
                ch.setMode("FM");
                break;
            default:
                Log.e("EDIT", "Unknown mode: "+ch.getToneMode());
                ch.setMode("FM"); //Bring the loop back on track.
                break;
        }
    }

    static long getARO(long freq) {
        //TODO These offsets assume Region 2 bandplans.

        long aro=600000;

        if(freq>100000000)
            aro=600000;
        if(freq>200000000)
            aro=1600000;
        if(freq>400000000)
            aro=5000000;

        return aro;
    }


    long splitfreq=0;
    //Next split dir.
    public void nextSplitDir(){
        //+, - and Simplex are easy enough, but for a split, we need to store the TX Freq separately.

        switch(ch.getSplitDir()){
            case "+":
                ch.setOffset("-", Math.abs(ch.getTXFrequency()-ch.getRXFrequency()));
                break;
            case "-":
                if(splitfreq==0)
                    ch.setOffset("split", ch.getTXFrequency());
                else
                    ch.setOffset("split", splitfreq);
                break;
            case "split":
                if(splitfreq==0)
                    splitfreq=ch.getTXFrequency();
                ch.setOffset("simplex", ch.getRXFrequency());
                break;
            case "simplex":
            case "":
                if(splitfreq!=0) {
                    ch.setOffset("+", Math.abs(ch.getRXFrequency() - splitfreq));
                }else{
                    ch.setOffset("+", getARO(ch.getRXFrequency()));
                }
                break;
            default:
                Log.e("EDIT", "Unknown split dir: "+ch.getToneMode());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        applyChannel();
        switch(v.getId()){
            case R.id.buttonTone:
                nextTone();
                break;
            case R.id.buttonMode:
                nextMode();
                break;
            case R.id.buttonShift:
                nextSplitDir();
                break;
            case R.id.buttonSave:
                try {
                    RadioState.csvradio.writeChannel(ch.getIndex(), ch);
                    RadioState.drawback(100);
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;
            case R.id.buttonCancel:

                break;
        }
        showChannel();
    }

    String getToneString(){
        return String.format("%03.3f", ch.getToneFreq()/10.0);
    }

    public EditFragment(int index){
        super();
        //Log.v("EDIT", "Attempting to show editing of "+index);

        try {
            ch = RadioState.csvradio.readChannel(index);
        }catch(IOException e){
            Log.e("EDIT", "Failed to read channel "+index);
            e.printStackTrace();
        }

        // If the channel is null, we have to init a new one.
        if(ch==null){
            Log.e("EDIT", "Channel is null, initializing.");
            ch=new CSVChannel("0,,146.520000,,0.600000,,88.5,88.5,023,NN,FM,5.00,,,,,,");
            ch.setIndex(index);
        }
    }

    /** The system calls this to get the DialogFragment's layout, regardless
        of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View root = inflater.inflate(R.layout.fragment_edit, container, false);


        Button cancel=root.findViewById(R.id.buttonCancel);
        Button save=root.findViewById(R.id.buttonSave);

        //These are the widgets that show the tones.
        name = root.findViewById(R.id.editTextChannelName);
        rxfreq = root.findViewById(R.id.editTextFrequency);
        txfreq = root.findViewById(R.id.editTextShift);
        shiftbutton = root.findViewById(R.id.buttonShift);
        tone = root.findViewById(R.id.editTextTone);
        tonebutton = root.findViewById(R.id.buttonTone);
        modebutton = root.findViewById(R.id.buttonMode);

        //Apply the buttons.
        shiftbutton.setOnClickListener(this);
        tonebutton.setOnClickListener(this);
        modebutton.setOnClickListener(this);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);

        //Do the rendering.
        showChannel();

        return root;
    }




    /*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }
     */
}
