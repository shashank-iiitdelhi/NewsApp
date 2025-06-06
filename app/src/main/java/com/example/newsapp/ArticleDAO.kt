package com.example.newsapp
import androidx.room.*
import com.example.newsapp.ArticleEntity

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Query("SELECT * FROM saved_articles")
    suspend fun getAllSavedArticles(): List<ArticleEntity>

    @Query("UPDATE saved_articles SET isRead = 1 WHERE id = :articleId")
    suspend fun markArticleAsRead(articleId: Int)

    @Query("DELETE FROM saved_articles WHERE url = :url")
    suspend fun deleteArticleByUrl(url: String)

    @Query("SELECT COUNT(*) FROM saved_articles WHERE title = :title")
    suspend fun countByTitle(title: String): Int

    @Query("DELETE FROM saved_articles WHERE id = :articleId")
    suspend fun deleteArticleById(articleId: Int)

}
