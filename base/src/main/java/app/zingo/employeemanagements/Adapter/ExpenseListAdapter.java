package app.zingo.employeemanagements.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import app.zingo.employeemanagements.Model.Expenses;
import app.zingo.employeemanagements.Model.Leaves;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.UI.NewAdminDesigns.AdminNewMainScreen;

public class ExpenseListAdapter  extends RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Expenses> list;

    public ExpenseListAdapter(Context context, ArrayList<Expenses> list) {

        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_expense_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Expenses dto = list.get(position);


        if(dto!=null){

            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            holder.mExpenseType.setText(""+dto.getExpenseTitle());
            holder.mExpenseComment.setText(""+dto.getDescription());
            holder.mAmount.setText("Rs."+dto.getAmount());

            String froms = dto.getDate();


            if(froms.contains("T")){

                String dojs[] = froms.split("T");

                try {
                    Date afromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dojs[0]);
                    froms = new SimpleDateFormat("MMM dd,yyyy").format(afromDate);
                    holder.mTo.setText(""+froms);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }

            String image = dto.getImageUrl();

            if(image!=null&&!image.isEmpty()){

                final LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                try{
                    View v = vi.inflate(R.layout.gallery_layout, null);
                    ImageView blogs = v.findViewById(R.id.blog_images);

                    Picasso.with(context).load(image).placeholder(R.drawable.profile_image).
                            error(R.drawable.profile_image).into(blogs);



                    holder.mExpenseImages.addView(v);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }


        }





    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        TextInputEditText mExpenseType,mAmount,mTo;
        EditText mExpenseComment;
        LinearLayout mExpenseImages;




        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            mExpenseType = itemView.findViewById(R.id.expense_title);

            mAmount = itemView.findViewById(R.id.amount_expense);
            mTo = itemView.findViewById(R.id.to_date);
            mExpenseComment = itemView.findViewById(R.id.expense_description);

            mExpenseImages = itemView. findViewById(R.id.expense_image);




        }
    }
}
