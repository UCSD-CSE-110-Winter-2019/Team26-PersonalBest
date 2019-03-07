const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.setUpNewUser = functions.auth.user().onCreate((user) => {
    const userData = {
        name: user.displayName,
        email: user.email,
        uid: user.uid,
        height: 0,
        friends: []
    };
    admin.firestore().collection("users").doc(user.uid).set(userData).then(writeResult => {
        console.log("Created new user " + userData.name + " with id " + userData.uid);
        return 0
    }).catch(err => {
        console.log(err);
    })
    return 0
});