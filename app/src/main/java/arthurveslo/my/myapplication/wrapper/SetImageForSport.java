package arthurveslo.my.myapplication.wrapper;

import android.widget.ImageView;

import arthurveslo.my.myapplication.R;

/**
 * Created by User on 25.04.2016.
 */
public class SetImageForSport {

    public static void set_Image(ImageView imageView, String sport) {
        if(sport.equals("Walk")) {
            imageView.setImageResource(R.drawable.ic_walk_black_36dp);
        }
        if(sport.equals("Bike")) {
            imageView.setImageResource(R.drawable.ic_bike_black_36dp);
        }
        if(sport.equals("Run")) {
            imageView.setImageResource(R.drawable.ic_run_black_36dp);
        }
    }
}
