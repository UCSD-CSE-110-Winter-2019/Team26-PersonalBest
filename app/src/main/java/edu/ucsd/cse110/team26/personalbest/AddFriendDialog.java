package edu.ucsd.cse110.team26.personalbest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


public class AddFriendDialog {
    Context context;
    private Resources resources;
    private IDataAdapter dataAdapter;

    public AddFriendDialog( Context context, IDataAdapter dataAdapter) {
        this.resources = context.getResources();
        this.context = context;
        this.dataAdapter = dataAdapter;
    }

    public AlertDialog createDialog() {
        Log.i(getClass().getName(), "Creating Add Friend Dialog box");
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        resources = context.getResources();
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint(R.string.friend_email);
        builder.setView(input);
        builder.setTitle("Add Friend");
        builder.setPositiveButton(R.string.confirm, (dialog, id) -> {
            dataAdapter.makeFriendRequest(input.getText().toString(), (friendsList) -> {
                        Log.i(getClass().getName(), "Getting result of friend request");
                        if (friendsList == null) {
                            Toast invToast = Toast.makeText(context,
                                    "Server Request failed",
                                    Toast.LENGTH_SHORT);
                            invToast.show();
                        } else if (friendsList.isEmpty()) {
                            Toast invToast = Toast.makeText(context,
                                    "Invalid email address",
                                    Toast.LENGTH_SHORT);
                            invToast.show();
                        } else {
                            Log.i(getClass().getName(), "Dismissing dialog because of successful friend request");
                            if( !FriendsListActivity.friendsList.sentRequests.contains(friendsList)) {
                                FriendsListActivity.friendsList.sentRequests.add(friendsList.get(0));
                            }
                            FriendsListActivity.friendAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });

        }).setNegativeButton(R.string.cancel, (dialog, id) -> {
            dialog.dismiss();
        });

        return builder.create();
    }

}
