package app.zingo.employeemanagements.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.NavBarItems;
import app.zingo.employeemanagements.base.R;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public class NavigationListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavBarItems> mList = new ArrayList<>();

    public NavigationListAdapter(Context context, ArrayList<NavBarItems> mList)
    {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int pos) {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {

        if(view == null)
        {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.navbar_list_item_layout,viewGroup,false);
        }

        TextView mTitle = view.findViewById(R.id.title);
        ImageView mIcon = view.findViewById(R.id.icon);


        mTitle.setText(mList.get(pos).getTitle());//.getTitle().toString());
        mIcon.setImageResource(mList.get(pos).getIcon());
        return view;
    }
}
