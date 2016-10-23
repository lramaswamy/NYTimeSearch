package com.example.lramaswamy.nytimesearch.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.lramaswamy.nytimesearch.Models.SearchFilter;
import com.example.lramaswamy.nytimesearch.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFilterFragment extends DialogFragment implements DatePickerFragment.DatePickerDialogListener {


    TextView editText;
    OnDataPass dataPasser;
    CompoundButton.OnCheckedChangeListener checkListener;
    boolean isArts = false;
    boolean isFashionStyle = false;
    boolean isSports = false;
    Spinner spinner;

    public void setOldFilter(SearchFilter oldFilter) {
        this.oldFilter = oldFilter;
    }

    SearchFilter oldFilter;

    public SearchFilterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_filter_fragment, container);

        initializeFormObjects(view);

        Button btnCancel = (Button)view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClick();
            }
        });

        Button btnOK = (Button)view.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okClick();
            }
        });
        return view;
    }

    private void initializeFormObjects(View view) {


        //Get the begin date and initialize the onclick listener for it
        editText = (TextView) view.findViewById(R.id.etBeginDate);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDatePicker();
            }
        });

        //Get the checkboxes and initialize the onclick listneres on them
        setupOncheckedChangeListener();
        CheckBox artsCheck = (CheckBox) view.findViewById(R.id.cbArts);
        CheckBox fashionStyleCheck = (CheckBox) view.findViewById(R.id.cbfashionStyle);
        CheckBox sportsCheck = (CheckBox) view.findViewById(R.id.cbSports);

        artsCheck.setOnCheckedChangeListener(checkListener);
        fashionStyleCheck.setOnCheckedChangeListener(checkListener);
        sportsCheck.setOnCheckedChangeListener(checkListener);

        spinner = (Spinner)view.findViewById(R.id.spSortOrder);

        if(oldFilter != null) {
            editText.setText(oldFilter.getBeginDate());
            artsCheck.setChecked(oldFilter.isArts());
            fashionStyleCheck.setChecked(oldFilter.isFasStyle());
            sportsCheck.setChecked(oldFilter.isSports());
            if(oldFilter.getSortOrder().equals("Newest"))
                spinner.setSelection(0);
            else
                spinner.setSelection(1);
        }
    }

    private void setupOncheckedChangeListener() {
        // Fires every time a checkbox is checked or unchecked
        checkListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean checked) {
                // compoundButton is the checkbox
                // boolean is whether or not checkbox is checked
                // Check which checkbox was clicked
                switch(view.getId()) {
                    case R.id.cbArts:
                        if (checked) {
                            isArts = true;
                        } else {
                            isArts = false;
                        }
                        break;
                    case R.id.cbfashionStyle:
                        if (checked) {
                            isFashionStyle = true;
                        } else {
                            isFashionStyle = false;
                        }
                        break;
                    case R.id.cbSports:
                        if (checked) {
                            isSports = true;
                        } else {
                            isSports = false;
                        }
                        break;
                }
            }
        };
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Set Filter Values");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    public static SearchFilterFragment newInstance(String title) {
        SearchFilterFragment frag = new SearchFilterFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public void launchDatePicker() {
        FragmentManager fm = getFragmentManager();
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setTargetFragment(SearchFilterFragment.this, 300);
        if(oldFilter != null)
            datePickerFragment.setDatePicked(oldFilter.getBeginDate());
        datePickerFragment.show(fm, "datePicker");

    }

    @Override
    public void onFinishEditDialog(String inputText) {
        editText.setText(inputText);
    }

    public void cancelClick() {
        dismiss();
    }


    public void okClick() {
        SearchFilter filter = new SearchFilter();
        filter.setBeginDate(editText.getText().toString());
        filter.setArts(isArts);
        filter.setFasStyle(isFashionStyle);
        filter.setSports(isSports);
        filter.setSortOrder(spinner.getSelectedItem().toString());
        dataPasser.onDataPass(filter);
        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    public interface OnDataPass {
        public void onDataPass(SearchFilter data);
    }

}
