package com.example.hackpalachia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this); // context, usually an Activity or Application
        String realmName = "My Project";
        RealmConfiguration config = new RealmConfiguration.Builder().name(realmName).build();
        Realm backgroundThreadRealm = Realm.getInstance(config);
        /*Task Task = new Task("New Task");
        backgroundThreadRealm.executeTransaction (transactionRealm -> {
            transactionRealm.insert(Task);
        });*/

    }

    public void onBtnClick (View view){
        TextView txtHello = findViewById(R.id.textHello);
        txtHello.setText("Hello");
    }
}