package com.matthijs.recepten;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class IngredientAddActivity extends AppCompatActivity {

    // Ingredient list variables
    IngredientlistAdapter ingredientlistAdapter;
    ListView ingredientList;

    ArrayList<String> ingredientArray = new ArrayList<>();
    ArrayList<Double> amountArray = new ArrayList<>();
    ArrayList<String> measureArray = new ArrayList<>();

    // Ingredient input variables
    EditText recipeTitleEt;
    EditText amountEt;
    EditText ingredientEt;
    Spinner measureSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_add);

        // fill ingredientlistAdapter
        ingredientlistAdapter = new IngredientlistAdapter(this,
                ingredientArray, amountArray, measureArray);

        // Create ingredient list
        ingredientList = findViewById(R.id.ingredient_add_listview);
        ingredientList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                // Build delete message
                String toastMessage = ingredientArray.get(position).concat(" ");
                toastMessage = toastMessage
                        .concat(getString(R.string.ingredient_add_toast_item_deleted));

                //Remove data from array's and update
                ingredientArray.remove(position);
                amountArray.remove(position);
                measureArray.remove(position);
                ingredientlistAdapter.notifyDataSetChanged();

                //show Toast
                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        ingredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Build hint messeage
                String toastMessage =
                        getString(R.string.ingredient_add_toast_item_delete_hint);

                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();

            }
        });
        ingredientList.setAdapter(ingredientlistAdapter);

    }

    public void addIngredients(View view) {
        // input views
        amountEt = findViewById(R.id.ingredient_add_amount_et);
        ingredientEt = findViewById(R.id.ingredient_add_ingredient_et);
        measureSpinner = findViewById(R.id.ingredient_add_measure_spinner);

         // Perform checks if input data exists and is of the right type of amountEt
        if(TextUtils.isEmpty(amountEt.getText())){
            Toast.makeText(this,
                    R.string.ingredient_add_toast_amount_missing, Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(ingredientEt.getText())){
            Toast.makeText(this,
                    R.string.ingredient_add_toast_ingredient_missing, Toast.LENGTH_SHORT).show();
        }else if(!isNumeric(amountEt.getText())){
            Toast.makeText(this,
                    R.string.ingredient_add_toast_amount_wrongtype, Toast.LENGTH_SHORT).show();
        }else {
            // Get data from view
            Double amount = Double.valueOf(amountEt.getText().toString());
            String measure = measureSpinner.getSelectedItem().toString();
            String ingredient = ingredientEt.getText().toString();

            // Check for duplicate ingredients
            if(ingredientArray.contains(ingredient)){
                String toastMessage = ingredient.concat(" ");
                toastMessage = toastMessage.concat(getString(R.string.ingredient_add_duplicate_ingredient));
                Toast.makeText(this, toastMessage,Toast.LENGTH_LONG).show();
            }else {
                // Add to arrays and update adapter
                amountArray.add(amount);
                measureArray.add(measure);
                ingredientArray.add(ingredient);
                ingredientlistAdapter.notifyDataSetChanged();
            }
        }

    }

    public void saveAndToSteps(View view) {
        // Create FireBaseInteraction instance
        FireBaseInteraction FBI = new FireBaseInteraction();

        // Get recipe title view and number of people view
        recipeTitleEt = findViewById(R.id.ingredient_add_recipe_title);
        EditText nPeopleEt = findViewById(R.id.ingredient_add_number_of_people_et);

        // Get ingredient count
        int ingredientCount = ingredientlistAdapter.getCount();

        // Recipe title cannot be empty
        if(TextUtils.isEmpty(recipeTitleEt.getText())){
            String toastMessage = getString(R.string.ingredient_add_no_title);
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(nPeopleEt.getText())){
            String toastMessage = getString(R.string.ingredient_add_no_people);
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        }
        // Check if ingredients are not empty
        else if(ingredientCount < 1){
            String toastMessage = getString(R.string.ingredient_add_no_ingredients);
            Toast.makeText(this,toastMessage,Toast.LENGTH_SHORT).show();
        }else{
            //TODO: Check if recipe all ready exists in Firestore

            final String recipeName = recipeTitleEt.getText().toString();

            // Create and fill ingredientMap from ListView items
            for(int i = 0; i < ingredientCount; i++){
                // Get ingredient data
                String ingredient = ingredientArray.get(i);
                Double amount = amountArray.get(i);
                String measure = measureArray.get(i);

                // Make amountMeasureMap
                Map<String,Object> amountMeasureMap = new HashMap<>();
                amountMeasureMap.put("amount",amount);
                amountMeasureMap.put("measure",measure);

                // Upload to FireStore
                FBI.addDocumentToFireStoreSubCollection(
                        "recipes",     // mainCollection
                        recipeName,                 // mainDocument
                        "ingredients",  // subCollection
                        ingredient,                 // subDocument
                        amountMeasureMap,           // docData
                        getApplicationContext());   // context

            }

            // Get number of people data and save to FireStore
            String nPeople = nPeopleEt.getText().toString();
            Map<String,Object> nPeopleData = new HashMap<>();
            nPeopleData.put("no_people", nPeople);

            FBI.addDocumentToFireStoreRoot(
                    "recipes",
                    recipeName,
                    nPeopleData,
                    getApplicationContext()
            );

            goToCookingSteps();

        }

    }

    // Method to start activity
    private void goToCookingSteps(){
        Log.d("toCooking", "function called");
        Intent intent = new Intent(this, CookingStepsActivity.class);
        intent.putExtra("recipeName", recipeTitleEt.getText().toString());

        this.startActivity(intent);
    }


    private boolean isNumeric(CharSequence c){
        AtomicBoolean numeric = new AtomicBoolean(false);

        String s = c.toString();
        try {
            Double.parseDouble(s);
            numeric.set(true);
        } catch (Exception e){
            e.printStackTrace();
            numeric.set(false);
        }

        return numeric.get();
    }
}
