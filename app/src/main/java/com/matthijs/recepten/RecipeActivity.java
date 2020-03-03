package com.matthijs.recepten;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class RecipeActivity extends AppCompatActivity {

    String recipeName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Get extra data from bundleObject, only start loading stuff
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recipeName = bundle.getString("recipeName");
            getRecipeDocData();

            ViewPager viewPager = findViewById(R.id.recipe_viewpager);
            TabLayout tabLayout = findViewById(R.id.recipe_tablayout);

            TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager());
            adapter.addFragment(new IngredientFragment(), getString(R.string.recipe_ingredient_frag_title));
            adapter.addFragment(new StepFragment(), getString(R.string.recipe_step_frag_title));

            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);


        }



        //TODO: make fragments with tabs and viewpager
        //  one: tab with ingredients
        //  two: tab with steps

        //https://www.tutlane.com/tutorial/android/android-tabs-with-fragments-and-viewpager <-- this one
        //https://codinginflow.com/tutorials/android/tab-layout-with-fragments

    }

    private void getRecipeDocData(){
        db.collection("recipes").document(recipeName).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();

                            for(Object val: document.getData().values()){
                                Log.d("values", "val: "+ val); //val = 5
                            }

                            for(String key: document.getData().keySet()){
                                Log.d("keys", "key: "+ key); //key - no_people
                            }
                            getRecipeIngredientData();
                        }
                    }
                });
    }

    private void getRecipeIngredientData(){
        final ArrayList<String> ingredientList = new ArrayList<>();

        db.collection("recipes").document(recipeName).collection("ingredients")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for (QueryDocumentSnapshot document:task.getResult()){
                                String id = document.getId();
                                Map<String, Object> data = document.getData();

                                Object amountObj = data.get("amount");
                                if(amountObj != null){
                                    String measureStr = (String) data.get("measure");
                                    String amountStr = Double.toString((Double) amountObj);
                                    String ingredientStr = amountStr + measureStr + " " + id;
                                    ingredientList.add(ingredientStr);
                                }

                                /*
                                Log.d("ingredients", "id: "+ document.getId()); //document.getId() = bloem
                                Log.d("ingredients", "data: "+ document.getData()); //document.getData = {measure=g, amount=200.0}
                                */
                            }
                            Log.d("ingredients","list: " + ingredientList.toString());
                        }
                    }
                });
    }
}
