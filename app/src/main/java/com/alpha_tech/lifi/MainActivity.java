package com.alpha_tech.lifi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.id_about) {
            //Intent to another activity
            Intent intentAbout=new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intentAbout);
            return true;
        }
        return true;
    }

    public void transmitActivityPage(View view) {
        Intent nextPage = new Intent(MainActivity.this, SendCommandActivity.class);
        startActivity(nextPage);
    }

    public void receiveActivityPage(View view) {

        Intent nextPage = new Intent(MainActivity.this, ReceiveCommandActivity.class);
        startActivity(nextPage);
    }

    public void sendTextPage(View view)
    {
        Intent nextPage = new Intent(MainActivity.this, SendTextActivity.class);
        startActivity(nextPage);
    }
    public void receiveTextPage(View view)
    {
        Intent nextPage = new Intent(MainActivity.this, ReceiveTextActivity.class);
        startActivity(nextPage);
    }
}
