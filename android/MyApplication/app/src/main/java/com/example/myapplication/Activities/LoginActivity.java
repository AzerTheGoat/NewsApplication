package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Helpers.LoginDataHolder;
import com.example.myapplication.R;
import com.example.myapplication.Services.LoginService;
import com.example.myapplication.assignment.exceptions.AuthenticationError;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        // Initialize LoginService
        loginService = new LoginService();

        // Set up login button click listener
        loginButton.setOnClickListener(v -> onLoginClicked());
    }

    private void onLoginClicked() {
        // Get username and password from input fields
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar while logging in
        progressBar.setVisibility(View.VISIBLE);

        // Call the login method in a background thread
        new Thread(() -> {
            try {
                loginService.login("https://sanger.dia.fi.upm.es/pmd-task/login", username, password);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE); // Hide the progress bar
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            } catch (AuthenticationError e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE); // Hide the progress bar
                    Toast.makeText(this, "Authentication failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}
