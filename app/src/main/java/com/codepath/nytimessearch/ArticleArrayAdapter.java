package com.codepath.nytimessearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nytimessearch.activities.ArticleActivity;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by selinabing on 6/20/16.
 */
public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder> {

    boolean isTopStory;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.ivImage) ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            tvTitle.setOnClickListener(this);
            ivImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // create an intent to display article
            Intent i = new Intent(v.getContext(), ArticleActivity.class);
            // display the article
            Article article = articles.get(getLayoutPosition());
            // pass in article to intent
            i.putExtra("article", Parcels.wrap(article));
            // launch activity
            v.getContext().startActivity(i);
        }
    }

    private List<Article> articles;

    public ArticleArrayAdapter(List<Article> articles) {
        this.articles = articles;

    }

    @Override
    public ArticleArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleArrayAdapter.ViewHolder viewHolder, int position) {
        //get data model based on position
        Article article = articles.get(position);

        TextView tvTitle = viewHolder.tvTitle;
        tvTitle.setText(article.getHeadline());

        ImageView ivImage = viewHolder.ivImage;
        ivImage.setImageResource(0);
        String thumbnail = article.getThumbNail();
        if(!TextUtils.isEmpty(thumbnail)) {
            Glide.with(ivImage.getContext()).load(thumbnail).bitmapTransform(new RoundedCornersTransformation(ivImage.getContext(),10,2)).placeholder(R.drawable.placeholder_img).into(ivImage);
        } else {
            Glide.with(ivImage.getContext()).load(R.drawable.not_available_placeholder_img).placeholder(R.drawable.placeholder_img).into(ivImage);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setIsTopStory(boolean isTopStory){
        this.isTopStory=isTopStory;
    }

    public boolean getIsTopStory(){
        return this.isTopStory;
    }

    // Clean all elements of the recycler
    public void clear() {
        articles.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Article> artl) {
        articles.addAll(artl);
        notifyDataSetChanged();
    }
/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for position
        Article article = this.getItem(position);
        // check to see if existing view is being reused for recycling
        // not using a recycled view -> inflate the layout
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }
        // find the imageview, clear out recycled image from last time
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);
        imageView.setImageResource(0);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadline());

        //populate the thumbnail image
        //remote download image in the background
        String thumbnail = article.getThumbNail();
        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getContext()).load(thumbnail).into(imageView);
        }

        return convertView;
    }*/
}
