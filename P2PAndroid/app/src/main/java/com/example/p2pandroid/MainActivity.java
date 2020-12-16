package com.example.p2pandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.company.AsymCryptography;
import com.company.Messenger;
import com.company.MultiCastSender;
import com.company.SymCryptography;
import com.company.TCPReceiver;
import com.company.MultiCastReceiver;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.crypto.SealedObject;


public class MainActivity extends AppCompatActivity {

    private String userName;
    private String userPassword;
    private String userPublicKye = null;
    private String userIpAddress = null;
    public SQLDataBase UsersTable;
    public ArrayList<String> NamesInTable;
    public ArrayList<MessageItem> mMessages;
    public ArrayAdapter<String> userListAdapter;
    public RecyclerViewAdapter recyclerViewAdapter;
    private Handler receiveMessagesHandler;
    private Handler receiveContactsHandler;
    private TCPReceiver tcpReceiver;
    private MultiCastReceiver mcReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        UsersTable = new SQLDataBase(MainActivity.this, "UsersContacts");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        userPassword = intent.getStringExtra(LoginActivity.EXTRA_PASSWORD);

        if (intent.hasExtra(LoginActivity.EXTRA_LOGIN)) {
            userName      = intent.getStringExtra(LoginActivity.EXTRA_LOGIN);
            userPublicKye = intent.getStringExtra(LoginActivity.EXTRA_PABLIC_KEY);
        } else {
            userName      = UsersTable.getNameById(String.valueOf(1)).get(0);
            userPublicKye = UsersTable.getPublicKeyById(String.valueOf(1)).get(0);
        }

        mMessages = new ArrayList<>();

        receiveMessagesHandler = new TCPReceiverHandler(this);
        receiveContactsHandler = new MCReceiverHandler(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(this, mMessages);
        recyclerView.setAdapter(recyclerViewAdapter);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        recyclerView.setLayoutManager(lm);


        ConnectToNetWork();

        Spinner usersList = findViewById(R.id.userList);

        NamesInTable = UsersTable.getAllNames();

        userListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, NamesInTable);
        userListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        usersList.setAdapter(userListAdapter);
    }

    @Override
    protected void onDestroy() {
        UsersTable.deleteInfoByName("Artem");
        UsersTable.deleteInfoByName("Ignat");
        tcpReceiver.interrupt();
        super.onDestroy();
    }

    private void ConnectToNetWork () {

        if (userPublicKye == null) {
            Toast.makeText(this, "Can't generate public key", Toast.LENGTH_SHORT).show();
            return;
        }

        UsersTable.WriteDB(userName, userIpAddress, userPublicKye);

        SharedPreferences kesStore = getSharedPreferences(AsymCryptography.KEY_STORE_NAME, MODE_PRIVATE);

        mcReceiver = new MultiCastReceiver(UsersTable, receiveContactsHandler);
        tcpReceiver = new TCPReceiver(receiveMessagesHandler, UsersTable, userPassword, kesStore);

        mcReceiver.start();
        tcpReceiver.start();

        new MultiCastSender("all", userName, userPublicKye).start();
    }


    public void onClickSendButton (View v) {

        EditText editText = findViewById(R.id.message);
        String message = editText.getText().toString();
        editText.setText(null);
        if (message.trim().isEmpty()) {
            Toast.makeText(MainActivity.this, "Empty message", Toast.LENGTH_SHORT).show();
            return;
        }

        new SendMessage().execute(message);
    }

    public void onClickTestButton (View v) {

        //UsersTable.deleteInfoByName("Artem");
        UsersTable.deleteInfoByName("Ignat");

        UsersTable.WriteDB("Ignat", "192.168.43.78", "12QWRTY");

        NamesInTable.clear();
        ArrayList<String> newUserNames = UsersTable.getAllNames();
        NamesInTable.addAll(newUserNames);
        userListAdapter.notifyDataSetChanged();


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

        mMessages.add(new MessageItem("Debug", text, "00:00"));
        recyclerViewAdapter.notifyItemInserted(mMessages.size());
    }

    public void onClickSendMCRequest (View v) {
        new MultiCastSender("all", userName, userPublicKye).start();
    }

    private class SendMessage extends AsyncTask<String, Void, Boolean> {

        private String ipAddress;
        MainActivity activity;

        @Override
        protected void onPreExecute() {
            Button bt = findViewById(R.id.send_button);
            bt.setClickable(false);
            Spinner spinner = findViewById(R.id.userList);

            activity = MainActivity.this;
            int position = spinner.getSelectedItemPosition();

            ArrayList<String> ipAddresses = UsersTable.getAllIpAddresses();
            ipAddress = ipAddresses.get(position);
        }

        @Override
        protected Boolean doInBackground(String... msgs) {

            ArrayList<String> aesKeys = activity.UsersTable.getAESKeyByIpAddress(ipAddress);

            boolean isAESKeyInTable = false;
            for (int i = 0; i < aesKeys.size(); ++i)
                if (aesKeys.get(i) != null)
                    isAESKeyInTable = true;

            if (!isAESKeyInTable) {
                ArrayList<String> pubKeys = activity.UsersTable.getPublicKeyByIpAddress(ipAddress);

                if (pubKeys.isEmpty()) {
                    // Error
                    return false;
                } else {
                    String pubKey = pubKeys.get(0);
                    String symKey = SymCryptography.generateStringSecretKey();

                    if (symKey == null) {
                        //Error
                        return false;
                    }

                    activity.UsersTable.updateAESKeyByPublicKey(symKey, pubKey);

                    SealedObject so = AsymCryptography.encryptMsg(symKey, AsymCryptography.getPublicKeyFromString(pubKey));

                    Messenger sender = new Messenger();
                    if (!sender.SendEncryptMessageToIp(so, ipAddress)) {
                        //Error
                        sender.Close();
                        return false;
                    } else {
                        so = SymCryptography.encryptMsg(msgs[0], SymCryptography.getSecretKeyByString(symKey));

                        if (so == null) {
                            //Error
                            return false;
                        } else {

                            boolean isSend = false;
                            if (sender.SendEncryptMessageToIp(so, ipAddress))
                                isSend = true;

                            sender.Close();
                            return isSend;
                        }
                    }
                }

            } else {
                String aesKey = aesKeys.get(0);
                SealedObject so = SymCryptography.encryptMsg(msgs[0], SymCryptography.getSecretKeyByString(aesKey));

                if (so == null) {
                    //Error
                    return false;
                }

                Messenger sender = new Messenger();

                boolean isSend = false;
                if (sender.SendEncryptMessageToIp(so, ipAddress))
                    isSend = true;

                sender.Close();
                return isSend;
            }
        }

        protected void onProgressUpdate() {
            //Код, передающий информацию о ходе выполнения задачи
        }
        protected void onPostExecute(Boolean success) {
            //Код, выполняемый при завершении задач
            Button bt = findViewById(R.id.send_button);
            bt.setClickable(true);
            if (!success) {
                Toast.makeText(MainActivity.this, "Cannot send message to " + ipAddress, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Message sent to " + ipAddress, Toast.LENGTH_SHORT).show();
            }
        }
    }
}


