package edu.ucsd.cse110.team26.personalbest;

import java.util.List;

interface IDataAdapter {

    /**
     * Get today's data currently stored in the database.
     * Calls the given lambda with the resulting Day, or null if request failed.
     *
     * @param dayCallback callback lambda to handle the resulting Day
     */
    void getToday(DayCallback dayCallback);

    /**
     * Gets the logged-in user's data stored in the database
     * Calls the given lambda with the fetched user's data, or null if request failed.
     *
     * @param userCallback callback lambda to handle the user's data
     */
    void getUser(UserCallback userCallback);

    /**
     * Updates the logged-in user's data stored in the database
     * Calls given callback with true or false depending on if the server request was successful.
     *
     * @param user User's data to update
     * @param booleanCallback callback lambda to handle success/failure
     */
    void updateUser(User user, BooleanCallback booleanCallback);

    /**
     * Gets the profile information of the friend with given email.
     * Calls given callback with resulting data, or empty list if no such friend was found,
     * or null list if server request failed.
     *
     * @param friendEmail email of friend to look up
     * @param userCallback callback lambda to handle result
     */
    void getFriend(String friendEmail, UserCallback userCallback);

    /**
     * Gets the last numOfDays Days of data of the friend with the specified ID,
     * calling the passed in callback with the resulting List of Day, or null if
     * the server request failed.
     *
     * @param friendID the uid of the friend to fetch
     * @param numOfDays the number of days to fetch before today, today inclusive
     * @param dayCallback lambda to handle the resulting List of Days
     */
    void getFriendDays(String friendID, int numOfDays, DayCallback dayCallback);

    /**
     * Updates the database with the given days' data.
     *
     * @param days List of days to update the database with.
     */
    void updateDays(List<Day> days);

    /**
     * Gets the public data of users who have been sent friend requests by the logged in user.
     * Calls given callback with List of Users who have been sent friend requests from the
     * logged in user, who have not accepted the request (i.e. pending outgoing requests).
     * Calls given callback with null list if server reqeust is unsuccessful.
     *
     * @param userCallback callback to handle resulting list of users
     */
    void getSentFriendRequests(UserCallback userCallback);

    /**
     * Gets the public data of users who have sent friend requests to the logged in user.
     * Calls given callback with List of Users who have sent friend requests to the
     * logged in user, whose requests have not been accepted (i.e. pending incoming requests).
     *
     * @param userCallback callback to handle resulting list of users
     */
    void getReceivedFriendRequests(UserCallback userCallback);

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
    void makeFriendRequest(String friendEmail, UserCallback userCallback);

    /**
     * Accepts the friend request made by the given requester to the currently logged in user.
     * Calls given callback with true or false depending on if the server request was successful.
     *
     * @param requesterEmail the UID of the requester
     * @param booleanCallback callback to handle success/failure
     */
    void acceptFriendRequest(String requesterEmail, BooleanCallback booleanCallback);

    interface DayCallback {
        void call(List<Day> days);
    }

    interface UserCallback {
        void call(List<User> users);
    }

    interface BooleanCallback {
        void call(boolean bool);
    }

}
