package com.example.taskmaster.Activites;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskState;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TaskDetailPage extends AppCompatActivity {

    private CompletableFuture<Task> TASKCompletableFuture = null;
    private CompletableFuture<List<Team>> TeamFuture = null;
    private Task TASKToEdit = null;
    private EditText nameEditText;
    private EditText descriptionEditText;

    private Spinner StateCategorySpinner = null;

    private Spinner TEAMSpinner = null;

    private String s3ImageKey = "";

    public static final String TAG = "taskDetailsActivity";
    ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_page);

        TASKCompletableFuture = new CompletableFuture<>();
        TeamFuture = new CompletableFuture<>();
        activityResultLauncher = getImagePickingActivityResultLauncher();
        setUpEditableUIElement();
        setUpSaveButton();
        setUpDeleteButton();
        setUpAddImageButton();
        setUpDeleteImageButton();
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
            Log.e(TAG, "InterruptedException while getting task");
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            Log.e(TAG, "ExecutionException while getting task");
        }

        nameEditText = ((EditText) findViewById(R.id.editTextName));
        nameEditText.setText(TASKToEdit.getName());
        descriptionEditText = ((EditText) findViewById(R.id.editTextdescription));
        descriptionEditText.setText(TASKToEdit.getDescription()); s3ImageKey = TASKToEdit.getProductImageS3Key();


        if (s3ImageKey != null && !s3ImageKey.isEmpty())
        {
            Amplify.Storage.downloadFile(
                    s3ImageKey,
                    new File(getApplication().getFilesDir(), s3ImageKey),
                    success ->
                    {
                        ImageView taskImageView = findViewById(R.id.taskimageView);
                        taskImageView.setImageBitmap(BitmapFactory.decodeFile(success.getFile().getPath()));
                    },
                    failure ->
                    {
                        Log.e(TAG, "Unable to get image from S3 for the product for S3 key: " + s3ImageKey + " for reason: " + failure.getMessage());
                    }
            );
        }
        setUpSpinners();
    }


    private void setUpSpinners() {
        TEAMSpinner = (Spinner) findViewById(R.id.editTeamSpinner);

        Amplify.API.query(
                ModelQuery.list(Team.class),
                success ->
                {
                    Log.i(TAG, "Read TEAMS successfully!");
                    ArrayList<String> TEAMSNames = new ArrayList<>();
                    ArrayList<Team> TEAMS = new ArrayList<>();
                    for (Team team : success.getData()) {
                        TEAMS.add(team);
                        TEAMSNames.add(team.getName());
                    }
                    TeamFuture.complete(TEAMS);

                    runOnUiThread(() ->
                    {
                        TEAMSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                TEAMSNames));
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



    private void setUpSaveButton() {
        Button saveButton = (Button) findViewById(R.id.updateButton);
        saveButton.setOnClickListener(v ->
        {
            saveProduct(s3ImageKey);
        });
    };


        private void saveProduct(String imageS3Key){
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
                    // Added image s3 key here
                    .taskImageS3Key(imageS3Key)
                    .build();

            Amplify.API.mutate(
                    ModelMutation.update(TASKToSave),  // making a GraphQL request to the cloud
                    successResponse ->
                    {
                        Log.i(TAG, "EditProductActivity.onCreate(): edited a product successfully");
                        // TODO: Display a Snackbar
                        Snackbar.make(findViewById(R.id.taskdetails), "Product saved!", Snackbar.LENGTH_SHORT).show();
                    },  // success callback
                    failureResponse -> Log.i(TAG, "EditProductActivity.onCreate(): failed with this response: " + failureResponse)  // failure callback
            );
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
                        Intent goToMain = new Intent(TaskDetailPage.this, MainActivity.class);
                        startActivity(goToMain);
                    },
                    failureResponse -> Log.i(TAG, "EditProductActivity.onCreate(): failed with this response: " + failureResponse)
            );
        });
    }
