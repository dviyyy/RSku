package com.vv.appbiodata.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vv.appbiodata.R;
import com.vv.appbiodata.helper.DataHelper;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity {

    String[] daftar;
    ListView lvData;
    protected Cursor cursor;
    DataHelper dataHelper;
    public static MainActivity mainActivity;
    private Button btnLogout;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("RSku");
        setSupportActionBar(toolbar);

        btnLogout = findViewById(R.id.btn_logout);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnLogout.setOnClickListener(view -> btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }));
        ExtendedFloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(arg0 -> {
            Intent intent = new Intent(MainActivity.this, BuatBiodataActivity.class);
            startActivity(intent);
        });

        mainActivity = this;
        dataHelper = new DataHelper(this);
        RefreshList();

    }

    public void RefreshList() {
        SQLiteDatabase sqLiteDatabase = dataHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM biodata", null);
        daftar = new String[cursor.getCount()];
        cursor.moveToFirst();

        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            daftar[cc] = cursor.getString(1);
        }

        lvData = findViewById(R.id.lvData);
        lvData.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, daftar));
        lvData.setSelected(true);
        lvData.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            final String selection = daftar[arg2]; //.getItemAtPosition(arg2).toString();
            final CharSequence[] dialogitem = {"Lihat Biodata", "Update Biodata", "Hapus Biodata"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Pilihan");
            builder.setItems(dialogitem, (dialog, item) -> {
                switch (item) {
                    case 0:
                        Intent i = new Intent(getApplicationContext(), LihatBiodataActivity.class);
                        i.putExtra("nama", selection);
                        startActivity(i);
                        break;
                    case 1:
                        Intent in = new Intent(getApplicationContext(), UpdateBiodataActivity.class);
                        in.putExtra("nama", selection);
                        startActivity(in);
                        break;
                    case 2:
                        SQLiteDatabase sqLiteDatabase1 = dataHelper.getWritableDatabase();
                        sqLiteDatabase1.execSQL("delete from biodata where nama = '" + selection + "'");
                        RefreshList();
                        break;
                }
            });
            builder.create().show();
        });
        ((ArrayAdapter) lvData.getAdapter()).notifyDataSetInvalidated();

    }

}
