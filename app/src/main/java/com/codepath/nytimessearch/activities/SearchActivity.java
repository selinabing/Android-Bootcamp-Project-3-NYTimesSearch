package com.codepath.nytimessearch.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.nytimessearch.Article;
import com.codepath.nytimessearch.ArticleArrayAdapter;
import com.codepath.nytimessearch.EndlessRecyclerViewScrollListener;
import com.codepath.nytimessearch.FilterDialogFragment;
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

public class SearchActivity extends AppCompatActivity implements FilterDialogFragment.FilterDialogListener{

    RequestParams params;
    String searchQuery;
    final int REQUEST_CODE_FILTER = 100;
    boolean isFiltered = false;
    String filterNewsType = "";
    String beginDate;
    String sortValue;
    boolean isTopStory;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvArticles) RecyclerView rvArticles;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(articles);

        isTopStory=true;
        adapter.setIsTopStory(isTopStory);

        rvArticles.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        rvArticles.setAdapter(adapter);

        //set layoutmanager before adapter
        rvArticles.setHasFixedSize(true);
        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener((StaggeredGridLayoutManager)rvArticles.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d("DEBUG","onLoadMore: "+searchQuery+"/"+page);
                params = new RequestParams();
                params.put("api-key", "eb8e15941677443286fe314e6fe7ebde");
                params.put("page", page);
                if(!isTopStory)
                    params.put("q", searchQuery);
                if (isFiltered) {
                    params.put("begin_date",beginDate);
                    params.put("sort",sortValue);
                    params.put("fq",String.format("news_desk:(%s)",filterNewsType));
                }
                requestSearch(params,page);
            }
        });

        generateTopStories();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                params = new RequestParams();
                params.put("api-key", "eb8e15941677443286fe314e6fe7ebde");
                params.put("page",0);
                articles.clear();
                adapter.notifyDataSetChanged();
                if(isTopStory){
                    requestSearch(params,0);
                } else {
                    params.put("q", searchQuery);
                    if(isFiltered){
                        Log.d("DEBUG","reached1");
                        params.put("begin_date",beginDate);
                        params.put("sort",sortValue);
                        params.put("fq",String.format("news_desk:(%s)",filterNewsType));
                    }
                    Log.d("DEBUG","reached2");
                    requestSearch(params,0);
                    Log.d("DEBUG","reached2");
                }
                Toast.makeText(getApplicationContext(),"Refreshing...",Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }



    public void generateTopStories(){
        isTopStory=true;
        adapter.setIsTopStory(isTopStory);
        params = new RequestParams();
        params.put("api-key", "eb8e15941677443286fe314e6fe7ebde");
        params.put("page",0);
        articles.clear();
        adapter.notifyDataSetChanged();
        requestSearch(params,0);
        getSupportActionBar().setTitle("Top Stories of NYTimes");
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                isTopStory=false;
                adapter.setIsTopStory(isTopStory);
                searchQuery=query;
                searchView.clearFocus();
                isFiltered = false;

                articles.clear();
                adapter.notifyDataSetChanged();

                params = new RequestParams();
                params.put("api-key","eb8e15941677443286fe314e6fe7ebde");
                params.put("page",0);
                params.put("q",searchQuery);
                requestSearch(params,0);
                getSupportActionBar().setTitle(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.clearFocus();
                searchView.setFocusable(false);
                searchView.onActionViewCollapsed();
                searchItem.collapseActionView();
                if(!isTopStory)
                    generateTopStories();
                return true;
            }
        });



        MenuItem filterItem = menu.findItem(R.id.filter_search);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /*Intent i = new Intent(SearchActivity.this, FilterActivity.class);
                i.putExtra("query",searchQuery);
                startActivityForResult(i,REQUEST_CODE_FILTER);
                */
                showFilterDialog();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void showFilterDialog(){
        FragmentManager fm = getSupportFragmentManager();
        FilterDialogFragment filterDF = FilterDialogFragment.newInstance("Filter");
        filterDF.show(fm,"filter_fragment_dialog");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void requestSearch(RequestParams params, int page) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url;
        if (!isTopStory) {
            url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        } else {
            url = "https://api.nytimes.com/svc/topstories/v2/home.json";
        }

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;

                try {
                    if (isTopStory)
                        articleJsonResults = response.getJSONArray("results");
                    else
                        articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJsonResults,isTopStory));
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
    public void onFinishDialog(ArrayList<String> res) {
        isFiltered = true;
        articles.clear();
        adapter.notifyDataSetChanged();

        if(res==null) {
            generateTopStories();
        } else {
            beginDate = res.get(0);
            sortValue = res.get(2);
            filterNewsType = String.format("news_desk:(%s)", res.get(1));

            params = new RequestParams();
            params.put("api-key", "eb8e15941677443286fe314e6fe7ebde");
            params.put("page", 0);
            params.put("q", searchQuery);
            params.put("begin_date", beginDate);
            params.put("sort", sortValue);
            params.put("fq", filterNewsType);
            requestSearch(params, 0);
        }
    }
}
