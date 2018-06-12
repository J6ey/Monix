package com.example.creator.nemonix;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MakeResults extends AppCompatActivity implements View.OnClickListener {

    private final String APP_ID = "cb76a849", APP_KEY = "1d4adb8dda540febd79a9e031fcd77d8";
    private TextView generateResults;
    private ProgressBar progressBar;
    private ListView listView;
    private ArrayList<String> permList, quoteList, authorList, speechList, randomList;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String acronym;
    private ImageView sadFace;
    private Button again, main, random;
    private boolean isPermutable, anagram, isRandom;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_make);
        sadFace = findViewById(R.id.sadCloud);
        sadFace.setVisibility(View.GONE);
        again = findViewById(R.id.tryAnother);
        main = findViewById(R.id.mainMenu);
        random = findViewById(R.id.random);
        random.setVisibility(View.GONE);
        again.setVisibility(View.GONE);
        main.setVisibility(View.GONE);
        again.setOnClickListener(this);
        main.setOnClickListener(this);
        random.setOnClickListener(this);
        Intent intent = getIntent();
        acronym = intent.getExtras().getString("abbrev");
        isPermutable = intent.getExtras().getBoolean("permutable");
        boolean isComplete = intent.getExtras().getBoolean("complete");
        anagram = intent.getExtras().getBoolean("anagram");
        isRandom = intent.getExtras().getBoolean("random");
        if(isRandom){
            if(intent.getExtras().getStringArrayList("randomList") != null){
                quoteList = intent.getExtras().getStringArrayList("randomList");
                authorList = intent.getExtras().getStringArrayList("authorList");
            } else {
                quoteList = new ArrayList<>();
                authorList = new ArrayList<>();
            }
        }
        if(anagram){
            setTitle("Your Anagram Results");
        } else {
            setTitle("Your Monix Results");
        }
        String text = "<font color='#dfdfdf'>Creating Monix for </font>" +
                "<font color='#1cc79c'>" + acronym + "</font><color font='#dfdfdf'>...</font>";
        progressBar = findViewById(R.id.loadingBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#1cc79c"), PorterDuff.Mode.MULTIPLY);
        generateResults = findViewById(R.id.generatingResults);
        generateResults.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        if(!isRandom){
            quoteList = new ArrayList<>();
            authorList = new ArrayList<>();
            speechList =  new ArrayList<>();
        }
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isRandom){
                    new randomParse().execute();
                } else if(isPermutable && !anagram) {
                    permList = new ArrayList<>();
                    char[] acro = acronym.toCharArray();
                    Arrays.sort(acro);
                    do {
                        permList.add(new String(acro));
                    } while (permuteLexically(acro));
                    listView = findViewById(R.id.resultsList);
                    for (int i = 0; i < permList.size(); i++) {
                        String find = permList.get(i);
                        if (dataSnapshot.hasChild(find)) {
                            for (DataSnapshot iter : dataSnapshot.child(find).getChildren()) {
                                quoteList.add(iter.getValue().toString());
                                if (iter.getKey().charAt(0) == '-' && iter.getKey().length() > 15) {
                                    authorList.add("Anonymous");
                                } else {
                                    authorList.add(iter.getKey());
                                }
                            }
                        }
                    }
                    getResults();
                } else if (anagram) {
                    new parseTask().execute();
                } else {
                    if(dataSnapshot.hasChild(acronym)){
                        for (DataSnapshot iter : dataSnapshot.child(acronym).getChildren()) {
                            quoteList.add(iter.getValue().toString());
                            if(iter.getKey().charAt(0) == '-' && iter.getKey().length() > 15) {
                                authorList.add("Anonymous");
                            } else {
                                authorList.add(iter.getKey());
                            }
                        }
                    }
                    getResults();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getDetails());
            }
        });
    }

    public void getResults(){
        if(!quoteList.isEmpty()) { // update list view if results exist
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    String quantity;
                    if(quoteList.size() > 1){
                        quantity = "<font color='#dfdfdf'> results for </font>";
                    } else {
                        quantity = "<font color='#dfdfdf'> result for </font>";
                    }
                    String text = "<font color='#dfdfdf'>" + quoteList.size() + "</font>" + quantity +
                            "<font color='#1cc79c'>" + acronym;
                    listView = findViewById(R.id.resultsList);
                    generateResults.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                    PhraseAdapter phraseAdapter = new PhraseAdapter();
                    listView.setAdapter(phraseAdapter);
                }
            });
        } else { // no results found
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    String text = "<font color='#dfdfdf'>No results found for </font>" +
                            "<font color='#1cc79c'>" + acronym + "</font>";
                    generateResults.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                    sadFace.setVisibility(View.VISIBLE);
                }
            });
        }
        if(!anagram){
            random.setVisibility(View.VISIBLE);
        }
        main.setVisibility(View.VISIBLE);
        again.setVisibility(View.VISIBLE);
    }

    protected class randomParse extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... args) {
            try {
                String url = "http://www.mnemonicgenerator.com/?words=";
                for (int i = 0; i < acronym.length(); i++) {
                    url += acronym.charAt(i) + "%20";
                }
                Document doc = Jsoup.connect(url).timeout(6000).get();
                Elements sentence = doc.select("#result");
                String results = sentence.text();
                quoteList.add(results);
                int count = authorList.size()+1;
                authorList.add("random monix #" + count);
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getResults();
        }
    }

    protected class parseTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String url = "https://www.wordplays.com/anagram-solver/" + acronym;
                Document doc = Jsoup.connect(url).timeout(6000).get();
                Elements group = doc.select("div#wordwrap");
                Element row = group.get(0);
                String results = row.select("div.word").text();
                String[] splitResults = results.split("\\s+");
                for (int i = 0; i < splitResults.length; i++) {
                    if (splitResults[i].length() == acronym.length()) {
                        authorList.add(splitResults[i]);
                    } else {
                        break;
                    }
                }
                for (int i = 0; i < authorList.size(); i++) {
                    try {
                        String durl = "http://www.dictionary.com/browse/" + authorList.get(i);
                        doc = Jsoup.connect(durl).timeout(6000).get();
                        String exists = doc.select("div:contains(No results found for)").text();
                        if (exists.equals("")) {
                            StringBuilder editor;
                            group = doc.select("section.css-1sdcacc.e10vl5dg0 > ol > li > span:last-child.css-4x41l7.e10vl5dg6");
                            row = group.get(0);
                            String def = row.ownText();
                            editor = new StringBuilder(def);
                            if(def.endsWith(":")){
                                editor.deleteCharAt(def.length()-1);
                            }
                            quoteList.add(editor.toString());
                            String speech = doc.select("span.luna-pos").first().text();
                            editor = new StringBuilder(speech);
                            if(!Character.isLetter(speech.charAt(speech.length()-1))){
                                editor.deleteCharAt(speech.length()-1);
                            }
                            speechList.add(editor.toString());
                        } else {
                            URL oxfordLink = new URL("https://od-api.oxforddictionaries.com:443/api/v1/entries/en/" + authorList.get(i));
                            HttpsURLConnection urlConnection = (HttpsURLConnection) oxfordLink.openConnection();
                            urlConnection.setRequestProperty("Accept", "application/json");
                            urlConnection.setRequestProperty("app_id", APP_ID);
                            urlConnection.setRequestProperty("app_key", APP_KEY);
                            if(urlConnection.getResponseCode() != 404) {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                StringBuilder builder = new StringBuilder();
                                String line = null;
                                while ((line = reader.readLine()) != null) {
                                    builder.append(line + "\n");
                                }
                                JSONObject jObject = new JSONObject(builder.toString());
                                JSONArray resultsArray = jObject.getJSONArray("results");
                                JSONObject resultsObj = resultsArray.getJSONObject(0);
                                JSONArray leXArray = resultsObj.getJSONArray("lexicalEntries");
                                JSONObject lexObj = leXArray.getJSONObject(0);
                                String category = lexObj.getString("lexicalCategory");
                                JSONArray entriesArray = lexObj.getJSONArray("entries");
                                JSONObject entriesObj = entriesArray.getJSONObject(0);
                                JSONArray sensesArray = entriesObj.getJSONArray("senses");
                                JSONObject sensesObj = sensesArray.getJSONObject(0);
                                JSONArray defArray = sensesObj.getJSONArray("definitions");
                                String def = defArray.getString(0);
                                quoteList.add(def);
                                speechList.add(category.toLowerCase());
                            } else {
                                authorList.remove(i);
                                i -= 1;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if(e.getStackTrace()[0].getLineNumber() == 254){
                            speechList.add("");
                        } else if (e.getStackTrace()[0].getLineNumber() == 247){
                            authorList.remove(i);
                            i -= 1;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getResults();
        }
    }

    public void onClick(View v){
        Intent intent;
        if(v.getId() == R.id.mainMenu){
            intent = new Intent(this, MainActivity.class);
        } else if(v.getId() == R.id.tryAnother){
            if(!anagram) {
                intent = new Intent(this, MakeActivity.class);
            } else {
                intent = new Intent(this, AnagramActivity.class);
            }
            intent.putExtra("from", 2); // coming from MakeResults activates keyboard
        } else {
            intent = new Intent(this, MakeResults.class);
            intent.putExtra("random", true);
            intent.putExtra("randomList", quoteList);
            intent.putExtra("authorList", authorList);
            intent.putExtra("abbrev", acronym);
        }
        startActivity(intent);
    }

    public static boolean permuteLexically(char[] data) {
        int k = data.length - 2;
        while (data[k] >= data[k + 1]) {
            k--;
            if (k < 0) {
                return false;
            }
        }
        int l = data.length - 1;
        while (data[k] >= data[l]) {
            l--;
        }
        swap(data, k, l);
        int length = data.length - (k + 1);
        for (int i = 0; i < length / 2; i++) {
            swap(data, k + 1 + i, data.length - i - 1);
        }
        return true;
    }

    public static void swap(char[] a, int i, int j) {
        char temp;
        temp = a[i] ;
        a[i] = a[j];
        a[j] = temp;
    }

    private class PhraseAdapter extends BaseAdapter {
        public int getCount(){
            return quoteList.size();
        }

        public Object getItem(int i){
            return null;
        }

        public long getItemId(int i){
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup){
            view = getLayoutInflater().inflate(R.layout.phrase_layout, null);
            TextView viewQuote = (TextView) view.findViewById(R.id.view_quote);
            TextView viewAuthor = (TextView) view.findViewById(R.id.view_author);
            TextView viewSpeech = (TextView) view.findViewById(R.id.view_speech);

            if(isRandom || !anagram){
                String format = "";
                for (int j = 0; j < quoteList.get(i).length(); j++) {
                    if(j == 0 || quoteList.get(i).charAt(j-1) == ' '){
                        format += "<u>" + quoteList.get(i).charAt(j) + "</u>";
                    } else {
                        format += quoteList.get(i).charAt(j);
                    }
                }
                viewQuote.setText(Html.fromHtml(format), TextView.BufferType.SPANNABLE);
            } else {
                viewQuote.setText(quoteList.get(i));
            }
            viewAuthor.setText(authorList.get(i));
            if(anagram){
                viewSpeech.setText(speechList.get(i));
            }
            return view;
        }
    }




}
