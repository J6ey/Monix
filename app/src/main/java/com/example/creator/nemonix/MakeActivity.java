package com.example.creator.nemonix;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.TooltipCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tooltip.Tooltip;

public class MakeActivity extends AppCompatActivity implements View.OnClickListener{

    private final int RESULTS = 2;
    private TextView instructions, question;
    private ImageButton permTip;
    private EditText textfield;
    private CheckBox permutable;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make);
        setTitle("Give an Acronym");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        int from = intent.getIntExtra("from", 0);
        if(from == RESULTS){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        permTip = findViewById(R.id.permhelp);
        String text = "<font color='#dfdfdf'>What would you like to </font><font color='#1cc79c'>recall</font>" +
                "<font color='#dfdfdf'>?</font>";
        String text2 = "<font color='#1cc79c'>Transform</font><font color='#dfdfdf'>" +
                " your acronym into a meaning quote or idiom<br><br>such as...</font>";
        permutable = findViewById(R.id.permutability);
        instructions = findViewById(R.id.learnInstruction);
        question = findViewById(R.id.learnText);
        instructions.setText(Html.fromHtml(text2), TextView.BufferType.SPANNABLE);
        question.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);

        textfield = (EditText) findViewById(R.id.inputSearch);
        textfield.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(textView.getText().length() < 2 ||
                            textView.getText().length() > 8){
                        Tooltip charError = new Tooltip.Builder(textView).setText("Must have at least 2, but no more than 8 letters")
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

        permTip.setOnClickListener(this);
    }

    public boolean areAllLetters(String acronym){
        return acronym.matches("[sa-zA-Z ]+");
    }

    public void enterResults(String acronym){
        Intent intent = new Intent(this, MakeResults.class);
        intent.putExtra("abbrev", acronym);
        intent.putExtra("anagram", false);
        if(permutable.isChecked()){
            intent.putExtra("permutable", true);
        } else {
            intent.putExtra("permutable", false);
        }
        startActivity(intent);
        finish();
    }

    public void onClick(View v){
        if(v.getId() == R.id.permhelp){
            showTooltip(v, Gravity.END, v.getId());
        } else {
            showTooltip(v, Gravity.END, v.getId());
        }
    }

    public void showTooltip(View v, int gravity, int id) {
        ImageButton ib = (ImageButton) v;
        String perm = "Is your\nacronym\narrangeable?";
        Tooltip.Builder tipPerm = new Tooltip.Builder(ib).setText(perm)
                .setTextColor(0xdfdfdfdf).setGravity(gravity).setCornerRadius(8f).setDismissOnClick(true).setCancelable(true)
                .setBackgroundColor(Color.BLACK);

        if(id == R.id.permhelp) {
            if(!tipPerm.build().isShowing()){
                tipPerm.show();
            }
        }
    }
}
