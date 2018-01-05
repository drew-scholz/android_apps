package com.scholz.andrew.ascholzlab5;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {
    private TextView mDigitalDisplay;
    public Clock.TimeObserver myTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDigitalDisplay = (TextView)findViewById(R.id.time);

        Clock clockDisplay = (Clock)findViewById(R.id.clock);
        clockDisplay.resume();

        myTime = clockDisplay.time;
        myTime.addObserver(this);
    }

    @Override
    public void update(Observable obj, Object data) {
        mDigitalDisplay.setText(myTime.getTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Toast.makeText(this,
                    "Lab 5, Spring 2017, Andrew Scholz",
                    Toast.LENGTH_SHORT)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
