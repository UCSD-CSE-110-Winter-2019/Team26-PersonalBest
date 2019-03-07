package edu.ucsd.cse110.team26.personalbest;

class User {
    int height; // in inches
    long goal;
    String name;
    String email;
    String uid;
    Friends friends;

    User(int height, long goal, String name, String email, String uid) {
        this.height = height;
        this.goal = goal;
        this.name = name;
        this.email = email;
        this.uid = uid;
        friends = new Friends();
    }
}
