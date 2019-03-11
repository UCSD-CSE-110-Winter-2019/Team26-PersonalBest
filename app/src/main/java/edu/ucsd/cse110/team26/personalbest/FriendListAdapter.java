package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FriendListAdapter extends BaseAdapter {
    private Context context;
    private IDataAdapter dataAdapter;

    FriendListAdapter(Context context, IDataAdapter dataAdapter){
        this.context = context;
        this.dataAdapter = dataAdapter;
    }

    @Override
    public int getCount() {
        return FriendsListActivity.friends.size();
    }

    @Override
    public Object getItem(int position) {
        return FriendsListActivity.friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.friend_item_list, parent, false);


            holder.acceptRequest = (ImageButton) convertView.findViewById(R.id.acceptFriendRequest);
            holder.rejectRequest = (ImageButton) convertView.findViewById(R.id.rejectFriendRequest);
            holder.friendEmail = (TextView) convertView.findViewById(R.id.friendEmail);
            holder.friendName = (TextView) convertView.findViewById(R.id.friendName);
            holder.pending = (LinearLayout) convertView.findViewById(R.id.pendingFriend);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.friendName.setText(FriendsListActivity.friends.get(position).name);
        holder.friendEmail.setText(FriendsListActivity.friends.get(position).email);

        holder.acceptRequest.setOnClickListener(v -> dataAdapter.acceptFriendRequest(FriendsListActivity.friends.get(position).name, (acceptRequestSuccess) -> {

        if( acceptRequestSuccess ) {
            holder.pending.setVisibility(View.GONE);
        }
    }));

        holder.rejectRequest.setOnClickListener(v -> {
            //reject friend's request
            //TODO

        });

        return convertView;
    }

    private class ViewHolder {
        ImageButton acceptRequest, rejectRequest;
        TextView friendName, friendEmail;
        LinearLayout pending;
    }

}
