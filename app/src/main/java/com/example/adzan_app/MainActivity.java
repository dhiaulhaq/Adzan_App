package com.example.adzan_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    //url
    String url;
    // Tag used to cancel the request
    String tag_json_obj = "json_obj_req";
    //ProgressDialog
    ProgressDialog pDialog;

    TextView mShubuhTv, mDzuhurTv, mAsharTv, mMaghribTv, mIsyaTv, mLocationTv, mDateTv;
    EditText mSearchEt;
    Button mSearchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShubuhTv = findViewById(R.id.shubuhTv);
        mDzuhurTv = findViewById(R.id.dzuhurTv);
        mAsharTv = findViewById(R.id.asharTv);
        mMaghribTv = findViewById(R.id.maghribTv);
        mIsyaTv = findViewById(R.id.isyaTv);
        mLocationTv = findViewById(R.id.locationTv);
        mDateTv = findViewById(R.id.dateTv);
        mSearchEt = findViewById(R.id.searchEt);
        mSearchBtn = findViewById(R.id.searchBtn);

        //handle button click
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get text from EditText
                String mLocation = mSearchEt.getText().toString().trim();
                //validate if it is null or not
                if (mLocation.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter location", Toast.LENGTH_SHORT).show();
                } else {
                    //function to get location
                    url = "https://muslimsalat.com/" + mLocation + ".json?key=5a9f16754733539f303d99f95c51380d";
                    searchLocation();
                }
            }
        });

        //alarm notification
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);

        Intent intent = new Intent("com.example.adzan_app.action.DISPLAY_NOTIFICATION");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
    }

    private void searchLocation() {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            //get data form JSON
                            try {
                                //get location
                                String country = response.get("country").toString();
                                String state = response.get("state").toString();
                                String city = response.get("city").toString();
                                String location = country +", "+ state +", "+ city;

                                //get date
                                String date = response.getJSONArray("items").getJSONObject(0).get("date_for").toString();

                                //get time
                                String mShubuh = response.getJSONArray("items").getJSONObject(0).get("fajr").toString();
                                String mDzuhur = response.getJSONArray("items").getJSONObject(0).get("dhuhr").toString();
                                String mAshar = response.getJSONArray("items").getJSONObject(0).get("asr").toString();
                                String mMaghrib = response.getJSONArray("items").getJSONObject(0).get("maghrib").toString();
                                String mIsya = response.getJSONArray("items").getJSONObject(0).get("isha").toString();

                                //set data to TextView
                                mShubuhTv.setText(mShubuh);
                                mDzuhurTv.setText(mDzuhur);
                                mAsharTv.setText(mAshar);
                                mMaghribTv.setText(mMaghrib);
                                mIsyaTv.setText(mIsya);
                                mLocationTv.setText(location);
                                mDateTv.setText(date);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            pDialog.hide();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                    // hide the progress dialog
                    pDialog.hide();
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }

}
