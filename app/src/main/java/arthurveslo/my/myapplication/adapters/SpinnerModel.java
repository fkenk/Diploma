package arthurveslo.my.myapplication.adapters;

/**
 * Created by User on 16.04.2016.
 */
public class SpinnerModel {

    private  String activityName="";
    private  String image="";

    public SpinnerModel(String companyName, String image) {
        activityName = companyName;
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