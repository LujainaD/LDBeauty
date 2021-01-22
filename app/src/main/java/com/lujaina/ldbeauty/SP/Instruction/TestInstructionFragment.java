package com.lujaina.ldbeauty.SP.Instruction;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import com.lujaina.ldbeauty.Adapters.TestAdapter;
import com.lujaina.ldbeauty.Models.TestInsModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;
import java.util.Objects;

public class TestInstructionFragment extends DialogFragment {

    ArrayList<TestInsModel> modelArrayList;
    private TestAdapter testAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private String page;

    public TestInstructionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView  = inflater.inflate(R.layout.fragment_test_instruction, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        ImageButton ibLeft = parentView.findViewById(R.id.ib_left);
        ImageButton ibRight = parentView.findViewById(R.id.ib_right);
         recyclerView = parentView.findViewById(R.id.rv);
        ibLeft.setOnClickListener(v -> {
            if (layoutManager.findFirstVisibleItemPosition() > 0) {
                recyclerView.smoothScrollToPosition(layoutManager.findFirstVisibleItemPosition() - 1);
            } else {
                recyclerView.smoothScrollToPosition(0);
            }
        });



        ibRight.setOnClickListener(v -> recyclerView.smoothScrollToPosition(layoutManager.findLastVisibleItemPosition() + 1));
        modelArrayList = new ArrayList();
        testAdapter = new TestAdapter(getContext(), modelArrayList);
        recyclerView.setAdapter(testAdapter);
        setupRecyclerView(recyclerView);
        if(page.equals("about")){
            createAboutDemo(modelArrayList);

        }if(page.equals("service")){
            createServiceDemo(modelArrayList);

        }

        return parentView;
    }

    private void createServiceDemo(ArrayList<TestInsModel> modelArrayList) {
        TestInsModel testInsModel ;
        testInsModel = new TestInsModel();
        testInsModel.setText("1. press adding button ");
        testInsModel.setImg(R.drawable.gradient_fb);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("2. A dialog will appear to you. you need to add picture of the service , title of the Service, name of the specialist who will do the service , price of the service"
                );
        testInsModel.setImg(R.drawable.add_serv_dialog);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("3. click on \"add\" to add the service to your salon page, or click on \"cancel\" .");
        testInsModel.setImg(R.drawable.add_serv_dialog);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("4. you can delete any service that you already add it in your page " +
                ", by swiping the service to the left..");
        testInsModel.setImg(R.drawable.service_ins);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("5. you can add the appointment  of the service that you already add in your page"+ "\"+\n"+
                "\", by swiping the service to the Right..");
        testInsModel.setImg(R.drawable.service_ins2);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("5. Appointment page:"+"\n" +
                "1. service title, 2. service price"+"\n" +
                " 3. service specialist, 4. date of service,"+"\n" +
                " 5. add service duration in minutes,"+"\n" +
                " 6. start service time, 7.end of the service time on the chosen date");

        testInsModel.setImg(R.drawable.appointment);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("5. continue Appointment page instructions:"+"\n" +
                "8. \"add time\" to add appointments to the service,"+"\n"+
                " 9. \"x\" to delete time,"+"\n" +
                " 10. gray time mean it already booked by a client"+"\n" +
                ", by swiping the service to the Right..");
        testInsModel.setImg(R.drawable.appointment);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("Example of adding services to the salon");
        testInsModel.setImg(R.drawable.services_in);
        modelArrayList.add(testInsModel);

    }

    private void createAboutDemo(ArrayList<TestInsModel> modelArrayList) {
        TestInsModel testInsModel ;
        testInsModel = new TestInsModel();
        testInsModel.setText("1. press adding button ");
        testInsModel.setImg(R.drawable.gradient_fb);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("2. A dialog will appear to you. you need to add title of the information, and add body which is" +
                " what the information that you want to add, then you can add the background color of the information.");
        testInsModel.setImg(R.drawable.dialog_info);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("3. click on \"add\" to add the information to your salon page, or click on \"cancel\" .");
        testInsModel.setImg(R.drawable.dialog_info);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("4. you can delete any information that you already add in your page " +
                ", by swiping the paragraph to the left..");
        testInsModel.setImg(R.drawable.swipe);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("5. To update the information: click on the title of the information." +
                "then edit the information that you want to update, click save");
        testInsModel.setImg(R.drawable.update);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("Example of adding information to a salon");
        testInsModel.setImg(R.drawable.ex_info);
        modelArrayList.add(testInsModel);

    }


    private void setupRecyclerView(RecyclerView recyclerView) {

         layoutManager = new LinearLayoutManager(getContext(), recyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public void setInstructionPage(String page) {
        this.page=page;
    }
}