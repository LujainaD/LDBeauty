package com.lujaina.ldbeauty.SP;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.lujaina.ldbeauty.Adapters.ColorAdapter;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.ColorModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class AddInfoDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

	private static final String TAG = "AddInforFrag";

	private MediatorInterface mMediatorInterface;
	private Context mContext;
	private ArrayList<ColorModel> colorList;

	public AddInfoDialogFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		mContext = context;
		if (context instanceof MediatorInterface) {
			mMediatorInterface = (MediatorInterface) context;
		} else {
			throw new RuntimeException(context.toString() + "must implement MediatorInterface");
		}
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
		View parentView = inflater.inflate(R.layout.fragment_add_info_dialog, container, false);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


		final TextInputEditText etTitle = parentView.findViewById(R.id.title);
		final TextInputEditText etBody = parentView.findViewById(R.id.body);

		Button btnAdd = parentView.findViewById(R.id.btn_add);
		Button btnCancel = parentView.findViewById(R.id.btn_cancel);

		createColorList();

		Spinner colorSpinner = parentView.findViewById(R.id.colorSpinner);
		colorSpinner.setOnItemSelectedListener(this);
		ColorAdapter mAdapter = new ColorAdapter(mContext, colorList);
		colorSpinner.setAdapter(mAdapter);

		btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = etTitle.getText().toString();
				String body = etBody.getText().toString();
				if (title.isEmpty()) {
					etTitle.setError(getString(R.string.erro_missin_title));
				} else if (body.isEmpty()) {
					etBody.setError(getString(R.string.erro_missin_info));
				}
			}
		});
		return parentView;
	}

	private void createColorList() {
		if (colorList == null) {
			colorList = new ArrayList<>();
			colorList.add(new ColorModel("White", "#FFFFFF"));
			colorList.add(new ColorModel("Beige", "#FDF8F8"));
			colorList.add(new ColorModel("Light Orange", "#FFC5CB"));
			colorList.add(new ColorModel("Light Gray", "#E6E7E8"));
			colorList.add(new ColorModel("Light Pink", "#FFCCEC"));
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Toast.makeText(mContext, "Selected is:" + position, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
