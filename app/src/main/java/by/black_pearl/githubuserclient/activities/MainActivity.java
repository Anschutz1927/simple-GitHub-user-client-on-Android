package by.black_pearl.githubuserclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import by.black_pearl.githubuserclient.R;
import by.black_pearl.githubuserclient.fragments.UsersFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_container, UsersFragment.newInstance()).commit();
    }
}
