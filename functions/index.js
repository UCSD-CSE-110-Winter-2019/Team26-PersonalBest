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
    return admin.firestore().collection("users").doc(user.email).set(userData)
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
            return { name: doc.get("name"), email: doc.get("email") };
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
            return { name: doc.get("name"), email: doc.get("email") };
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
            var updateRequester = userChecks.then(data => requesterRef.create({ status: "requested", name: data[1].name, email: data[1].email }))
            .then(() => console.log("Set requested " + requesteeEmail + " from " + requesterEmail))
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });
            var updateRequestee = userChecks.then(data => requesteeRef.create({ status: "received", name: data[0].name, email: data[0].email }))
            .then(() => console.log("Set received " + requesteeEmail + " from " + requesterEmail))
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });
            return Promise.all([updateRequester, updateRequestee])
            .then(console.log("Set request from " + requesterEmail + " to " + requesteeEmail));
        case "ACCEPT":

            // check if already friends
            var checkStatus = userChecks.then(requesterRef.get())
            .then(doc => {
                if(doc.exists && doc.get("status") === "friends") {
                    throw new functions.https.HttpsError('invalid-argument', 'Users already friends');
                }
                else return userChecks;
            })
            .catch(error => {
                console.log(error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });

            // generate new chat document for the 2 users
            var chatdata = { users: [requesterEmail, requesteeEmail] };
            var newChatDocRef = admin.firestore().collection("chats").doc()
            var createChatPromise = checkStatus.then(newChatDocRef.create(chatdata))
            .then(docRef => {
                console.log("Created new chat collection " + newChatDocRef.id + " with users " + chatdata.users);
                return newChatDocRef.id;
            })
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });

            // update friend relationships with status and chat document id
            var requesterUpdate = Promise.all([checkStatus, createChatPromise])
            .then((data) => requesterRef.update({ status: "friends", chat: data[1] }))
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });

            var requesteeUpdate = Promise.all([checkStatus, createChatPromise])
            .then((data, chatid) => requesteeRef.update({ status: "friends", chat: data[1] }))
            .catch(error => {
                console.error("Error setting document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });

            return Promise.all([requesterUpdate, requesteeUpdate])
            .then(console.log("Accepted friend request from " + requesterEmail + " to " + requesteeEmail));
        case "REJECT":
        case "DELETE":
            console.log("Attempting to DELETE from " + requesterEmail + " to " + requesteeEmail);

            var deleteChat = userChecks.then(requesterRef.get())
            .then(snapshot => {
                console.log(JSON.stringify(snapshot.data()));
                var chatid = snapshot.get("chat");
                console.log("deleting chat at " + chatid);
                return admin.firestore().collection("chats").doc(chatid).delete();
            })
            .then(console.log("deleted chat"))
            .catch(error => {
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });

            var requesterDelete = userChecks.then(requesterRef.delete())
            .catch(error => {
                console.error("Error removing document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });
            var requesteeDelete = userChecks.then(requesteeRef.delete())
            .catch(error => {
                console.error("Error removing document: ", error);
                throw new functions.https.HttpsError('internal', 'could not access firestore');
            });

            return Promise.all([requesterDelete, requesteeDelete, deleteChat])
            .then(console.log("Deleted friend relationship"))
            .catch(error => console.error(error));
        default:
            console.log("Unrecognized friend request type " + reqType);
            throw new functions.https.HttpsError('invalid-argument', 'Request type not recognized');
    }
});

exports.sendChatNotifications = functions.firestore
    .document('chats/{chatId}/messages/{messageId}')
    .onCreate((snap, context) => {

    // check if valid message
    const document = snap.exists ? snap.data() : null;

    if (document) {

        // update timestamp
        snap.ref.update({ timestamp: Date.now() })

        // generate notification messsage
        var message = {
            notification: {
                title: document.from + ' sent you a message',
                body: document.text
            },
            android: {
                notification: {
                    click_action: "OPEN_CHAT_VIEW"
                }
            },
            data: {
                title: document.from + ' sent you a message',
                body: document.text,
                chat: context.params.chatId
            },
            topic: context.params.chatId
        };

        // send FCM message
        return admin.messaging().send(message)
        .then((response) => {
            // Response is a message ID string.
            console.log('Successfully sent message: ', response);
            return response;
        })
        .catch((error) => {
            console.log('Error sending message: ', error);
            return error;
        });
    }

    return "document was null or empty";
});