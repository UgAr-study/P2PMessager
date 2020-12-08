package com.example.p2pandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.company.AsymCryptography;
import com.company.Messenger;
import com.company.MultiCastSender;
import com.company.TCPReceiver;
import com.company.MultiCastReceiver;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String userName;
    private String userPassword;
    private String userPublicKye = null;
    private String userIpAddress = "192.168.43.78";
    private SQLDataBase UsersTable;
    private SimpleCursorAdapter cursorAdapter;

    private String[] users = { "Artem", "Ignat"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userName = intent.getStringExtra(LoginActivity.EXTRA_LOGIN);
        userPassword = intent.getStringExtra(LoginActivity.EXTRA_PASSWORD);

        UsersTable = new SQLDataBase(this);

        ConnectToNetWork();

        Spinner usersList = findViewById(R.id.userList);

        ArrayList<String> NamesInTable = UsersTable.getAllNames();


        //usersList.setAdapter(adapter);
    }

    private void ConnectToNetWork () {

        int i = 0;
        while (i < 2) {
            try {
                userPublicKye = AsymCryptography.getStringPublicKey(AsymCryptography.generateNewPair(userPassword));
                break;
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                ++i;
            }
        }

        //userPublicKye = "ofwofhwoef";

        if (userPublicKye == null) {
            Toast.makeText(this, "Can't generate public key", Toast.LENGTH_SHORT).show();
            return;
        }

        UsersTable.WriteDB(userName, userIpAddress, userPublicKye);


        //new MultiCastReceiver(UsersTable).start();
        //new TCPReceiver().start();
        //new MultiCastSender("all", userName, userPublicKye).start();
    }


    public void onClickSendButton (View v) {

        Spinner spinner = findViewById(R.id.userList);

        int position = spinner.getSelectedItemPosition();

        ArrayList<String> publicKeys = UsersTable.getAllPublicKeys();
        String selectedPublicKey = publicKeys.get(position);

    }

    public void onClickTestButton (View v) {
        TextView textField = findViewById(R.id.textField);
        textField.setText("TextView");
        textField.setTextSize(24);

        UsersTable.WriteDB("Ignat", "192.168.43.78", "12QWRTY");
        UsersTable.WriteDB("Nikita", "192.168.43.79", "56RYUEW");


        cursorAdapter.notifyDataSetChanged();


        ArrayList<String> ips = UsersTable.getIpAddressByName("Artem");
        ArrayList<String> ids = UsersTable.getIdByName("Artem");
        ArrayList<String> pubKey = UsersTable.getPublicKeyById(ids.get(0));

        String text = pubKey.get(0) + "\n";

        for (int i = 0; i < 3; ++i) {
            for (String cn : ips) {
                text = text.concat(cn + "\n");
            }

            for (String cn : ids) {
                text = text.concat(cn + "\n");
            }
        }

        textField.setText(text);

        UsersTable.deleteInfoByName("Artem");
        UsersTable.deleteInfoByName("Ignat");
        UsersTable.deleteInfoByName("Nikita");

    }
}