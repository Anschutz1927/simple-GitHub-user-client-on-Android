package by.black_pearl.githubuserclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import by.black_pearl.githubuserclient.R;
import by.black_pearl.githubuserclient.gsons.Repos;

/**
 * Created by BLACK_Pearl.
 */

public class ReposRecyclerViewAdapter extends RecyclerView.Adapter<ReposRecyclerViewAdapter.Holder> {
    private List<Repos> mReposes;
    private final Context mContext;
    private final ReposRecyclerViewAdapter.OnViewButtonClickListener mCallback;

    public ReposRecyclerViewAdapter(Context context, List<Repos> reposes, OnViewButtonClickListener listener) {
        this.mContext = context;
        this.mReposes = reposes;
        this.mCallback = listener;
    }

    public ReposRecyclerViewAdapter(Context context, OnViewButtonClickListener callback) {
        mContext = context;
        mCallback = callback;
        mReposes = new ArrayList<>();
    }

    public void changeReposData(List<Repos> reposes) {
        mReposes = reposes;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_repos, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final int pos = position;
        holder.name.setText(mReposes.get(position).name);
        String desc = mReposes.get(position).description == null
                || mReposes.get(position).description.equals("")
                ? "no description"
                : mReposes.get(position).description;
        holder.desc.setText(desc);
        holder.toReposBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReposRecyclerViewAdapter.this.mCallback.onButtonClick(mReposes.get(pos).html_url);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReposes.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView desc;
        private final ImageButton toReposBtn;

        public Holder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.tv_repos_name);
            this.desc = (TextView) itemView.findViewById(R.id.tv_repos_desc);
            this.toReposBtn = (ImageButton) itemView.findViewById(R.id.btn_opn_link);
        }
    }

    public interface OnViewButtonClickListener {
        void onButtonClick(String url);
    }
}
