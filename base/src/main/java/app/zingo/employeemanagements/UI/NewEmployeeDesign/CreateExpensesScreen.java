package app.zingo.employeemanagements.UI.NewEmployeeDesign;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import app.zingo.employeemanagements.Model.Expenses;
import app.zingo.employeemanagements.UI.Employee.ApplyLeaveScreen;
import app.zingo.employeemanagements.Utils.Constants;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.ExpensesApi;
import app.zingo.employeemanagements.WebApi.UploadApi;
import app.zingo.employeemanagements.base.R;
import info.hoang8f.widget.FButton;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateExpensesScreen extends AppCompatActivity {

    TextInputEditText mExpenseType,mAmount,mTo;
    FButton disabledBtn;
    EditText mExpenseComment;
    LinearLayout mExpenseImages,mUploadImages,to_date_TextInputEditText;
    AppCompatButton mApply;
    static int REQUEST_GALLERY = 200;
    String selectedImage;
    Expenses expenses;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    int employeeId = 0,managerId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{

            setContentView(R.layout.activity_create_expenses_screen);
            mExpenseType = findViewById(R.id.expense_title);
            mAmount = findViewById(R.id.amount_expense);
            mTo = findViewById(R.id.to_date);
            mExpenseComment = findViewById(R.id.expense_description);
            to_date_TextInputEditText = findViewById(R.id.to_date_TextInputEditText);
            mApply = findViewById(R.id.apply_expense);
            mExpenseImages = findViewById(R.id.expense_image);
            mUploadImages = findViewById(R.id.image_layout);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                employeeId = bundle.getInt("EmployeeId");
                managerId = bundle.getInt("ManagerId");
            }


            mTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDatePicker(mTo);
                }
            });
            mApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    validate();
                }
            });

            mUploadImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateExpensesScreen.this);
        builder.setTitle("Add Image!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
      /*  Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);*/

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        try {
            intent.putExtra("return-data", true);
            startActivityForResult(Intent.createChooser(intent,"Select File"), SELECT_FILE);
        } catch (ActivityNotFoundException e) {
            // Do nothing for now
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {

        try{
            final Uri selectedImageUri = data.getData( );
            InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
            final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
           // mProfileImage.setImageBitmap(bitmap);

            Uri temUri = getImageUri( getApplicationContext(), bitmap );
            String picturePath = getPath( CreateExpensesScreen.this, temUri );
            Log.d("Picture Path", picturePath);
            String[] all_path = {picturePath};
            selectedImage = all_path[0];
            System.out.println("allpath === "+data.getPackage());
            for (String s:all_path)
            {
                //System.out.println(s);
                File imgFile = new  File(s);
                if(imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    //addView(null,Util.getResizedBitmap(myBitmap,400));
                    addImage(null, Util.getResizedBitmap(myBitmap,700));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private Uri getImageUri(Context applicationContext, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(),bitmap,"Title",null);
        return Uri.parse(path);
    }
    private String getPath(CreateExpensesScreen createExpensesScreen, Uri selectedImageUri) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getApplication().getContentResolver( ).query( selectedImageUri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    public void addImage(String uri,Bitmap bitmap)
    {
        try{

            if(uri != null)
            {

            }
            else if(bitmap != null)
            {
               mExpenseImages.removeAllViews();

                final LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                try{
                    View v = vi.inflate(R.layout.gallery_layout, null);
                    ImageView blogs = v.findViewById(R.id.blog_images);

                    blogs.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateExpensesScreen.this);
                            alertDialogBuilder.setTitle("Create Expense");
                            alertDialogBuilder
                                    .setMessage("What do you want to do?")
                                    .setCancelable(false)
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            mExpenseImages.removeAllViews();
                                            selectedImage = "";
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }
                    });


                    if(uri != null)
                    {

                    }
                    else if(bitmap != null)
                    {
                        blogs.setImageBitmap(bitmap);
                    }


                    mExpenseImages.addView(v);
                }catch (Exception e){
                    e.printStackTrace();
                }

                //mExpenseImages.setImageBitmap(bitmap);


            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void uploadImage(final String filePath,final Expenses expenses)
    {
        //String filePath = getRealPathFromURIPath(uri, ImageUploadActivity.this);

        final File file = new File(filePath);
        int size = 1*1024*1024;

        if(file != null)
        {
            if(file.length() > size)
            {
                System.out.println(file.length());
                compressImage(filePath,expenses);
            }
            else
            {
                final ProgressDialog dialog = new ProgressDialog(CreateExpensesScreen.this);
                dialog.setCancelable(false);
                dialog.setTitle("Uploading Image..");
                dialog.show();
                Log.d("Image Upload", "Filename " + file.getName());

                RequestBody mFile = RequestBody.create(MediaType.parse("image"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                UploadApi uploadImage = Util.getClient().create(UploadApi.class);

                Call<String> fileUpload = uploadImage.uploadProfileImages(fileToUpload, filename);
                fileUpload.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(dialog != null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }


                        if(Util.IMAGE_URL==null){
                            expenses.setImageUrl(Constants.IMAGE_URL+ response.body());
                        }else{
                            expenses.setImageUrl(Util.IMAGE_URL+ response.body());
                        }
                       // expenses.setImageUrl(Constants.IMAGE_URL+response.body().toString());
                        addExpense(expenses);




                        if(filePath.contains("MyFolder/Images"))
                        {
                            file.delete();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("UpdateCate", "Error " + t.getMessage());
                    }
                });
            }
        }
    }

    public String compressImage(String filePath,final Expenses expenses) {

        //String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = actualHeight/2;//2033.0f;
        float maxWidth = actualWidth/2;//1011.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String[] filearray = filePath.split("/");
        final String filename = getFilename(filearray[filearray.length-1]);
        try {
            out = new FileOutputStream(filename);


//          write the compressed bitmap at the field_icon specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            uploadImage(filename,expenses);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    public String getFilename(String filePath) {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println("getFilePath = "+filePath);
        String uriSting;
        if(filePath.contains(".jpg"))
        {
            uriSting = (file.getAbsolutePath() + "/" + filePath);
        }
        else
        {
            uriSting = (file.getAbsolutePath() + "/" + filePath+".jpg" );
        }
        return uriSting;
    }

    private void addExpense(final Expenses expenses) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Loading.");
        dialog.show();

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {
                ExpensesApi auditApi = Util.getClient().create(ExpensesApi.class);
                Call<Expenses> response = auditApi.addExpenses(expenses);
                response.enqueue(new Callback<Expenses>() {
                    @Override
                    public void onResponse(Call<Expenses> call, Response<Expenses> response) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        System.out.println(response.code());
                        if(response.code() == 201||response.code() == 200||response.code() == 204)
                        {
                            Toast.makeText(CreateExpensesScreen.this,"Expense added",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(CreateExpensesScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Expenses> call, Throwable t) {
                        if(dialog != null)
                        {
                            dialog.dismiss();
                        }
                        Toast.makeText(CreateExpensesScreen.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void openDatePicker(final TextInputEditText tv) {
        // Get Current Date

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        //launch datepicker modal
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            Log.d("Date", "DATE SELECTED " + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);

                            String date1 = (monthOfYear + 1) + "/" + (dayOfMonth) + "/" + year;

                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            try {
                                Date fdate = simpleDateFormat.parse(date1);
                                String from1 = sdf.format(fdate);
                                tv.setText(from1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void validate(){

        String expenseTitle = mExpenseType.getText().toString();
        String from = mAmount.getText().toString();
        String to = mTo.getText().toString();
        String leaveComment = mExpenseComment.getText().toString();
        if(expenseTitle.isEmpty()){
            Toast.makeText(this, "Expense title is required", Toast.LENGTH_SHORT).show();
        }else if(from.isEmpty()){
            Toast.makeText(this, "Amount is required", Toast.LENGTH_SHORT).show();
        }else if(to.isEmpty()){
            Toast.makeText(this, "Date is required", Toast.LENGTH_SHORT).show();
        }else if(leaveComment.isEmpty()){
            Toast.makeText(this, "Expense Comment is required", Toast.LENGTH_SHORT).show();

        }else{

            try{
                Expenses expenses = new Expenses();
                expenses.setExpenseTitle(expenseTitle);
                expenses.setAmount(Double.parseDouble(from));
                expenses.setDate(new SimpleDateFormat("MM/dd/yyyy").format(new SimpleDateFormat("MMM dd,yyyy").parse(to)));
                expenses.setDescription(leaveComment);

                if(employeeId!=0){

                    expenses.setEmployeeId(employeeId);

                }else{

                    expenses.setEmployeeId(PreferenceHandler.getInstance(CreateExpensesScreen.this).getUserId());
                }


                if(managerId!=0){

                    expenses.setManagerId(managerId);

                }else{
                    expenses.setManagerId(PreferenceHandler.getInstance(CreateExpensesScreen.this).getManagerId());
                }



                expenses.setOrganizationId(PreferenceHandler.getInstance(CreateExpensesScreen.this).getCompanyId());
                expenses.setStatus("Pending");
                try {

                    if(selectedImage != null && !selectedImage.isEmpty())
                    {
                        File file = new File(selectedImage);

                        if(file.length() <= 1*1024*1024)
                        {
                            FileOutputStream out = null;
                            String[] filearray = selectedImage.split("/");
                            final String filename = getFilename(filearray[filearray.length-1]);

                            out = new FileOutputStream(filename);
                            Bitmap myBitmap = BitmapFactory.decodeFile(selectedImage);
                            //write the compressed bitmap at the field_icon specified by filename.
                            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                            uploadImage(filename,expenses);
                        }
                        else
                        {
                            compressImage(selectedImage,expenses);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
