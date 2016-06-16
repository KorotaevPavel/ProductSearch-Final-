package com.example.pavel.productsearch;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.os.AsyncTask;
import android.widget.SimpleAdapter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.util.ArrayList;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    EditText txt, lowPrice, highPrice;
    Button btn;
    ListView lv;
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    HashMap<String, String> map;
    Map<String, String> treeMap;
    SimpleAdapter adapter;
    String sWebsite, website, sTitle, sPrice;
    int low, high;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        txt = (EditText) findViewById(R.id.editText);
        btn = (Button) findViewById(R.id.button);
        lv = (ListView) findViewById(R.id.listView);

        lowPrice = (EditText) findViewById(R.id.editText2);
        highPrice = (EditText) findViewById(R.id.editText3);

        sWebsite = "http://m.ulmart.ru/search?string=";
        sTitle = ".b-what-looked-product__name";
        sPrice = ".b-what-looked-product__cost";

        adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                new String[] {"Title", "Price"},
                new int[] {android.R.id.text1, android.R.id.text2});

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.clear();
                String toSearch = getSearchName().toString();
                website = sWebsite + toSearch;
                if (lowPrice.getText().toString().trim().length() > 0)
                    low = Integer.parseInt(lowPrice.getText().toString());
                else
                    low = 0;
                if (highPrice.getText().toString().trim().length() > 0)
                    high = Integer.parseInt(highPrice.getText().toString());
                else
                    high = Integer.MAX_VALUE;
                MyTask mt = new MyTask();
                mt.execute();

            }
        });
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //String search = getSearchName();
            try {
                //if (search.isEmpty())
                    //sWebsite = "https://m.ulmart.ru/";
                    //sWebsite = "https://m.citilink.ru/";
                    //sWebsite = "http://m.dns-shop.ru/";
                //else
                    //sWebsite = "http://m.ulmart.ru/search?string="+search;
                //sWebsite = "https://m.citilink.ru/search/?text="+search;
                //sWebsite = "http://www.dns-shop.ru/search/?q="+search;

                Document doc = Jsoup.connect(website).get();
                Elements title = doc.select(sTitle);
                Elements price = doc.select(sPrice);
                //Elements title = doc.select(".b-what-looked-product__name");
                //Elements price = doc.select(".b-what-looked-product__cost");
                //Elements title = doc.select(".product-card-for-list__title-container");
                //Elements price = doc.select(".product-card-for-list__price-container");
                //Elements title = doc.select(".item-name");
                //Elements price = doc.select(".item-price");

                for (int i = 0; i < title.size(); i++) {
                    if ((Integer.parseInt(price.get(i).text().replaceAll("[\\D]", ""))>low)
                            &&(Integer.parseInt(price.get(i).text().replaceAll("[\\D]", ""))<high)) {
                        map = new HashMap<String, String>();
                        map.put("Title", title.get(i).text());
                        map.put("Price", price.get(i).text());
                        data.add(map);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            lv.setAdapter(adapter);
        }

        //protected String getSearchName() {
            //return txt.getText().toString();
        //}
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public String getSearchName() {
        return txt.getText().toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.sort_by_name) {
            Collections.sort(data, new Comparator<Map<String, String>>() {
                public int compare(final Map<String, String> name1, final Map<String, String> name2) {
                    return name1.get("Title").compareTo(name2.get("Title"));
                }
            });

            lv.setAdapter(adapter);
        }

        if (id == R.id.sort_by_price) {
            Collections.sort(data, new Comparator<Map<String, String>>() {
                public int compare(final Map<String, String> price1, final Map<String, String> price2) {
                    return price1.get("Price").compareTo(price2.get("Price"));
                }
            });

            lv.setAdapter(adapter);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.select1) {
            sWebsite = "http://m.ulmart.ru/search?string=";
            sTitle = ".b-what-looked-product__name";
            sPrice = ".b-what-looked-product__cost";
        } else if (id == R.id.select2) {
            sWebsite = "https://www.mediamarkt.ru/search?q=";
            sTitle = ".product__title--right";
            sPrice = ".product__price";
            //sWebsite = "https://m.citilink.ru/search/?text=";
            //sTitle = ".product-card-for-list__title-container";
            //sPrice = ".product-card-for-list__price-container";
        } else if (id == R.id.select3) {
            sWebsite = "http://www.dns-shop.ru/search/?q=";
            sTitle = ".item-name";
            //sPrice = ".item-price";
            sPrice = ".price_g";
        }

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
