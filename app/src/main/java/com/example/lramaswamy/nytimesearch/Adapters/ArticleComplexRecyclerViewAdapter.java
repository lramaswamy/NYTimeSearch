package com.example.lramaswamy.nytimesearch.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lramaswamy.nytimesearch.Activities.ArticleActivity;
import com.example.lramaswamy.nytimesearch.Models.Article;
import com.example.lramaswamy.nytimesearch.R;

import java.util.List;

/**
 * Created by lramaswamy on 10/23/16.
 */

public class ArticleComplexRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // The items to display in your RecyclerView
    private List<Article> articles;

    private final int HEADLINE = 0, IMAGEWITHTEXT = 1;


    // Provide a suitable constructor (depends on the kind of dataset)
    public ArticleComplexRecyclerViewAdapter(List<Article> articles) {
        this.articles = articles;
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return articles.size();
    }

    @Override
    public int getItemViewType(int position) {
        String thumbnail = articles.get(position).getThumbnail();
        if ( thumbnail != null &&
                !thumbnail.isEmpty()) {
            return IMAGEWITHTEXT;
        } else
            return HEADLINE;
        }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case HEADLINE:
                View v1 = inflater.inflate(R.layout.article_text_only, parent, false);
                viewHolder = new ViewHolder1(parent.getContext(), v1, articles);
                break;
            case IMAGEWITHTEXT:
                View v2 = inflater.inflate(R.layout.article_image_text, parent, false);
                viewHolder = new ViewHolder2(parent.getContext(), v2, articles);
                break;
//            default:
//                View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
//                viewHolder = new RecyclerViewSimpleTextViewHolder(v);
//                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case HEADLINE:
                ViewHolder1 vh1 = (ViewHolder1) holder;
                configureViewHolder1(vh1, position);
                break;
            case IMAGEWITHTEXT:
                ViewHolder2 vh2 = (ViewHolder2) holder;
                configureViewHolder2(vh2, position);
                break;
//            default:
//                RecyclerViewSimpleTextViewHolder vh = (RecyclerViewSimpleTextViewHolder) viewHolder;
//                configureDefaultViewHolder(vh, position);
//                break;
        }
    }

    private void configureViewHolder1(ViewHolder1 holder1, int position) {
        Article article = articles.get(position);
        if (article != null) {
            holder1.tvHeadlines.setText(article.getHeadline());
        }
    }

    private void configureViewHolder2(ViewHolder2 holder2, int position) {
        //get the data model for this position
        Article article = articles.get(position);

        Drawable noImage = AppCompatResources.getDrawable(holder2.mContext2, R.drawable.camera_off);
        holder2.tvHeadlines.setText(article.getHeadline());
        Glide.with(holder2.mContext2)
                .load(article.getThumbnail())
                .placeholder(noImage).into(holder2.imageView);
    }

    //ViewHolder 1 contains only Headlines, no image
    public static class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvHeadlines;
        Context mContext1;
        List<Article> mArticles1;

        public ViewHolder1(Context context, View itemView, List<Article> articles) {
            super(itemView);

            this.mContext1 = context;
            tvHeadlines = (TextView) itemView.findViewById(R.id.tvTitle1);
            this.mArticles1 = articles;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Intent activityIntent = new Intent(mContext1, ArticleActivity.class);
            Article article = mArticles1.get(getAdapterPosition());
            activityIntent.putExtra("url", article.getWebUrl());
            activityIntent.putExtra("title", article.getHeadline());
            mContext1.startActivity(activityIntent);
        }
    }
    
    public static class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView tvHeadlines;
        Context mContext2;
        List<Article> mArticles2;

        public ViewHolder2(Context context, View itemView, List<Article> articles) {
            super(itemView);

            this.mContext2 = context;
            imageView = (ImageView) itemView.findViewById(R.id.ivImage);
            tvHeadlines = (TextView) itemView.findViewById(R.id.tvTitle);
            this.mArticles2 = articles;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Intent activityIntent = new Intent(mContext2, ArticleActivity.class);
            Article article = mArticles2.get(getAdapterPosition());
            activityIntent.putExtra("url", article.getWebUrl());
            activityIntent.putExtra("title", article.getHeadline());
            mContext2.startActivity(activityIntent);
        }
    }



}
