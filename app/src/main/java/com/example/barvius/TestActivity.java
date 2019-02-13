package com.example.barvius;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private boolean trueAnswer;
    private DBItems currentAnswer;
    private String directionTranslate;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        directionTranslate = getIntent().getStringExtra("direction");
        if (directionTranslate.equals("РУС -> ENG")){
            TTS.getInstance().setLanguage("ru");
        } else {
            TTS.getInstance().setLanguage("en");
        }

        createTest();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void createTest(){
        if (DBHandler.getInstance().dictionaryIsEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Словарь пуст, добавьте слова!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
            return;
        }
        if (!DBHandler.getInstance().testIsAvailable()){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Все слова изучены, очистете архив!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
            return;
        }
        List<Button> buttons = new ArrayList<Button>();
        LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.answer_button);
        buttonContainer.removeAllViews();
        setTrueAnswer(true);
        currentAnswer = DBHandler.getInstance().getRandomItems();

        TextView text = findViewById(R.id.masterText);
        text.setText(getLabelFromDirection(currentAnswer));

        TTS.getInstance().setTextToSpeech(getLabelFromDirection(currentAnswer));

        final Button btmpm = new Button(this);
        btmpm.setText(getButtonFromDirection(currentAnswer));
        btmpm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if(isTrueAnswer()){
                    DBHandler.getInstance().moveToArchive(currentAnswer);
                }
                createTest();
            }
        });
        buttons.add(btmpm);

        for (DBItems i : DBHandler.getInstance().getRandomSet(5, currentAnswer.getId())) {
            Button btmps = new Button(this);
            btmps.setText(getButtonFromDirection(i));
            btmps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    setTrueAnswer(false);
                }
            });
            buttons.add(btmps);
        }

        Collections.shuffle(buttons);
        for (Button i :buttons) {
            buttonContainer.addView(i);
        }

    }

    public boolean isTrueAnswer() {
        return trueAnswer;
    }

    public void setTrueAnswer(boolean trueAnswer) {
        this.trueAnswer = trueAnswer;
    }

    String getLabelFromDirection(DBItems items){
        if (directionTranslate.equals("РУС -> ENG")){
            return items.getRu();
        } else {
            return items.getEn();
        }
    }

    String getButtonFromDirection(DBItems items){
        if (directionTranslate.equals("РУС -> ENG")){
            return items.getEn();
        } else {
            return items.getRu();
        }
    }
}
