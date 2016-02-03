package com.twitter.mobile.githubexercise.dao;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SJ on 1/30/16.
 */
public class Issue implements Serializable, Comparable<Issue> {

    private static final String TAG = "Issue";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final String KEY_UPDATED_AT = "updated_at";
    private static final String KEY_COMMENTS_URL = "comments_url";
    private String mIssueTitle;
    private String mIssueBody;
    private String mCommentsUrl;
    private Date mUpdatedAtDate;

    public Issue(JSONObject issueObject) {
        if(null != issueObject) {
            mIssueTitle = issueObject.optString(KEY_TITLE);
            mIssueBody = issueObject.optString(KEY_BODY);
            String updatedAt = issueObject.optString(KEY_UPDATED_AT);
            mUpdatedAtDate = getDate(updatedAt);
            mCommentsUrl = issueObject.optString(KEY_COMMENTS_URL);
        }
    }

    public String getIssueTitle() {
        return mIssueTitle;
    }

    public String getIssueBody() {
        return mIssueBody;
    }

    public String getKeyCommentsUrl() {
        return mCommentsUrl;
    }

    public Date getUpdatedAtDate() {
        return mUpdatedAtDate;
    }

    private Date getDate(String dateInString) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            date = sdf.parse(dateInString);
        } catch(ParseException pe) {
            Log.e(TAG, "Date parse exception occurred: " + pe.getMessage());
        }
        return date;
    }

    @Override
    public int compareTo(Issue o) {
        return getUpdatedAtDate().compareTo(o.getUpdatedAtDate());
    }
}
