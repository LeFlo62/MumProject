package fr.isep.mobiledev.mumproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.TypedArray;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    private TypedArray images;
    private List<Integer> imageOrder;
    private int imageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonCall = findViewById(R.id.callButton);
        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+33422521010"));
                startActivity(intent);
            }
        });

        Button buttonText = findViewById(R.id.textButton);
        buttonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "0625122512";
                String message = "Shrek, I miss you so much </3.";

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + phoneNumber));
                intent.putExtra("sms_body", message);

                startActivity(intent);
            }
        });

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.placeholder_1);

        images = getResources().obtainTypedArray(R.array.mum_images);
        imageOrder = IntStream.rangeClosed(0, images.length()-1).boxed().collect(Collectors.toList());
        Collections.shuffle(imageOrder);

        imageView.setImageResource(getRandomImage());

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);

        imageView.startAnimation(fadeIn);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    imageView.startAnimation(fadeOut);
                }, 4000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setImageResource(getRandomImage());
                imageView.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public int getRandomImage() {
        if (imageIndex == images.length()) {
            imageIndex = 0;
            Collections.shuffle(imageOrder);
        }
        return images.getResourceId(imageOrder.get(imageIndex++), R.drawable.ic_launcher_background);
    }
}
