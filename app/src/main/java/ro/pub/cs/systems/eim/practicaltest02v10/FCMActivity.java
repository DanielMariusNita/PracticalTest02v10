package ro.pub.cs.systems.eim.practicaltest02v10;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.messaging.FirebaseMessaging;

public class FCMActivity extends AppCompatActivity {

    private static final String TAG = "FCMActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm);

        EditText topicNameEditText = findViewById(R.id.topic_name);
        Button subscribeButton = findViewById(R.id.subscribe_button);
        Button unsubscribeButton = findViewById(R.id.unsubscribe_button);

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = topicNameEditText.getText().toString().trim();
                if (!topic.isEmpty()) {
                    FirebaseMessaging.getInstance().subscribeToTopic(topic)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Subscribed to topic: " + topic);
                                } else {
                                    Log.d(TAG, "Failed to subscribe to topic: " + topic);
                                }
                            });
                }
            }
        });

        unsubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = topicNameEditText.getText().toString().trim();
                if (!topic.isEmpty()) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Unsubscribed from topic: " + topic);
                                } else {
                                    Log.d(TAG, "Failed to unsubscribe from topic: " + topic);
                                }
                            });
                }
            }
        });

        // Obtain the Firebase token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.d(TAG, "Firebase token: " + token);
            } else {
                Log.d(TAG, "Failed to obtain Firebase token");
            }
        });
    }
}