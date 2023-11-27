package com.example.taskmaster.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskState;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EditTask extends AppCompatActivity {

    public static final String TAG = "editTASKActivity";
    private CompletableFuture<Task> TASKCompletableFuture = null;
    private CompletableFuture<List<Team>> TeamFuture = null;
    private Task TASKToEdit = null;
    private EditText nameEditText;
    private EditText descriptionEditText;

    private Spinner StateCategorySpinner = null;

    private Spinner TEAMSpinner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        TASKCompletableFuture = new CompletableFuture<>();
        TeamFuture = new CompletableFuture<>();

        setUpEditableUIElement();
        setUpSaveButton();
        setUpDeleteButton();

    }


    private void setUpEditableUIElement() {
        Intent callingIntent = getIntent();
        String TASKID = null;

        if (callingIntent != null) {
            TASKID = callingIntent.getStringExtra(MainActivity.Main_ID_TAG);
        }

        String tASKId2 = TASKID; //ugly hack just to fix lambda processing

        Amplify.API.query(
                ModelQuery.list(Task.class),
                success ->
                {
                    Log.i(TAG, "Read Tasks Successfully");

                    for (Task databasetask : success.getData()) {
                        if (databasetask.getId().equals(tASKId2)) {
                            TASKCompletableFuture.complete(databasetask);
                        }
                    }

                    runOnUiThread(() ->
                    {
                        //update ui element
                    });
                },
                failure -> Log.i(TAG, "Did not read task successfully")
        );

        try {
            TASKToEdit = TASKCompletableFuture.get();
        } catch (InterruptedException ie) {
            Log.e(TAG, "InterruptedException while getting product");
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            Log.e(TAG, "ExecutionException while getting product");
        }

        nameEditText = ((EditText) findViewById(R.id.editTextName));
        nameEditText.setText(TASKToEdit.getName());
        descriptionEditText = ((EditText) findViewById(R.id.editTextdescription));
        descriptionEditText.setText(TASKToEdit.getDescription());
        setUpSpinners();
    }


    private void setUpSpinners() {
        TEAMSpinner = (Spinner) findViewById(R.id.editTeamSpinner);

        Amplify.API.query(
                ModelQuery.list(Team.class),
                success ->
                {
                    Log.i(TAG, "Read TEAMSEDIT successfully!");
                    ArrayList<String> contactNames = new ArrayList<>();
                    ArrayList<Team> TEAMS = new ArrayList<>();
                    for (Team team : success.getData()) {
                        TEAMS.add(team);
                        contactNames.add(team.getName());
                    }
                    TeamFuture.complete(TEAMS);

                    runOnUiThread(() ->
                    {
                        TEAMSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                contactNames));
                        TEAMSpinner.setSelection(getSpinnerIndex(TEAMSpinner, TASKToEdit.getTeamPerson().getName()));
                    });
                },
                failure -> {
                    TeamFuture.complete(null);
                    Log.i(TAG, "Did not read Teams successfully!");
                }
        );
//EXTRSCT FROM DATA BASE
        StateCategorySpinner = (Spinner) findViewById(R.id.editStateCategorySpinner);
        StateCategorySpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskState.values()));
        StateCategorySpinner.setSelection(getSpinnerIndex(StateCategorySpinner, TASKToEdit.getState().toString()));


    }

    private int getSpinnerIndex(Spinner spinner, String stringValueToCheck) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(stringValueToCheck)) {
                return i;
            }
        }

        return 0;
    }

    private void setUpSaveButton() {
        Button saveButton = (Button) findViewById(R.id.updateButton);
        saveButton.setOnClickListener(v ->
        {
            List<Team> TEAMS = null;
            String teamToSaveString = TEAMSpinner.getSelectedItem().toString();
            try {
                TEAMS = TeamFuture.get();
            } catch (InterruptedException ie) {
                Log.e(TAG, "InterruptedException while getting product");
                Thread.currentThread().interrupt();
            } catch (ExecutionException ee) {
                Log.e(TAG, "ExecutionException while getting product");
            }
            assert TEAMS != null;
            Team TEAMToSave = TEAMS.stream().filter(c -> c.getName().equals(teamToSaveString)).findAny().orElseThrow(RuntimeException::new);
            Task TASKToSave = Task.builder()
                    .name(nameEditText.getText().toString())
                    .id(TASKToEdit.getId())
                    .description(descriptionEditText.getText().toString())
                    .teamPerson(TEAMToSave)
                    .state(productCategoryFromString(StateCategorySpinner.getSelectedItem().toString()))
                    .build();

            Amplify.API.mutate(
                    ModelMutation.update(TASKToSave),  // making a GraphQL request to the cloud
                    successResponse ->
                    {
                        Log.i(TAG, "EditProductActivity.onCreate(): edited a product successfully");
                        // TODO: Display a Snackbar
//                        Snackbar.make(findViewById(R.id.taskdetails), "Product saved!", Snackbar.LENGTH_SHORT).show();
                    },  // success callback
                    failureResponse -> Log.i(TAG, "EditProductActivity.onCreate(): failed with this response: " + failureResponse)  // failure callback
            );
        });
    }

    //Extract the enum value
    public static TaskState productCategoryFromString(String inputProductCategoryText) {
        for (TaskState taskStates : TaskState.values()) {
            if (taskStates.toString().equals(inputProductCategoryText)) {
                return taskStates;
            }
        }
        return null;

    }

    private void setUpDeleteButton() {
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            Amplify.API.mutate(
                    ModelMutation.delete(TASKToEdit),
                    successResponse ->
                    {
                        Log.i(TAG, "EditProductActivity.onCreate(): deleted a product successfully");
                        Intent goToMain = new Intent(EditTask.this, MainActivity.class);
                        startActivity(goToMain);
                    },
                    failureResponse -> Log.i(TAG, "EditProductActivity.onCreate(): failed with this response: " + failureResponse)
            );
        });
    }
}