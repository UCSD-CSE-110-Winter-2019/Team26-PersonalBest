const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.setUpNewUser = functions.auth.user().onCreate((user) => {
    const userData = {
        name: user.displayName,
        email: user.email,
        uid: user.uid,
        height: 0
    };
    admin.firestore().collection("users").doc(user.email).set(userData).then(writeResult => {
        console.log("Created new user " + userData.name + " with id " + userData.uid);
        return 0
    }).catch(err => {
        console.log(err);
    })
    return 0
});


/*
Takes data object of structure:
{
    requesterEmail: <user's email>
    requesteeEmail: <friend's email>
    reqType: "REQUEST" || "ACCEPT" || "REJECT" || "DELETE"
}
*/
exports.handleFriendRequest = functions.https.onCall((data, context) => {
    const { requesterEmail, requesteeEmail, reqType } = data
    if(requesterEmail === null || requesteeEmail === null || request === null) {
        console.log("Malformed friend request");
        throw new functions.https.HttpsError('invalid-argument', 'Malformed friend request');
    }
    context && context.auth && context.auth.token && console.log(context.auth.token.name);

    const userRef = admin.firestore.collection("users");
    const requesterRef = userRef.doc(requesterEmail).collection("friends").doc(requesteeEmail);
    const requesteeRef = userRef.doc(requesteeEmail).collection("friends").doc(requesterEmail);

    // sanity check - are both valid user emails?
    var validUsers = true;
    userRef.doc(requesterEmail).get()
        .then((doc) => {
            if(!doc.exists) console.log("Requester not found: " + requesterEmail);
            validUsers = false;
            return -1;
        })
        .catch((error) => {
            console.log("Error getting document:", error);
            validUsers = false;
        });
    userRef.doc(requesteeEmail).get()
        .then((doc) => {
            if(!doc.exists) console.log("Requestee not found: " + requesteeEmail);
            validUsers = false;
            return -1;
        })
        .catch((error) => {
            console.log("Error getting document:", error);
            validUsers = false;
        });

    if(validUsers) {
        switch(reqType) {
            case "REQUEST":
                requesterRef.set({ status: "requested" });
                requesteeRef.set({ status: "received" });
                console.log("Set new friend request from " + requesterEmail + " to " + requesteeEmail);
                break;
            case "ACCEPT":
                requesterRef.set({ status: "friends" });
                requesteeRef.set({ status: "friends" });
                console.log("Accepted friend request from " + requesterEmail + " to " + requesteeEmail);
                break;
            case "REJECT":
            case "DELETE":
                requesterRef.delete().then(() => console.log("Deleted friend " + requesteeEmail + " from " + requesterEmail))
                    .catch((error) => {
                        console.error("Error removing document: ", error);
                    });
                requesteeRef.delete().then(() => console.log("Deleted friend " + requesterEmail + " from " + requesteeEmail))
                    .catch((error) => {
                        console.error("Error removing document: ", error);
                    });
                break;
            default:
                console.log("Unrecognized friend request type " + reqType);
                throw new functions.https.HttpsError('invalid-argument', 'Request type not recognized');
        }
    } else {
        throw new functions.https.HttpsError('invalid-argument', 'Invalid user email');
    }
});