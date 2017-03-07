package com.moticon.UI;

import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.moticon.network.datagrams.LL2PFrame;
import com.moticon.router_6.R;
import com.moticon.support.BootLoader;
import com.moticon.support.FrameLogger;
import com.moticon.support.ParentActivity;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by pat.smith on 11/14/2016.
 */

public class SnifferUI implements Observer {
    private Activity parentActivity;
    private Context context;
    private FrameLogger frameLogger;
    private SnifferFrameListAdapter frameListAdapter;

    private ListView frameList;
    private TextView protocolBreakoutTextView;
    private TextView frameBytesTextView;


    public SnifferUI (){
    }

    private void connectWidgets(){
        // This is the top window, the summary frame list
        frameList = (ListView) parentActivity.findViewById(R.id.frameList);
        frameListAdapter = new SnifferFrameListAdapter(context, frameLogger.getFrameList());
        frameList.setAdapter(frameListAdapter);
        frameList.setOnItemClickListener(showThisFrame);
        // this is the text view for the protocol breakout window
        protocolBreakoutTextView = (TextView) parentActivity.findViewById(R.id.protocolLayersView);
        protocolBreakoutTextView.setMovementMethod(new ScrollingMovementMethod());
        // this is the text view for the hex bytes, indexed.
        frameBytesTextView = (TextView) parentActivity.findViewById(R.id.packetBytesView);
    }

    private AdapterView.OnItemClickListener showThisFrame=new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //UIManager.getInstance().displayMessage("Touched Item "+Integer.toString(i));
            displayFrameBreakout(frameListAdapter.getItem(i));
            displayFrameHexBytes(frameListAdapter.getItem(i));
        }
    };

    /**
     * displayFrameBreakout - this displays the layers in the frame
     * @param frame - the LL2P frame object
     *
     * this primarily works by having the frame provide it's "Explain yourself" methods. Each of
     *              these cause the element to provide it's own explanation.
     */
    private void displayFrameBreakout(LL2PFrame frame){
        protocolBreakoutTextView.setText(frame.toProtocolExplanationString());
    }

    /**
     * displayFrameHexBytes
     * @param frame - this is the frame to display the bytes
     */
    private void displayFrameHexBytes(LL2PFrame frame){
        frameBytesTextView.setText(frame.getHexDisplayString());
    }

    /*
     * This class observes the Logger. When a new frame arrives the logger will
     * notify this class. This class then needs to go through the process of
     * updating the list on the screen.
     */
    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == BootLoader.class) {
            parentActivity = ParentActivity.getInstance().getActivity();
            context = parentActivity.getBaseContext();
            frameLogger = FrameLogger.getInstance();

            // now that I know all the other objects.. I can connect to the screen widgets.
            connectWidgets();
        } else if (observable.getClass() == FrameLogger.class){
                frameListAdapter.notifyDataSetChanged();
        }

    }

    private static class ViewHolder {
        TextView packetNumber;
        TextView packetSummaryString;
    }


    /**
     * SnifferFrameListAdapter is a private adapter to display numbered rows from a List
     * object which contains all frames transmitted or received.
     *
     * It is instantiated above and note that the constructor passes the context as well as
     * the frameList.
     */
    private class SnifferFrameListAdapter extends ArrayAdapter<LL2PFrame> {
        // this is the ArrayList that will be displayed in the rows on the ListView.
        private ArrayList<LL2PFrame> frameList;

        /*
         *  The constructor is passed the context and the arrayList.
         *  the arrayList is assigned to the local variable so its contents can be
         *  adapted to the listView.
         */
        public SnifferFrameListAdapter(Context context, ArrayList<LL2PFrame> frames){
            super(context, 0, frames);
            frameList = frames;
        }

        /**
         * Here is where the work is performed to adapt a specific row in the arrayList to
         * a row on the screen.
         * @param position  - position in the array we're working with
         * @param convertView - a row View that passed in - will have a View object if Android is recycling objects. Otherwise null
         * @param parent - the main view that contains the rows.  Note that is is the ListView object.
         * @return
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            // First retrieve a frame object from the arrayList at the position we're working on
            LL2PFrame ll2PFrame = getItem(position);
            // declare a viewHolder - this simply is a single object to hold a two widgets
            ViewHolder viewHolder;

            /**
             * If convertView is null it means we didn't get a recycled View and have to create from scratch.
             * We do that here.
             */
            if (convertView == null){
                // inflate the view defined in the layout xml file using an inflater we create here.
                LayoutInflater inflator = LayoutInflater.from(context);
                convertView = inflator.inflate(R.layout.sniffer_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.packetNumber = (TextView) convertView.findViewById(R.id.snifferFrameNumberTextView);
                viewHolder.packetSummaryString = (TextView) convertView.findViewById(R.id.snifferItemTextView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.packetNumber.setText(Integer.toString(position));
            viewHolder.packetSummaryString.setText(frameList.get(position).toSummaryString());
            return convertView;
        }

    }
}



























