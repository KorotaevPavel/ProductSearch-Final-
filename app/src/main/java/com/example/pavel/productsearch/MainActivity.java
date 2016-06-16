package com.example.pavel.productsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Patterns;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    EditText txt, lowPrice, highPrice;
    TextView pageView;
    Button btn;
    FloatingActionButton fabNext, fabBack;
    ProgressBar progress;
    ListView lv;
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    HashMap<String, String> map;
    SimpleAdapter adapter;
    String sWebsite, website, sTitle, sPrice, sLink, sLink2, sDescription, sText, sText2;
    String sPage;
    int low, high, page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        page = 1;
        pageView = (TextView) findViewById(R.id.textView13);

        fabNext = (FloatingActionButton) findViewById(R.id.fab);
        fabBack = (FloatingActionButton) findViewById(R.id.fab2);
        fabNext.setVisibility(View.INVISIBLE);
        fabBack.setVisibility(View.INVISIBLE);
        pageView.setVisibility(View.INVISIBLE);

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn.setEnabled(false);
                data.clear();
                page++;
                pageView.setText(Integer.toString(page));

                String toSearch = getSearchName().toString();
                website = sWebsite + toSearch + sPage + Integer.toString(page);

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

                btn.setEnabled(true);
                fabBack.setVisibility(View.VISIBLE);
            }
        });

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn.setEnabled(false);
                data.clear();
                page--;
                pageView.setText(Integer.toString(page));

                if (page == 1)
                    fabBack.setVisibility(View.INVISIBLE);
                String toSearch = getSearchName().toString();
                website = sWebsite + toSearch + sPage + Integer.toString(page);

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

                btn.setEnabled(true);
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
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.INVISIBLE);

        lowPrice = (EditText) findViewById(R.id.editText2);
        highPrice = (EditText) findViewById(R.id.editText3);

        sWebsite = "http://m.ulmart.ru/search?string=";
        sTitle = ".b-what-looked-product__name";
        sPrice = ".b-what-looked-product__cost";
        sLink = ".b-what-looked-product__img";
        sLink2 = ".gtm-product-page-click";
        sDescription = ".l-what-looked-product__r";
        sText = ".b-details__description";
        sText2 = ".b-product-list";
        sPage = "&pageNum=";

        navigationView.setCheckedItem(R.id.select1);

        adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                new String[]{"Title", "Price"},
                new int[]{android.R.id.text1, android.R.id.text2});

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabNext.setVisibility(View.VISIBLE);
                fabBack.setVisibility(View.INVISIBLE);
                btn.setEnabled(false);
                data.clear();
                page = 1;
                pageView.setVisibility(View.VISIBLE);
                pageView.setText(Integer.toString(page));
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
                btn.setEnabled(true);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, SingleItemView.class);
                intent.putExtra("name", data.get(position).get("Title"));
                intent.putExtra("imglink", data.get(position).get("Link"));
                intent.putExtra("descr", data.get(position).get("Description"));
                intent.putExtra("dText", sText);
                intent.putExtra("price", data.get(position).get("Price"));
                startActivity(intent);
            }
        });
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String uaString = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.15 (KHTML, like Gecko) Chrome/24.0.1295.0 Safari/537.15";
                int time = 10000;
                Document doc = Jsoup.connect(website).userAgent(uaString).timeout(time).get();
                Elements title = doc.select(sTitle);
                Elements price = doc.select(sPrice);
                Elements pics = doc.select(sLink);
                Elements descrA = doc.select(sDescription);
                Element descr;

                int number = Math.min(Math.min(title.size(), price.size()), Math.min(pics.size(),descrA.size()));

                for (int i = 0; i < number; i++) {
                    if ((tryParseInt(price.get(i).text().replaceAll("[\\D]", "")))
                            && (Integer.parseInt(price.get(i).text().replaceAll("[\\D]", "")) > low)
                            && (Integer.parseInt(price.get(i).text().replaceAll("[\\D]", "")) < high)) {
                        map = new HashMap<String, String>();
                        String priceWithoutCurrency = Integer.toString(Integer.parseInt(price.get(i).text().replaceAll("[\\D]", "")));
                        map.put("Title", title.get(i).text());
                        map.put("Price", priceWithoutCurrency + " руб");
                        Matcher m = Patterns.WEB_URL.matcher(pics.get(i).toString());
                        String url = null;
                        while (m.find()) {
                            url = m.group();
                        }
                        map.put("Link", url);
                        if (descrA.size()!=0) {
                            descr = descrA.get(i).select("a").first();
                            map.put("Description", descr.attr("abs:href"));
                        }
                        data.add(map);
                    }
                }
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
            lv.setAdapter(adapter);
        }
    }

    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
                    Integer pr1 = Integer.parseInt(price1.get("Price").replaceAll("[\\D]", ""));
                    Integer pr2 = Integer.parseInt(price2.get("Price").replaceAll("[\\D]", ""));
                    return pr1.compareTo(pr2);
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

            fabNext.setVisibility(View.INVISIBLE);
            fabBack.setVisibility(View.INVISIBLE);
            pageView.setVisibility(View.INVISIBLE);

            sWebsite = "http://m.ulmart.ru/search?string=";
            sTitle = ".b-what-looked-product__name";
            sPrice = ".b-what-looked-product__cost";
            sLink = ".b-what-looked-product__img";
            sDescription = ".l-what-looked-product__r";
            sText = ".b-details__description";
            sText2 = ".b-product-list";
            sPage = "&pageNum=";
        } else if (id == R.id.select3) {
            fabNext.setVisibility(View.INVISIBLE);
            fabBack.setVisibility(View.INVISIBLE);
            pageView.setVisibility(View.INVISIBLE);

            sWebsite = "http://www.dns-shop.ru/search/?q=";
            sTitle = ".item-name";
            sPrice = ".price_g";
            sLink = ".popover-content";
            sDescription = ".item-name";
            sText = ".price_item_description";
            sText2 = ".table-params.table-no-bordered";
            sPage = "&pageNum=";
        } else if (id == R.id.select6) {
            fabNext.setVisibility(View.INVISIBLE);
            fabBack.setVisibility(View.INVISIBLE);
            pageView.setVisibility(View.INVISIBLE);

            sWebsite = "http://www.enter.ru/search?q=";
            sTitle = ".bSimplyDesc__eText";
            sPrice = ".bPrice";
            sLink = ".bProductImg__eImg";
            sDescription = ".bListingItem__eInner";
            sText = ".product-section__desc";
            sText2 = ".product-section__props";
            sPage = "&page=";
        }

        if (id == R.id.database) {
            Intent intent = new Intent(MainActivity.this, DBList.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
