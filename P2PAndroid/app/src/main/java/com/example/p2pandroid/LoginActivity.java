package com.example.p2pandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.AsymCryptography;
import com.company.MultiCastSender;
import com.company.TCPReceiver;
import com.company.MultiCastReceiver;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;

public class LoginActivity extends AppCompatActivity {

    private EditText loginField;
    private EditText passwordField;
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_LOGIN = "login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginField    = findViewById(R.id.login);
        passwordField = findViewById(R.id.password);

        /*TextView locIp = findViewById(R.id.localAddress);
        String myIp = null;
        try {
            myIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            Toast.makeText(this, "Cannot get my ip", Toast.LENGTH_SHORT).show();
        }

        if (myIp != null)  {
            locIp.setText(myIp);
        } else {
            locIp.setText("Oooops");
        }*/
    }

    public void onClickSignUpButton (View v) {

        if (loginField.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Error: login is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordField.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Error: password is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = loginField.getText().toString();
        String password = passwordField.getText().toString();

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(EXTRA_PASSWORD, password);
        intent.putExtra(EXTRA_LOGIN, name);

        startActivity(intent);
    }
}