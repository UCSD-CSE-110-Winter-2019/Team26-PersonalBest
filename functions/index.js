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
        admin.firestore().collection("users").doc(user.email).collection("friends").add({})
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
    if(requesterEmail === null || requesteeEmail === null || reqType === null) {
        console.log("Malformed friend request");
        throw new functions.https.HttpsError('invalid-argument', 'Malformed friend request');
    }

    console.log("Request: " + reqType + " " + requesterEmail + " " + requesteeEmail);
    console.log(context);

    const userRef = admin.firestore().collection("users");
    const requesterRef = userRef.doc(requesterEmail).collection("friends").doc(requesteeEmail);
    const requesteeRef = userRef.doc(requesteeEmail).collection("friends").doc(requesterEmail);

    // sanity check - are both valid user emails?
    var requesterCheckPromise = userRef.doc(requesterEmail).get()
    .then(doc => {
        if(!doc.exists){
            console.log("Requester not found: " + requesterEmail);
            throw new functions.https.HttpsError('not-found', 'Invalid user email' + requesterEmail);
        } else {
            console.log("Requester found: " + requesterEmail);
            return doc.data();
        }
    })
    .catch(error => {
        console.log("Error getting document:", error);
        throw new functions.https.HttpsError('internal', 'could not access firestore');
    });

    var requesteeCheckPromise = userRef.doc(requesteeEmail).get()
    .then(doc => {
        if(!doc.exists) {
            console.log("Requestee not found: " + requesteeEmail);
            throw new functions.https.HttpsError('not-found', 'Invalid user email' + requesteeEmail);
        } else {
            return doc.data();
        }
    })
    .catch(error => {
        console.log("Error getting document:", error);
        throw new functions.https.HttpsError('internal', 'could not access firestore');
    });

    var userChecks = Promise.all([requesterCheckPromise, requesteeCheckPromise]);

    switch(reqType) {
        case "REQUEST":
            console.log("Attempting to send REQUEST from " + requesterEmail + " to " + requesteeEmail);
            userChecks.then(requesterRef.create({ status: "requested" }))
            .then(() => console.log("Set requested " + requesteeEmail + " from " + requesterEmail))
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });
            userChecks.then(requesteeRef.create({ status: "received" }))
            .then(() => console.log("Set received " + requesteeEmail + " from " + requesterEmail))
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });
            break;
        case "ACCEPT":

            // check if already friends
            var checkStatus = requesterRef.get().then(snapshot => {
                if(snapshot.exists && snapshot.data().status === "friends") {
                    console.error("Users already friends - aborting request");
                    throw new functions.https.HttpsError('invalid-argument', 'Users already friends');
                } else {
                    console.log("Users are not friends - proceeding with request");
                    return;
                }
            })
            .catch(error => {
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });


            console.log("Attempting to ACCEPT request from " + requesteeEmail + " to " + requesterEmail);

            // generate new chat document for the 2 users
            var chatdata = { users: [requesterEmail, requesteeEmail] };
            console.log("Creating new chat collection with users " + chatdata.users);
            var createChatPromise = checkStatus.then(admin.firestore().collection("chats").doc().create(chatdata))
            .then(docRef => {
                console.log("Created new chat collection " + docRef + " with users " + chatdata.users);
                return docRef;
            })
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });

            // update friend relationships with status and chat document id
            var requesterUpdate = createChatPromise
            .then(docRef => requesterRef.update({ status: "friends", chat: docRef }))
            .then(() => console.log("Accepted request from " + requesteeEmail + " to " + requesterEmail))
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });

            var requesteeUpdate = createChatPromise
            .then(docRef => requesteeRef.update({ status: "friends", chat: docRef }))
            .then(() => console.log("Accepted request from " + requesteeEmail + " to " + requesterEmail))
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });

            Promise.all([requesterUpdate, requesteeUpdate])
            .then(console.log("Accepted friend request from " + requesterEmail + " to " + requesteeEmail))
            .catch(error => console.error(error));
            break;
        case "REJECT":
        case "DELETE":
            console.log("Attempting to DELETE from " + requesterEmail + " to " + requesteeEmail);

            // check if friends - delete chat if friends
            var checkFriends = userChecks.then(requesterRef.get())
            .then(snapshot => {
                if(snapshot.exists && snapshot.data().status === "friends") {
                    return snapshot.data().chat;
                } else {
                    return "";
                }
            })
            .then(chatid => admin.firestore().collection("chats").doc(chatid).delete());

            var requesterDelete = checkFriends.then(requesterRef.delete())
            .then(() => console.log("Deleted friend " + requesteeEmail + " from " + requesterEmail))
            .catch(error => {
                console.error("Error removing document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });
            var requesteeDelete = checkFriends.then(requesteeRef.delete())
            .then(() => console.log("Deleted friend " + requesterEmail + " from " + requesteeEmail))
            .catch(error => {
                console.error("Error removing document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });
            Promise.all([requesterDelete, requesteeDelete])
            .then(console.log("Deleted friend relationship"))
            .catch(error => console.error(error));
            break;
        default:
            console.log("Unrecognized friend request type " + reqType);
            throw new functions.https.HttpsError('invalid-argument', 'Request type not recognized');
    }
});