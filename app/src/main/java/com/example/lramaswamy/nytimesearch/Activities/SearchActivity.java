package com.example.lramaswamy.nytimesearch.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lramaswamy.nytimesearch.Adapters.ArticleRecyclerAdapter;
import com.example.lramaswamy.nytimesearch.Decorators.SpacesItemDecoration;
import com.example.lramaswamy.nytimesearch.Fragments.SearchFilterFragment;
import com.example.lramaswamy.nytimesearch.Listeners.EndlessRecyclerViewScrollListener;
import com.example.lramaswamy.nytimesearch.Models.Article;
import com.example.lramaswamy.nytimesearch.Models.SearchFilter;
import com.example.lramaswamy.nytimesearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements SearchFilterFragment.OnDataPass{


    ArrayList<Article> articles;
    ArticleRecyclerAdapter adapter;
    String query;
    RecyclerView rvArticles;
    EndlessRecyclerViewScrollListener rvScrollListener;
    StaggeredGridLayoutManager gridLayoutManager;
    SearchFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setToolBar();
        articles = new ArrayList<>();

        //Create the recycler view
        rvArticles = (RecyclerView) findViewById(R.id.rvArticle);
        //Set adapter on the recycler view for the articles model view
        adapter = new ArticleRecyclerAdapter(articles, this);
        rvArticles.setAdapter(adapter);
        //Add the decoration for better spacing
        SpacesItemDecoration decoration = new SpacesItemDecoration(30);
        rvArticles.addItemDecoration(decoration);
        //Create the staggered grid layout and add to the recycler view
        gridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        // Attach the layout manager to the recycler view
        rvArticles.setLayoutManager(gridLayoutManager);

        //add endless pagination to the recyclerview.
        rvScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                customLoadMoreDataFromApi(page);
            }
        };
        rvArticles.addOnScrollListener(rvScrollListener);
    }


    private void customLoadMoreDataFromApi(int page) {

        AsyncHttpClient client =  new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = buildParams(page);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleResults = null;

                try {
                    articleResults = response.getJSONObject("response").getJSONArray("docs");
                    List<Article> moreArticles = Article.fromJSONArray(articleResults);
                    int curSize = adapter.getItemCount();
                    articles.addAll(moreArticles);
                    adapter.notifyItemRangeInserted(curSize, moreArticles.size() - 1);
                    Log.d("Debug", articles.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setToolBar() {
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

    }

    public void onArticleSearch(String query, int pageValue) {

        final View view = findViewById(R.id.activity_search);
        if(!isNetworkAvailable() || !isOnline()) {
            Snackbar.make(view, R.string.snackbar_text, Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToSettings(SearchActivity.this);
                        }
                    }).setActionTextColor(Color.RED).show();
            return;
        }

        this.query = query;
        final AsyncHttpClient client =  new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = buildParams(pageValue);
        rvScrollListener.resetState();
        Toast.makeText(view.getContext(), client.getUrlWithQueryString(true, url, params), Toast.LENGTH_LONG).show();

        Log.d("Query String", client.getUrlWithQueryString(true, url, params));

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleResults = null;

                try {
                    articleResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.clear();
                    List<Article> moreArticles = Article.fromJSONArray(articleResults);
                    articles.addAll(moreArticles);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace(System.out);
            }
        });
    }

    public static void goToSettings(AppCompatActivity activity) {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_WIFI_SETTINGS);
        activity.startActivity(i);
    }

    public RequestParams buildParams(int page) {
        RequestParams params = new RequestParams();
        params.put("api-key", "c97a679e697d4a3796df1f7b389d7183");

        if(filter != null) {
            if(filter.getBeginDate() != null || !filter.getBeginDate().isEmpty()) {
                String newBeingDate = filter.getBeginDate().replace("-", "");
                params.put("begin_date", newBeingDate);
            }
            if(filter.getSortOrder() != null || !filter.getSortOrder().isEmpty()) {
                params.put("sort", filter.getSortOrder());
            }
            StringBuffer fqValue = new StringBuffer("news_desk:(");

            if(filter.isArts()) {
                fqValue.append("\"Arts\"");

            }
            if(filter.isFasStyle()) {
                fqValue.append("\"Fashion & Style\" ");

            }
            if(filter.isSports()) {
                fqValue.append("\"Sports\" ");
            }
            if(!filter.isArts() || !filter.isSports() || !filter.isFasStyle()) {
                fqValue.append(")");
                params.put("fq", fqValue.toString());
            }
        }

        params.put("page", page);
        params.put("q", query);
        return params;
    }

    public void showEditDialog(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        SearchFilterFragment searchFilterFragment = SearchFilterFragment.newInstance("Set Filter for Query");
        searchFilterFragment.setOldFilter(filter);
        searchFilterFragment.show(fm, "search_filter_fragment");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Customize searchview text and hint colors
        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.RED);
        et.setHintTextColor(Color.BLACK);

        searchItem.expandActionView();
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                onArticleSearch(query, 0);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });
        return true;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public void onDataPass(SearchFilter data) {
        filter = data;
        onArticleSearch(query, 0);
    }
}
