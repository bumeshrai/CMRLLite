package com.cmrl.cmrllite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateLayout{
    public CheckedTextView checkedTextView = null;
    public EditText editText = null;
    public LinearLayout linearLayout;
    Button button;
    String alertText[];
    int checkedId = 100;
    boolean proceed = false;
    JSONObject parent;
    int checkValue;

    Context context;
    LinearLayout.LayoutParams textParams, editParams,checkBoxParams,buttonParams;

    //Constructor to initialize values
    public CreateLayout(Context context, LinearLayout linearLayout){
        //Log.i("value1", "CreateLayout: context: " + context);

        this.context = context;
        this.linearLayout = linearLayout;
        this.checkedTextView = new CheckedTextView(context);
        this.editText = new EditText(context);
        alertText = new String[2];

        //Layout Parameters for different Views
        textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(15, 20, 0, 15);

        checkBoxParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        checkBoxParams.setMargins(2, 10, 2, 10);

        editParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        editParams.setMargins(2, 10, 2, 10);

        buttonParams = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(0, 50, 0, 20);
        buttonParams.gravity = Gravity.CENTER_HORIZONTAL;

    }

    //Function to create CheckedTextView (Check box)
    void createCheckedTextView(String checkName){
        //("value1", "createCheckedTextView: 12");
        SaveData.checkedList.add(checkedTextView);
        checkedTextView.setChecked(false);
        checkedTextView.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
        checkedTextView.setBackgroundResource(R.drawable.rounded_lightblue);
        checkedTextView.setText(checkName);
        checkedTextView.setTextColor(Color.BLACK);
        checkedTextView.setTextSize(17);
        checkedTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        checkedTextView.setGravity(Gravity.CENTER_VERTICAL);
        checkedTextView.setPadding(20, 20, 20, 20);

        checkedTextView.setId(checkedId++);
        checkedTextView.setClickable(true);
        checkedTextView.setFocusable(true);

        /*OnClick of CheckBox:
        It is checked if it's already not checked
        It is unchecked if it's already checked
        */
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedTextView.isChecked()) {
                    checkedTextView.setChecked(false);
                    checkedTextView.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
                } else {
                    checkedTextView.setChecked(true);
                    checkedTextView.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
                }
            }
        });
        checkedTextView.setLayoutParams(checkBoxParams);
        linearLayout.addView(checkedTextView);
        //Log.i("checked",checkedTextView.toString());
    }

    //Function to create EditText (Comment box)
    void createEditText() {
        //Log.i("value1", "createEditText: 1");
        SaveData.editTextList.add(editText);
        editText.setLayoutParams(editParams);
        editText.setTextColor(Color.BLACK);
        editText.setTextSize(17);
        editText.setHint("Comments");
        editText.setHintTextColor(Color.DKGRAY);
        editText.setPadding(20, 20, 20, 20);
        editText.setBackgroundResource(R.drawable.rounded_white);
        linearLayout.addView(editText);
    }

    //Function to create Submit button
    public void createSubmitButton(final String[][] createdViews) {
        button = new Button(context);
        button.setText("SUBMIT");
        //Create JSON String onclick of submit button and Show Alert window
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Log.i("here","Context: "+context.toString()+" LinearLayout: "+linearLayout.toString()
                        +" CreatedViews: "+ Arrays.deepToString(createdViews)+" tunnelParameters "+tunnelParameters.toString());*/
                alertText[0] = "";
                alertText[1] = "";
                alertText = createJSONString(createdViews);
                //Log.i("value", "URLParams: "+ URLParams);
                //Log.i("value", "alertText: "+ Arrays.toString(alertText));
                show_alert();

            }
        });
        button.setBackgroundResource(R.drawable.rounded_button);
        button.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        button.setTextColor(Color.WHITE);
        button.setLayoutParams(buttonParams);
        linearLayout.addView(button);
    }

    //Function to show alert before submitting the form
    private void show_alert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Spanned message = Html.fromHtml("<b>" + "List of Items checked: " + "</b><br>" + alertText[0] + "<br><br><b>" + "List of Items not checked: " + "</b><br>" + alertText[1]);
        builder.setTitle("Do you want to proceed?")
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dismiss Dialog onclick of NO
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Submit to server onclick of YES
                        Intent createIntent = new Intent(context, CreateActivity.class);
                        createIntent.putExtra("parent", parent.toString());
                        createIntent.putExtra("urlParams",SaveData.EQUIPMENT.toLowerCase());
                        context.startActivity(createIntent);
                        ((Activity) context).finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Function that returns a String for display in the alert window
    public String[] createJSONString(String[][] createdViews) {
        int i= 0, j = 0;
        String key;
        try {
            //JSONObject to be submitted
            parent = new JSONObject();
            parent.put("freq_id", SaveData.FREQ_ID);

            //Initialize all keys with a blank value
            for(int k=0; k < SaveData.checkedList.size(); k++){
                key = putApiParams(SaveData.checkedList.get(k).getText().toString());
                if(SaveData.checkedList.get(k).isChecked())
                    parent.put(key,1);
                else
                    parent.put(key,0);
            }
            /*Log.i("value1","Context: "+context.toString()+" LinearLayout: "+linearLayout.toString()
                    +" CreatedViews: "+ Arrays.deepToString(createdViews));*/
            //Log.i("value1","Parent: "+parent);

            //Iterate through the created views which is a 2D array
            for(int len=0; len<createdViews.length; len++) {
                switch (createdViews[len][1]) {
                    case "Check":
                        //For a checkbox write the parameter name and checked value to the JSONObject
                        //Call checked() function to see if checkbox is ticked or not
                        key = putApiParams(createdViews[len][0]);
                        checked(len);
                        parent.put(key, checkValue);
                        break;
                    case "Edit":
                        key = putApiParams(createdViews[len][0]);
                        //For an EditText write the parameter name and Text value to the JSONObject
                        parent.put(key, SaveData.editTextList.get(j++).getText().toString());
                        break;
                }
            }

            parent.put("asset_code", SaveData.ASSET_CODE);
            parent.put("eng_id", SaveData.USER_ID);
            parent.put("contractor", SaveData.ORGANISATION);

            //Set List of checked Items to NONE if none have been checked
            if(alertText[0].equals(""))
                alertText[0]= "(None)";
            //Set List of unchecked Items to NONE if none are unchecked
            if(alertText[1].equals(""))
                alertText[1]= "(None)";

            //Log.i("value1","Parent: "+parent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return alertText;
    }

    private String putApiParams(String s) {
        String[] tokens = s.split(" ");
        s = "";
        for(String str: tokens)
            s += "_" + Character.toLowerCase(str.charAt(0)) + str.substring(1);
        s = s.substring(1);
        return s;
    }

    //Function to check if a checkbox has been ticked or not
    private void checked(int i) {
        //alertText[0] stores checked Items; alertText[1] stores unchecked Items
        if(SaveData.checkedList.get(i).isChecked()){
            checkValue = 1;
            alertText[0] += SaveData.checkedList.get(i).getText().toString() + "<br>";
        }
        else{
            checkValue = 0;
            alertText[1] += SaveData.checkedList.get(i).getText().toString() + "<br>";
        }
    }

    public JSONObject getParent(){
        return parent;
    }
}
