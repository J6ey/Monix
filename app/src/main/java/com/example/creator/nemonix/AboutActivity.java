package com.example.creator.nemonix;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.internal.Storage;

public class AboutActivity extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("About Monix");

        String text = "<font color='#dfdfdf'>" +
                "The purpose of Monix is to aid you in </font><font color='#1cc79c'>learning" +
                "</font><font color='#dfdfdf'> and</font><font color='#1cc79c'>" +
                " teaching</font><font color='#dfdfdf'> by " +
                "turning a long list of words or terms into smart " +
                "mnemonics and anagrams. Whether you need to remember conceptual terms " +
                "to study for an exam or maybe to just remember a list of groceries, Monix " +
                "has a way with words.</font>";
        String text2 = "<font color='#dfdfdf'>Thank you for using Monix.<br>If you enjoy " +
                "using it please rate us!<br><br>Contact:<br>theonlypersonhere@gmail.com</font>";

        TextView about = findViewById(R.id.aboutInfo);
        about.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        TextView thanks = findViewById(R.id.thanksView);
        thanks.setText(Html.fromHtml(text2), TextView.BufferType.SPANNABLE);

    }
}
