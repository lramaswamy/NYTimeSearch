package com.example.lramaswamy.nytimesearch.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lramaswamy.nytimesearch.Activities.ArticleActivity;
import com.example.lramaswamy.nytimesearch.Models.Article;
import com.example.lramaswamy.nytimesearch.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lramaswamy on 10/18/16.
 */

public class ArticleRecyclerAdapter extends RecyclerView.Adapter<ArticleRecyclerAdapter.ViewHolder>{


    private List<Article> mArticles;
    private Context mContext;

    public ArticleRecyclerAdapter(List<Article> articles, Context context) {
        mArticles = articles;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //Inflate the layout
        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        //Return a new holder instance
        ViewHolder holder =  new ViewHolder(context, articleView, mArticles);
        return holder;
    }

    @Override
    public void onBindViewHolder(ArticleRecyclerAdapter.ViewHolder holder, int position) {
        //get the data model for this position
        Article article = mArticles.get(position);

        holder.tvHeadlines.setText(article.getHeadline());
//        if(article.getThumbnail()== null) {
//            Picasso.with(mContext).load(R.mipmap.no_image).into(holder.imageView);
//        } else
            Picasso.with(mContext).load(article.getThumbnail()).placeholder(R.mipmap.no_image).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView tvHeadlines;
        Context context;
        List<Article> mArticles;

        public ViewHolder(Context context, View itemView, List<Article> articles) {
            super(itemView);

            this.context = context;
            imageView = (ImageView) itemView.findViewById(R.id.ivImage);
            tvHeadlines = (TextView) itemView.findViewById(R.id.tvTitle);
            this.mArticles = articles;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Intent activityIntent = new Intent(context, ArticleActivity.class);
            Article article = mArticles.get(getAdapterPosition());
            activityIntent.putExtra("url", article.getWebUrl());
            context.startActivity(activityIntent);
        }
    }

}
