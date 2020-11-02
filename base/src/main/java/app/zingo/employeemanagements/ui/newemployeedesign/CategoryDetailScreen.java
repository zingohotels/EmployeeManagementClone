package app.zingo.employeemanagements.ui.newemployeedesign;

import android.os.Build;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.zingo.employeemanagements.adapter.SubCategoryGridAdapter;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.StockCategoryModel;

public class CategoryDetailScreen extends AppCompatActivity {

    StockCategoryModel category;
    private static TextView mCategoryDesc,mCateDes;
    private static GridView mSubCategory;
    private static LinearLayout mDesLay;
    private static NestedScrollView mMain;
    private static Animation uptodown,downtoup;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );


        try{

            setContentView(R.layout.activity_category_detail_screen);
            // Get Toolbar component.
            Toolbar toolbar = (Toolbar)findViewById(R.id.collapsing_toolbar);

            mMain = (NestedScrollView) findViewById(R.id.main_layout);
            mCateDes = (TextView) findViewById(R.id.category_desc_name);
            mCategoryDesc = (TextView) findViewById(R.id.category_desc);
            mSubCategory = (GridView) findViewById(R.id.sub_category_grid);
            mDesLay = (LinearLayout) findViewById(R.id.des_lay);
            uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
            downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
            mDesLay.setAnimation(downtoup);
            // Use Toolbar to replace default activity action bar.
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();


            if(actionBar!=null)
            {
                // Display home menu item.
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            recyclerAnimation();

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                category = (StockCategoryModel ) bundle.getSerializable("Category");

            }

            // Set collapsing tool bar title.
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar_layout);
            // Set collapsing tool bar image.
            ImageView collapsingToolbarImageView = (ImageView)findViewById(R.id.collapsing_toolbar_image_view);
            //collapsingToolbarImageView.setImageResource(R.drawable.img1);

            if(category!=null){
                collapsingToolbarLayout.setTitle(""+category.getStockCategoryName ());
                Picasso.with(CategoryDetailScreen.this).load(category.getStockCategoryImage ()).placeholder(R.drawable.no_image).
                        error(R.drawable.no_image).into(collapsingToolbarImageView);
                mCateDes.setText("About "+category.getStockCategoryName ());

                if( Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {


                    mCategoryDesc.setText( category.getStockCategoryDescription ());

                }else {


                    mCategoryDesc.setText(category.getStockCategoryDescription ());
                }


                if(category.getStockSubCatList ()!=null&&category.getStockSubCatList().size()!=0){

                    SubCategoryGridAdapter adapter = new SubCategoryGridAdapter (CategoryDetailScreen.this,category.getStockSubCatList());
                    mSubCategory.setAdapter(adapter);
                }

            }else{
                collapsingToolbarLayout.setTitle("Category Detail");
                Picasso.with(CategoryDetailScreen.this).load(category.getStockCategoryImage ()).placeholder(R.drawable.no_image).
                        error(R.drawable.no_image).into(collapsingToolbarImageView);
            }






        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void recyclerAnimation(){
        mMain.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        mMain.getViewTreeObserver().removeOnPreDrawListener(this);

                        for (int i = 0; i < mMain.getChildCount(); i++) {
                            View v = mMain.getChildAt(i);
                            v.setAlpha(0.0f);
                            v.animate().alpha(1.0f)
                                    .setDuration(300)
                                    .setStartDelay(i * 50)
                                    .start();
                        }

                        return true;
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCateDes.requestFocus();
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected (item);
    }
}

