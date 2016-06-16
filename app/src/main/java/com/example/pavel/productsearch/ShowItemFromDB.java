package com.example.pavel.productsearch;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ShowItemFromDB extends Activity{
    TextView tv;
    ImageView iv;
    Bitmap bitmap;
    Button buttonAdd, buttonOpen;
    ProgressBar progress;
    TextView tvDescr, tvPrice, tvLink;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading_from_db);

        tv = (TextView) findViewById(R.id.textView8);
        tvDescr = (TextView) findViewById(R.id.textView9);
        tvLink = (TextView) findViewById(R.id.textView11);
        buttonAdd = (Button) findViewById(R.id.button4);
        buttonOpen = (Button) findViewById(R.id.button5);
        tvPrice = (TextView) findViewById(R.id.textView10);
        iv = (ImageView) findViewById(R.id.imageView3);
        progress = (ProgressBar) findViewById(R.id.progressBar3);
        progress.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = mSqLiteDatabase.query("items", new String[]{DatabaseHelper.NAME,
                        DatabaseHelper.PRICE, DatabaseHelper.LINK, DatabaseHelper.DESC, DatabaseHelper.IMAGE},
                null, null,
                null, null, null);

        while (cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));

            if (itemName.equals(name))
            {
                int price = cursor.getInt(1);
                String link = cursor.getString(2);
                String desc = cursor.getString(3);
                byte[] image = cursor.getBlob(4);
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

                tvPrice.setText("Цена: " + price + " руб");
                tvLink.setText(link);
                tvDescr.setText(desc);
                iv.setImageBitmap(bitmap);
            }
        }

        cursor.close();
    }
}
