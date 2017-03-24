package by.black_pearl.githubuserclient.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import by.black_pearl.githubuserclient.R;
import by.black_pearl.githubuserclient.gsons.SearchItem;

/**
 * Created by BLACK_Pearl.
 */

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.Holder>{
    private final Context mContext;
    private final OnItemClick mCallback;
    private List<SearchItem> mSrcItems;

    public SearchRecyclerViewAdapter(Context context, OnItemClick callback) {
        this.mContext = context;
        this.mCallback = callback;
        this.mSrcItems = new ArrayList<>();
    }

    public void changeData(List<SearchItem> items) {
        this.mSrcItems.clear();
        this.mSrcItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final int pos = position;
        boolean fromDrawable = mSrcItems.get(position).avatar_url.contains("R.drawable");
        Glide.with(mContext).load(
                fromDrawable ? R.drawable.no_logo : mSrcItems.get(position).avatar_url
        ).diskCacheStrategy(DiskCacheStrategy.RESULT)
                .fitCenter().placeholder(android.R.drawable.ic_menu_camera)
                .crossFade().into(holder.userImage);
        holder.userName.setText(mSrcItems.get(position).login);
        if (!fromDrawable) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onItemClick(mSrcItems.get(pos).login);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSrcItems.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView userImage;
        private TextView userName;
        private CardView cardView;

        Holder(View itemView) {
            super(itemView);
            userImage = (ImageView) itemView.findViewById(R.id.iv_avatar);
            userName = (TextView) itemView.findViewById(R.id.tv_user_name);
            cardView = (CardView) itemView.findViewById(R.id.cv_item);
        }
    }

    public interface OnItemClick {
        void onItemClick(String login);
    }
}
