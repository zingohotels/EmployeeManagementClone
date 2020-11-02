package app.zingo.employeemanagements.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import app.zingo.employeemanagements.Custom.MyTextView;
import app.zingo.employeemanagements.base.R;
import app.zingo.employeemanagements.model.PermissionModel;

public class PermissionRequestAdapter  extends RecyclerView.Adapter< PermissionRequestAdapter.ViewHolder> {

    private Context context;
    private ArrayList < PermissionModel > permissionModels;

    public PermissionRequestAdapter(Context context, ArrayList< PermissionModel > permissionModels) {
        this.context = context;
        this.permissionModels = permissionModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_permission_request,parent,false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder( @NonNull ViewHolder holder, final int position) {

        holder.permission_name.setText ( permissionModels.get ( position ).getPermissionName () );
        holder.permission_desc.setText ( permissionModels.get ( position ).getPermissionDescription () );
        holder.permission_image.setImageResource(permissionModels.get(position).getPermissionImage ());


    }

    @Override
    public int getItemCount() {return permissionModels.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView permission_image;
        MyTextView permission_name;
        TextView permission_desc;


        public ViewHolder( View itemView) {
            super(itemView);
            permission_image = itemView.findViewById( R.id.permission_image);
            permission_name = itemView.findViewById( R.id.permission_name);
            permission_desc = itemView.findViewById( R.id.permission_desc);


        }
    }
}
