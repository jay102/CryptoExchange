package com.greenmousetech.cryptoexchange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

public class Home extends AppCompatActivity {
    Spinner Cryptocurrency, currency;
    ArrayList<String> CryptoC;
    ArrayList<String> ReturnedCurrencies;
    ArrayList<String> ReturnedRates;
    String CryptoToUse;
    String selectedCurrencyItem;
    ImageView CryptoCurrencyImage;
    TextView ExchangeRate,TouchToConvert;
    ArrayAdapter<String> adapter1;
    ArrayAdapter<String> adapter;
    SharedPreferences preferences;
    CardView convert;
    AppBarLayout appBarLayout;
    RelativeLayout relativeLayout;
    public static String CHOSEN_CURRENCY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Couple variables with views
        Cryptocurrency = (Spinner) findViewById(R.id.cryptocurrency);
        currency = (Spinner) findViewById(R.id.currency);
        CryptoCurrencyImage = (ImageView) findViewById(R.id.crypto_image);
        ExchangeRate = (TextView) findViewById(R.id.exchange_rate);
        convert = (CardView) findViewById(R.id.crypto);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        TouchToConvert = (TextView) findViewById(R.id.touch_to_convert);

        //initialiaze CryptoCurrency ArrayList
        CryptoC = new ArrayList<>();
        ReturnedCurrencies = new ArrayList<>();
        ReturnedRates = new ArrayList<>();
        //add CrytoCurrency Items
        CryptoC.add("BTC");
        CryptoC.add("ETH");

        //initialize SharedPreferences to hold currency rate to be converted
        preferences = this.getSharedPreferences(CHOSEN_CURRENCY,MODE_PRIVATE);

        //setOnclickListener to Navigate to Conversion Screen
        TouchToConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,CryptoConversion.class);
                startActivity(intent);
            }
        });

        //set Spinner adapters to populate it
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CryptoC);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Attach Spinners to adapter
        Cryptocurrency.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //get Selected Item from Crytpcurrency
        Cryptocurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CryptoToUse = parent.getSelectedItem().toString();
                preferences.edit().putString("CryptoCurrency",CryptoToUse).apply();
                FetchCurrencies();
                if (CryptoToUse.equals("ETH")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        CryptoCurrencyImage.setImageDrawable(getResources().getDrawable(R.drawable.ether, getApplicationContext().getTheme()));
                        appBarLayout.setBackgroundResource(R.color.etherColor);
                        relativeLayout.setBackgroundResource(R.color.etherColor);
                        ExchangeRate.setTextColor(getResources().getColor(R.color.etherColor));
                        GradientDrawable drawable = (GradientDrawable)ExchangeRate.getBackground();
                        drawable.setStroke(10, getResources().getColor(R.color.etherColor));
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        CryptoCurrencyImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_bitcoin, getApplicationContext().getTheme()));
                        appBarLayout.setBackgroundResource(R.color.bitcoinColor);
                        relativeLayout.setBackgroundResource(R.color.bitcoinColor);
                        ExchangeRate.setTextColor(getResources().getColor(R.color.bitcoinColor));
                        GradientDrawable drawable = (GradientDrawable)ExchangeRate.getBackground();
                        drawable.setStroke(10, getResources().getColor(R.color.bitcoinColor));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //set Spinner adapters to populate it
        adapter1 = new ArrayAdapter<String>(this,
                R.layout.spinner_item, ReturnedCurrencies);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        currency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCurrencyItem = parent.getSelectedItem().toString();
                if (selectedCurrencyItem == ReturnedCurrencies.get(position)) {
                    preferences.edit().putString("selectedCurrencyItem",selectedCurrencyItem).apply();
                    preferences.edit().putString("selectedCurrencyValue",ReturnedRates.get(position)).apply();
                    //  Toast.makeText(getApplicationContext(),"Selected Item is "+ReturnedCurrencies.get(position),Toast.LENGTH_LONG).show();
                    ExchangeRate.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
                    ExchangeRate.setText(ReturnedRates.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    void FetchCurrencies() {
        //Use Timestamp to get Latest Exchange Rates
        TimeZone lagosTimeZone = TimeZone.getTimeZone("Africa/Lagos");
        Calendar calendar = Calendar.getInstance(lagosTimeZone);
        Long dateMilli = calendar.getTimeInMillis() / 1000;
        final String Timestamp = dateMilli.toString();
        String FirstUrlPart = "https://min-api.cryptocompare.com/data/price?fsym=" + CryptoToUse + "&tsyms=";
        String Currencies = "USD,EUR,NGN,JPY,GBP,AUD,CAD,CHF,HKD,INR,KRW,SEK,RUB,BRL,DKK,PLN,ZAR,MYR,THB,NZD&" + Timestamp;

        String url = FirstUrlPart + Currencies;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                ReturnedCurrencies.clear();
                ReturnedRates.clear();
                try {
                    j = new JSONObject(response);
                    Map<String, String> map = new HashMap<>();
                    Iterator iter = j.keys();
                    while (iter.hasNext()) {
                        String key = (String) iter.next();
                        String value = j.getString(key);
                        map.put(key, value);
                        ReturnedCurrencies.add(key);
                        ReturnedRates.add(value);
                        Log.d("KEY", key);
                        Log.d("VALUE", value);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Attach Spinners to adapter
                currency.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError){
                    ExchangeRate.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                    ExchangeRate.setText("No Connection...");
                }
            }
        }) {
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
