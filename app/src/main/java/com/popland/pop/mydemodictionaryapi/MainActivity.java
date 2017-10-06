package com.popland.pop.mydemodictionaryapi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextToSpeech textToSpeech;
    EditText edtType;
    Button btnSearch;
    ImageButton imageButton;
    TextView tvDefinition;
    String word = "";
    String dictionaryAPI_key = "f61eeb35-a753-4abc-92b9-b2af77adee48";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtType = (EditText) findViewById(R.id.EDTtype);
        btnSearch = (Button) findViewById(R.id.BTNsearch);
        imageButton = (ImageButton)findViewById(R.id.imageButton);
        tvDefinition = (TextView) findViewById(R.id.TVdefinition);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        word = edtType.getText().toString();
                        new XMLparser().execute("http://www.dictionaryapi.com/api/v1/references/collegiate/xml/" + word + "?key=" + dictionaryAPI_key);
                    }
                });
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            textToSpeech.setLanguage(Locale.ENGLISH);
                            textToSpeech.speak(edtType.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
                        }
                    }
                });

            }
        });

    }

    class XMLparser extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... params) {
            return DocNoiDung_TuURL(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            XMLDOMParser parser = new XMLDOMParser();
            Document document = parser.getDocument(s);
            NodeList nodeList = document.getElementsByTagName("dt");
            String definition = "";
            for(int i=0;i<nodeList.getLength();i++){
                Node node = nodeList.item(i);
                definition += node.getFirstChild().getNodeValue() +"\n";
            }
            tvDefinition.setText(definition);
        }
    }

    public String DocNoiDung_TuURL(String theurl){
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theurl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line ="";
            while((line=bufferedReader.readLine())!=null){
                content.append(line+"\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
