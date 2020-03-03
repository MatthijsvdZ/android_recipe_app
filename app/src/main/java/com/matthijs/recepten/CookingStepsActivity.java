package com.matthijs.recepten;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CookingStepsActivity extends AppCompatActivity {

    Map<String, Map<String,Object>> stepIdMap = new HashMap<>();
    int stepCount;
    String recipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooking_steps);

        stepCount = 0;

        // Get extra data from bundleObject
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recipeName = bundle.getString("recipeName");
        }

        // Set recipe name
        TextView recipeTitleTV = findViewById(R.id.cooking_steps_recipe_title_tv);
        recipeTitleTV.setText(recipeName);

    }

    public void addStep(View view) {
        //TODO: Hide keyboard when clicked

        // Get parent layout and inflate individual step layout
        LinearLayout ll_0 = findViewById(R.id.cooking_steps_scroll_ll);

        stepCount++;

        String stepCountstr = "Stap: " + stepCount;

        // Create LinearLayout for "step" TextView and for "title" EditText
        LinearLayout ll_1 = new LinearLayout(this);
        ll_1.setOrientation(LinearLayout.HORIZONTAL);
        ll_1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // TextView Step indicator
        TextView tvStep = new TextView(this);
        tvStep.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.25f));
        tvStep.setText(stepCountstr);
        tvStep.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);

        // EditView for step title
        EditText etTitle = new EditText(this);
        etTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.75f));
        etTitle.setHint(getText(R.string.individual_steps_hint_title));
        etTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        int idEtTitle = etTitle.generateViewId();
        etTitle.setId(idEtTitle);

        // EditView for step description
        EditText etDescription = new EditText(this);
        etDescription.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        etDescription.setHint(getText(R.string.individual_steps_hint_step));
        etDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        int idEtDescription = etDescription.generateViewId();
        etDescription.setId(idEtDescription);

        ll_1.addView(tvStep);
        ll_1.addView(etTitle);

        ll_0.addView(ll_1);
        ll_0.addView(etDescription);

        // Add ids to idMap
        Map<String, Object> idMap = new HashMap<>();
        idMap.put("idEtTitle",idEtTitle);
        idMap.put("idEtDescription", idEtDescription);

        // Add idMap to stepIdMap
        stepIdMap.put(Integer.toString(stepCount),idMap);
    }

    public void saveSteps(View view) {
        // Check steps
        if(stepCount < 1){
            Toast.makeText(this, getString(R.string.cooking_steps_minimum), Toast.LENGTH_SHORT).show();
            return;
        }

        // Check Title fields
        for(int i = 1; i<stepCount+1; i++){
            if(!hasTitle(i)){
                Toast.makeText(this,getString(R.string.cooking_steps_empty_title), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.cooking_steps_final_check);
        alertBuilder.setPositiveButton(R.string.cooking_steps_diaglog_pos_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                extractDataAndSendToFirestore();
                toRecipeOverview();
            }
        });
        alertBuilder.setNegativeButton(R.string.cooking_steps_diaglog_neg_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        }).show();


    }

    private void extractDataAndSendToFirestore(){
        EditText etTitle;
        EditText etDescription;
        // Extra data from fields and store to FireStore
        for(int i = 1; i<stepCount+1; i++){
            // Declare stepMap
            Map<String, Object> stepMap = new HashMap<>();
            // Get idMap
            Map<String, Object> idMap = stepIdMap.get(Integer.toString(i));

            if(idMap != null){
                // Get title id
                Object titleIdObj = idMap.get("idEtTitle");
                if(titleIdObj != null){
                    // Get title view
                    int titleId = (Integer) titleIdObj;
                    etTitle = findViewById(titleId);

                    // Get title text
                    String titleStr = etTitle.getText().toString();

                    // Store in stepMap
                    stepMap.put("title", titleStr);

                    // Get Description id
                    Object descriptionIdObj = idMap.get("idEtDescription");
                    if(descriptionIdObj != null){
                        // Get description view
                        int descriptionId = (Integer) descriptionIdObj;
                        etDescription = findViewById(descriptionId);

                        // Get description text
                        String descriptionStr = etDescription.getText().toString();

                        // Check if description is empty
                        if(TextUtils.isEmpty(etDescription.getText())){
                            descriptionStr = "";
                        }else{
                            descriptionStr = etDescription.getText().toString();
                        }

                        // Store in stepMap
                        stepMap.put("description", descriptionStr);

                        // upload to FireStore
                        FireBaseInteraction FBI = new FireBaseInteraction();
                        FBI.addDocumentToFireStoreSubCollection(
                                "recipes",
                                recipeName,
                                "steps",
                                String.valueOf(i),
                                stepMap,
                                getApplicationContext());

                    }else return;

                }else return;

            }else return;
        }
    }

    private boolean hasTitle(int i){
        AtomicBoolean title = new AtomicBoolean(false);
        // Get idMap
        Map<String, Object> idMap = stepIdMap.get(Integer.toString(i));
        // Check if valid
        if(idMap != null){
            // Get id from title View
            Object id = idMap.get("idEtTitle");
            if(id != null){
                // Check if tile is filled out, otherwise cancel method
                int titleViewId = (Integer) id;
                EditText etTitle = findViewById(titleViewId);
                if(TextUtils.isEmpty(etTitle.getText())){
                    title.set(false);
                }else {
                    title.set(true);
                }
            }else title.set(false);
        }else title.set(false);

        return title.get();
    }

    private void toRecipeOverview(){
        Intent intent = new Intent(this, RecipeOverviewActivity.class);
        startActivity(intent);
    }
}
