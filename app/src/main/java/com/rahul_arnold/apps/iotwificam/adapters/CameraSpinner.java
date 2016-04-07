package com.rahul_arnold.apps.iotwificam.adapters;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.rahul_arnold.apps.iotwificam.R;


/**
 * Created by Rahul on 3/15/2016.
 */
public class CameraSpinner extends ArrayAdapter<String> {

    private String[] listOfItems;
    private String[] iconList;
    private  LayoutInflater inflater;

    public CameraSpinner(Context context, int displayId, String[] listOfItems, String[] iconList, LayoutInflater inflater){
        super(context, displayId, listOfItems);
        this.listOfItems = listOfItems;
        this.iconList =  iconList;
        this.inflater = inflater;
    }

    @Override
    public View getDropDownView(int position, View  choiceView, ViewGroup group){
        return getViewForOption(position,choiceView,  group);
    }

    @Override
    public View getView(int position, View  viewToDisplay, ViewGroup parentView){
        return getViewForOption(position, viewToDisplay, parentView);
    }

    public View  getViewForOption(int position, View  viewToShow, ViewGroup parent){
        View spinnerView = null;
        if(inflater!=null){
            spinnerView = inflater.inflate(R.layout.layout_spinner, parent, false);
            TextView  choiceToDisplay = (TextView)spinnerView.findViewById(R.id.cameraType);
            choiceToDisplay.setText(listOfItems[position]);
            TextView icon = (TextView)spinnerView.findViewById(R.id.cameraIcon);
            icon.setText(iconList[position]);
        }
        return spinnerView;
    }
}
