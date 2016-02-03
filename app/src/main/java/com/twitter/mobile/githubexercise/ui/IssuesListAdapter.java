package com.twitter.mobile.githubexercise.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twitter.mobile.githubexercise.R;
import com.twitter.mobile.githubexercise.dao.Issue;

import java.util.ArrayList;

/**
 * Created by SJ on 1/30/16.
 */
public class IssuesListAdapter extends RecyclerView.Adapter<IssuesListAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<Issue> mIssues;
    private IIssueClickListener mIssueClickListener;

    public IssuesListAdapter(ArrayList<Issue> issues, IIssueClickListener issueClickListener) {
        this.mIssues = issues;
        this.mIssueClickListener = issueClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mIssueTitleTv, mIssueBodyTv;
        private RelativeLayout mParentIssuesLayout;

        public ViewHolder(View view, View.OnClickListener onClickListener) {
            super(view);
            if (view != null) {
                mIssueTitleTv = (TextView) view.findViewById(R.id.issue_title);
                mIssueBodyTv = (TextView) view.findViewById(R.id.issue_body);
                mParentIssuesLayout = (RelativeLayout) view.findViewById(R.id.parent_issues_layout);
                mParentIssuesLayout.setOnClickListener(onClickListener);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.issues_row, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public int getItemCount() {
        return (null != mIssues) ? mIssues.size() : 0;
    }

    public void setIssues(ArrayList<Issue> issues) {
        this.mIssues = issues;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Issue issue = mIssues.get(position);
        if (holder != null) {
            holder.mIssueTitleTv.setText(issue.getIssueTitle());
            holder.mIssueBodyTv.setText(issue.getIssueBody());
            holder.mParentIssuesLayout.setTag(position);
        }
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        mIssueClickListener.onIssueClicked(mIssues.get(position));
    }

    public interface IIssueClickListener {
        void onIssueClicked(Issue issue);
    }
}
