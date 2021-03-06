package com.example.android.yelpsearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.yelpsearch.R;
import com.example.android.yelpsearch.data.YelpRest;
import com.example.android.yelpsearch.utils.YelpUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements YelpSearchAdapter.OnSearchItemClickListener, LoaderManager.LoaderCallbacks<String>,
            NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String REPOS_ARRAY_KEY = "YelpRests";
    private static final String SEARCH_URL_KEY = "YelpSearchURL";

    private static final int Yelp_SEARCH_LOADER_ID = 0;

    private RecyclerView mSearchResultsRV;
    private EditText mSearchBoxET;
    private TextView mLoadingErrorTV;
    private ProgressBar mLoadingPB;
    private DrawerLayout mDrawerLayout;

    private YelpSearchAdapter mYelpSearchAdapter;
    private ArrayList<YelpRest> mRepos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxET = findViewById(R.id.et_search_box);
        mSearchResultsRV = findViewById(R.id.rv_search_results);
        mLoadingErrorTV = findViewById(R.id.tv_loading_error);
        mLoadingPB = findViewById(R.id.pb_loading);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nv_nav_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_nav_menu);

        mSearchResultsRV.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsRV.setHasFixedSize(true);

        mYelpSearchAdapter = new YelpSearchAdapter(this);
        mSearchResultsRV.setAdapter(mYelpSearchAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(REPOS_ARRAY_KEY)) {
            mRepos = (ArrayList<YelpRest>) savedInstanceState.getSerializable(REPOS_ARRAY_KEY);
            mYelpSearchAdapter.updateSearchResults(mRepos);
        }

        getSupportLoaderManager().initLoader(Yelp_SEARCH_LOADER_ID, null, this);

        Button searchButton = findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = mSearchBoxET.getText().toString();
                if (!TextUtils.isEmpty(searchQuery)) {
                    doYelpSearch(searchQuery);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doYelpSearch(String query) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = preferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
//        String language = preferences.getString(getString(R.string.pref_language_key),
//                getString(R.string.pref_language_default));
        String city = preferences.getString(getString(R.string.pref_location_key),"USA");
//        boolean searchInName = preferences.getBoolean(getString(R.string.pref_in_name_key), true);
//        boolean searchInDescription = preferences.getBoolean(getString(R.string.pref_in_description_key), true);
//        boolean searchInReadme = preferences.getBoolean(getString(R.string.pref_in_readme_key), false);

//        String url = YelpUtils.buildYelpSearchURL(query, sort, language, user, searchInName,
//                searchInDescription, searchInReadme);
        String url = YelpUtils.buildYelpSearchURL(query, sort, city);
        Log.d(TAG, "querying search URL: " + url);

        Bundle args = new Bundle();
        args.putString(SEARCH_URL_KEY, url);
        mLoadingPB.setVisibility(View.VISIBLE);
        getSupportLoaderManager().restartLoader(Yelp_SEARCH_LOADER_ID, args, this);
    }

    @Override
    public void onSearchItemClick(YelpRest restaurant) {
        Intent intent = new Intent(this, RepoDetailActivity.class);
        intent.putExtra(YelpUtils.EXTRA_YELP_REST, restaurant);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRepos != null) {
            outState.putSerializable(REPOS_ARRAY_KEY, mRepos);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        String url = null;
        if (bundle != null) {
            url = bundle.getString(SEARCH_URL_KEY);
        }
        return new YelpSearchLoader(this, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        Log.d(TAG, "loader finished loading");
        if (s != null) {
            mLoadingErrorTV.setVisibility(View.INVISIBLE);
            mSearchResultsRV.setVisibility(View.VISIBLE);
            mRepos = YelpUtils.parseYelpSearchResults(s);
            mYelpSearchAdapter.updateSearchResults(mRepos);
        } else {
            mLoadingErrorTV.setVisibility(View.VISIBLE);
            mSearchResultsRV.setVisibility(View.INVISIBLE);
        }
        mLoadingPB.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        // Nothing to do here...
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mDrawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.nav_search:
                return true;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.nav_saved_repos:
                Intent savedReposIntent = new Intent(this, SavedReposActivity.class);
                startActivity(savedReposIntent);
                return true;
            default:
                return false;
        }
    }
}