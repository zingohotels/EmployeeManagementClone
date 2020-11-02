package app.zingo.employeemanagements.ui.Common;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.zingo.employeemanagements.adapter.ImageListVPAdapter;
import app.zingo.employeemanagements.base.R;

public class ImageFullScreenActivity extends AppCompatActivity {

    ImageView mSrc;
    ViewPager mImageListvp;

    String src="";
    int srcImage;
    ArrayList<String> imageList = new ArrayList <> (  );

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
            mImageListvp = findViewById(R.id.imageList_vp);

            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){

                src = bundle.getString("Image");
                srcImage = bundle.getInt("Images");
                imageList =(ArrayList< String> ) bundle.getStringArrayList ("imageList");
            }

            if(src != null && !src.isEmpty()){
                mSrc.setVisibility ( View.VISIBLE );
                mImageListvp.setVisibility ( View.GONE );
                Picasso.with( ImageFullScreenActivity.this).load(src).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(mSrc);

            }else if(srcImage!=0){
                mSrc.setVisibility ( View.VISIBLE );
                mImageListvp.setVisibility ( View.GONE );
                mSrc.setImageResource(srcImage);
            }else if(imageList!=null&&imageList.size ()!=0){
                mSrc.setVisibility ( View.GONE );
                mImageListvp.setVisibility ( View.VISIBLE );
                ImageListVPAdapter adapter = new ImageListVPAdapter ( ImageFullScreenActivity.this, imageList);
                mImageListvp.setAdapter(adapter);
            }else{
                Toast.makeText ( ImageFullScreenActivity.this , "Something went wrong" , Toast.LENGTH_SHORT ).show ( );
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
