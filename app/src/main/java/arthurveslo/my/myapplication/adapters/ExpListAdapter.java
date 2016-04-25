package arthurveslo.my.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.images.ImageManager;

import java.util.ArrayList;
import java.util.List;

import arthurveslo.my.myapplication.DB.ActivityDB;
import arthurveslo.my.myapplication.DB.Foo;
import arthurveslo.my.myapplication.R;
import arthurveslo.my.myapplication.wrapper.SetImageForSport;

/**
 * Created by User on 25.04.2016.
 */
public class ExpListAdapter extends BaseExpandableListAdapter {

    private ArrayList<Foo> mGroups;
    private Context mContext;

    public ExpListAdapter (Context context, List<Foo> groups){
        mContext = context;
        mGroups = (ArrayList<Foo>) groups;
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).getChildren().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_view, null);
        }

        if (isExpanded){
            //Изменяем что-нибудь, если текущая Group раскрыта
        }
        else{
            //Изменяем что-нибудь, если текущая Group скрыта
        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
        textGroup.setText(((Foo)getGroup(groupPosition)).getTitle());

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view, null);
        }

        SetImageForSport.set_Image(((ImageView) convertView.findViewById(R.id.imageSport)),
                ((ActivityDB) getChild(groupPosition,childPosition)).get_activity());
        TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
        textChild.setText(((ActivityDB) getChild(groupPosition,childPosition)).get_cur_time());

       /* Button button = (Button)convertView.findViewById(R.id.buttonChild);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"button is pressed",5000).show();
            }
        });*/

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
