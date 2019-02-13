package com.example.barvius;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TTS.getInstance().init(this);
        DBHandler.init(this);
        setContentView(R.layout.activity_main);
    }

    protected void loadDBFromFile(){
        String type="*/*";

        Intent i=new Intent(Intent.ACTION_GET_CONTENT);
        i.setType(type);
        startActivityForResult(Intent.createChooser(i,"select file") ,0);
    }

    protected void exportDBToFile(){
        saveToFile(new File(Environment.getExternalStorageDirectory() + "/db.csv"),DBHandler.getInstance().getAll());
    }

    void saveToFile(File f, List<DBItems> itemsList) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            for (DBItems i: itemsList) {
                String tmp = i.getId()+","+i.getRu()+","+i.getEn()+"\n";
                fos.write(tmp.getBytes());
            }
            fos.close();
            Toast.makeText (getApplicationContext(), "Сохранено в " + f.getAbsolutePath(), Toast.LENGTH_LONG).show ();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText (getApplicationContext(), "Error: " + e.getMessage (), Toast.LENGTH_LONG).show ();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK && data != null) {
                    for (DBItems i: CSV.parseCSV(new File(data.getData().getPath()))) {
                        DBHandler.getInstance().addItems(i);
                    }
                }
                break;
        }


    }

    protected void addDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавление слова в словарь");

        final EditText ru = new EditText(this);
        ru.setHint("ru");
        ru.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    if (!s.toString().matches("[а-я]+")) {
                        s.delete(s.length()-1,s.length());
                    }
                }
            }
        });

        final EditText en = new EditText(this);
        en.setHint("en");
        en.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    if (!s.toString().matches("[a-z]+")) {
                        s.delete(s.length()-1,s.length());
                    }
                }
            }
        });

        LinearLayout lay = new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(ru);
        lay.addView(en);
        builder.setView(lay);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHandler.getInstance().addItems(new DBItems(ru.getText().toString(),en.getText().toString()));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Добавить слово").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addDialog();
                return true;
            }
        });

        menu.add(0, 2, 0, "Загрузить словарь").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                loadDBFromFile();
                return true;
            }
        });

        menu.add(0, 3, 0, "Выгрузить словарь").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                exportDBToFile();
                return true;
            }
        });

        menu.add(0, 4, 0, "drop").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DBHandler.getInstance().truncateArchive();
                return true;
            }
        });

        menu.add(0, 5, 0, "info").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        DBHandler.getInstance().info(), Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void startBtn(View v){
        final String[] list = {"РУС -> ENG", "ENG -> РУС"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Язык");

        builder.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                intent.putExtra("direction", list[which]);
                startActivity(intent);
            }
        });

        builder.setCancelable(true);
        builder.create().show();
    }
}
