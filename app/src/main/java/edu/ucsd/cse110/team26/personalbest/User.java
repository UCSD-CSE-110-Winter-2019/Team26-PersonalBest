package edu.ucsd.cse110.team26.personalbest;

class User {
    int height; // in inches
    String name;
    String email;
    String uid;

    String chatID;
    Friends friends;

    User() {

    }

    User(int height, String name, String email, String uid) {
        this.height = height;
        this.name = name;
        this.email = email;
        this.uid = uid;
        friends = new Friends();
    }

    public int getHeight()
    {
        return height;
    }

    public String getEmail()
    {
        return email;
    }
    public void setHeight(int height)
    {
        this.height = height;
    }

    public String getName()
    {
        return this.name;
    }

    public String getUid()
    {
        return this.uid;
    }
}