//    private ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher() {
//    }




    private void setUpAddImageButton()
    {
        Button addImageButton = (Button) findViewById(R.id.TaskAddImageButton);
        addImageButton.setOnClickListener(b ->
        {
            launchImageSelectionIntent();
        });

    }

    private void launchImageSelectionIntent() {
        // Part 1: Launch activity to pick file

        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageFilePickingIntent.setType("*/*");
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});//any image type
        // Below is simple version for testing
        //startActivity(imageFilePickingIntent);

        // Part 2: Create an image picking activity result launcher
        activityResultLauncher.launch(imageFilePickingIntent);

    }



    private ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher()
    {
        // Part 2: Create an image picking activity result launcher
        ActivityResultLauncher<Intent> imagePickingActivityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>()
                        {
                            @Override
                            public void onActivityResult(ActivityResult result)
                            {
                                Button addImageButton = findViewById(R.id.TaskAddImageButton);
                                if (result.getResultCode() == Activity.RESULT_OK)
                                {
                                    if (result.getData() != null)
                                    {
                                        Uri pickedImageFileUri = result.getData().getData();
                                        try
                                        {
                                            InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
                                            String pickedImageFilename = getFileNameFromUri(pickedImageFileUri);
                                            Log.i(TAG, "Succeeded in getting input stream from file on phone! Filename is: " + pickedImageFilename);
                                            // Part 3: Use our InputStream to upload file to S3
                                            switchFromAddButtonToDeleteButton(addImageButton);
                                            uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename,pickedImageFileUri);

                                        } catch (FileNotFoundException fnfe)
                                        {
                                            Log.e(TAG, "Could not get file from file picker! " + fnfe.getMessage(), fnfe);
                                        }
                                    }
                                }
                                else
                                {
                                    Log.e(TAG, "Activity result error in ActivityResultLauncher.onActivityResult");
                                }
                            }
                        }
                );

        return imagePickingActivityResultLauncher;
    }
    private void uploadInputStreamToS3(InputStream pickedImageInputStream, String pickedImageFilename,Uri pickedImageFileUri)
    {
        Amplify.Storage.uploadInputStream(
                pickedImageFilename,  // S3 key
                pickedImageInputStream,
                success ->
                {
                    Log.i(TAG, "Succeeded in getting file uploaded to S3! Key is: " + success.getKey());
                    // Part 4: Update/save our Product object to have an image key
//                    saveProduct(success.getKey());
                    updateImageButtons();
                    ImageView productImageView = findViewById(R.id.taskimageView);
                    InputStream pickedImageInputStreamCopy = null;  // need to make a copy because InputStreams cannot be reused!
                    try
                    {
                        pickedImageInputStreamCopy = getContentResolver().openInputStream(pickedImageFileUri);
                    }
                    catch (FileNotFoundException fnfe)
                    {
                        Log.e(TAG, "Could not get file stream from URI! " + fnfe.getMessage(), fnfe);
                    }
                    productImageView.setImageBitmap(BitmapFactory.decodeStream(pickedImageInputStreamCopy));

                },
                failure ->
                {
                    Log.e(TAG, "Failure in uploading file to S3 with filename: " + pickedImageFilename + " with error: " + failure.getMessage());
                }
        );
    }
//
    private void setUpDeleteImageButton()
    {
        Button deleteImageButton = (Button)findViewById(R.id.deleteImage);
        String s3ImageKey = this.s3ImageKey;
        deleteImageButton.setOnClickListener(v ->
        {
            Amplify.Storage.remove(
                    s3ImageKey,
                    success ->
                    {
                        Log.i(TAG, "Succeeded in deleting file on S3! Key is: " + success.getKey());

                    },
                    failure ->
                    {
                        Log.e(TAG, "Failure in deleting file on S3 with key: " + s3ImageKey + " with error: " + failure.getMessage());
                    }
            );
            ImageView productImageView = findViewById(R.id.taskimageView);
            productImageView.setImageResource(android.R.color.transparent);

//            saveProduct("");
            switchFromDeleteButtonToAddButton(deleteImageButton);
        });
    }
//
    private void updateImageButtons() {
        Button addImageButton = findViewById(R.id.TaskAddImageButton);
        Button deleteImageButton = findViewById(R.id.TaskAddImageButton);
        runOnUiThread(() -> {
            if (s3ImageKey.isEmpty()) {
                deleteImageButton.setVisibility(View.INVISIBLE);
                addImageButton.setVisibility(View.VISIBLE);
            } else {
                deleteImageButton.setVisibility(View.VISIBLE);
                addImageButton.setVisibility(View.INVISIBLE);
            }
        });
    }
//
    private void switchFromDeleteButtonToAddButton(Button deleteImageButton) {
        Button addImageButton = findViewById(R.id.TaskAddImageButton);
        deleteImageButton.setVisibility(View.INVISIBLE);
        addImageButton.setVisibility(View.VISIBLE);
    }

    private void switchFromAddButtonToDeleteButton(Button addImageButton) {
        Button deleteImageButton = findViewById(R.id.deleteImage);
        deleteImageButton.setVisibility(View.VISIBLE);
        addImageButton.setVisibility(View.INVISIBLE);
    }
    // Taken from https://stackoverflow.com/a/25005243/16889809
    @SuppressLint("Range")
    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("team")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        ImageView imageView = findViewById(R.id.arrowImage);
        Intent intent = getIntent();
        if (intent != null) {
            String taskTitle = intent.getStringExtra("taskTitle");
            String taskBody=intent.getStringExtra("taskBody");
            String taskState = intent.getStringExtra("taskState");

            if (taskTitle != null && taskState != null) {
                titleTextView.setText(taskBody+"\n"+taskTitle +"\n");

                descriptionTextView.setText(taskState);
            } else {
                Log.e("TaskDetailPage", "Title or state is null");
            }
        } else {
            Log.e("TaskDetailPage", "Intent is null");
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentArrow = new Intent(TaskDetailPage.this, MainActivity.class);
                startActivity(intentArrow);
            }
        });
    }
    private int getSpinnerIndex(Spinner spinner, String stringValueToCheck) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(stringValueToCheck)) {
                return i;
            }
        }

        return 0;
    }



}