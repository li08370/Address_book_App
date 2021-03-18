package com.example.addressbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;


import com.example.addressbook.AddAddressService.AddressBinder;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText inputAddress;
    ListView addressList;
    Button addAddress;
    ArrayList<String> storage = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    AddAddressService AddressService;
    boolean isBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputAddress = (EditText) findViewById(R.id.inputText);
        addAddress = (Button) findViewById(R.id.addButton);
        addressList = (ListView) findViewById(R.id.addressList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, storage);
        addressList.setAdapter(arrayAdapter);

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(v);
            }
        });
        inputAddress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        onButtonClick(v);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void onButtonClick(View v) {
        if (isBound) {
            AddAddressService.addAddress(inputAddress.getText().toString(), storage);
            arrayAdapter.notifyDataSetChanged();
            inputAddress.setText("");
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AddressBinder binder = (AddressBinder) service;
            AddressService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, AddAddressService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBound) {
            unbindService(mConnection);
            isBound = false;
        }
    }
}