package com.matthijs.recepten;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RecipeOverviewActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipeoverview);

    }

    @Override
    public void onResume(){
        super.onResume();
        // Fetch recipes
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<String> aL = new ArrayList<>();

        db.collection("recipes").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            // fill arraylist
                            for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                aL.add(documentSnapshot.getId());
                            }
                            makeToastDownload( true);
                            updateRecipeList(aL);

                        }else {
                            makeToastDownload( false);
                        }
                    }
                });
       }


    private void updateRecipeList(ArrayList<String> docIds){
        // Create ArrayAdapter for ListView
        ArrayAdapter<String> recipeAdapter = new ArrayAdapter<>(this,
                R.layout.listitem_recipelist,
                docIds);

        // Create, populate and update ListView
        final ListView recipeList = findViewById(R.id.recipe_listview);
        recipeList.setAdapter(recipeAdapter);
        recipeAdapter.notifyDataSetChanged();

        recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get clicked item
                String recipe = (String) recipeList.getItemAtPosition(position);

                Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
                intent.putExtra("recipeName",recipe);
                startActivity(intent);
            }
        });

    }

    public void toAddRecipesActivity(View view){
        Intent intent = new Intent(this, IngredientAddActivity.class);
        startActivity(intent);
    }

    private void makeToastDownload(boolean successful){
        String toastMessage;
        if(successful){
            toastMessage = getString(R.string.firebase_interaction_download_successful);
        }else{
            toastMessage = getString(R.string.firebase_interaction_download_failed);
        }
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}



