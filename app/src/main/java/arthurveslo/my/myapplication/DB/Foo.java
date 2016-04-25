package arthurveslo.my.myapplication.DB;

import java.util.List;

/**
 * Created by User on 25.04.2016.
 */
public class Foo {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;
    private List<ActivityDB> children;

    public void setChildren(List<ActivityDB> children) {
        this.children = children;
    }

    public List<ActivityDB> getChildren() {
        return children;
    }
}
