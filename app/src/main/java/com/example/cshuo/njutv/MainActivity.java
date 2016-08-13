package com.example.cshuo.njutv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TvContent tvContent;
    private static String LOG_TAG = "MainActivity";
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        new DownloadTask().execute();
//        mAdapter = new MyRecyclerViewAdapter(getDataSet());
//        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }


    LinkedHashMap<String,String> parseTvs() throws ParserException {
        final String TV_HOME_URL = "http://tv.nju.edu.cn/";
        Parser htmlParser = new Parser(TV_HOME_URL);
        htmlParser.setEncoding("UTF-8");
        LinkedHashMap<String,String> TvMap = new LinkedHashMap<String,String>();

        String postTitle = "";
        String url = "";

        NodeList tvTable = htmlParser.extractAllNodesThatMatch(new HasAttributeFilter("id", "main"));

        if (tvTable != null && tvTable.size() > 0) {
            NodeList itemList = tvTable.elementAt(0).getChildren().extractAllNodesThatMatch(new TagNameFilter("li"));
            for(int i=0; i<itemList.size(); i++){
                NodeList titleItem = itemList.elementAt(i).getChildren().extractAllNodesThatMatch(
                        new TagNameFilter("h4"));
                NodeList linkItem = itemList.elementAt(i).getChildren().extractAllNodesThatMatch(
                        new AndFilter(new TagNameFilter("a"), new HasAttributeFilter("class", "mac")));
                postTitle = titleItem.elementAt(0).toPlainTextString();
                url = ((LinkTag)linkItem.elementAt(0)).getLink();
                TvMap.put(postTitle, url);
            }
        }
        return TvMap;
    }


    private class DownloadTask extends AsyncTask<String, Void, LinkedHashMap> {


        @Override
        protected LinkedHashMap<String,String> doInBackground(String... urls) {
            try {
                return parseTvs();
            } catch (ParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(LinkedHashMap results) {
            tvContent = new TvContent();
            Iterator item = null;

            if(results != null){
                item = results.entrySet().iterator();
                while(item.hasNext()){
                    Map.Entry entry = (Map.Entry) item.next();
                    tvContent.addItem(new TvContent.TvItem(entry.getKey().toString(), entry.getValue().toString()));
                }
            }

            mAdapter = new MyRecyclerViewAdapter((ArrayList<TvContent.TvItem>) tvContent.getItems());
            mRecyclerView.setAdapter(mAdapter);
            ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Log.i(LOG_TAG, " Clicked on Item " + position);
                    Log.i(LOG_TAG, tvContent.getItems().get(position).getUrl());
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, tvContent.getItems().get(position).getUrl());
                    startActivity(intent);
                }
            });
        }
    }


}
