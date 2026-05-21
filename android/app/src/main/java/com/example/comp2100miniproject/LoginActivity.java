package com.example.comp2100miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dao.AllReactions;
import dao.RandomContentGenerator;
import userstate.AdminState;
import userstate.StateManager;

public class LoginActivity extends AppCompatActivity {

    // Guards one-time app-wide initialisation so it doesn't repeat if the user
    // logs out and the launcher activity is recreated.
    private static boolean dataInitialised = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If state was retained across a process-survived recreation and the user
        // is already logged in, skip straight to the home screen.
        if (StateManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // App data bootstrap (moved here from MainActivity since this is now the launcher).
        if (!dataInitialised) {
            AllReactions.getAllReactions();
            AllReactions.getAllUserReactions();
            RandomContentGenerator.populateRandomData();
            dataInitialised = true;
        }

        EditText usernameField   = findViewById(R.id.editTextUsername);
        EditText passwordField   = findViewById(R.id.editTextPassword);
        TextView errorText       = findViewById(R.id.textViewError);
        Button   loginButton     = findViewById(R.id.buttonLogin);
        Button   registerButton  = findViewById(R.id.buttonRegister);

        loginButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                showError(errorText, "Please enter both a username and a password.");
                return;
            }

            if (StateManager.login(username, password)) {
                clearError(errorText);
                goToHome();
            } else {
                showError(errorText, "Incorrect username or password.");
            }
        });

        registerButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                showError(errorText, "Please enter both a username and a password.");
                return;
            }

            // StateManager.register() returns true when the GuestState transitions to a
            // MemberState — i.e. the new user is now logged in. No second login call needed.
            if (StateManager.register(username, password)) {
                clearError(errorText);
                goToHome();
            } else {
                showError(errorText,
                        "Could not register. Usernames must be 4–20 alphanumeric characters " +
                                "and passwords at least 4 characters. The username may already be taken.");
            }
        });
    }

    private void showError(TextView errorText, String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void clearError(TextView errorText) {
        errorText.setText("");
        errorText.setVisibility(View.GONE);
    }

    private void goToHome() {
        // Admins and members currently share MainActivity. If/when an AdminActivity
        // exists, branch here on (StateManager.getState() instanceof AdminState).
        if (StateManager.getState() instanceof AdminState) {
            // TODO: route to admin-only screen once it exists.
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}