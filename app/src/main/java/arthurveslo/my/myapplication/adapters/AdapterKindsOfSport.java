package arthurveslo.my.myapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import arthurveslo.my.myapplication.AddActivity;
import arthurveslo.my.myapplication.R;

/**
 * Created by User on 16.04.2016.
 */
public class AdapterKindsOfSport extends ArrayAdapter<String> {

    private Activity activity;
    private ArrayList data;
    public Resources res;
    SpinnerModelKindOfSports tempValues = null;
    LayoutInflater inflater;

    public AdapterKindsOfSport(
            AddActivity activitySpinner,
            int textViewResourceId,
            ArrayList objects,
            Resources resLocal
    ) {
        super(activitySpinner, textViewResourceId, objects);

        /********** Take passed values **********/
        activity = activitySpinner;
        data = objects;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () **********************/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_kind_of_sports_of_sports.xml file for each row  ************/
        View row = inflater.inflate(R.layout.spinner_kind_of_sports, parent, false);

        /***** Get each Model object from Arraylist ********/
        tempValues = null;
        tempValues = (SpinnerModelKindOfSports) data.get(position);

        TextView label = (TextView) row.findViewById(R.id.textSport);
        ImageView img = (ImageView) row.findViewById(R.id.image);

        label.setText(tempValues.getActivityName());
        img.setImageResource(res.getIdentifier
                ("arthurveslo.my.myapplication:drawable/"
                        + tempValues.getImage(), null, null));


        return row;
    }
}
