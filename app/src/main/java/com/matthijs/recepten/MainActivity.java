package com.matthijs.recepten;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    GlobalKeys GB = new GlobalKeys();
    String SHARED_PREF_FILENAME_UPDATE = GB.SHARED_PREF_FILENAME_UPDATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF_FILENAME_UPDATE,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("update", false);
        editor.apply();


        checkGooglePlayServices();

    }


    //Go to overview TODO: replace with login to backend
    public void toOverviewActivity(View view){
        Intent intent = new Intent(this, RecipeOverviewActivity.class);
        this.startActivity(intent);
    }

    private void checkGooglePlayServices() {
        switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)) {
            case ConnectionResult.SERVICE_MISSING:
                GoogleApiAvailability.getInstance().getErrorDialog(this, ConnectionResult.SERVICE_MISSING, 0).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                GoogleApiAvailability.getInstance().getErrorDialog(this, ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, 0).show();
                break;
            case ConnectionResult.SERVICE_DISABLED:
                GoogleApiAvailability.getInstance().getErrorDialog(this, ConnectionResult.SERVICE_DISABLED, 0).show();
                break;
        }
    }
}
