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

        //pegando as views
        valorTotal = (EditText) findViewById(R.id.valorTextNumber);
        nPessoas = (EditText) findViewById(R.id.totalPessoasTextNumber);
        valorDividido = (TextView) findViewById(R.id.valortextView);
        compartilhar = (ImageButton) findViewById(R.id.shareImageButton);
        ouvir = (ImageButton) findViewById(R.id.listenImageButton);

        //inicio da verificacao de se o TTS está apto a ser executado
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        //startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

        //preparando o formato da moeda na view de resultado
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

        //setando um listener que verifica se os dois edittext não são vazio. Quando os dois estão com algum valor a view de resultado é alterada
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

        //funcao de compartilhar
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

        //funcao de ler um texto
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
        //checa se o TTS esta apto a ser executado
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // sucesso, cria a instancia do TTS
                tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {

                           Log.i("TTS", getString(R.string.sucessoTTS));
                        } else {
                            Intent installIntent = new Intent();
                            installIntent.setAction(
                                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                            startActivity(installIntent);
                        }
                    }
                });
            } else {
                //falha, tenta instalar
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //para o servico do TTS quando sai do aplicativo
    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}