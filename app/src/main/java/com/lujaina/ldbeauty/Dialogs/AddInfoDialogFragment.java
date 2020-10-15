package com.lujaina.ldbeauty.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lujaina.ldbeauty.Adapters.ColorAdapter;
import com.lujaina.ldbeauty.Adapters.InfoAdapter;
import com.lujaina.ldbeauty.Adapters.NothingSelectedSpinnerAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.AddInfoModel;
import com.lujaina.ldbeauty.Models.ColorModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class AddInfoDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

	FirebaseUser mFirebaseUser;
	private DatabaseReference myRef;

	private Context mContext;

	private ArrayList<ColorModel> colorList;
	private ArrayList<AddInfoModel> mUpdate;
	private InfoAdapter mAdapter;
	private AddInfoModel about;

	public AddInfoDialogFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		mContext = context;
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
		FirebaseAuth mAuth = FirebaseAuth.getInstance();
		mFirebaseUser = mAuth.getCurrentUser();
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		final EditText etTitle = parentView.findViewById(R.id.ti_title);
		final EditText etBody = parentView.findViewById(R.id.ti_info);

		etTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					etTitle.setHint("");
				}else{
					etTitle.setHint(R.string.title);
				}

			}
		});

		etBody.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					etBody.setHint("");
				}else{
					etBody.setHint(R.string.addInfo);
				}

			}
		});
		Button btnAdd = parentView.findViewById(R.id.btn_add);
		Button btnCancel = parentView.findViewById(R.id.btn_cancel);

		createColorList();
		mUpdate = new ArrayList<>();
		mAdapter = new InfoAdapter(mContext);

		about = new AddInfoModel();

		Spinner colorSpinner = parentView.findViewById(R.id.colorSpinner);
		colorSpinner.setOnItemSelectedListener(this);
		ColorAdapter mAdapter = new ColorAdapter(mContext, colorList);
		colorSpinner.setAdapter(mAdapter);
		/*colorSpinner.setAdapter(
				new NothingSelectedSpinnerAdapter(
						mAdapter,
						R.layout.contact_spinner_row_nothing_selected,
						// R.layout.contact_spinner_nothing_selected_dropdown, // Optional
						mContext));*/
		btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = etTitle.getText().toString();
				String body = etBody.getText().toString();
				if (title.isEmpty()) {
					etTitle.setError(getString(R.string.erro_missin_title));
				} else if (body.isEmpty()) {
					etBody.setError(getString(R.string.erro_missin_info));
				}else{
					addInfoToDB(title, body);
				}
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		return parentView;
	}

	private void addInfoToDB(String title, String body) {
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Info);
		String Id = myRef.push().getKey();
		about.setInfoId(Id);
		about.setTitle(title);
		about.setBody(body);
		about.setSalonOwnerId(mFirebaseUser.getUid());
		mUpdate.add(about);
		mAdapter.update(mUpdate.indexOf(about), about);

		myRef.child(Id).setValue(about).addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				dismiss();
			}
		});

	}

	private void createColorList() {
		if (colorList == null) {
			colorList = new ArrayList<>();
			colorList.add(new ColorModel( "#FFFFFF"));
			colorList.add(new ColorModel( "#FDF8F8"));
			colorList.add(new ColorModel("#FFC5CB"));
			colorList.add(new ColorModel( "#E6E7E8"));
			colorList.add(new ColorModel( "#FFCCEC"));
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		about.setBackgroundColor(colorList.get(position).getColorValue());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}


