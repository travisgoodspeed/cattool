package com.kk4vcz.goodspeedscattool.ui.codeplug;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kk4vcz.codeplug.Channel;
import com.kk4vcz.goodspeedscattool.R;
import com.kk4vcz.goodspeedscattool.RadioState;

import java.io.IOException;

public class CodeplugViewAdapter extends RecyclerView.Adapter<CodeplugViewAdapter.ViewHolder> implements View.OnCreateContextMenuListener {
    public CodeplugViewAdapter(){
        RadioState.codeplugViewAdapter=this;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.codeplug_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        //Old handlers.
        view.setOnClickListener(holder);
        view.setOnLongClickListener(holder);  //Rather show the context menu.

        view.setOnCreateContextMenuListener(this);
        return holder;
    }

    /***
     * "Binding" the data to the view holder
     *
     * This function is what informs a holder that it's data has changed (ie, every)
     * time the view is recycled
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return 1000; //FIXME Don't hardcode this.
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Memory "+RadioState.index+":");

        Log.e("EDIT", "Memory "+RadioState.index);

        //groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Edit");
        //menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Cut");
        menu.add(0, v.getId(), 0, "Copy");
        menu.add(0, v.getId(), 0, "Paste");
        menu.add(0, v.getId(), 0, "Tune");
        menu.add(0, v.getId(), 0, "M->V");
    }

    /***
     * The ViewHolder is our "Presenter"- it links the View and the data to display
     * and handles how to draw the visual presentation of the data on the View
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        int index;

        /***
         * A label from the view to display some info about the datum
         */
        TextView modelNameLabel;
        /***
         * Another label from the view
         */
        TextView modelFrequencyLabel;

        /***
         * ViewHolder constructor takes a view that will be used to display a single datum
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        /***
         * This is a function that takes the piece of data currently stored in someModel
         * and displays it using this ViewHolder's view.
         *
         * This will be called by the onBindViewHolder method of the adapter every time
         * a view is recycled
         */
        public void binData(){
            bindData(index);
        }
        public void bindData(int index) {
            //Log the index number in case we need it again.
            this.index=index;

            if (modelNameLabel == null) {
                modelNameLabel = (TextView) itemView.findViewById(R.id.modelNameLabel);
            }
            if (modelFrequencyLabel == null) {
                modelFrequencyLabel = (TextView) itemView.findViewById(R.id.modelFrequencyLabel);
            }



            if(RadioState.csvradio!=null){
                try {
                    Channel c = RadioState.csvradio.readChannel(index);
                    if (c == null) {
                        modelNameLabel.setText(String.format("%03d -- %s", index, ""));
                        modelFrequencyLabel.setText("");
                    }else {

                        //Split dir as one letter.
                        String splitdir=c.getSplitDir();
                        if(splitdir.equals("split"))
                            splitdir="s";
                        if(splitdir.equals("off"))
                            splitdir=" ";

                        //Tone mode and Tone.
                        String tonemode=c.getToneMode();
                        if(tonemode.equals("tone"))
                            tonemode="t";
                        String tone=String.format("%2s%05.1f", tonemode, c.getToneFreq()/10.0);
                        if(tonemode.equals("dcs"))
                            tone=String.format("dcs %03d",c.getDTCSCode());
                        if(tonemode.equals(""))
                            tone="";


                        modelNameLabel.setText(String.format("%03d -- %s", index, c.getName()));
                        modelFrequencyLabel.setText(String.format("       %f%1s %8s", c.getRXFrequency() / 1000000.0, splitdir, tone));
                        modelFrequencyLabel.setVisibility(View.VISIBLE);
                    }
                }catch(IOException e){
                    modelNameLabel.setText(String.format("%03d -- %s", index, "IOException"));
                    modelFrequencyLabel.setText("IOException");
                }
            }else {
                modelNameLabel.setText(String.format("%03d -- %s", index, "Missing"));
                modelFrequencyLabel.setText("Missing");
            }
        }

        @Override
        public void onClick(View v) {
            binData();
            RadioState.showEditor(index);
        }

        @Override
        public boolean onLongClick(View v) {
            Log.e("CODEPLUG", "Opening menu for channel "+index);
            RadioState.index=index;

            //We return false so that the menu pops up, but *after* we record the index for handling.
            return false;
        }
    }
}
