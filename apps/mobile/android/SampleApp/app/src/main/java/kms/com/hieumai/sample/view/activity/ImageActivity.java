package kms.com.hieumai.sample.view.activity;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import image.TouchImageView;
import kms.kms.hieumai.sample.R;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TouchImageView imageView = new TouchImageView(getBaseContext());
        imageView.setImageBitmap(BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.batman));
        final int activity_image = R.layout.activity_image;
        setContentView(imageView);
    }
}
