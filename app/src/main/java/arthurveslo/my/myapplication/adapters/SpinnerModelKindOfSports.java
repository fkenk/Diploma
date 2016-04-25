package arthurveslo.my.myapplication.adapters;

/**
 * Created by User on 16.04.2016.
 */
public class SpinnerModelKindOfSports {

    private  String activityName="";
    private  String image="";

    public SpinnerModelKindOfSports(String sportName, String image) {
        activityName = sportName;
        this.image = image;
    }

    /*********** Set Methods ******************/
    public void setActivityName(String CompanyName)
    {
        this.activityName = CompanyName;
    }

    public void setImage(String Image)
    {
        this.image = Image;
    }


    /*********** Get Methods ****************/
    public String getActivityName()
    {
        return this.activityName;
    }

    public String getImage()
    {
        return this.image;
    }


}