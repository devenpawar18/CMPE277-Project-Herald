package com.herald.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.herald.ApplicationEx;
import com.herald.R;
import com.herald.adapter.NewsListAdapter;
import com.herald.data.DatabaseHandler;
import com.herald.entity.News;
import com.herald.services.RetrieveNewsService;
import com.herald.services.RetrieveNewsService.RetrieveEventsServiceListener;
import com.herald.utils.HeraldUtils;

/**
 * Displays the news of a specific user selected news category
 * 
 * @author DEVEN
 * 
 */
public class NewsListActivity extends Activity implements
		RetrieveEventsServiceListener {

	private ListView newsListView;
	private NewsListAdapter newsAdapter;
	private List<News> newsList = new ArrayList<News>();
	private ProgressDialog pd;
	private String query = "";
	private DatabaseHandler db;
	private TextView noDataTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_list);

		noDataTextView = (TextView) findViewById(R.id.no_data);
		query = getIntent().getExtras().getString("query");
		getActionBar().setTitle(query);
		getActionBar().setHomeButtonEnabled(false);
		/**
		 * whether to show Standard Home Icon or not
		 */
		getActionBar().setDisplayHomeAsUpEnabled(true);
		newsListView = (ListView) findViewById(R.id.news_list_view);
		newsAdapter = new NewsListAdapter(newsList, this);
		newsListView.setAdapter(newsAdapter);
		/**
		 * Checks if the network connection is available, if yes then hit the
		 * web service call else take the cached data from the SQLite db.
		 */
		if (HeraldUtils.isConnectionAvailable(this)) {
			pd = ProgressDialog.show(this, "", "Loading News");
			getNews();
		} else {
			db = new DatabaseHandler(getApplicationContext());
			db.openInternalDB();
			newsList = db.getNewsList(query);
			ApplicationEx.newsList = newsList;
			newsAdapter.setNewsList(newsList);
			if (newsList.size() > 0) {
				noDataTextView.setVisibility(View.GONE);
				newsListView.setVisibility(View.VISIBLE);
			} else {
				noDataTextView.setVisibility(View.VISIBLE);
				newsListView.setVisibility(View.GONE);
			}

			Toast.makeText(this,
					"Network error. Please check your connection...",
					Toast.LENGTH_SHORT).show();
		}

		newsListView.setOnItemClickListener(onItemClickListener);
	}

	/**
	 * News web service call
	 */
	private void getNews() {
		RetrieveNewsService service = new RetrieveNewsService(
				getApplicationContext(), query);
		service.setListener(this);
		ApplicationEx.operationsQueue.execute(service);
	}

	/**
	 * Handles back press
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Handles on item click of a list view
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			News news = (News) view.getTag(R.id.news_id);
			ApplicationEx.selectedPosition = position;
			Intent intent = new Intent(NewsListActivity.this,
					NewsDetailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("news", news);
			startActivity(intent);

		}
	};

	/*
	 * Success Callback (non-Javadoc)
	 * 
	 * @see
	 * com.herald.services.RetrieveEventsService.RetrieveEventsServiceListener
	 * #onRetrieveEventsFinished(java.util.ArrayList)
	 */
	@Override
	public void onRetrieveEventsFinished(List<News> newsList) {
		if (pd != null || pd.isShowing())
			pd.dismiss();
		newsAdapter.setNewsList(newsList);
		ApplicationEx.newsList = newsList;
		if (newsList.size() > 0) {
			noDataTextView.setVisibility(View.GONE);
			newsListView.setVisibility(View.VISIBLE);
		} else {
			noDataTextView.setVisibility(View.VISIBLE);
			newsListView.setVisibility(View.GONE);
		}
		System.out.println("*****Reached Success*****");
	}

	/*
	 * Failure Callback (non-Javadoc)
	 * 
	 * @see
	 * com.herald.services.RetrieveEventsService.RetrieveEventsServiceListener
	 * #onRetrieveEventsFailed(int, java.lang.String)
	 */
	@Override
	public void onRetrieveEventsFailed(int error, String message) {
		if (pd != null || pd.isShowing())
			pd.dismiss();
		noDataTextView.setVisibility(View.VISIBLE);
		newsListView.setVisibility(View.GONE);
		Toast.makeText(NewsListActivity.this, message, Toast.LENGTH_SHORT)
				.show();

	}

}
