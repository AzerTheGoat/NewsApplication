package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adapter.ArticleCardAdapter;
import com.example.myapplication.Helpers.LoginDataHolder;
import com.example.myapplication.R;
import com.example.myapplication.Services.ArticleService;
import com.example.myapplication.Services.LoginService;
import com.example.myapplication.assignment.Article;
import com.example.myapplication.assignment.ModelManager;
import com.example.myapplication.assignment.exceptions.AuthenticationError;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    private static final int LOGINBUTTON = R.id.menu_login;
    private static final int LOGOUT = R.id.menu_logout;
    private static final int CREATEARTICLE = R.id.menu_create_article;

    private RecyclerView recyclerView;
    private ArticleCardAdapter adapter;
    private ProgressBar progressBar;

    private List<Article> allArticles = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);  // Initialize ProgressBar

        // Initialize RecyclerView with an empty list
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ArticleCardAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Fetch articles and update adapter
        fetchArticles();

        // Hide the welcome text if the user is not logged in
        if (LoginDataHolder.getInstance().getIdUser() == null) {
            ((TextView) findViewById(R.id.welcomeText)).setVisibility(View.GONE);
        }

        // Initialize the menu icon click listener
        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> {
            // Create and show the PopupMenu
            PopupMenu popupMenu = new PopupMenu(this, v);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_items, popupMenu.getMenu());  // Inflate menu

          boolean isLoggedIn = LoginDataHolder.getInstance().isConnected();
          popupMenu.getMenu().findItem(R.id.menu_login).setVisible(!isLoggedIn);
          popupMenu.getMenu().findItem(R.id.menu_logout).setVisible(isLoggedIn);
          popupMenu.getMenu().findItem(R.id.menu_create_article).setVisible(isLoggedIn);


          popupMenu.show();  // Show the popup menu

            // Handle item selection
            popupMenu.setOnMenuItemClickListener(item ->
                    onOptionsItemSelected(item)
            );
        });

        // Set up BottomNavigationView for category selection
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_all) {
                filterArticlesByCategory("All");
                return true;
            }else if(item.getItemId() == R.id.nav_national) {
                filterArticlesByCategory("National");
                return true;
            }else if(item.getItemId() == R.id.nav_economy) {
                filterArticlesByCategory("Economy");
                return true;
            }else if(item.getItemId() == R.id.nav_sports) {
                filterArticlesByCategory("Sports");
                return true;
            }else if(item.getItemId() == R.id.nav_technology) {
                filterArticlesByCategory("Technology");
                return true;
            }
            return false;
        });
    }

    private void filterArticlesByCategory(String category) {
        List<Article> filteredArticles;

        if (category.equals("All")) {
            filteredArticles = allArticles; // Show all articles
        } else {
            filteredArticles = new ArrayList<>();
            for (Article article : allArticles) {
                if (article.getCategory().equalsIgnoreCase(category)) {
                    filteredArticles.add(article);
                }
            }
        }

        adapter.updateArticles(filteredArticles); // Update adapter with filtered articles
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LoginDataHolder.getInstance().isConnected()){
            ((TextView) findViewById(R.id.welcomeText)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.welcomeText)).setText("Welcome " + LoginDataHolder.getInstance().getUsername());
        }
        fetchArticles();
        invalidateOptionsMenu();
    }

    private void fetchArticles() {
        // Show the progress bar while fetching
        progressBar.setVisibility(View.VISIBLE);

        Properties properties = new Properties();
        properties.setProperty("service_url", "http://your.service.url/here");

        try {
            ArticleService articleService = new ArticleService();
            articleService.fetchArticles("https://sanger.dia.fi.upm.es/pmd-task/articles", new ModelManager(properties), new ArticleService.OnArticlesFetchedListener() {
                @Override
                public void onSuccess(List<Article> articles) {
                    runOnUiThread(() -> {
                        adapter.updateArticles(articles);  // Update adapter with fetched articles
                        allArticles = articles;  // Save all articles for filtering
                        progressBar.setVisibility(View.GONE);  // Hide progress bar after fetching
                    });
                }

                @Override
                public void onError(Exception error) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);  // Hide progress bar in case of error
                        // Handle error, e.g., show a message to the user
                        Toast.makeText(MainActivity.this, "Error fetching articles", Toast.LENGTH_SHORT).show();
                    });
                }
            });

        } catch (AuthenticationError e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == LOGINBUTTON) {
            Toast.makeText(this, "Login clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }else if (item.getItemId() == CREATEARTICLE) {
            Toast.makeText(this, "Create article clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, CreateArticleActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == LOGOUT) {
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
            // Perform logout action
            LoginDataHolder.getInstance().logout();
            findViewById(R.id.welcomeText).setVisibility(View.GONE);
            invalidateOptionsMenu();
            fetchArticles();
            return true;
        }  else {
            return super.onOptionsItemSelected(item);
        }
    }
}
