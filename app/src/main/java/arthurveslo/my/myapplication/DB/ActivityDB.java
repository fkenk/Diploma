package arthurveslo.my.myapplication.DB;

/**
 * Created by User on 13.04.2016.
 */
public class ActivityDB {
    int _num;
    String _activity;
    String _user_id;
    double _calories;
    int _steps;
    String _date;
    String _time;
    double _avr_speed;
    double _distance;


    public ActivityDB(double _distance, String _time, double _avr_speed, String _date, int _steps, double _calories, String _user_id, String _activity) {
        this._distance = _distance;
        this._time = _time;
        this._avr_speed = _avr_speed;
        this._date = _date;
        this._steps = _steps;
        this._calories = _calories;
        this._user_id = _user_id;
        this._activity = _activity;
    }

    public ActivityDB() {
    }

    public int get_num() {
        return _num;
    }

    public void set_num(int _num) {
        this._num = _num;
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

    public int get_steps() {
        return _steps;
    }

    public void set_steps(int _steps) {
        this._steps = _steps;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }

    public double get_avr_speed() {
        return _avr_speed;
    }

    public void set_avr_speed(double _avr_speed) {
        this._avr_speed = _avr_speed;
    }

    public double get_distance() {
        return _distance;
    }

    public void set_distance(double _distance) {
        this._distance = _distance;
    }
}
