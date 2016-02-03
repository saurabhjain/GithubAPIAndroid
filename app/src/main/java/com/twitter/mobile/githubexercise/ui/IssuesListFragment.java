package com.twitter.mobile.githubexercise.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.twitter.mobile.githubexercise.R;
import com.twitter.mobile.githubexercise.api.ApiHelper;
import com.twitter.mobile.githubexercise.dao.Issue;
import com.twitter.mobile.githubexercise.utils.Constants;
import com.twitter.mobile.githubexercise.utils.DividerItemDecoration;
import com.twitter.mobile.githubexercise.utils.Utils;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class IssuesListFragment extends Fragment {

    private static final String TAG = "IssuesListFragment";
    private static final String COMMENTS_DIALOG_TAG = "comments_dialog_tag";
    private RecyclerView mRecyclerView;
    private IssuesListAdapter mAdapter;
    private ProgressBar mProgressBar;
    private IntentFilter mGetIssuesFilter = new IntentFilter(Constants.FILTER_ACTION_ISSUES_LIST);

    private IssuesListAdapter.IIssueClickListener issueClickListener = new IssuesListAdapter.IIssueClickListener() {
        @Override
        public void onIssueClicked(Issue issue) {
            if(issue != null) {
                fetchComments(issue.getKeyCommentsUrl());// Load only on demand(Per assignment instructions: Minimize network bandwidth where possible)
                DialogFragment dialogFragment = CommentsDialogFragment.newInstance(R.string.comments_dialog_title);
                dialogFragment.show(getActivity().getSupportFragmentManager(), COMMENTS_DIALOG_TAG);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.issues_rv);
        mRecyclerView.setHasFixedSize(true);//rv optimizations for fixed sized rv
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mAdapter = new IssuesListAdapter(null, issueClickListener);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchIssues();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.unregisterReceiver(mGetIssuesReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.registerReceiver(mGetIssuesReceiver, mGetIssuesFilter);
    }

    private void fetchIssues() {
        mProgressBar.setVisibility(View.VISIBLE);
        new FetchServerDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                String.format(Constants.ISSUES_REPOSITORY_URL,
                        Constants.RAILS_REPOSITORY_NAME, Constants.RAILS_ORGANIZATION_NAME));
    }

    private void fetchComments(String url) {
        new FetchServerDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    private class FetchServerDataTask extends AsyncTask<String, Void, Integer> {
        private Activity mActivity;

        public FetchServerDataTask() {
            mActivity = getActivity();
            if (!Utils.isActivityUsable(mActivity)) {
                this.cancel(true);
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            return ApiHelper.fetchServerData(params[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0)
                Toast.makeText(mActivity, getString(R.string.error_fetching_server_data), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mActivity, getString(R.string.fetched_server_data), Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver mGetIssuesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                switch (action) {
                    case Constants.FILTER_ACTION_ISSUES_LIST:
                        mProgressBar.setVisibility(View.GONE);
                        ArrayList<Issue> issues = (ArrayList<Issue>) intent.getSerializableExtra(Constants.EXTRA_ISSUES_LIST);
                        reloadIssuesData(issues);
                        break;
                }
            }
        }
    };

    private void reloadIssuesData(ArrayList<Issue> issues) {
        if (mAdapter == null) {
            mAdapter = new IssuesListAdapter(issues, issueClickListener);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setIssues(issues);
            mAdapter.notifyDataSetChanged();
        }
    }
}