class TCPReceiverHandler extends Handler {
    WeakReference<MainActivity> wrActivity;

    private final int ERROR = 1;
    private final int SUCCESS = 0;
    private final String KEY_DATA = "Data";
    private final String KEY_NAME = "Name";
    private final String KEY_ERROR = "ErrorMsg";

    public TCPReceiverHandler(MainActivity activity) {
        wrActivity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        MainActivity activity = wrActivity.get();

        if (activity == null)
            return;

        RecyclerView rv = activity.findViewById(R.id.recyclerView);

        switch (msg.what) {
            case SUCCESS:

                String name;
                String ip = msg.getData().getString(KEY_NAME);
                ArrayList<String> names = activity.UsersTable.getNameByIpAddress(ip);
                if (names.isEmpty()) {
                    name = ip;
                } else
                    name = names.get(0);

                String text = msg.getData().getString(KEY_DATA);

                Date currentDate = new Date();
                DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String time = timeFormat.format(currentDate);

                activity.recyclerViewAdapter.addItem(new MessageItem(name, text, time));
                rv.scrollToPosition(activity.recyclerViewAdapter.getItemCount() - 1);

                break;

            case ERROR:
                String errorMessage = msg.getData().getString(KEY_ERROR);

                activity.recyclerViewAdapter.addItem(new MessageItem("Error", errorMessage, "-00:00"));
                rv.scrollToPosition(activity.recyclerViewAdapter.getItemCount() - 1);

                break;
        }
    }
}

class MCReceiverHandler extends Handler {
    WeakReference<MainActivity> wrActivity;

    private final int ERROR = 1;
    private final int SUCCESS = 0;
    private final String KEY_DATA = "Data";
    private final String KEY_ERROR = "ErrorMsg";

    public MCReceiverHandler(MainActivity activity) {
        wrActivity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        MainActivity activity = wrActivity.get();

        if (activity == null)
            return;

        RecyclerView rv = activity.findViewById(R.id.recyclerView);

        switch (msg.what) {
            case SUCCESS:

                String name = "Success";
                String text = msg.getData().getString(KEY_DATA);

                Date currentDate = new Date();
                DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String time = timeFormat.format(currentDate);
                //String time = "12:00";

                activity.recyclerViewAdapter.addItem(new MessageItem(name, text, time));
                rv.scrollToPosition(activity.recyclerViewAdapter.getItemCount() - 1);
                /*activity.mMessages.add(new MessageItem(name, text, time));
                activity.recyclerViewAdapter.notifyItemInserted(activity.mMessages.size());
*/
                activity.NamesInTable.clear();
                ArrayList<String> newUsers = activity.UsersTable.getAllNames();
                activity.NamesInTable.addAll(newUsers);
                activity.userListAdapter.notifyDataSetChanged();
                break;

            case ERROR:
                String errorMessage = msg.getData().getString(KEY_ERROR);

                activity.recyclerViewAdapter.addItem(new MessageItem("Error", errorMessage, "11:11"));
                rv.scrollToPosition(activity.recyclerViewAdapter.getItemCount() - 1);
                break;
        }
    }
}
