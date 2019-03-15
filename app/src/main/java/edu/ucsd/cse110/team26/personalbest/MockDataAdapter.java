package edu.ucsd.cse110.team26.personalbest;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MockDataAdapter implements IDataAdapter {

    List<User> friendsList;
    List<User> receivedFriendRequests;
    List<User> sentFriendRequests;

    String userEmail;

    Map<String, ArrayList<Message>> dataBase = new HashMap<String, ArrayList<Message>>();
    Map<String, Callback<Message>> dataBase2 = new HashMap<String, Callback<Message>>();


    MockDataAdapter() {
        friendsList = new ArrayList<User>();
        receivedFriendRequests = new ArrayList<User>();
        sentFriendRequests = new ArrayList<User>();
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

    }

    /**
     * Updates the logged-in user's height stored in the database
     * Calls given callback with true or false depending on if the server request was successful.
     *
     * @param height User's height to update
     * @param booleanCallback callback lambda to handle success/failure
     */
    @Override
    public void updateUserHeight(int height, Callback<Boolean> booleanCallback) {
        booleanCallback.call(true);
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
    }

    @Override
    public void getDays(int numOfDays, Callback<List<Day>> dayCallback) {

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
        booleanCallback.call(true);
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
        userCallback.call(sentFriendRequests);
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
        userCallback.call(receivedFriendRequests);
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
     * If called twice with same email, makes friend request to current user from user of the
     * given email address
     *
     * @param friendEmail  the email to make a request to
     * @param userCallback callback to handle resulting list of users
     */
    @Override
    public void makeFriendRequest(String friendEmail, Callback<List<User>> userCallback) {
        User user = new User(0, "name", friendEmail, "1");
        if( sentFriendRequests.contains(user) )
            receivedFriendRequests.add(new User(0, "name", friendEmail, "1"));
        else
            sentFriendRequests.add(new User(0, "name", friendEmail, "1"));
    }

    /**
     * Accepts the friend request made by the given requester to the currently logged in user.
     * Calls given callback with true or false depending on if the server request was successful.
     *
     * @param requesterEmail  the UID of the requester
     * @param booleanCallback callback to handle success/failure
     */
    @Override
    public void acceptFriendRequest(String requesterEmail, Callback<Boolean> booleanCallback) {
        if( receivedFriendRequests.contains(new User(0, "name", requesterEmail, "1"))) {
            friendsList.add(new User(0, "name", requesterEmail, "1"));
            booleanCallback.call(true);
        } else {
            booleanCallback.call(false);
        }
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
        friendsList.remove(new User(0, "name", requesterEmail, "1"));
    }

    /**
     * Deletes the given friend from the current user's friends
     *
     * @param friendEmail     email of friend to delete
     * @param booleanCallback callback to handle success/failure of request
     */
    @Override
    public void deleteFriend(String friendEmail, Callback<Boolean> booleanCallback) {

    }

    @Override
    public void sendMessage(String chatId, String text, Callback<Boolean> booleanCallback) {

        Message msg = new Message("Bob@gmail.com",text);
        System.out.println(msg);
        if(dataBase.get(chatId) == null){
            dataBase.put(chatId, new ArrayList<>());
        }
        dataBase.get(chatId).add(msg);

        if(dataBase2.get(chatId)!=null){
            dataBase2.get(chatId).call(msg);

        }
        booleanCallback.call(true);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void startChatListener(String chatId, Callback<Message> messageCallback) {


        if(dataBase.get(chatId).size()!=0){
            ArrayList<Message> messages = dataBase.get(chatId);
            for(Message m : messages) {
                messageCallback.call(m);

            }
            dataBase2.put(chatId,messageCallback);
        }

    }

    @Override
    public void subscribeToChatNotifications(String chatId) {

    }
}
