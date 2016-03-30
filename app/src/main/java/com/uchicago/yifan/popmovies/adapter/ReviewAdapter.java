package com.uchicago.yifan.popmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uchicago.yifan.popmovies.R;
import com.uchicago.yifan.popmovies.model.Review;

import java.util.List;

/**
 * Created by Yifan on 3/29/16.
 */
public class ReviewAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final Review mLock = new Review();

    private List<Review> mObjects;

    public ReviewAdapter(Context context, List<Review> objects) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObjects = objects;
    }

    public Context getContext() {
        return mContext;
    }

    public void add(Review object) {
        synchronized (mLock) {
            mObjects.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mLock) {
            mObjects.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Review getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.item_movie_review, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final Review review = getItem(position);

        viewHolder = (ViewHolder) view.getTag();

        viewHolder.authorView.setText(review.getAuthor());
        viewHolder.contentView.setText(review.getContent());

        return view;
    }

    public static class ViewHolder {
        public final TextView authorView;
        public final TextView contentView;

        public ViewHolder(View view) {
            authorView = (TextView) view.findViewById(R.id.review_author);
            contentView = (TextView) view.findViewById(R.id.review_content);
        }
    }

}