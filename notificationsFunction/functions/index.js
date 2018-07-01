'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendReqNotif = functions.firestore.document('Notifications/request/{receiverId}/{notificationId}').onCreate( (snap, context) => {

  const receiver = context.params.receiverId;
  const notification = context.params.notificationId;

  if (! snap.data()){
    return console.log('A notification has been deleted from the database: ', notification);
  }
  const sender = snap.data().from;

  return admin.firestore().collection('Users').doc(sender).get().then(user => {
    if (user.exists){
      const senderName = user.data().name;
      const image = user.data().image;

      return admin.firestore().collection('Devices').doc(receiver).get().then(doc => {
        if (doc.exists) {
          const payload = {
            notification: {
              title: "Solicitud de amistad",
              body: senderName + " quiere ser tu amigo!",
              clickAction: "NOTIFICATION_ACTIVITY"
            },
            data: {
              name: senderName,
              senderId: sender,
              picUrl: image
            }
          };

          return admin.messaging().sendToDevice(doc.data().deviceToken, payload)
                    .then((response) => {
                      return console.log('Successfully sent message: ', payload);
                    })
                    .catch((error) => {
                      return console.log('Error sending message: ', error);
                    });
        } else {
          return console.log('Error!');
        }
      })
    } else {
      return console.log('Error!');
    }
  })

});
