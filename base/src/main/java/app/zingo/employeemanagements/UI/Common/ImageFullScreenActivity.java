package app.zingo.employeemanagements.UI.Common;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import app.zingo.employeemanagements.base.R;

public class ImageFullScreenActivity extends AppCompatActivity {

    ImageView mSrc;

    String src="";
    int srcImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_image_full_screen);
            //setContentView(new Zoom(this));
            getWindow().getDecorView().setBackground(new ColorDrawable(Color.TRANSPARENT));


            mSrc = findViewById(R.id.image_src);

            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){

                src = bundle.getString("Image");
                srcImage = bundle.getInt("Images");
            }

            if(src != null && !src.isEmpty()){
                Picasso.with(ImageFullScreenActivity.this).load(src).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(mSrc);


            }else{

                if(srcImage!=0){
                    mSrc.setImageResource(srcImage);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
