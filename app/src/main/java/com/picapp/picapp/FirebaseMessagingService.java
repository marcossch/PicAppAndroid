package com.picapp.picapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.picapp.picapp.AndroidModels.Picapp;
import com.picapp.picapp.Interfaces.WebApi;
import com.picapp.picapp.Models.FriendshipStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    private static final String CHANNEL_ID = "notifications";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        createNotificationChannel();

        final RemoteMessage fRemoteMessage = remoteMessage;


        final String title = fRemoteMessage.getNotification().getTitle();
        final String message = fRemoteMessage.getNotification().getBody();

        final String senderId = fRemoteMessage.getData().get("senderId");

        final FirebaseMessagingService self = this;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WebApi webApi = retrofit.create(WebApi.class);
        Picapp picapp = Picapp.getInstance();
        final String token = picapp.getToken();
        Call<FriendshipStatus> friendshipStatus = webApi.getFriendshipStatusCustom(senderId, token);
        friendshipStatus.enqueue(new Callback<FriendshipStatus>() {
            @Override
            public void onResponse(Call<FriendshipStatus> call, Response<FriendshipStatus> response) {
                String status = response.body().getState();
                Intent intent;
                // Depends on friendship status
                if (status.equals("friends")) intent = new Intent(self, FriendProfileActivity.class);
                else intent = new Intent(self, OtherProfileActivity.class);
                // Create an explicit intent for an Activity in your app
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("name", fRemoteMessage.getData().get("name"));
                intent.putExtra("id", senderId);
                intent.putExtra("pic", fRemoteMessage.getData().get("picUrl"));
                intent.putExtra("from", "search");
                PendingIntent pendingIntent = PendingIntent.getActivity(self, 0, intent, 0);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(self, CHANNEL_ID)
                        .setSmallIcon(R.drawable.picapp_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.mipmap.ic_launcher))
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                int notificationId = (int) System.currentTimeMillis();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(self);
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(notificationId, mBuilder.build());
            }

            @Override
            public void onFailure(Call<FriendshipStatus> call, Throwable t) {
                Log.d("FRIENDSHIP STATUS", "-----> No se pudo obtener el estado de amistad <-----");
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notificationChannel);
            String description = getString(R.string.notificationChannelDescr);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
