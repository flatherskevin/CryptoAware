package com.flathers.cryptoaware;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by flath on 10/6/2017.
 */

public class SelectionDialog extends Dialog {
    Context mContext;
    private String[] mList;
    public String[] mListAvailable;
    public Button btnSelect;
    public Button btnCancel;
    public NumberPicker selector;
    EditText edttxtSearch;

    public SelectionDialog(Context context, String[] mList){
        super(context);
        this.mContext = context;
        this.mList = mList;
        this.mListAvailable = mList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_dialog);

        //Get search field and both buttons
        edttxtSearch = (EditText) findViewById(R.id.watching_edttxt_searchField);
        btnSelect = (Button) findViewById(R.id.watching_btn_confirm);
        btnCancel = (Button) findViewById(R.id.watching_btn_cancel);

        //Usage of a NumberPicker is satisfactory for now to choose selection
        selector = (NumberPicker) findViewById(R.id.watching_numpkr_selector);
        selector.setMinValue(0);

        //Error handle the NumberPicker in case of error gathering SelectionList
        try{
            selector.setMaxValue(mList.length - 1);
            selector.setDisplayedValues(mList);
        }catch (Exception e){
            selector.setDisplayedValues(null);
            selector.setMaxValue(0);
            selector.setDisplayedValues(new String[] {"Error"});
        }
        selector.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        //Every time anything is entered into the text field, run a search query
        edttxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            //Best to do query after text has been changed
            @Override
            public void afterTextChanged(Editable editable) {
                String editText = editable.toString();
                ArrayList<String> searchArrayList = new ArrayList<String>();
                for(String mListAvail : mList){
                    if(mListAvail.toLowerCase().contains(editText.toLowerCase())){
                        searchArrayList.add(mListAvail);
                    }
                }
                final String[] mListAvailableSearch = searchArrayList.toArray(new String[searchArrayList.size()]);

                //Null out display values and refresh NumberPicker
                try{
                    selector.setDisplayedValues(null);
                    selector.setMaxValue(mListAvailableSearch.length - 1);

                    selector.setDisplayedValues(mListAvailableSearch);
                }catch (Exception e){
                    selector.setDisplayedValues(null);
                    selector.setMaxValue(0);
                    selector.setDisplayedValues(new String[] {"No coins found"});
                }
                //Allow for proper string list searching on selection if search field is used
                mListAvailable = mListAvailableSearch;
            }
        });
        //OnClickListener does nothing until overridden
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectListener();
            }
        });

        //Dismiss dialog if on clicking cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelListener();
            }
        });
    }

    public void onSelectListener(){
        //Override this perform custom operation
    }
    public void onCancelListener(){
        //Override this perform custom operation
        dismiss();
    }


    public void hideSearchBar(){
        //Makes sure to call this function after SelectionDialog.create()
        //Removes search field
        edttxtSearch.setVisibility(View.GONE);
    }

    public void setWrapSelectorWheel(Boolean state){
        //Makes sure to call this function after SelectionDialog.create()
        //Prevents endless scrolling in NumberPicker
        selector.setWrapSelectorWheel(state);
    }
}
