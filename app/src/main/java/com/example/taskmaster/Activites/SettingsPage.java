package com.example.taskmaster.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

//import com.example.taskmaster.Adapter.ViewAdapter;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SettingsPage extends AppCompatActivity {
public static final String USERNAME_TAG="username";
    public static final String TEAM_TAG="teamName";

     SharedPreferences sharedPreferences;
    CompletableFuture<List<Team>> teamFuture = new CompletableFuture<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        setupSpinners();

        ImageView imageView=findViewById(R.id.arrowImageSetting);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentArrow = new Intent(SettingsPage.this, MainActivity.class);
                startActivity(intentArrow);

            }
        });


                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Button Savebutton=findViewById(R.id.Savebutton);
        Savebutton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongViewCast")
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor PreferenceEditor=sharedPreferences.edit();
                EditText usernameeditor=findViewById(R.id.editTextUserName);
                String usernameString=usernameeditor.getText().toString();
                 Spinner teamSpinnerMain = findViewById(R.id.teamlist);
          String teamString=teamSpinnerMain.getSelectedItem().toString();

             PreferenceEditor.putString(USERNAME_TAG,usernameString);

                PreferenceEditor.putString(TEAM_TAG,teamString);

                PreferenceEditor.apply();


                Snackbar.make(findViewById(R.id.settingPage),"UserName AND Team name Saved",Snackbar.LENGTH_SHORT).show();
            }
        });


    }
    private void setupSpinners() {
       Spinner teamsSpinner = (Spinner) findViewById(R.id.teamlist);
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success ->
                {
                    Log.i("SettingsActivity", "Read Team Successfully");
                    ArrayList<String> teamName = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for(Team team: success.getData()){
                        teams.add(team);
                        teamName.add(team.getName());
                        Log.d("SettingsActivity", "setupSpinners() returned: " + team.getName());
                    }
                    teamFuture.complete(teams);
                    runOnUiThread(() ->
                    {
                        teamsSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                (android.R.layout.simple_spinner_item),
                                teamName
                        ));
                    });
                },
                failure-> {
                    teamFuture.complete(null);
                    Log.i("SettingsActivity", "Did not read contacts successfully");
                }
        );
    }

    }


