package com.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.codepath.nytimessearch.Article;
import com.codepath.nytimessearch.ArticleArrayAdapter;
import com.codepath.nytimessearch.EndlessRecyclerViewScrollListener;
import com.codepath.nytimessearch.FilterActivity;
import com.codepath.nytimessearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    //EditText etQuery;
    //Button btnSearch;
    RequestParams params;
    String searchQuery;
    final int REQUEST_CODE_FILTER = 100;
    boolean isFiltered = false;
    String filterNewsType = "";
    String beginDate;
    String sortValue;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvArticles) RecyclerView rvArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //etQuery = (EditText) findViewById(R.id.etQuery);
        //btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();

        adapter = new ArticleArrayAdapter(articles);

        rvArticles.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));

        rvArticles.setAdapter(adapter);

        //set layoutmanager before adapter

        rvArticles.setHasFixedSize(true);

        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener((StaggeredGridLayoutManager)rvArticles.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d("DEBUG","onLoadMore: "+searchQuery);
                params = new RequestParams();
                params.put("api-key", "eb8e15941677443286fe314e6fe7ebde");
                params.put("page", page);
                params.put("q", searchQuery);
                if (isFiltered) {
                    params.put("begin_date",beginDate);
                    params.put("sort",sortValue);
                    params.put("fq",String.format("news_desk:(%s)",filterNewsType));
                } else {
                    requestSearch(params, page);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery=query;
                Log.d("DEBUG","reached text submission");
                searchView.clearFocus();
                //rvArticles.clearOnScrollListeners();
                //rvArticles.addOnScrollListener();
                isFiltered = false;
                articles.clear();
                adapter.notifyDataSetChanged();

                params = new RequestParams();
                params.put("api-key","eb8e15941677443286fe314e6fe7ebde");
                params.put("page",0);
                params.put("q",searchQuery);
                requestSearch(params,0);

                //getSupportActionBar().setTitle(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem filterItem = menu.findItem(R.id.filter_search);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(SearchActivity.this, FilterActivity.class);
                i.putExtra("query",searchQuery);
                startActivityForResult(i,REQUEST_CODE_FILTER);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home)
            finish();
/*

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }
/*
    public void onArticleSearch(View view) {
        articles.clear();
        adapter.notifyDataSetChanged();
        requestSearch(etQuery.getText().toString(),0);
        //Toast.makeText(this, "searching for "+query, Toast.LENGTH_LONG).show();
    }
*/

    public void requestSearch(RequestParams params, int page) {
        //String query = etQuery.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";


        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "here"+response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    //adapter.notifyItemRangeChanged(adapter.getItemCount(),articles.size()-1);
                    adapter.notifyDataSetChanged();
                    //adapter.addAll(Articles.fromJSONArray(articleJsonResults));
                    Log.d("DEBUG", "articles - "+articles.toString());
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.d("DEBUG","on failure: "+statusCode+" ");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FILTER && resultCode == RESULT_OK) {
            //searchQuery = data.getExtras().getString("new query").toString();

            isFiltered = true;
            params = new RequestParams();
            params.put("api-key","eb8e15941677443286fe314e6fe7ebde");
            params.put("page",0);
            params.put("q",searchQuery);
            beginDate = data.getStringExtra("date filter");
            sortValue = data.getStringExtra("sort value");

            filterNewsType = "";
            if (data.getBooleanExtra("politics",false))
                filterNewsType+="\"Politics\"";
            if (data.getBooleanExtra("financial",false))
                filterNewsType+=" "+"\"Financial\"";
            if (data.getBooleanExtra("tech",false))
                filterNewsType+=" "+"\"Technology\"";

            Log.d("DEBUG","this is filter fq: "+filterNewsType);

            articles.clear();
            adapter.notifyDataSetChanged();
            params.put("begin_date",beginDate);
            params.put("sort",sortValue);
            params.put("fq",String.format("news_desk:(%s)",filterNewsType));
            requestSearch(params,0);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
