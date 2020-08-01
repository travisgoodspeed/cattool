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

public class EditFragment  extends DialogFragment {
    Channel ch;

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

        //These are the widgets that show the tones.
        final TextView name = root.findViewById(R.id.editTextChannelName);
        final TextView rxfreq = root.findViewById(R.id.editTextFrequency);
        final TextView txfreq = root.findViewById(R.id.editTextShift);
        final Button shiftbutton = root.findViewById(R.id.buttonShift);
        final TextView tone = root.findViewById(R.id.editTextTone);
        final Button tonebutton = root.findViewById(R.id.buttonTone);

        //This configures them to show the right values.
        name.setText(ch.getName());
        rxfreq.setText(String.format("%f", ch.getRXFrequency()/1000000.0));
        tonebutton.setText(ch.getToneMode());
        switch(ch.getToneMode()){
            case "tone":
                tone.setEnabled(true);
                tone.setText(getToneString());
                break;
            case "ct":
                tone.setEnabled(true);
                tone.setText(getToneString());
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
                break;
            default:
                Log.e("EDIT", "Unknown split dir: "+ch.getToneMode());
                break;
        }

        return root;
    }


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
}
