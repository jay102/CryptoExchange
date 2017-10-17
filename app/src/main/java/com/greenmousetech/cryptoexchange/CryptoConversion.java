package com.greenmousetech.cryptoexchange;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import static com.greenmousetech.cryptoexchange.Home.CHOSEN_CURRENCY;

public class CryptoConversion extends AppCompatActivity {
    TextView CryptoCurrency;
    EditText baseCurrency, ConvertedCurrency;
    Spinner chosenCryptoDropdown;
    ImageView cryptoImage;
    SharedPreferences preferences;
    String Crypto;
    String SelectedCurrency;
    String SelectedCurrencyValue;
    ArrayAdapter<String> adapter;
    ArrayList<String> CryptoConvertedIn;
    double ValueToUse;
    double ConvertedValue;
    TextWatcher watcher1;
    TextWatcher watcher2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cryto_conversion);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //initialize and couple all views
        CryptoCurrency = (TextView) findViewById(R.id.currency);
        baseCurrency = (EditText) findViewById(R.id.currency_to_convert);
        ConvertedCurrency = (EditText) findViewById(R.id.converted_currency);
        chosenCryptoDropdown = (Spinner) findViewById(R.id.crypto_currency_dropdown);
        cryptoImage = (ImageView) findViewById(R.id.crypto_image);

        //getting our stored values from previous activity
        preferences = this.getSharedPreferences(CHOSEN_CURRENCY, MODE_PRIVATE);
        Crypto = preferences.getString("CryptoCurrency", "");
        SelectedCurrency = preferences.getString("selectedCurrencyItem", "");
        SelectedCurrencyValue = preferences.getString("selectedCurrencyValue", "");

        //setting our Textview with Chosen base currency from previous Activity
        CryptoCurrency.setText(SelectedCurrency);
        ConvertedCurrency.setText(SelectedCurrencyValue);


        //initialize ArrayList to hold CryptoCurrency from previous Activity to Feed to our Adapter
        CryptoConvertedIn = new ArrayList<>();
        CryptoConvertedIn.add(Crypto);

        if (Crypto.equals("BTC")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cryptoImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_bitcoin_converted, getApplicationContext().getTheme()));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cryptoImage.setImageDrawable(getResources().getDrawable(R.drawable.ether_converted, getApplicationContext().getTheme()));
            }
        }

        //create adapter for spinner and populate it
        adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item_two, CryptoConvertedIn);

        //Attach Spinners to adapter
        chosenCryptoDropdown.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //TextWatcher to Handle Our Conversions Concurrently
        watcher1 = new TextWatcher() {
            Double userCurrency;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!baseCurrency.getText().toString().equalsIgnoreCase("")) {
                    ConvertedCurrency.removeTextChangedListener(watcher2);
                    ValueToUse = Double.parseDouble(SelectedCurrencyValue);

                    //converions
                    userCurrency = Double.parseDouble(baseCurrency.getText().toString().toString());
                    ConvertedValue = userCurrency * ValueToUse;

                    double rounded = (double) Math.round(ConvertedValue * 10000000) / 10000000;

                    //Finally setting The Text of the Converted box
                    ConvertedCurrency.setText(Double.toString(rounded));
                    ConvertedCurrency.addTextChangedListener(watcher2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        watcher2 = new TextWatcher() {
            Double userCurrency;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ConvertedCurrency.getText().toString().equalsIgnoreCase("")) {
                    baseCurrency.removeTextChangedListener(watcher1);
                    //convert CryptoValue to Integer to be used for Conversions
                    ValueToUse = Double.parseDouble(SelectedCurrencyValue);

                    //conversions
                    userCurrency = Double.parseDouble(s.toString());
                    ConvertedValue = userCurrency / ValueToUse;

                    double rounded = (double) Math.round(ConvertedValue * 10000000) / 10000000;

                    //Finally setting The Text of the Converted box
                    baseCurrency.setText(Double.toString(rounded));
                    baseCurrency.addTextChangedListener(watcher1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        baseCurrency.addTextChangedListener(watcher1);
        ConvertedCurrency.addTextChangedListener(watcher2);
    }

    //Override Method to handle our back button
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}



