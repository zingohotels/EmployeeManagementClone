package app.zingo.employeemanagements.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressBarUtil {

    private ProgressDialog progressBar;
    public ProgressBarUtil( Context context){
        if(progressBar==null) {
            progressBar = new ProgressDialog(context);
        }
    }
    public void showProgress(String s){
        if(progressBar!=null) {
            progressBar.setCancelable(true);
            progressBar.setMessage(s);
            progressBar.setMax(20);
            progressBar.show();
        }
    }
    public void hideProgress(){
        if(progressBar!=null){
            progressBar.dismiss();
        }
    }
}
