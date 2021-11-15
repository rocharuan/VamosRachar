package com.example.vamosrachar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private EditText valorTotal;
    private EditText nPessoas;
    private TextView valorDividido;
    private ImageButton compartilhar;
    private ImageButton ouvir;
    private static final int MY_DATA_CHECK_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        valorTotal = (EditText) findViewById(R.id.valorTextNumber);
        nPessoas = (EditText) findViewById(R.id.totalPessoasTextNumber);
        valorDividido = (TextView) findViewById(R.id.valortextView);
        compartilhar = (ImageButton) findViewById(R.id.shareImageButton);
        ouvir = (ImageButton) findViewById(R.id.listenImageButton);

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

        valorTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(nPessoas.getText().length() != 0) {
                    float divisao = (Float.parseFloat(s.toString()) / (Float.parseFloat(nPessoas.getText().toString())));
                    valorDividido.setText(numberFormat.format(divisao));
                }
            }
        });

        nPessoas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(valorTotal.getText().length() != 0) {
                    float divisao = (Float.parseFloat(valorTotal.getText().toString()) / (Float.parseFloat(s.toString())));

                    valorDividido.setText(numberFormat.format(divisao));
                }
            }
        });

        compartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(valorDividido.getText().length() != 0){
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, valorDividido.getText().toString());
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                }
            }
        });

        ouvir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(valorDividido.getText().length() != 0){

                    tts.speak(valorDividido.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {

                           Log.i("TTS", "Initialization success.");
                        } else {
                            Intent installIntent = new Intent();
                            installIntent.setAction(
                                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                            startActivity(installIntent);
                        }
                    }
                });
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}