package com.example.capstoneprojectfinal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView  main_fname, main_course, main_faculty, main_scholar, main_department, main_sched;
    private ImageView userPicture;
    private Button btnFirstSem, btnSecondSem;
    private String selectedSem, selectedMonth;
    RecyclerView mRecyclerView;
    MyAdapter myAdapter;

    ArrayList<String> arrayList_parent;
    ArrayAdapter<String> arrayAdapter_parent;
    ArrayList<Model> dtrList;
    ArrayList<String> arrayList_FirstSemMonth, arrayList_SecondSemMonth;
    ArrayAdapter<String> arrayAdapter_child;

    Spinner semspinner, monthspinner;

    ImageView user;
    String studentID;
    StorageReference storageReference;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.home_fragment, container, false);
        storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        firestore = FirebaseFirestore.getInstance();
        dtrList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        myAdapter = new MyAdapter(getContext(), dtrList);
        mRecyclerView.setAdapter(myAdapter);

        semspinner = view.findViewById(R.id.semSpinner);
        monthspinner = view.findViewById(R.id.monthsSpinner);


        main_fname = view.findViewById(R.id.main_fullname);
        main_course = view.findViewById(R.id.main_course);
        main_scholar = view.findViewById(R.id.main_scholarship);
        main_department = view.findViewById(R.id.main_department);
        main_faculty = view.findViewById(R.id.assign_faculty);
        main_sched = view.findViewById(R.id.schedule);
        userPicture = view.findViewById(R.id.userProfileHome);


        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("key_name", Context.MODE_PRIVATE);
        String fnamePref = sharedPreferences.getString("fnameAdd","");
        String coursePref = sharedPreferences.getString("courseAdd","");

        main_fname.setText(fnamePref);
        main_course.setText(coursePref);

        RecyclerView.ItemDecoration divide = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(divide);


        /*READ! Doesnt show the arralist for months in Second Spinner Drop Down*/
        /*READ! Pakiretrieve din yung Type of scholaship at Assigned Department sa Home Fragment kase wala sa Fields ng App yun kundi sa pag assigned ng Admin*/

        /*Array in Parent Spinner Semester*/

        arrayList_parent = new ArrayList<>();
        arrayList_parent.add("1st Semester");
        arrayList_parent.add("2nd Semester");

        /*Adapter for the parent spinner*/

        arrayAdapter_parent = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, arrayList_parent);

        semspinner.setAdapter(arrayAdapter_parent);

        /*Array List for Months of Whole Semester*/

        arrayList_FirstSemMonth = new ArrayList<>();
        arrayList_FirstSemMonth.add("June");
        arrayList_FirstSemMonth.add("July");
        arrayList_FirstSemMonth.add("August");
        arrayList_FirstSemMonth.add("September");
        arrayList_FirstSemMonth.add("October");

        arrayList_SecondSemMonth = new ArrayList<>();
        arrayList_SecondSemMonth.add("November");
        arrayList_SecondSemMonth.add("December");
        arrayList_SecondSemMonth.add("January");
        arrayList_SecondSemMonth.add("February");
        arrayList_SecondSemMonth.add("March");


        semspinner.setOnItemSelectedListener(this);

        arrayAdapter_child = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, arrayList_FirstSemMonth);
        monthspinner.setAdapter(arrayAdapter_child);

        monthspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = monthspinner.getSelectedItem().toString();
                RetrieveRecords();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        RetrieveStudenInfo();
        retrieveImageFromFirebase();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (position==0){
            selectedSem = "First Semester";
            arrayAdapter_child = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, arrayList_FirstSemMonth);

        }
        if (position==1){
            selectedSem = "Second Semester";
            arrayAdapter_child = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, arrayList_SecondSemMonth);

        }
        monthspinner.setAdapter(arrayAdapter_child);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
    // methods here
    private void retrieveImageFromFirebase(){

        final StorageReference fileRef = storageReference.child("student/" +
                firebaseUser.getUid());
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(getActivity()).load(uri).into(userPicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void RetrieveStudenInfo(){

        db.collection("student").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    Map<String, Object> mapData = snapshot.getData();
                    main_fname.setText(mapData.get("FullName").toString());
                    main_course.setText(mapData.get("Course").toString());
                    studentID = mapData.get("StudentID").toString();

                    db.collection("scholar_list").whereEqualTo("StudentID", studentID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                RetrieveDataFromSuper(studentID);
                            } else {
                              //  Toast.makeText(getContext(), "No Scholarship", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                } else {
                    Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void RetrieveRecords(){
        if(studentID == null) {
            return;
        }
        db.collection("scholar_list").whereEqualTo("StudentID", studentID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    db.collection("student_record").document(studentID).collection("Semester")
                            .document(selectedSem).collection(selectedMonth).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot docu : task.getResult()){
                                    Map<String, Object> mapData = docu.getData();
                                    Model newModel = new Model();
                                    newModel.setDate(changeDateFormat(mapData.get("Date").toString()));
                                    newModel.setTimein(mapData.get("Timein").toString());
                                    newModel.setTimeout(mapData.get("Timeout").toString());
                                    newModel.setTotalHrs(mapData.get("Hours").toString());
                                    dtrList.add(newModel);
                                }
                            } else {
                                Toast.makeText(getContext(), "No Records on the list", Toast.LENGTH_LONG).show();
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                   // Toast.makeText(getContext(), "Not scholar", Toast.LENGTH_LONG).show();
                }
            }
        });
        dtrList.removeAll(dtrList);

    }

    private void RetrieveDataFromSuper(String studID){
        db.collection("scholar_list").document(studID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    Map<String, Object> mapData = snapshot.getData();
                    if(mapData != null) {
                        main_department.setText(mapData.get("Department").toString());
                        main_scholar.setText(mapData.get("Scholarship").toString());
                        main_faculty.setText(mapData.get("Faculty").toString());
                        main_sched.setText(mapData.get("Schedule").toString());
                    } else {
                        Toast.makeText(getContext(), "Not scholar", Toast.LENGTH_LONG).show();
                    }
                    System.out.println(snapshot.getData());
                } else {

                }
            }
        });
    }

    private String changeDateFormat(String date) {
        String newDate = "";
        String finalDate = "";
        for (int c = 0; c < date.length(); c++){
            switch (c) {
                case 0: case 1:
                    break;
                default:
                    newDate+=date.charAt(c);
            }
        }
        String[] strArr = newDate.split("-");
        for (int e = strArr.length - 1; e >= 0; e--){
            finalDate+=strArr[e] + '-';
        }
        StringBuffer buffer = new StringBuffer(finalDate);
        buffer.deleteCharAt(buffer.length() - 1);
        finalDate = buffer.toString();
        return finalDate;
    }
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Are you sure you want to Exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                        System.exit(0);
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}



