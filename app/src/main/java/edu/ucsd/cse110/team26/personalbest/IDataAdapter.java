package edu.ucsd.cse110.team26.personalbest;

import java.util.List;

interface IDataAdapter {

    /**
     * Get today's data currently stored in the database.
     * Calls the given lambda with the resulting Day, or null if request failed.
     *
     * @param dayCallback callback lambda to handle the resulting Day
     */
    void getToday(Callback<Day> dayCallback);

    /**
     * Gets the logged-in user's data stored in the database
     * Calls the given lambda with the fetched user's data, or null if request failed.
     *
     * @param userCallback callback lambda to handle the user's data
     */
    void getUser(Callback<User> userCallback);

    /**
     * Updates the logged-in user's height stored in the database
     * Calls given callback with true or false depending on if the server request was successful.
     *
     * @param height User's height to update
     * @param booleanCallback callback lambda to handle success/failure
     */
    void updateUserHeight(int height, Callback<Boolean> booleanCallback);

    /**
     * Gets the profile information of the friend with given email.
     * Calls given callback with resulting data, or empty list if no such friend was found,
     * or null list if server request failed.
     *
     * @param friendEmail email of friend to look up
     * @param userCallback callback lambda to handle result
     */
    void getFriend(String friendEmail, Callback<List<User>> userCallback);

    /**
     * Gets the last numOfDays Days of data of the currently logged in user,
     * calling the passed in callback with the resulting List of Day, or null if
     * the server request failed.
     *
     * @param numOfDays the number of days to fetch before today, today inclusive
     * @param dayCallback lambda to handle the resulting List of Days
     */
    void getDays(int numOfDays, Callback<List<Day>> dayCallback);

    /**
     * Gets the last numOfDays Days of data of the friend with the specified ID,
     * calling the passed in callback with the resulting List of Day, or null if
     * the server request failed.
     *
     * @param friendEmail the email of the friend to fetch
     * @param numOfDays the number of days to fetch before today, today inclusive
     * @param dayCallback lambda to handle the resulting List of Days
     */
    void getFriendDays(String friendEmail, int numOfDays, Callback<List<Day>> dayCallback);

    /**
     * Updates the database with the given days' data.
     *
     * @param days List of days to update the database with.
     * @param booleanCallback callback to handle success/failure
     */
    void updateDays(List<Day> days, Callback<Boolean> booleanCallback);

    /**
     * Gets the public data of users who have been sent friend requests by the logged in user.
     * Calls given callback with List of Users who have been sent friend requests from the
     * logged in user, who have not accepted the request (i.e. pending outgoing requests).
     * Calls given callback with null list if server reqeust is unsuccessful.
     *
     * @param userCallback callback to handle resulting list of users
     */
    void getSentFriendRequests(Callback<List<User>> userCallback);

    /**
     * Gets the public data of users who have sent friend requests to the logged in user.
     * Calls given callback with List of Users who have sent friend requests to the
     * logged in user, whose requests have not been accepted (i.e. pending incoming requests).
     *
     * @param userCallback callback to handle resulting list of users
     */
    void getReceivedFriendRequests(Callback<List<User>> userCallback);

    /**
     * Gets the public data of all users who are friends with the currently logged in user.
     *
     * @param userCallback callback to handle the resulting friend list
     */
    void getFriends(Callback<List<User>> userCallback);

    /**
     * Attempts to send a friend request from the logged in user to the user of the given email
     * address.
     * If the email is found and request successfully made, calls the given callback with the public
     * info of the requestee. Otherwise returns an empty User List.
     * If server request failed, calls callback with null User List.
     *
     * @param friendEmail the email to make a request to
     * @param userCallback callback to handle resulting list of users
     */
    void makeFriendRequest(String friendEmail, Callback<List<User>> userCallback);

    /**
     * Accepts the friend request made by the given requester to the currently logged in user.
     * Calls given callback with true or false depending on if the server request was successful.
     *
     * @param requesterEmail the UID of the requester
     * @param booleanCallback callback to handle success/failure
     */
    void acceptFriendRequest(String requesterEmail, Callback<Boolean> booleanCallback);

    /**
     * Rejects the friend request made by the given requester to the currently logged in user.
     * Calls given callback with true or false depending on if the server request was successful.
     *
     * @param requesterEmail the UID of the requester
     * @param booleanCallback callback to handle success/failure
     */
    void rejectFriendRequest(String requesterEmail, Callback<Boolean> booleanCallback);

    /**
     * Deletes the given friend from the current user's friends
     *
     * @param friendEmail email of friend to delete
     * @param booleanCallback callback to handle success/failure of request
     */
    void deleteFriend(String friendEmail, Callback<Boolean> booleanCallback);

    /**
     * Sends a message from the current user in the given chat.
     * Calls given callback with true or false depending on if the server request was successful.
     *
     * @param chatId the ID of the chat to send to
     * @param text message to send
     * @param booleanCallback callback to handle success/failure
     */
    void sendMessage(String chatId, String text, Callback<Boolean> booleanCallback);

    /**
     * Starts listening for new messages in the given chat, calling the given callback
     * when new messages are sent to the chat.
     *
     * @param chatId the Id of the chat to watch
     * @param messageCallback callback to handle new messages received
     */
    void startChatListener(String chatId, Callback<Message> messageCallback);

    /**
     * Subscribes the user to get push notifications from the server when the given chat
     * receives new messages.
     *
     * @param chatId the ID of the chat to subscribe to for push notifications
     */
    void subscribeToChatNotifications(String chatId);

}
