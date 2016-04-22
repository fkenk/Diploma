package arthurveslo.my.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.List;

import arthurveslo.my.myapplication.DB.DatabaseHandler;
import arthurveslo.my.myapplication.DB.User;

public class ParametersActivity extends AppCompatActivity {

    private static final String TAG = "ParametersActivity" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");

        final DatabaseHandler db = new DatabaseHandler(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                user.set_id(id);
                user.set_weight(Double.parseDouble(((EditText)findViewById(R.id.editWeight)).getText().toString()));
                user.set_height(Double.parseDouble(((EditText)findViewById(R.id.editHeight)).getText().toString()));
                if(((RadioButton)findViewById(R.id.radioMale)).isChecked()){
                    user.set_sex(1);
                } else {
                    user.set_sex(0);
                }
                db.updateUserParameters(user);

                System.out.println("Reading all contacts..");
                List<User> users = db.getAllUsers();
                for (User user1 : users) {
                    String log = "Id: " + user1.get_id()
                            + " ,Name: " + user1.get_name()
                            + " ,Sex: " + user1.get_sex()
                            + " ,Weight: " + user1.get_weight()
                            + " ,Height: " + user1.get_height();
                    Log.d(TAG, log);
                }
            }
        });
    }

}
