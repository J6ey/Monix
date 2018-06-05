package com.example.creator.nemonix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tooltip.Tooltip;

public class AnagramActivity extends AppCompatActivity{

    private final int RESULTS = 2;
    private EditText textfield;
    private TextView question, instructions;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anagram);
        setTitle("Give an Acronym");
        Intent intent = getIntent();
        int from = intent.getIntExtra("from", 0);
        if(from == RESULTS){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        String text = "<font color='#dfdfdf'>What would you like to </font><font color='#1cc79c'>recall</font>" +
                "<font color='#dfdfdf'>?</font>";
        String text2 = "<font color='#1cc79c'>Transform</font><font color='#dfdfdf'>" +
                " your acronym into anagrams<br><br>such as...</font>";
        question = findViewById(R.id.learnText);
        question.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        instructions = findViewById(R.id.learnInstruction);
        instructions.setText(Html.fromHtml(text2), TextView.BufferType.SPANNABLE);

        textfield = (EditText) findViewById(R.id.anagramSearch);
        textfield.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(textView.getText().length() < 2){
                        Tooltip charError = new Tooltip.Builder(textView).setText("Must have at least 2")
                                .setTextColor(0xdfdfdfdf).setGravity(Gravity.BOTTOM).setCornerRadius(8f).setDismissOnClick(true).setCancelable(true)
                                .setBackgroundColor(Color.parseColor("#7F00FF")).show();
                    } else if(areAllLetters(textView.getText().toString())){
                        enterResults((textView.getText().toString().toLowerCase().replaceAll("\\s+", "")));
                    } else {
                        Tooltip charError = new Tooltip.Builder(textView).setText("Must contain only letters")
                                .setTextColor(0xdfdfdfdf).setGravity(Gravity.BOTTOM).setCornerRadius(8f).setDismissOnClick(true).setCancelable(true)
                                .setBackgroundColor(Color.parseColor("#7F00FF")).show();
                    }
                }
                return true;
            }
        });
    }

    public boolean areAllLetters(String acronym){
        return acronym.matches("[sa-zA-Z ]+");
    }

    public void enterResults(String acronym){
        Intent intent = new Intent(this, MakeResults.class);
        intent.putExtra("abbrev", acronym);
        intent.putExtra("anagram", true);
        intent.putExtra("random", false);
        startActivity(intent);
    }
}
