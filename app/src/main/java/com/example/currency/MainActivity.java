package com.example.currency;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;

    private XmlPullParserFactory factory;
    private XmlPullParser parser;
    private String url;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> currencies;
    private HashMap<String, Double> rates;

    Button refresh;
    Button button;
    EditText CurrencyToConvert;
    EditText currencyConverted;
    Spinner convertToDropdown;
    Spinner convertFromDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        setupLayout();
    }

    private void setupLayout() {
        url = "http://www.nbg.ge/rss.php";
        updateData();
        currencyConverted = (EditText) findViewById(R.id.currency_converted);
        CurrencyToConvert = (EditText) findViewById(R.id.currency_to_be_converted);
        convertToDropdown = (Spinner) findViewById(R.id.convert_to);
        convertFromDropdown = (Spinner) findViewById(R.id.convert_from);
        button = (Button) findViewById(R.id.button);
        refresh = (Button) findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currency = Double.valueOf(CurrencyToConvert.getText().toString());
                double multiplier = Double.valueOf(rates.get(convertToDropdown.getSelectedItem().toString()).toString());
                double result = currency * multiplier;
                currencyConverted.setText(String.valueOf(result));
            }
        });
    }

    private void updateData() {
        final StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    parser.setInput(new StringReader(response));
                    int event = parser.getEventType();

                    String tag = "", rate = "";
                    Double valDouble;

                    while (event != XmlPullParser.END_DOCUMENT) {
                        currencies.add("a");
                        tag = parser.getName();
                        switch (event) {
                            case XmlPullParser.START_TAG:
                                if (tag.equals("td")) {
                                    event = parser.next();
                                    rate = parser.getText();
                                    currencies.add(rate);
                                    for (int i = 0; i < 6; i++) event = parser.next();
                                    valDouble = Double.valueOf(parser.getText());
                                    rates.put(rate, valDouble);
                                }
                                break;
                        }
                        for (int i = 0; i < 8; i++) event = parser.next();
                    }

                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, currencies);
        requestQueue.add(stringRequest);
        convertToDropdown.setAdapter(adapter);
        convertFromDropdown.setAdapter(adapter);
    }

}