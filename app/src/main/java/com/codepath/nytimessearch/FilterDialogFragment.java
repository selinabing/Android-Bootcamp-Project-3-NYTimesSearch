package com.codepath.nytimessearch;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by selinabing on 6/24/16.
 */


public class FilterDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener{

    public interface FilterDialogListener {
        void onFinishDialog(ArrayList<String> res);
    }

    @BindView(R.id.cbPolitics) CheckBox cbPolitics;
    @BindView(R.id.cbFinancial) CheckBox cbFinancial;
    @BindView(R.id.cbTechnology) CheckBox cbTechnology;
    @BindView(R.id.spnrSortOrder) Spinner spnrSortOrder;
    @BindView(R.id.dpFilterDate) DatePicker dpFilterDate;
    @BindView(R.id.btnSubmitFilter)
    Button btnSubmitFilter;
    @BindView(R.id.btnFilterBack) Button btnFilterBack;


    public FilterDialogFragment(){
    }

    public static FilterDialogFragment newInstance(String title){
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putString("title","Filter");
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_fragment_dialog,container,false);
        ButterKnife.bind(this,view);
        btnFilterBack.setOnClickListener(this);
        btnSubmitFilter.setOnClickListener(this);
        cbFinancial.setOnClickListener(this);
        cbTechnology.setOnClickListener(this);
        cbPolitics.setOnClickListener(this);
        getDialog().setTitle("Filter");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Filter");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        //getDialog().getWindow().setSoftInputMode(
        //        WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    public void sendBackResult() {
        String sortValue = spnrSortOrder.getSelectedItem().toString();

        String date = dpFilterDate.getYear()+"";
        if (dpFilterDate.getMonth()<9)
            date=date+"0"+(dpFilterDate.getMonth()+1);
        else
            date=date+(dpFilterDate.getMonth()+1);
        if (dpFilterDate.getDayOfMonth()<10)
            date=date+"0"+dpFilterDate.getDayOfMonth();
        else
            date=date+dpFilterDate.getDayOfMonth();

        String filterNewsType = "";
        if (cbPolitics.isChecked())
            filterNewsType+="\"Politics\"";
        if (cbFinancial.isChecked())
            filterNewsType+=" "+"\"Financial\"";
        if (cbTechnology.isChecked())
            filterNewsType+=" "+"\"Technology\"";

        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        FilterDialogListener listener = (FilterDialogListener) getActivity();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(0,date);
        arrayList.add(1,filterNewsType);
        arrayList.add(2,sortValue);
        Log.d("DEBUG",date+"\\"+filterNewsType+"\\"+sortValue);
        listener.onFinishDialog(arrayList);
        dismiss();
    }

    public void backWithoutResults(){
        FilterDialogListener listener = (FilterDialogListener) getActivity();
        listener.onFinishDialog(null);
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cbFinancial:
                break;
            case R.id.cbPolitics:
                break;
            case R.id.cbTechnology:
                break;
            case R.id.btnSubmitFilter:
                sendBackResult();
                break;
            case R.id.btnFilterBack:
                backWithoutResults();
                break;
        }
    }
}
