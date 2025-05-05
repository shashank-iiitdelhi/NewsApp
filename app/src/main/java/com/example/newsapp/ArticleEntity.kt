package com.example.newsapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_articles")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String?,
    val content: String?,
    val description:String?,
    val url: String?,
    val urlToImage: String?

)
