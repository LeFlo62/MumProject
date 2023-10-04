package fr.isep.mobiledev.mumproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    private String[] images;
    private List<Integer> imageOrder;
    private int imageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ImageButton buttonParameters = findViewById(R.id.parametersButton);
        buttonParameters.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ParametersActivity.class);
            startActivity(intent);
        });

        ImageView imageView = findViewById(R.id.imageView);

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
                if(images != null && images.length != 0){
                    imageView.setImageURI(getRandomImage());
                }
                imageView.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("fr.isep.mobiledev.mumproject", MODE_PRIVATE);

        Button buttonCall = findViewById(R.id.callButton);
        buttonCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + preferences.getString("phone", "")));
            startActivity(intent);
        });

        Button buttonText = findViewById(R.id.textButton);
        buttonText.setOnClickListener(v -> {
            String phoneNumber = preferences.getString("phone", "");
            String message = "Shrek, I miss you so much </3.";

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body", message);

            startActivity(intent);
        });

        File imagesFolder = new File(getDataDir(), "/images/");
        if(!imagesFolder.exists()){
            imagesFolder.mkdirs();
        }
        images = Arrays.stream(imagesFolder.listFiles()).map(File::getAbsolutePath).toArray(String[]::new);
        imageOrder = IntStream.rangeClosed(0, images.length-1).boxed().collect(Collectors.toList());
        Collections.shuffle(imageOrder);

        ImageView imageView = findViewById(R.id.imageView);
        if(images != null && images.length != 0){
            imageView.setImageURI(getRandomImage());
        }
    }

    public Uri getRandomImage() {
        if (imageIndex == images.length) {
            imageIndex = 0;
            Collections.shuffle(imageOrder);
        }
        return Uri.parse(images[imageOrder.get(imageIndex++)]);
    }
}
