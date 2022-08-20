package com.example.speech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements main_recycler.ItemClickListener {
    main_recycler adapter;
     static Databasehelper databasehelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<String> options;
        options = new ArrayList<>();
        options.add("Pronunciation training");
        options.add("Comprehension training");
        options.add("Fluency test");

        SharedPreferences sharedPreferences = getSharedPreferences("database", MODE_PRIVATE);
        boolean isDatabaseInstalled = sharedPreferences.getBoolean("databaseInstalled", false);
        databasehelper = new Databasehelper(this);


        boolean a = databasehelper.checkDatabase();
        if (!a) {
            try {
                databasehelper.createDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            databasehelper.openDatabase();
        }
        // set up the RecyclerView2717:ff88
        RecyclerView recyclerView = findViewById(R.id.options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new main_recycler(this, options);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onItemClick(View view, int position) {
        final int PRONOUNCE_INTENT = 0;
        final int COMPREHENSION_INTENT = 1;
        final int FLUENCY = 2;
        Bundle bundle;
        Intent intent,intent1;
        intent = new Intent(getApplicationContext(), pronounce.class);
        bundle = new Bundle();
        if (position == 0) {
            bundle.putInt("response", PRONOUNCE_INTENT);
            intent.putExtras(bundle);
            this.startActivity(intent);
        } else if (position == 1) {
            bundle.putInt("response", COMPREHENSION_INTENT);
            intent.putExtras(bundle);
            this.startActivity(intent);
        } else if (position == 2) {
            //under development
            /*intent1 = new Intent(this, Rec_audio.class);

            startActivity(intent1);*/

            Toast.makeText(getApplicationContext(),"Under development",Toast.LENGTH_LONG).show();
        }


    }
}