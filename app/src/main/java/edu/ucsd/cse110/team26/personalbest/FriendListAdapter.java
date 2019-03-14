package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.util.Log;
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
    private FriendType type;

    FriendListAdapter(Context context, IDataAdapter dataAdapter){
        this.context = context;
        this.dataAdapter = dataAdapter;
    }

    @Override
    public int getCount() {
        return FriendsListActivity.friendsList.sentRequests.size() + FriendsListActivity.friendsList.receivedRequests.size()
                + FriendsListActivity.friendsList.friends.size();
    }

    @Override
    public Object getItem(int position) {
        return getFriend(position);
    }

    private User getFriend(int position) {
        if( position < FriendsListActivity.friendsList.receivedRequests.size()) {
            type = FriendType.RECEIVEDREQUEST;
            Log.i(getClass().getName(), "Received request at list position" + position);
            return FriendsListActivity.friendsList.receivedRequests.get(position);
        } else if( position - FriendsListActivity.friendsList.receivedRequests.size()
                < FriendsListActivity.friendsList.sentRequests.size()) {
            int newPos = position - FriendsListActivity.friendsList.receivedRequests.size();
            type = FriendType.SENTREQUEST;
            Log.i(getClass().getName(), "Sent request at list position" + position);
            return FriendsListActivity.friendsList.sentRequests.get(newPos);
        } else {
            int newPos = position - (FriendsListActivity.friendsList.sentRequests.size() +
                    FriendsListActivity.friendsList.receivedRequests.size());
            type = FriendType.FRIEND;
            Log.i(getClass().getName(), "Friend at list position" + position);
            return FriendsListActivity.friendsList.friends.get(newPos);
        }
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

        User friend = getFriend(position);

        switch(type) {
            case FRIEND:
                holder.pending.setVisibility(View.GONE); break;
            case RECEIVEDREQUEST:
                holder.pending.setVisibility(View.VISIBLE);
                holder.acceptRequest.setVisibility(View.VISIBLE);
                holder.rejectRequest.setVisibility(View.VISIBLE); break;
            case SENTREQUEST:
                holder.pending.setVisibility(View.VISIBLE);
                holder.acceptRequest.setVisibility(View.GONE);
                holder.rejectRequest.setVisibility(View.GONE); break;
        }

        holder.friendName.setText(friend.name);
        holder.friendEmail.setText(friend.email);

        holder.acceptRequest.setOnClickListener(v -> dataAdapter.acceptFriendRequest(friend.email, (acceptRequestSuccess) -> {

        if( acceptRequestSuccess ) {
            holder.pending.setVisibility(View.GONE);
            FriendsListActivity.friendsList.receivedRequests.remove(position);
            FriendsListActivity.friendsList.friends.add(friend);
            notifyDataSetChanged();
        }
    }));

        holder.rejectRequest.setOnClickListener(v -> dataAdapter.rejectFriendRequest(friend.email, (rejectRequestSuccess) -> {

            if( rejectRequestSuccess ) {
                FriendsListActivity.friendsList.receivedRequests.remove(position);
                notifyDataSetChanged();
            }
        }));


        return convertView;
    }

    private class ViewHolder {
        ImageButton acceptRequest, rejectRequest;
        TextView friendName, friendEmail;
        LinearLayout pending;
    }

    private enum FriendType {
        FRIEND,
        SENTREQUEST,
        RECEIVEDREQUEST
    }
}
