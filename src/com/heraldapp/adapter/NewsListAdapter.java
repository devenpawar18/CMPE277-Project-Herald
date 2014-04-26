package com.heraldapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heraldapp.R;
import com.heraldapp.entity.News;

/**
 * News Adapter that holds News list in a list view
 * 
 * @author DEVEN
 * 
 */
public class NewsListAdapter extends BaseAdapter {
	private List<News> newsList = new ArrayList<News>();
	private int lastPosition = -1;
	private Context context;

	/**
	 * 
	 * @param NewsList
	 * @param applicationContext
	 */
	public NewsListAdapter(List<News> newsList, Context applicationContext) {
		this.newsList = newsList;
		this.context = applicationContext;
	}

	/**
	 * Update the News list
	 * 
	 * @param NewsList
	 */
	public void setNewsList(List<News> newsList) {
		this.newsList = newsList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return newsList.size();
	}

	@Override
	public Object getItem(int position) {
		return newsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactsViewHolder contactsViewHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.activity_list_row, null);
			contactsViewHolder = new ContactsViewHolder();
			contactsViewHolder.name = (TextView) convertView
					.findViewById(R.id.item);
		} else {
			contactsViewHolder = (ContactsViewHolder) convertView.getTag();
		}
		News news = newsList.get(position);
		contactsViewHolder.name.setText(news.getTitle());
		convertView.setTag(R.id.news_id, news);
		convertView.setTag(contactsViewHolder);

		Animation animation = AnimationUtils.loadAnimation(context,
				(position > lastPosition) ? R.anim.up_from_bottom
						: R.anim.down_from_top);
		convertView.startAnimation(animation);
		lastPosition = position;

		return convertView;
	}

	/**
	 * View holder for News title
	 * 
	 * @author DEVEN
	 * 
	 */
	private class ContactsViewHolder {
		TextView name;
	}
}
