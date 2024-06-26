package com.example.reciperecomendation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {
    private String[] ingredients = {
            "pasta", "eggs", "cheese", "bacon", "black pepper",
            "pizza dough", "tomatoes", "mozzarella", "basil",
            "bread", "lettuce", "tomato", "mayonnaise",
            "butter", "cucumber", "feta cheese", "olives", "olive oil"
    };
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pb=new ProgressBar(home.this);
        AppBarLayout ab=findViewById(R.id.appbar);
        pb.setVisibility(View.GONE);
        ab.addView(pb);
        LinearLayout checkboxContainer = findViewById(R.id.checkbox_container);
        for (String ingredient : ingredients) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(ingredient);
            checkBox.setTextColor(getResources().getColor(R.color.black));
            checkboxContainer.addView(checkBox);
        }
        Button submitButton = findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                List<String> selectedIngredients = new ArrayList<>();
                Toast.makeText(home.this,"Clicked",Toast.LENGTH_LONG).show();
                for (int i = 0; i < checkboxContainer.getChildCount(); i++) {
                    CheckBox checkBox = (CheckBox) checkboxContainer.getChildAt(i);
                    if (checkBox.isChecked()) {
                        selectedIngredients.add(checkBox.getText().toString());
                    }
                }
                if (selectedIngredients.isEmpty()) {
                    Toast.makeText(home.this, "Please select at least one ingredient", Toast.LENGTH_SHORT).show();
                } else {
                    new RecommendRecipesTask().execute(selectedIngredients.toArray(new String[0]));
                    selectedIngredients.clear();
                    pb.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private class RecommendRecipesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... ingredients) {
            try {
                URL url = new URL("https://eminently-rare-pegasus.ngrok-free.app/recommend");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                JSONArray jsonIngredients = new JSONArray();
                for (String ingredient : ingredients) {
                    jsonIngredients.put(ingredient);
                }
                jsonParam.put("ingredients", jsonIngredients);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }

                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONArray recommendations = jsonResponse.getJSONArray("recommendations");

                    StringBuilder recommendationsText = new StringBuilder("Recommendations:\n");

                        JSONObject recipe = recommendations.getJSONObject(0);
                        recommendationsText.append(recipe.getString("recipe_name"))
                                .append("\nIngredients: ")
                                .append(recipe.getString("ingredients"))
                                .append("\nInstructions: ")
                                .append(recipe.getString("instructions"))
                                .append("\n\n");
                        pb.setVisibility(View.GONE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
                    builder.setTitle("First Recommendation");
                    builder.setMessage(recommendationsText);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(home.this, "Error parsing recommendations", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(home.this, "Error getting recommendations", Toast.LENGTH_SHORT).show();
            }
        }
    }
}