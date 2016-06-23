package com.codepath.nytimessearch;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    @BindView(R.id.cbPolitics) CheckBox cbPolitics;
    @BindView(R.id.cbFinancial) CheckBox cbFinancial;
    @BindView(R.id.cbTechnology) CheckBox cbTechnology;
    @BindView(R.id.spnrSortOrder) Spinner spnrSortOrder;
    @BindView(R.id.dpFilterDate) DatePicker dpFilterDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    public void onSubmitFilter(View view) {
        String sortValue = spnrSortOrder.getSelectedItem().toString();

        Intent i = new Intent();
        i.putExtra("sort value", sortValue);
        String date = dpFilterDate.getYear()+"";
        if (dpFilterDate.getMonth()<9)
            date=date+"0"+(dpFilterDate.getMonth()+1);
        else
            date=date+(dpFilterDate.getMonth()+1);
        if (dpFilterDate.getDayOfMonth()<10)
            date=date+"0"+dpFilterDate.getDayOfMonth();
        else
            date=date+dpFilterDate.getDayOfMonth();

        i.putExtra("date filter", date);
        Log.d("DEBUG","the date:"+date+"*** sortValue"+sortValue+"*** politics finance tech:"+cbPolitics.isChecked()+cbFinancial.isChecked()+cbTechnology.isChecked());
        i.putExtra("politics",cbPolitics.isChecked());
        i.putExtra("financial",cbFinancial.isChecked());
        i.putExtra("tech",cbTechnology.isChecked());
        setResult(RESULT_OK, i);
        finish();
    }

    public void onCheckboxClicked(View view){
        return;
    }
}
