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
    admin.firestore().collection("users").doc(user.email).set(userData)
    .then(admin.firestore().collection("users").doc(user.email).collection("friends").add({}))
    .then(console.log("Created new user " + userData.name + " with id " + userData.uid))
    .catch(err => console.log(err));
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
    if(requesterEmail === null || requesteeEmail === null || reqType === null || requesterEmail === requesteeEmail) {
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
            throw new functions.https.HttpsError('not-found', 'Invalid user email ' + requesterEmail);
        } else {
            console.log("Requester found: " + JSON.stringify(doc.data()));
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
            throw new functions.https.HttpsError('not-found', 'Invalid user email ' + requesteeEmail);
        } else {
            console.log("Requestee found: " + JSON.stringify(doc.data()));
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
            userChecks.then((requesterData, requesteeData) => requesterRef.create({ status: "requested", name: requesteeData.name, email: requesteeData.email }))
            .then(() => console.log("Set requested " + requesteeEmail + " from " + requesterEmail))
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });
            userChecks.then((requesterData, requesteeData) => requesteeRef.create({ status: "received", name: requesterData.name, email: requesteeData.email }))
            .then(() => console.log("Set received " + requesteeEmail + " from " + requesterEmail))
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });
            break;
        case "ACCEPT":

            // check if already friends
            var checkStatus = userChecks.then((requesterData, requesteeData) => {
                if(requesterData.status === "friends") {
                    throw new functions.https.HttpsError('invalid-argument', 'Users already friends');
                }
                console.log("Attempting to ACCEPT request from " + requesteeEmail + " to " + requesterEmail);
                return { requester: requesterData.name, requestee: requesteeData.name };
            })
            .catch(error => {
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });

            // generate new chat document for the 2 users
            var chatdata = { users: [requesterEmail, requesteeEmail] };
            var createChatPromise = checkStatus.then(admin.firestore().collection("chats").doc().create(chatdata))
            .then(docRef => {
                console.log("Created new chat collection " + docRef.id + " with users " + chatdata.users);
                return docRef.id;
            })
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });

            // update friend relationships with status and chat document id
            var requesterUpdate = Promise.all([checkStatus, createChatPromise])
            .then((data, chatid) => requesterRef.update({ status: "friends", chat: chatid }))
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });

            var requesteeUpdate = Promise.all([checkStatus, createChatPromise])
            .then((data, chatid) => requesteeRef.update({ status: "friends", chat: chatid }))
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
            var checkFriends = userChecks.then((requesterData, requesteeData) => {
                if(requesterData.status === "friends") {
                    admin.firestore().collection("chats").doc(requesterData.chat).delete();
                }
                return;
            });

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