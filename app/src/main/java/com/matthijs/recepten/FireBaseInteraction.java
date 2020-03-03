package com.matthijs.recepten;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FireBaseInteraction {

    // Create one firestore instance to handle all the write requests
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addDocumentToFireStoreRoot(String mainCollection,
                                           String docName,
                                           Map<String, Object> docData,
                                           final Context context){
        /*
        Upload Map data to a specific document from a main collection.
        For example upload a 'recipe' document to the 'recipes' collection.

        mainCollection: id of main collection as String (e.g. 'recipes')
        docName: id of the document as a String (e.g. 'lasagna')
        docData: data to be uploaded in hashmap, will be saved as 'field' in firestore
        context: Application context, needed to create Toast messages in case of failed upload.
         */

        db.collection(mainCollection).document(docName).set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        makeToastUpload(context, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToastUpload(context,false);
                        e.printStackTrace();
                    }
                });


    }

    public void addDocumentToFireStoreSubCollection(String mainCollection,
                                                    String mainDocName,
                                                    String subCollection,
                                                    String subDocName,
                                                    Map<String, Object> docData,
                                                    final Context context){
        /*
        Same story as for the addDocumentToFireStoreRoot method, except store data one level deeper.
         */

        db.collection(mainCollection).document(mainDocName).collection(subCollection).document(subDocName)
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        makeToastUpload(context, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToastUpload(context, false);
                        e.printStackTrace();
                    }
                });


    }



    private void makeToastUpload(Context context, boolean successful){
        String toastMessage;
        if(successful){
            toastMessage = context.getResources().getString(R.string.firebase_interaction_upload_successful);
        }else {
            toastMessage = context.getResources().getString(R.string.firebase_interaction_upload_failed);
        }
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void makeToastDownload(Context context, boolean successful){
        String toastMessage;
        if(successful){
            toastMessage = context.getResources().getText(R.string.firebase_interaction_download_successful).toString();
        }else{
            toastMessage = context.getResources().getText(R.string.firebase_interaction_download_failed).toString();
        }
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

}
