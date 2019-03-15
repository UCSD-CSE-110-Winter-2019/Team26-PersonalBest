package edu.ucsd.cse110.team26.personalbest;

class User {
    int height; // in inches
    String name;
    String email;
    String uid;
    String chatID;
    Friends friends;

    User() {
        height = 0;
        name = "";
        email = "";
        uid = "";
        friends = new Friends();
    }

    User(int height, String name, String email, String uid) {
        this.height = height;
        this.name = name;
        this.email = email;
        this.uid = uid;
        friends = new Friends();
    }

    @Override
    public String toString() {
        return name + ": " + email;
    }
}
