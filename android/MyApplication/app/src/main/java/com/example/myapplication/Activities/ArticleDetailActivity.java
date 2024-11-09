package com.example.myapplication.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Helpers.ArticleDataHolder;
import com.example.myapplication.Helpers.LoginDataHolder;
import com.example.myapplication.R;
import com.example.myapplication.Services.FetchArticleDetailsService;
import com.example.myapplication.assignment.Article;
import com.example.myapplication.assignment.ModelManager;
import com.example.myapplication.assignment.exceptions.AuthenticationError;
import com.example.myapplication.assignment.exceptions.ServerCommunicationError;

import java.util.Properties;

public class ArticleDetailActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView articleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // Initialize UI components
        progressBar = findViewById(R.id.progressBar); // Ensure this is in your layout
        articleImage = findViewById(R.id.articleImage);

        // Display the progress bar while loading data
        progressBar.setVisibility(View.VISIBLE);

        // Fetch article details in background
        new FetchArticleTask().execute();
    }

    private Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private class FetchArticleTask extends AsyncTask<Void, Void, Article> {
        @Override
        protected Article doInBackground(Void... voids) {
            try {
                // Get login and service details
                String authType = LoginDataHolder.getInstance().getAuthType();
                String apikey = LoginDataHolder.getInstance().getApikey();
                String requestUrl = "https://sanger.dia.fi.upm.es/pmd-task/article/";
                int articleId = ArticleDataHolder.getInstance().getArticle().getId(); // assuming ID is stored here
                Properties properties = new Properties();
                properties.setProperty("service_url", "http://your.service.url/here");
                ModelManager mm = new ModelManager(properties);

                // Fetch article using service
                FetchArticleDetailsService fetchService = new FetchArticleDetailsService();
                return fetchService.getArticle(authType, apikey, requestUrl, articleId, mm);
            } catch (ServerCommunicationError e) {
                e.printStackTrace();
                return null;
            } catch (AuthenticationError e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Article article) {
            progressBar.setVisibility(View.GONE);

            if (article != null) {
                // Update the UI with article details
                ((TextView) findViewById(R.id.articleTitle)).setText(article.getTitleText());
                ((TextView) findViewById(R.id.articleCategory)).setText(article.getCategory());
                ((TextView) findViewById(R.id.articleAbstract)).setText(article.getAbstractText());
                ((TextView) findViewById(R.id.articleBody)).setText(article.getBodyText());
                ((TextView) findViewById(R.id.articleFooter)).setText(article.getFooterText());
                ((TextView) findViewById(R.id.articleModifiedDate)).setText("Last modified on: " + article.getUpdate_date());
                ((TextView) findViewById(R.id.articleModifiedBy)).setText("Modified by: " + article.getUsername());

                try {
                    if (article.getThumbnail() != null && article.getImage() != null) {
                        String base64Image = article.getImage().getImage();
                        articleImage.setImageBitmap(decodeBase64(base64Image));
                    } else {
                        articleImage.setVisibility(View.GONE);
                    }
                } catch (ServerCommunicationError e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(ArticleDetailActivity.this, "Failed to load article details.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
