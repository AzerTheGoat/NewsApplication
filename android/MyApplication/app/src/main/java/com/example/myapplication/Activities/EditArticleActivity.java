package com.example.myapplication.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Helpers.LoginDataHolder;
import com.example.myapplication.R;
import com.example.myapplication.Services.FetchArticleDetailsService;
import com.example.myapplication.Services.SaveArticleService;
import com.example.myapplication.assignment.Article;
import com.example.myapplication.assignment.ModelManager;
import com.example.myapplication.assignment.exceptions.AuthenticationError;
import com.example.myapplication.assignment.exceptions.ServerCommunicationError;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class EditArticleActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private EditText editTitle, editSubtitle, editCategory, editAbstract, editBody;
    private ImageView imageView;
    private Button btnChangeImage, btnSaveChanges, btnCancel;
    private Article currentArticle;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);

        // Initialize views
        editTitle = findViewById(R.id.editTitle);
        editSubtitle = findViewById(R.id.editSubtitle);
        editCategory = findViewById(R.id.editCategory);
        editAbstract = findViewById(R.id.editAbstract);
        editBody = findViewById(R.id.editBody);
        imageView = findViewById(R.id.imageView);
        btnChangeImage = findViewById(R.id.btnChangeImage2);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);

        // Fetch the article data and populate fields
        fetchArticleData();

        // Set listeners
        btnChangeImage.setOnClickListener(v -> openImagePicker());
        btnSaveChanges.setOnClickListener(v -> saveArticleChanges());
        btnCancel.setOnClickListener(v -> finish());
    }
    private void fetchArticleData() {
        int articleId = getIntent().getIntExtra("article_id", -1);
        if (articleId != -1) {
            new Thread(() -> {
                try {
                    Properties properties = new Properties();
                    properties.setProperty("service_url", "http://your.service.url/here");

                    FetchArticleDetailsService fetchService = new FetchArticleDetailsService();
                    currentArticle = fetchService.getArticle(
                            LoginDataHolder.getInstance().getAuthType(),
                            LoginDataHolder.getInstance().getApikey(),
                            "https://sanger.dia.fi.upm.es/pmd-task/article/",
                            articleId,
                            new ModelManager(properties)
                    );

                    runOnUiThread(this::populateFields);
                } catch (ServerCommunicationError | AuthenticationError e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Error fetching article", Toast.LENGTH_SHORT).show());
                }
            }).start();
        }
    }

  private void populateFields() {
    if (currentArticle != null) {
      editTitle.setText(currentArticle.getTitleText());
      editSubtitle.setText(currentArticle.getSubtitleText());
      editCategory.setText(currentArticle.getCategory());
      editAbstract.setText(currentArticle.getAbstractText());
      editBody.setText(currentArticle.getBodyText());

      // Load image if applicable or set default image
      if (currentArticle.getThumbnail() != null && !currentArticle.getThumbnail().isEmpty()) {
        try {
          imageView.setImageBitmap(decodeBase64(currentArticle.getImage().getImage()));
        } catch (ServerCommunicationError e) {
          throw new RuntimeException(e);
        }
      } else {
        // Set a default image if the article doesn't have a thumbnail
        imageView.setImageResource(R.drawable.default_image);
      }
      encodedImage = currentArticle.getThumbnail();
    }
  }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imageView.setImageBitmap(bitmap);
                encodeImage(bitmap);  // Encode the new image
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
    }

    private void saveArticleChanges() {
        // Update article object with new data
        currentArticle.setTitleText(editTitle.getText().toString());
        currentArticle.setSubtitleText(editSubtitle.getText().toString());
        currentArticle.setCategory(editCategory.getText().toString());
        currentArticle.setAbstractText(editAbstract.getText().toString());
        currentArticle.setBodyText(editBody.getText().toString());

        if (encodedImage != null) {
            currentArticle.setThumbnail(encodedImage);  // Set the new encoded image if updated
        }

        new Thread(() -> {
            try {
                SaveArticleService saveService = new SaveArticleService();
                saveService.saveArticle(
                        LoginDataHolder.getInstance().getAuthType(),
                        LoginDataHolder.getInstance().getApikey(),
                        "https://sanger.dia.fi.upm.es/pmd-task/article",
                        currentArticle
                );
                runOnUiThread(() -> {
                    Toast.makeText(this, "Article updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (ServerCommunicationError e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error updating article", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
