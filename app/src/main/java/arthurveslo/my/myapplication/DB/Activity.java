package arthurveslo.my.myapplication.DB;

/**
 * Created by User on 13.04.2016.
 */
public class Activity {
    String _activity;
    String _user_id;
    double _calories;
    int steps;

    public Activity(String _activity, String _user_id, double _calories, int steps) {
        this._activity = _activity;
        this._user_id = _user_id;
        this._calories = _calories;
        this.steps = steps;
    }

    public String get_activity() {
        return _activity;
    }

    public void set_activity(String _activity) {
        this._activity = _activity;
    }

    public String get_user_id() {
        return _user_id;
    }

    public void set_user_id(String _user_id) {
        this._user_id = _user_id;
    }

    public double get_calories() {
        return _calories;
    }

    public void set_calories(double _calories) {
        this._calories = _calories;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
