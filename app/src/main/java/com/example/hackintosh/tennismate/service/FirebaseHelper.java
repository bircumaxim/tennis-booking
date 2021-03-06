package com.example.hackintosh.tennismate.service;

import com.example.hackintosh.tennismate.portability.Consumer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nicu on 11/11/17.
 */

public class FirebaseHelper {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();


    private FirebaseHelper() {
    }

    public static void setValue(String ref, Object value, Runnable onSuccess, Consumer<String> onError) {
        DatabaseReference reference = getReference(ref);

        reference.setValue(value, (databaseError, databaseReference) -> {
            if(databaseError != null){
                onError.accept(databaseError.getMessage());
            } else {
                onSuccess.run();
            }
        });
    }

    public static <T> void getValue(String ref, Class<T> klass, Consumer<T> consumer, Consumer<String> error) {

        DatabaseReference reference = database.getReference(ref);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                T value = dataSnapshot.getValue(klass);

                consumer.accept(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                error.accept(databaseError.getMessage());
            }
        });
    }


    @SuppressWarnings("unchecked")
    public static <T> void getAllValues(String ref, Class<T> klass, Consumer<List<T>> consumer, Consumer<String> error) {

        DatabaseReference reference = database.getReference(ref);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<T> ts = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ts.add(snapshot.getValue(klass));
                }

                consumer.accept(ts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                error.accept(databaseError.getMessage());
            }
        });
    }

    private static DatabaseReference getReference(String reference){
        DatabaseReference result = database.getReference();
        return result.child(reference);
    }
}
