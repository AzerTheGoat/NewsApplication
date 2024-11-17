package com.example.myapplication.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activities.ArticleDetailActivity;
import com.example.myapplication.Activities.EditArticleActivity;
import com.example.myapplication.Helpers.ArticleDataHolder;
import com.example.myapplication.Helpers.LoginDataHolder;
import com.example.myapplication.R;
import com.example.myapplication.Services.DeleteArticleService;
import com.example.myapplication.assignment.Article;
import com.example.myapplication.assignment.exceptions.ServerCommunicationError;

import java.util.List;

public class ArticleCardAdapter extends RecyclerView.Adapter<ArticleCardAdapter.ArticleViewHolder> {
    private List<Article> articles;
    private Context context;

    public ArticleCardAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        this.context = context;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.article_card, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.title.setText(article.getTitleText());
        holder.category.setText(article.getCategory());
        holder.abstractText.setText(article.getAbstractText());

        try {
            if (article.getThumbnail() != null && article.getImage() != null) {
                String base64Image = article.getImage().getImage();
                holder.setImage(base64Image);
                holder.articleImage.setVisibility(View.VISIBLE);
            } else {
              holder.articleImage.setImageResource(R.drawable.default_image);            }
        } catch (ServerCommunicationError e) {
            throw new RuntimeException(e);
        }

        // Conditionally show the Edit and Delete buttons based on login status
        if (LoginDataHolder.getInstance().isConnected()) {
            holder.actionButtons.setVisibility(View.VISIBLE);
        } else {
            holder.actionButtons.setVisibility(View.GONE);
        }

        // Add click listener for the Edit button
        if(article.getUsername().equals(LoginDataHolder.getInstance().getUsername())) {

            holder.buttonEdit.setVisibility(View.VISIBLE);
            holder.buttonDelete.setVisibility(View.VISIBLE);

            holder.buttonEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditArticleActivity.class);
                intent.putExtra("article_id", article.getId());
                context.startActivity(intent);
            });

            // Add click listener for the Delete button with confirmation popup
            holder.buttonDelete.setOnClickListener(v -> {
                // Create an AlertDialog to confirm deletion
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this article?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            // Start a new thread for the deletion process
                            new Thread(() -> {
                                DeleteArticleService deleteArticleService = new DeleteArticleService();
                                try {
                                    deleteArticleService.deleteArticle(
                                            LoginDataHolder.getInstance().getAuthType(),
                                            LoginDataHolder.getInstance().getApikey(),
                                            "https://sanger.dia.fi.upm.es/pmd-task/article/",
                                            article.getId()
                                    );

                                    // Notify user on successful deletion
                                    ((Activity) holder.itemView.getContext()).runOnUiThread(() ->
                                            Toast.makeText(holder.itemView.getContext(), "Article deleted successfully!", Toast.LENGTH_SHORT).show()
                                    );

                                    // Remove the article from the list and refresh the adapter
                                    articles.remove(position);
                                    ((Activity) holder.itemView.getContext()).runOnUiThread(this::notifyDataSetChanged);

                                } catch (ServerCommunicationError e) {
                                    e.printStackTrace();
                                    ((Activity) holder.itemView.getContext()).runOnUiThread(() ->
                                            Toast.makeText(holder.itemView.getContext(), "Failed to delete article.", Toast.LENGTH_SHORT).show()
                                    );
                                }
                            }).start();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Dismiss the dialog if "Cancel" is clicked
                            dialog.dismiss();
                        })
                        .show();
            });
        } else {
            holder.buttonEdit.setVisibility(View.GONE);
            holder.buttonDelete.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            ArticleDataHolder.getInstance().setArticle(article);
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            context.startActivity(intent);
        });
    }


    public void updateArticles(List<Article> articles) {
        this.articles.clear();
        this.articles.addAll(articles);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView category;
        TextView abstractText;
        ImageView articleImage;
        LinearLayout actionButtons;
        Button buttonEdit, buttonDelete;
        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.articleTitle);
            category = itemView.findViewById(R.id.articleCategory);
            abstractText = itemView.findViewById(R.id.articleAbstract);
            articleImage = itemView.findViewById(R.id.articleImage);

            actionButtons = itemView.findViewById(R.id.actionButtons);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
        public void setImage(String base64Image) {
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (decodedByte != null) {
                    articleImage.setImageBitmap(decodedByte);
                } else {
                    articleImage.setImageResource(R.drawable.ic_launcher_background); // Fallback if decoding fails
                }
            } else {
                articleImage.setImageResource(R.drawable.ic_launcher_background); // Fallback for empty images
            }
        }

    }
}
