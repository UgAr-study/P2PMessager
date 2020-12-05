package com.example.p2pandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String[] users = { "Artem", "Ignat"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner usersList = findViewById(R.id.userList);

        ArrayAdapter adapter = new ArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item, users);

        usersList.setAdapter(adapter);
    }

    public void onClickSendButton (View v) {

        TextView textField = findViewById(R.id.textField);
        textField.setText("TextView");
        textField.setTextSize(24);

        SQLDataBase usersInfo = new SQLDataBase(this);

        usersInfo.WriteDB("Artem", "192.168.43.78", "12QWERTY");
        usersInfo.WriteDB("Artem", "192.168.43.79", "56RTYUEW");


        ArrayList<String> ips = usersInfo.getIpAddressByName("Artem");
        ArrayList<String> ids = usersInfo.getIdByName("Artem");
        ArrayList<String> pubKey = usersInfo.getPublicKeyById(ids.get(0));

        String text = pubKey.get(0) + "\n";

        for (String cn : ips) {
            text = text.concat(cn + "\n");
        }

        for (String cn : ids) {
            text = text.concat(cn + "\n");
        }

        textField.setText(text);

        usersInfo.deleteInfoByName("Artem");

    }
}