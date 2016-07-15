package com.cmrl.cmrllite;

import android.content.Context;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class FetchData {

    private Map<String, String> params;
    Context context;
    LinearLayout linearLayout;
    String createdViews[][];


    public FetchData(Context context){
        this.context = context;
        this.createdViews= new String[100][2];
        for(int i = 0; i< createdViews.length; i++){
            createdViews[i][0] = "";
            createdViews[i][1] = "";
        }
    }

    public Map<String, String> getApiParam(JSONObject jsonResponse) {

        Iterator<String> keys = jsonResponse.keys();
        params = new HashMap<>();
        String parameter = "";
        String infoRead ="";

        //Iterate through the keys
        while(keys.hasNext()){
            //Add key's name to List of Parameters
            parameter = keys.next();
            infoRead ="";
            try {
            infoRead = jsonResponse.getString(parameter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Parse the key name & replace '_' with SPACE and Replace the lowercase letter following it, with Uppercase letter
            String label = parameter.replace("_"," ");
            String[] tokens = label.split(" ");
            label = "";
            for(String str: tokens)
                label += Character.toUpperCase(str.charAt(0)) + str.substring(1) + " ";

            params.put(label, infoRead);
            //Log.i("value1", "label: " + label + "  infoRead: " + infoRead);
        }
        return params;
    }

    //Function for Dynamic Creation of Views --> Checkboxes, EditTexts and Buttons
    public String[][] createViews(Map<String, String> params, LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
        int viewCount=0;

        for (Object o : params.entrySet()) {
            Map.Entry thisEntry = (Map.Entry) o;
            //Create EditText whenever there's a "yes" and add it to the 2D array of created Views
            if (thisEntry.getValue().equals("1")) {
                createdViews[viewCount][0] = thisEntry.getValue().toString();
                createdViews[viewCount++][1] = "Check";
                //Log.i("value","Text: "+ thisEntry.getKey());
                new CreateLayout(context, linearLayout).createCheckedTextView(thisEntry.getKey().toString());
            }
            //Create EditText whenever there's a "yes" and add it to the 2D array of created Views
            else if (thisEntry.getValue().equals("yes")) {
                createdViews[viewCount][0] = thisEntry.getValue().toString();
                createdViews[viewCount++][1] = "Edit";
                new CreateLayout(context, linearLayout).createEditText();
            }
        }

        createdViews[viewCount][0] = "description";
        createdViews[viewCount][1] = "Edit";
        new CreateLayout(context, linearLayout).createEditText();

        return createdViews;
    }
}
