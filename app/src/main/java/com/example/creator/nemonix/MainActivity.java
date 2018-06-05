package com.example.creator.nemonix;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button make;
    private Button anagram;
    private Button about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Learn with Monix");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        make = findViewById(R.id.make);
        anagram = findViewById(R.id.anagram);
        about = findViewById(R.id.about);

        make.setOnClickListener(this);
        anagram.setOnClickListener(this);
        about.setOnClickListener(this);

    }

    public void onClick(View v){
        Intent intent;
        if(v.getId() == R.id.make){
            intent = new Intent(this, MakeActivity.class);
        } else if(v.getId() == R.id.anagram){
            intent = new Intent(this, AnagramActivity.class);
        } else {
            intent = new Intent(this, AboutActivity.class);
        }
        startActivity(intent);

    }

}
