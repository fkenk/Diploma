package arthurveslo.my.myapplication.DB;

/**
 * Created by User on 13.04.2016.
 */
public class User {
    public static String current_id;
    String _id = null;
    String _name = null;
    double _weight = 0;
    double _height = 0;
    int _age = 0;
    int _sex = 3;
    double _BMR = 0;

    public User(){
    }

    public User(String _id, String _name, double _weight, double _height, int _age, int _sex, double _BMR) {
        this._id = _id;
        this._name = _name;
        this._weight = _weight;
        this._height = _height;
        this._age = _age;
        this._sex = _sex;
        this._BMR = _BMR;
    }

    public User(String _name, String _id) {
        this._name = _name;
        this._id = _id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public double get_weight() {
        return _weight;
    }

    public void set_weight(double _weight) {
        this._weight = _weight;
    }

    public double get_height() {
        return _height;
    }

    public void set_height(double _height) {
        this._height = _height;
    }

    public int get_age() {
        return _age;
    }

    public void set_age(int _age) {
        this._age = _age;
    }

    public int get_sex() {
        return _sex;
    }

    public void set_sex(int _sex) {
        this._sex = _sex;
    }

    public double get_BMR() {
        return _BMR;
    }

    public void set_BMR(double _BMR) {
        this._BMR = _BMR;
    }
}