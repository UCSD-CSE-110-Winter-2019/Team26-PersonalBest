package edu.ucsd.cse110.team26.personalbest;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FirestoreAdapter implements IDataAdapter {
    private final static String TAG = "FirestoreAdapter";
    private FirebaseFirestore db;
    private FirebaseFunctions funcs;
    private String userEmail;

    FirestoreAdapter(Context context) {
        db = FirebaseFirestore.getInstance();
        funcs = FirebaseFunctions.getInstance("us-central1");
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(context);
        userEmail = lastSignedInAccount.getEmail();
        Log.i(TAG, "name: " + lastSignedInAccount.getDisplayName() + " email: " + userEmail);
    }

    /**
     * Get today's data currently stored in the database.
     * Calls the given lambda with the resulting Day, or null if request failed.
     *
     * @param dayCallback callback lambda to handle the resulting Day
     */
    @Override
    public void getToday(Callback<Day> dayCallback) {
        
    }

    /**
     * Gets the logged-in user's data stored in the database
     * Calls the given lambda with the fetched user's data, or null if request failed.
     *
     * @param userCallback callback lambda to handle the user's data
     */
    @Override
    public void getUser(Callback<User> userCallback) {
        db.collection("users").document(userEmail).get().addOnSuccessListener(user -> {
            if(user.exists()) {

            }
        });

    }

    /**
     * Updates the logged-in user's data stored in the database
     * Calls given callback with true or false depending on if the server request was successful.
     *
     * @param user User's data to update
     * @param booleanCallback callback lambda to handle success/failure
     */
    @Override
    public void updateUser(User user, Callback<Boolean> booleanCallback) {
        Map<String, Object> u = new HashMap<>();
        u.put("name", user.name);
        u.put("height", user.height);

        db.collection("users").document(user.email)
                .set(u, SetOptions.merge())
                .addOnSuccessListener(a -> booleanCallback.call(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, e.getLocalizedMessage());
                    booleanCallback.call(false);
                });

    }

    /**
     * Gets the profile information of the friend with given email.
     * Calls given callback with resulting data, or empty list if no such friend was found,
     * or null list if server request failed.
     *
     * @param friendEmail  email of friend to look up
     * @param userCallback callback lambda to handle result
     */
    @Override
    public void getFriend(String friendEmail, Callback<List<User>> userCallback) {
        db.collection("users").document(userEmail)
                .collection("friends").document(friendEmail).get().addOnCompleteListener((task) -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if(doc.exists() && doc.getData().get("status").toString().equals("friends")) {
                            db.collection("users").document(friendEmail).get().addOnCompleteListener((t) -> {
                                if(t.isSuccessful()) {
                                    DocumentSnapshot friend = t.getResult();
                                    if(friend.exists()) {
                                        Map<String, Object> friendData = friend.getData();
                                        ArrayList<User> friendList = new ArrayList<>();
                                        friendList.add(new User(0, friendData.get("name").toString(), friendData.get("email").toString(), ""));
                                        userCallback.call(friendList);
                                    } else {
                                        Log.d(TAG, "Friend not found");
                                        userCallback.call(new ArrayList<>());
                                    }
                                } else {
                                    Log.d(TAG, "failed with ", t.getException());
                                    userCallback.call(null);
                                }
                            });
                        } else {
                            Log.d(TAG, "Friend not found");
                            userCallback.call(new ArrayList<>());
                        }
                    } else {
                        Log.d(TAG, "failed with ", task.getException());
                        userCallback.call(null);
                    }
                });
    }

    /**
     * Gets the last numOfDays Days of data of the friend with the specified ID,
     * calling the passed in callback with the resulting List of Day, or null if
     * the server request failed.
     *
     * @param friendID    the uid of the friend to fetch
     * @param numOfDays   the number of days to fetch before today, today inclusive
     * @param dayCallback lambda to handle the resulting List of Days
     */
    @Override
    public void getFriendDays(String friendID, int numOfDays, Callback<List<Day>> dayCallback) {

    }

    /**
     * Updates the database with the given days' data.
     *
     * @param days            List of days to update the database with.
     * @param booleanCallback callback to handle success/failure
     */
    @Override
    public void updateDays(List<Day> days, Callback<Boolean> booleanCallback) {

    }

    /**
     * Gets the public data of users who have been sent friend requests by the logged in user.
     * Calls given callback with List of Users who have been sent friend requests from the
     * logged in user, who have not accepted the request (i.e. pending outgoing requests).
     * Calls given callback with null list if server reqeust is unsuccessful.
     *
     * @param userCallback callback to handle resulting list of users
     */
    @Override
    public void getSentFriendRequests(Callback<List<User>> userCallback) {

    }

    /**
     * Gets the public data of users who have sent friend requests to the logged in user.
     * Calls given callback with List of Users who have sent friend requests to the
     * logged in user, whose requests have not been accepted (i.e. pending incoming requests).
     *
     * @param userCallback callback to handle resulting list of users
     */
    @Override
    public void getReceivedFriendRequests(Callback<List<User>> userCallback) {

    }

    /**
     * Gets the public data of all users who are friends with the currently logged in user.
     *
     * @param userCallback callback to handle the resulting friend list
     */
    @Override
    public void getFriends(Callback<List<User>> userCallback) {

    }

    /**
     * Attempts to send a friend request from the logged in user to the user of the given email
     * address.
     * If the email is found and request successfully made, calls the given callback with the public
     * info of the requestee. Otherwise returns an empty User List.
     * If server request failed, calls callback with null User List.
     *
     * @param friendEmail  the email to make a request to
     * @param userCallback callback to handle resulting list of users
     */
    @Override
    public void makeFriendRequest(String friendEmail, Callback<List<User>> userCallback) {
        Log.d(TAG, "Starting friend request to " + friendEmail);
        db.collection("users").document(friendEmail).get()
                .addOnSuccessListener(friend -> {
                    if(friend.exists()) {
                        Log.d(TAG, friendEmail + " is a user");
                        Map<String, Object> data = new HashMap<>();
                        data.put("requesterEmail", userEmail);
                        data.put("requesteeEmail", friendEmail);
                        data.put("reqType", "REQUEST");

                        funcs.getHttpsCallable("handleFriendRequest").call(data)
                                .addOnSuccessListener(result -> {
                                    Log.d(TAG, "Friend request successful");
                                    Map<String, Object> friendData = friend.getData();
                                    ArrayList<User> friendList = new ArrayList<>();
                                    friendList.add(new User(0, friendData.get("name").toString(), friendData.get("email").toString(), ""));
                                    userCallback.call(friendList);
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "failed with ", e);
                                    userCallback.call(null);
                                });
                    } else {
                        Log.d(TAG, "Friend not found");
                        userCallback.call(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "failed with ", e);
                    userCallback.call(null);
                });
    }

    /**
     * Accepts the friend request made by the given requester to the currently logged in user.
     * calls given callback with true or false depending on if the server request was successful.
     *
     * @param friendEmail the UID of the requester
     * @param booleanCallback callback to handle success/failure
     */
    @Override
    public void acceptFriendRequest(String friendEmail, Callback<Boolean> booleanCallback) {

        Log.d(TAG, "Accepting friend request from " + friendEmail);
        db.collection("users").document(friendEmail).get()
                .addOnSuccessListener(friend -> {
                    if(friend.exists()) {
                        Log.d(TAG, friendEmail + " is a user");
                        Map<String, Object> data = new HashMap<>();
                        data.put("requesterEmail", userEmail);
                        data.put("requesteeEmail", friendEmail);
                        data.put("reqType", "ACCEPT");

                        funcs.getHttpsCallable("handleFriendRequest").call(data)
                                .addOnSuccessListener(result -> {
                                    Log.d(TAG, "Friend request accepted");
                                    booleanCallback.call(true);
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "failed with ", e);
                                    booleanCallback.call(false);
                                });
                    } else {
                        Log.d(TAG, "Friend not found");
                        booleanCallback.call(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "failed with ", e);
                    booleanCallback.call(false);
                });
    }

    /**
     * Rejects the friend request made by the given requester to the currently logged in user.
     * Calls given callback with true or false depending on if the server request was successful.
     *
     * @param requesterEmail  the UID of the requester
     * @param booleanCallback callback to handle success/failure
     */
    @Override
    public void rejectFriendRequest(String requesterEmail, Callback<Boolean> booleanCallback) {
        this.deleteFriend(requesterEmail, booleanCallback);
    }

    /**
     * Deletes the given friend from the current user's friends
     *
     * @param friendEmail     email of friend to delete
     * @param booleanCallback callback to handle success/failure of request
     */
    @Override
    public void deleteFriend(String friendEmail, Callback<Boolean> booleanCallback) {

        Log.d(TAG, "Deleting friend from " + friendEmail);
        db.collection("users").document(friendEmail).get()
                .addOnSuccessListener(friend -> {
                    if(friend.exists()) {
                        Log.d(TAG, friendEmail + " is a user");
                        Map<String, Object> data = new HashMap<>();
                        data.put("requesterEmail", userEmail);
                        data.put("requesteeEmail", friendEmail);
                        data.put("reqType", "DELETE");

                        funcs.getHttpsCallable("handleFriendRequest").call(data)
                                .addOnSuccessListener(result -> {
                                    Log.d(TAG, "Friend removed");
                                    booleanCallback.call(true);
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "failed with ", e);
                                    booleanCallback.call(false);
                                });
                    } else {
                        Log.d(TAG, "Friend not found");
                        booleanCallback.call(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "failed with ", e);
                    booleanCallback.call(false);
                });

    }
}
