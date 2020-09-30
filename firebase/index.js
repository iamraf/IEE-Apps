const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

exports.sendNotification = functions.database.ref('/Notifications/{id}').onWrite((change, context) =>
{
    if(change.before.exists())
    {
        return null;
    }

    if(!change.after.exists())
    {
        return null;
    }

    const notification = change.after.val();

    const payload =
    {
        'data':
        {
            'id':context.params.id,
            'category':notification.category,
            'title':notification.title,
            'name':notification.name
        }
    };

    return admin.messaging().sendToTopic(notification.about, payload).then(response =>
    {
        return console.log('Succesfully sent a notification for ', notification.title);
    })
    .catch((error) =>
    {
        return console.error('Failed: ', error);
    });
});
