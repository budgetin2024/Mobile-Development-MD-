package com.example.budgee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.json.Articles
import com.bumptech.glide.Glide

class NewsAdapter(private val articles: List<Articles>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.title.text = article.title
        holder.description.text = article.description

    }

    override fun getItemCount(): Int {
        return articles.size
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.news_title)
        val description: TextView = itemView.findViewById(R.id.news_description)
    }
}
