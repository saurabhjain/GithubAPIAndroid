package com.twitter.mobile.githubexercise.api;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.twitter.mobile.githubexercise.GithubExerciseApplication;
import com.twitter.mobile.githubexercise.dao.Issue;
import com.twitter.mobile.githubexercise.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by SJ on 1/30/16.
 */
public class ApiHelper {

    private static final String TAG = "ApiHelper";
    private static final String KEY_STATE = "state";
    private static final String KEY_USER = "user";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_BODY = "body";
    private static final String STATE_OPEN = "open";

    public ApiHelper() {}

    public static int fetchServerData(String serverUrl) {
        InputStream is = null;
        HttpURLConnection urlConnection = null;
        Integer result = 0;

        try {
            // form url
            URL url = new URL(serverUrl);

            // open connection
            urlConnection = (HttpURLConnection) url.openConnection();

            // set request headers
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");

            // GET request
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();

            if(statusCode == 200) {
                is = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(is);
                if(serverUrl.equalsIgnoreCase(String.format(Constants.ISSUES_REPOSITORY_URL,
                        Constants.RAILS_REPOSITORY_NAME, Constants.RAILS_ORGANIZATION_NAME)))
                    result = parseIssuesResponse(response);
                else
                    result = parseCommentsResponse(response);
            } else {
                result = 0; // Failed to fetch the data
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception occurred: " + e.getMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream is) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line = "", result = "";
        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        // close stream
        if(null != is) {
            is.close();
        }
        return  result;
    }

    private static int parseIssuesResponse(String jsonString) {
        if(TextUtils.isEmpty(jsonString)) {
            return 0; // Failed to parse the data
        }
        ArrayList<Issue> issues = new ArrayList<>();
        try {
            JSONArray rootArray = new JSONArray(jsonString);
            for (int i = 0; i < rootArray.length(); i++) {
                if(!rootArray.optJSONObject(i).optString(KEY_STATE).equalsIgnoreCase(STATE_OPEN)) continue;

                issues.add(new Issue(rootArray.optJSONObject(i)));
            }
        } catch(JSONException je) {
            Log.e(TAG, "JSONException occurred: " + je.getMessage());
        }

        Collections.sort(issues, Collections.reverseOrder());
        Intent intent = new Intent(Constants.FILTER_ACTION_ISSUES_LIST);
        intent.putExtra(Constants.EXTRA_ISSUES_LIST, issues);
        LocalBroadcastManager.getInstance(GithubExerciseApplication.getAppContext()).sendBroadcast(intent);
        return 1; // Successfully fetched and parsed the data
    }

    private static int parseCommentsResponse(String jsonString) {
        if(TextUtils.isEmpty(jsonString)) {
            return 0; // Failed to parse the data
        }
        StringBuilder sb = new StringBuilder();
        try {
            JSONArray rootArray = new JSONArray(jsonString);
            for (int i = 0; i < rootArray.length(); i++) {
                if(i!=0) {
                    sb.append(System.getProperty("line.separator"));
                    sb.append(System.getProperty("line.separator"));
                    sb.append(System.getProperty("line.separator"));
                }
                sb.append(rootArray.optJSONObject(i).optString(KEY_BODY));
                sb.append(System.getProperty("line.separator"));
                sb.append(rootArray.optJSONObject(i).optJSONObject(KEY_USER).optString(KEY_LOGIN));
            }
        } catch(JSONException je) {
            Log.e(TAG, "JSONException occurred: " + je.getMessage());
        }

        Intent intent = new Intent(Constants.FILTER_ACTION_COMMENTS_LIST);
        intent.putExtra(Constants.EXTRA_COMMENTS_LIST, sb.toString());
        LocalBroadcastManager.getInstance(GithubExerciseApplication.getAppContext()).sendBroadcast(intent);
        return 1; // Successfully fetched and parsed the data
    }

}
