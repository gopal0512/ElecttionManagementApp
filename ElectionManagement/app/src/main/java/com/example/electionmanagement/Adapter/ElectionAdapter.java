package com.example.electionmanagement.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electionmanagement.Activity.CandidateListActivity;
import com.example.electionmanagement.Model.Election;
import com.example.electionmanagement.R;
import com.example.electionmanagement.Utils.DateTimeUtils;

import java.util.List;

public class ElectionAdapter extends RecyclerView.Adapter<ElectionAdapter.H> {
    private List<Election> list;
    private Context context;
    private OnElectionActionListener listener;

    // Constructor
    public ElectionAdapter(Context context, List<Election> list, OnElectionActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    class H extends RecyclerView.ViewHolder {
        TextView tvTitle, tvState, tvTime, tvStatus;
        H(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvState = v.findViewById(R.id.tvState);
            tvTime = v.findViewById(R.id.tvTime);
            tvStatus = v.findViewById(R.id.tvStatus);
        }
    }

    @Override
    public H onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_election, parent, false);
        return new H(v);
    }

    @Override
    public void onBindViewHolder(H holder, int pos) {
        Election e = list.get(pos);

        holder.tvTitle.setText(e.title);
        holder.tvState.setText(e.state);
        holder.tvTime.setText(e.startIso + " → " + e.endIso);

        boolean active = DateTimeUtils.isNowBetween(e.startIso, e.endIso);
        holder.tvStatus.setText(active ? "Ongoing" : (DateTimeUtils.isEnded(e.endIso) ? "Ended" : "Upcoming"));

        // Open candidate list
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, CandidateListActivity.class);
            i.putExtra("election_id", e.id);
            context.startActivity(i);
        });

        // Edit/Delete actions
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Election Options")
                    .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                        if (listener != null) {
                            if (which == 0) {
                                listener.onEditElection(e);
                            } else {
                                listener.onDeleteElection(e);
                            }
                        }
                    }).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Listener interface for Activity to handle DB operations
    public interface OnElectionActionListener {
        void onEditElection(Election election);
        void onDeleteElection(Election election);
    }
}
