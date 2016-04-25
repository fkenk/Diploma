package arthurveslo.my.myapplication.DB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 25.04.2016.
 */
public class Foo {
    private String title;
    private List<ActivityDB> children;
    private static  HashMap<String, Double> selector = new HashMap<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setChildren(List<ActivityDB> children) {
        this.children = children;
    }

    public List<ActivityDB> getChildren() {
        return children;
    }

    public static HashMap<String, Double> getSelector() {
        return selector;
    }

    public static void setSelector(HashMap<String, Double> selector) {
        Foo.selector = selector;
    }
}
