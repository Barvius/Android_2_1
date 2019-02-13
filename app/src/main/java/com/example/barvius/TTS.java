package com.example.barvius;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;

import java.util.Locale;

public class TTS implements TextToSpeech.OnInitListener {

    private static TTS instance;

    private TextToSpeech textToSpeech;
    private boolean mIsInit;

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("ru"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                mIsInit = false;
            } else {
                mIsInit = true;
                textToSpeech.setSpeechRate(0.7f);
            }
        } else {
            mIsInit = false;
        }
    }

    private TTS() { }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setTextToSpeech(String text){
        if (mIsInit) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
        }
    }

    public void init(Context context){
        textToSpeech = new TextToSpeech(context, this);
    }

    public void setLanguage(String language){
        textToSpeech.setLanguage(new Locale(language));
    }

    public static TTS getInstance(){
        if(instance == null){
            instance = new TTS();
        }
        return instance;
    }
}
