package com.matthijs.recepten;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class IngredientlistAdapter extends ArrayAdapter {

    //Activity reference
    private final Activity context;

    //Store ingredients
    private final ArrayList<String> ingredientArray;

    //Store amount
    private final ArrayList<Double> amountArray;

    //Store measure
    private final ArrayList<String> measureArray;

    public IngredientlistAdapter(Activity context,
                                 ArrayList<String> ingredientArrayParam,
                                 ArrayList<Double> amountArrayParam,
                                 ArrayList<String> measureArrayParam){
        super(context, R.layout.listitem_ingredient, ingredientArrayParam);

        this.context = context;
        this.ingredientArray = ingredientArrayParam;
        this.amountArray = amountArrayParam;
        this.measureArray = measureArrayParam;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.listitem_ingredient, null, true);

        TextView ingredient_tv = itemView.findViewById(R.id.list_item_ingredient_tv);
        TextView amount_tv = itemView.findViewById(R.id.list_item_amount);
        TextView measure_tv = itemView.findViewById(R.id.list_item_measure);

        ingredient_tv.setText(ingredientArray.get(position));
        amount_tv.setText(Double.toString(amountArray.get(position)));
        measure_tv.setText(measureArray.get(position));

        return itemView;
    }

    


}
