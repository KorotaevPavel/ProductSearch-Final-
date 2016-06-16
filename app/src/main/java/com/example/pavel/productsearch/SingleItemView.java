package com.example.pavel.productsearch;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class SingleItemView extends Activity {
    TextView tv;
    ImageView iv;
    Bitmap bitmap;
    Button buttonAdd, buttonOpen;
    ProgressBar progress;
    TextView tvDescr, tvPrice, tvLink;
    String name, link, descr, dText;
    String itemD, price;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_item_view);
        tv = (TextView) findViewById(R.id.textView2);
        tvDescr = (TextView) findViewById(R.id.textView3);
        tvLink = (TextView) findViewById(R.id.textView4);
        buttonAdd = (Button) findViewById(R.id.button2);
        buttonOpen = (Button) findViewById(R.id.button3);
        tvPrice = (TextView) findViewById(R.id.textView5);
        iv = (ImageView) findViewById(R.id.imageView2);
        progress = (ProgressBar) findViewById(R.id.progressBar2);
        progress.setVisibility(View.INVISIBLE);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        link = intent.getStringExtra("imglink");
        descr = intent.getStringExtra("descr");
        dText = intent.getStringExtra("dText");
        price = intent.getStringExtra("price");

        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = mSqLiteDatabase.query("items", new String[]{DatabaseHelper.NAME,
                        DatabaseHelper.PRICE, DatabaseHelper.LINK, DatabaseHelper.DESC, DatabaseHelper.IMAGE},
                null, null,
                null, null, null);

        while (cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));

            if (itemName.equals(name))
                buttonAdd.setEnabled(false);
        }

        cursor.close();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.NAME, name);
                values.put(DatabaseHelper.PRICE, price);
                values.put(DatabaseHelper.LINK, descr);
                values.put(DatabaseHelper.DESC, tvDescr.getText().toString());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();
                values.put(DatabaseHelper.IMAGE, image);

                mSqLiteDatabase.insert("items", null, values);
                mSqLiteDatabase.close();
                Toast.makeText(getApplicationContext(), "Добавлено в список покупок", Toast.LENGTH_LONG).show();
                buttonAdd.setEnabled(false);
            }
        });

        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();
                Cursor cursor = mSqLiteDatabase.query("items", new String[]{DatabaseHelper.NAME,
                                DatabaseHelper.PRICE, DatabaseHelper.LINK, DatabaseHelper.DESC, DatabaseHelper.IMAGE},
                        null, null,
                        null, null, null);

                while (cursor.moveToNext()) {
                    String itemName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));
                    String itemPrice = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRICE));
                    String itemLink = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LINK));
                    String itemDesc = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESC));

                    tvDescr.setText(itemName + itemPrice + itemLink + itemDesc);
                }

                cursor.close();
            }
        });

        MyTask mt = new MyTask();
        mt.execute();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input= connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);

                String uaString = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.15 (KHTML, like Gecko) Chrome/24.0.1295.0 Safari/537.15";
                int time = 10000;
                Document doc = Jsoup.connect(descr).userAgent(uaString).timeout(time).get();
                Elements itemText = doc.select(dText);
                itemD = itemText.text();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            progress.setVisibility(View.INVISIBLE);
            iv.setImageBitmap(bitmap);
            if ("".equals(itemD))
                tvDescr.setText("Описание отсутствует.");
            else
                tvDescr.setText(itemD);
            tvLink.setText(descr);
            tvPrice.setText("Цена: " + price);
        }
    }
}


