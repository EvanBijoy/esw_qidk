package org.tungabhadra.yogesh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_selection);

        MaterialCardView cardSuryanamaskara = findViewById(R.id.card_suryanamaskara);
        MaterialCardView cardYogaSet1 = findViewById(R.id.card_yoga_set1);
        MaterialCardView cardYogaSet2 = findViewById(R.id.card_yoga_set2);

        cardSuryanamaskara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity("Suryanamaskara");
            }
        });

        cardYogaSet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity("Yoga Set 1");
            }
        });

        cardYogaSet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity("Yoga Set 2");
            }
        });

        // Add ripple effect to cards
        cardSuryanamaskara.setClickable(true);
        cardSuryanamaskara.setFocusable(true);

        cardYogaSet1.setClickable(true);
        cardYogaSet1.setFocusable(true);

        cardYogaSet2.setClickable(true);
        cardYogaSet2.setFocusable(true);
    }

    private void startCameraActivity(String yogaSet) {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("YOGA_SET", yogaSet);
        startActivity(intent);
    }
}