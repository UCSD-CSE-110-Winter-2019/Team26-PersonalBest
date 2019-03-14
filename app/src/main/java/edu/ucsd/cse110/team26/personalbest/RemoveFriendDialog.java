package edu.ucsd.cse110.team26.personalbest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;

public class RemoveFriendDialog {
    User requested;
    Context context;
    private Resources resources;
    IDataAdapter dataAdapter;

    RemoveFriendDialog(User toRemove, Context context, IDataAdapter dataAdapter) {
        this.requested = toRemove;
        this.resources = context.getResources();
        this.context = context;
        this.dataAdapter = dataAdapter;
    }

    AlertDialog createDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        resources = context.getResources();
        builder.setMessage(String.format(resources.getString(R.string.remove_friend_dialog_message), requested.name));
        builder.setTitle(resources.getString(R.string.remove_friend_dialog_title));
        builder.setPositiveButton(R.string.dialog_yes, (dialog, id) -> {
            dataAdapter.rejectFriendRequest(requested.email, (removeSuccess) -> {
                if( removeSuccess ) {
                    FriendsListActivity.friendsList.friends.remove(requested);
                    FriendsListActivity.friendAdapter.notifyDataSetChanged();
                }
            });
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.dialog_no, (dialog, id) -> {
            dialog.dismiss();
        });
        return builder.create();
    }
}
