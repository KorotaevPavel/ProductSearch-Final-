package com.example.pavel.productsearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DBList extends Activity {

    ListView lv;
    ArrayAdapter<Item> adapter;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_list);
        lv = (ListView) findViewById(R.id.listView2);

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = mSqLiteDatabase.query("items", new String[]{DatabaseHelper.NAME,
                        DatabaseHelper.PRICE, DatabaseHelper.LINK, DatabaseHelper.DESC, DatabaseHelper.IMAGE},
                null, null, null, null, null);
        items = new ArrayList<>();
        while (cursor.moveToNext()) {
            String itemName = cursor.getString(0);
            int itemPrice = cursor.getInt(1);
            items.add(new Item(itemName, itemPrice));
        }

        cursor.close();

        adapter = new InternalAdapter(this, android.R.layout.simple_list_item_2, items);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        menu.add(0, v.getId(), 0, "Открыть");
        menu.add(0, v.getId(), 0, "Удалить");
    }

    public boolean onContextItemSelected(MenuItem item){
        final AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = menuInfo.position;
        if(item.getTitle()=="Открыть") {
            Intent intent = new Intent(DBList.this, ShowItemFromDB.class);
            intent.putExtra("name", items.get(pos).title);
            startActivity(intent);
        }
        if(item.getTitle()=="Удалить") {
            mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
            mSqLiteDatabase.delete("items", mDatabaseHelper.NAME + " = ?",
                    new String[] { items.get(pos).title });
            mSqLiteDatabase.close();
            Toast.makeText(getApplicationContext(), "Удалено из списка покупок", Toast.LENGTH_LONG).show();

            items.remove(pos);
            adapter.notifyDataSetChanged();
        }
        return true;
    }

    private class Item {
        public String title;
        public int price;

        public Item(String title, int price) {
            this.title = title;
            this.price = price;
        }
    }

    private class InternalAdapter extends ArrayAdapter<Item> {

        private final int mResource;
        private final LayoutInflater mInflater;

        public InternalAdapter(Context context, int resource, List<Item> objects) {
            super(context, resource, objects);
            mResource = resource;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(mResource, parent, false);
            }
            Item item = getItem(position);
            TextView title = (TextView) view.findViewById(android.R.id.text1);
            title.setText(item.title);
            TextView price = (TextView) view.findViewById(android.R.id.text2);
            price.setText(Integer.toString(item.price) + " руб");
            return view;
        }
    }
}
