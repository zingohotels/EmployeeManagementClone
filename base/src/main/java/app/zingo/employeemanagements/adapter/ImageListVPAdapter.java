package app.zingo.employeemanagements.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.zingo.employeemanagements.base.R;

public class ImageListVPAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> images;
    LayoutInflater layoutInflater;


    public ImageListVPAdapter(Context context,  ArrayList<String> images) {
        this.context = context;
        this.images = images;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size ();
    }

    @Override
    public boolean isViewFromObject( View view, Object object) {
        return view == (( LinearLayout ) object);
    }

    @Override
    public Object instantiateItem( ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate( R.layout.faq_view_pager_image, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        Picasso.with( context).load(images.get ( position )).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imageView);

        container.addView(itemView);



        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
