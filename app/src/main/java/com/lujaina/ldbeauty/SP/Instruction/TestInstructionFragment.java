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
            Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView  = inflater.inflate(R.layout.fragment_test_instruction, container, false);
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
        creatDemo(modelArrayList);

        return parentView;
    }

    private void creatDemo(ArrayList<TestInsModel> modelArrayList) {
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
        testInsModel.setText("4. you can delete any information that you already add it in your page " +
                ", by swiping the paragraph to the left..");
        testInsModel.setImg(R.drawable.swipe);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("5. To update the information: click on the title of the information." +
                "then edit the information that you want to update, click save");
        testInsModel.setImg(R.drawable.update);
        modelArrayList.add(testInsModel);

        testInsModel = new TestInsModel();
        testInsModel.setText("Example of adding information page to a salon");
        testInsModel.setImg(R.drawable.ex_info);
        modelArrayList.add(testInsModel);

    }

    private void setupRecyclerView(RecyclerView recyclerView) {

         layoutManager = new LinearLayoutManager(getContext(), recyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
}