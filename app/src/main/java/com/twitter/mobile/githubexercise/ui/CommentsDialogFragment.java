package com.twitter.mobile.githubexercise.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.twitter.mobile.githubexercise.R;
import com.twitter.mobile.githubexercise.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsDialogFragment extends DialogFragment {

    public static final String EXTRA_DIALOG_TITLE = "extra_dialog_title";
    private IntentFilter mGetCommentsFilter = new IntentFilter(Constants.FILTER_ACTION_COMMENTS_LIST);
    private TextView mCommentsDialogBodyTv;
    private ProgressBar mProgressBar;

    public CommentsDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static CommentsDialogFragment newInstance(int title) {
        CommentsDialogFragment dialogFragment = new CommentsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_DIALOG_TITLE, title);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments_dialog, container, false);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        mCommentsDialogBodyTv = (TextView) view.findViewById(R.id.comments_dialog_body_text);
        Button closeButton = (Button) view.findViewById(R.id.comments_dialog_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        int title = getArguments().getInt(EXTRA_DIALOG_TITLE);
        getDialog().setTitle(title);
        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.unregisterReceiver(mGetCommentsReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.registerReceiver(mGetCommentsReceiver, mGetCommentsFilter);
    }

    private BroadcastReceiver mGetCommentsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                String action = intent.getAction();
                switch (action) {
                    case Constants.FILTER_ACTION_COMMENTS_LIST:
                        mProgressBar.setVisibility(View.GONE);
                        String dialogText = intent.getStringExtra(Constants.EXTRA_COMMENTS_LIST);
                        StringBuilder sb = new StringBuilder();
                        sb.append(System.getProperty("line.separator"));
                        if(!TextUtils.isEmpty(dialogText))
                            sb.append(dialogText);
                        else
                            sb.append(getString(R.string.no_comments_found));
                        sb.append(System.getProperty("line.separator"));
                        mCommentsDialogBodyTv.setText(sb.toString());
                        break;
                }
            }
        }
    };
}
