package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.employeemanagements.Model.Departments;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.base.R;

public class OrgSpinnerAdapter  extends BaseAdapter {
    private Context context;
    private ArrayList<Organization> mList = new ArrayList<>();

    public OrgSpinnerAdapter(Context context, ArrayList<Organization> mList)
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
            view = mInflater.inflate(R.layout.adapter_department_spinner,viewGroup,false);
        }

        TextView mCategoryName = view.findViewById(R.id.category_spinner_item);


        mCategoryName.setText(mList.get(pos).getOrganizationName());

        return view;
    }
}
