package com.example.myapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.Helpers.LoginDataHolder;
import com.example.myapplication.R;
import com.example.myapplication.Services.SaveArticleService;
import com.example.myapplication.assignment.Article;
import com.example.myapplication.assignment.Image;
import com.example.myapplication.assignment.ModelManager;
import com.example.myapplication.assignment.exceptions.AuthenticationError;
import com.example.myapplication.assignment.exceptions.ServerCommunicationError;

import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

public class CreateArticleActivity extends Activity {
    private static final int PICK_IMAGE = 1;
    private ImageView mainImageView;
    private ProgressBar progressBar;
    private String encodedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_creation);

        EditText titleText = findViewById(R.id.editTextTitle);
        EditText categoryText = findViewById(R.id.editTextCategory);
        EditText abstractText = findViewById(R.id.editTextAbstract);
        EditText bodyText = findViewById(R.id.editTextBody);
        mainImageView = findViewById(R.id.imageViewMainImage);
        progressBar = findViewById(R.id.progressBar);
        Button selectImageButton = findViewById(R.id.buttonSelectImage);
        Button saveArticleButton = findViewById(R.id.buttonSaveArticle);

        selectImageButton.setOnClickListener(v -> openImagePicker());

        saveArticleButton.setOnClickListener(v -> {
            String title = titleText.getText().toString();
            String category = categoryText.getText().toString();
            String abstractTextValue = abstractText.getText().toString();
            String body = bodyText.getText().toString();

            if (title.isEmpty() || category.isEmpty() || abstractTextValue.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Properties properties = new Properties();
            properties.setProperty("service_url", "http://your.service.url/here");

            Article article = null;
            ModelManager mm = null;
            try {
                article = new Article(new ModelManager(properties), category, title, abstractTextValue, body, "footer", new Date());
            } catch (AuthenticationError e) {
                throw new RuntimeException(e);
            }

            article.setThumbnail(encodedImage);


            if (encodedImage != null) {
                Image mainImage = new Image(mm, 0, "", 0, encodedImage);
                article.setImage(mainImage);
            }

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Execute saveArticle in a background thread
            Article finalArticle = article;
            new Thread(() -> {
                try {
                    SaveArticleService saveService = new SaveArticleService();
                    saveService.saveArticle(LoginDataHolder.getInstance().getAuthType(),
                            LoginDataHolder.getInstance().getApikey(),
                            "https://sanger.dia.fi.upm.es/pmd-task/article",
                            finalArticle);

                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);  // Hide the progress bar
                        Toast.makeText(this, "Article saved successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateArticleActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });

                } catch (ServerCommunicationError e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);  // Hide the progress bar
                        Toast.makeText(this, "Failed to save article.", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                mainImageView.setImageBitmap(bitmap);
                encodeImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        encodedImage = Base64.getEncoder().encodeToString(b);
    }
}
