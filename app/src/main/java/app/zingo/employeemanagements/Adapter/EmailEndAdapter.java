package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.R;

public class EmailEndAdapter  extends BaseAdapter {
    private Context context;
    private String[] mList ;

    public EmailEndAdapter(Context context, String[] mList)
    {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.length;
    }

    @Override
    public Object getItem(int pos) {
        return mList[pos];
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
            view = mInflater.inflate(R.layout.spinner_dropdown_item,viewGroup,false);
        }

        TextView mCategoryName = (TextView) view.findViewById(R.id.category_spinner_item);


        mCategoryName.setText(mList[pos]);

        return view;
    }
}
