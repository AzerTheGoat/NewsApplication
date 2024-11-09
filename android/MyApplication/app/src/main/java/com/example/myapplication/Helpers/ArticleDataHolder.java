package com.example.myapplication.Helpers;

import com.example.myapplication.assignment.Article;

public class ArticleDataHolder {
    private static ArticleDataHolder instance;
    private Article article;

    private ArticleDataHolder() {}

    public static synchronized ArticleDataHolder getInstance() {
        if (instance == null) {
            instance = new ArticleDataHolder();
        }
        return instance;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }
}
