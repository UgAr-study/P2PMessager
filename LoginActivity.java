package com.example.p2pandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.crypto.SealedObject;

import com.company.SymCryptography;

public class LoginActivity extends AppCompatActivity {

    private EditText loginField;
    private EditText passwordField;
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_LOGIN = "login";
    SharedPreferences userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginField    = findViewById(R.id.login);
        passwordField = findViewById(R.id.password);

        //TextView locIp = findViewById(R.id.localAddress);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        userInfo = getSharedPreferences("UserInfo", MODE_PRIVATE);

        //locIp.setText(getLocalIp());
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

        if (userInfo.getAll().isEmpty()) {
            SharedPreferences.Editor userInfoEditor = userInfo.edit();
            try {
                //SealedObject encryptPwd = SymCryptography.encryptByPwd(password, password);
                String encryptPwd = SymCryptography.encryptByPwdGson(password, password);
                Toast.makeText(this, encryptPwd + 1, Toast.LENGTH_SHORT).show();
                userInfoEditor.putString(name, encryptPwd);
                userInfoEditor.apply();
            } catch (Exception e) {
                Toast.makeText(this, "Sym Crypto ERROR\n", Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(intent);
        } else {
            if (!userInfo.contains(name)) {
                Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    String encryptPwd = userInfo.getString(name, null);
                    Toast.makeText(this, encryptPwd + "+", Toast.LENGTH_SHORT).show();
                    if (SymCryptography.decryptByPwdGson(encryptPwd, password).equals(password)) {
                        Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Invalid password", Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Sym Crypro ERROR, Dcrypt", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    private String getLocalIp () {

        String res = "";
        try {
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();  // gets All networkInterfaces of your device
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface inet = (NetworkInterface) networkInterfaces.nextElement();
                Enumeration address = inet.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) address.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        res =  res.concat(inetAddress.getHostAddress() + "\n");
                    }
                }
            }
        } catch (Exception e) {
            res = e.getMessage();
        }

        return res;
    }

}