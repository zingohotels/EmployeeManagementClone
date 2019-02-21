package app.zingo.employeemanagements.UI.NewEmployeeDesign;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.zingo.employeemanagements.Custom.MyEditText;
import app.zingo.employeemanagements.Model.Employee;
import app.zingo.employeemanagements.Model.EmployeeDocuments;
import app.zingo.employeemanagements.Model.Organization;
import app.zingo.employeemanagements.Model.PaySlips;
import app.zingo.employeemanagements.R;
import app.zingo.employeemanagements.UI.Admin.CreatePaySlip;
import app.zingo.employeemanagements.UI.Landing.InternalServerErrorScreen;
import app.zingo.employeemanagements.UI.NewAdminDesigns.TeamMembersList;
import app.zingo.employeemanagements.Utils.PreferenceHandler;
import app.zingo.employeemanagements.Utils.ThreadExecuter;
import app.zingo.employeemanagements.Utils.Util;
import app.zingo.employeemanagements.WebApi.OrganizationApi;
import app.zingo.employeemanagements.WebApi.PayslipAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPaySlipScreen extends AppCompatActivity {

    private static TextInputEditText mName,mDesignation,mEId,mPAN,mDepartment,mLeaveTaken,
            mBasic,mHouse,mConvey,mMedical,mVehicle,mWashing,mOther,mOthers,mPF,mESI,mLoan,mPT,mLeaves,mAdvance;//mYear

    //TextInputEditText mDOJ;
    MyEditText mDOJ,mMonth,mFilterDate;
    Button mPaySlip;

    private static AppCompatButton mCreate;

    String name,design,eid,pan,month,year,doj,
            dept,leaveTaken,basic,house,convey,medical,vehicle,
            washing,other,others,pf,esi,loan,pt,leaves,advance;

    String companyName,city,state,websites;

    String email;

    double addition=0,deduction=0,net=0;

    //invoice
    String invoicepdfFilename = "";
    String invoicePdf;
    String invoicepdfFile,mGSTNumber;

    private BaseFont bfBold;
    private BaseFont bf;
    private int pageNumber = 0;

    Employee employee;
    int employeeId,leaveCount=0;

    private String current = "";
    private String ddmmyyyy = "MMYYYY";
    private Calendar cal = Calendar.getInstance();

    String dojsValue = "";
    int dojsMValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_view_pay_slip_screen);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Pay Slip");

            mName = (TextInputEditText)findViewById(R.id.employee_name);
            mDesignation = (TextInputEditText)findViewById(R.id.employee_desgination);
            mEId = (TextInputEditText)findViewById(R.id.employee_id);
            mPAN = (TextInputEditText)findViewById(R.id.employee_pan);
            mMonth = (MyEditText) findViewById(R.id.salary_month);
            mFilterDate = (MyEditText) findViewById(R.id.filter_month);
            //mYear = (TextInputEditText)findViewById(R.id.salary_year);
            mDOJ = (MyEditText) findViewById(R.id.doj);
            mDOJ.setEnabled(false);
            mDepartment = (TextInputEditText)findViewById(R.id.department);
            mLeaveTaken = (TextInputEditText)findViewById(R.id.leave_taken);
            mBasic = (TextInputEditText)findViewById(R.id.basic_pay);
            mHouse = (TextInputEditText)findViewById(R.id.house_allow);
            mConvey = (TextInputEditText)findViewById(R.id.convey_allow);
            mMedical = (TextInputEditText)findViewById(R.id.medi_allow);
            mVehicle = (TextInputEditText)findViewById(R.id.vehicle_allow);
            mWashing = (TextInputEditText)findViewById(R.id.washing_allow);
            mOther = (TextInputEditText)findViewById(R.id.other_allow);
            mOthers = (TextInputEditText)findViewById(R.id.other_allows);
            mPF = (TextInputEditText)findViewById(R.id.pf);
            mESI = (TextInputEditText)findViewById(R.id.esi);
            mLoan = (TextInputEditText)findViewById(R.id.loan);
            mPT = (TextInputEditText)findViewById(R.id.pt);
            mLeaves = (TextInputEditText)findViewById(R.id.leaves);
            mAdvance = (TextInputEditText)findViewById(R.id.advance);
            mPaySlip = (Button) findViewById(R.id.search_slip);


            mCreate = (AppCompatButton) findViewById(R.id.save);


           // mMonth.setText(new SimpleDateFormat("MM/yyyy").format(new Date()));
            // mYear.setText(new SimpleDateFormat("yyyy").format(new Date()));

            mMonth.addTextChangedListener(new TextWatcher() {



                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (!s.toString().equals(current)) {
                        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 0; i <= cl && i < 4; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;

                        if (clean.length() < 6){
                            clean = clean + ddmmyyyy.substring(clean.length());
                        }else{
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            //int day  = Integer.parseInt(clean.substring(0,2));
                            int mon  = Integer.parseInt(clean.substring(0,2));
                            int year = Integer.parseInt(clean.substring(2,6));

                            String currentYear = new SimpleDateFormat("yyyy").format(new Date());

                            int years = Integer.parseInt(currentYear);

                            String currentMonth = new SimpleDateFormat("MM").format(new Date());

                            int months = Integer.parseInt(currentMonth);
                            int dojMonth = 0;
                            int dojYear =0;


                            if(dojsValue!=null&&!dojsValue.isEmpty()){


                                dojMonth = Integer.parseInt(dojsValue);
                                dojYear = dojsMValue;

                            }

                            mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                            cal.set(Calendar.MONTH, mon-1);
                            year = (year<dojYear)?dojYear:(year>years)?years:year;
                            cal.set(Calendar.YEAR, year);
                            if(year==years){

                                if(mon>months){
                                    mon = months;
                                    cal.set(Calendar.MONTH,mon-1);
                                }

                            }else if(year==dojYear){

                                if(mon<dojMonth){
                                    mon = dojMonth;
                                    cal.set(Calendar.MONTH,mon-1);
                                }
                            }

                            clean = String.format("%02d%02d", mon, year);
                        }

                        clean = String.format("%s/%s",
                                clean.substring(0, 2),
                                clean.substring(2, 6));

                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        mMonth.setText(current);
                        mMonth.setSelection(sel < current.length() ? sel : current.length());
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            getCompany(PreferenceHandler.getInstance(ViewPaySlipScreen.this).getCompanyId());

            mFilterDate.addTextChangedListener(new TextWatcher() {



                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (!s.toString().equals(current)) {
                        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 0; i <= cl && i < 4; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;

                        if (clean.length() < 6){
                            clean = clean + ddmmyyyy.substring(clean.length());
                        }else{
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            //int day  = Integer.parseInt(clean.substring(0,2));
                            int mon  = Integer.parseInt(clean.substring(0,2));
                            int year = Integer.parseInt(clean.substring(2,6));

                            String currentYear = new SimpleDateFormat("yyyy").format(new Date());

                            int years = Integer.parseInt(currentYear);

                            String currentMonth = new SimpleDateFormat("MM").format(new Date());

                            int months = Integer.parseInt(currentMonth);


                            mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                            cal.set(Calendar.MONTH, mon-1);
                            year = (year<1900)?1900:(year>years)?years:year;
                            cal.set(Calendar.YEAR, year);
                            if(year==years){

                                if(mon>months){
                                    mon = months;
                                    cal.set(Calendar.MONTH,mon-1);
                                }

                            }

                            clean = String.format("%02d%02d", mon, year);
                        }

                        clean = String.format("%s/%s",
                                clean.substring(0, 2),
                                clean.substring(2, 6));

                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        mFilterDate.setText(current);
                        mFilterDate.setSelection(sel < current.length() ? sel : current.length());
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });






            mPaySlip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                  //  validate();

                    String month = mFilterDate.getText().toString();

                    if(month==null||month.isEmpty()){
                        Toast.makeText(ViewPaySlipScreen.this, "Please enter the month", Toast.LENGTH_SHORT).show();
                    }else{
                        try{

                            Date date = new SimpleDateFormat("MM/yyyy").parse(month);
                            String monthSearch = new SimpleDateFormat("MMM").format(date);
                            String yearSearch = new SimpleDateFormat("yyyy").format(date);

                            getPayslip(monthSearch,yearSearch);


                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(ViewPaySlipScreen.this, "Check format for search month", Toast.LENGTH_SHORT).show();
                        }
                    }




                }
            });

            mCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    validate();


                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getCompany(final int id) throws Exception{

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final OrganizationApi subCategoryAPI = Util.getClient().create(OrganizationApi.class);
                Call<ArrayList<Organization>> getProf = subCategoryAPI.getOrganizationById(id);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<Organization>>() {

                    @Override
                    public void onResponse(Call<ArrayList<Organization>> call, Response<ArrayList<Organization>> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {


                            companyName = response.body().get(0).getOrganizationName();
                            city = response.body().get(0).getCity();
                            state = response.body().get(0).getState();
                            websites = response.body().get(0).getWebsite();


                        }else{


                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Organization>> call, Throwable t) {

                    }
                });

            }

        });
    }

    public void getPayslip(final String month,final String year) throws Exception{

        new ThreadExecuter().execute(new Runnable() {
            @Override
            public void run() {

                final PayslipAPI subCategoryAPI = Util.getClient().create(PayslipAPI.class);
                Call<ArrayList<PaySlips>> getProf = subCategoryAPI.getPaySlipByMonYearEmp(PreferenceHandler.getInstance(ViewPaySlipScreen.this).getUserId(),month,year);
                //Call<ArrayList<Blogs>> getBlog = blogApi.getBlogs();

                getProf.enqueue(new Callback<ArrayList<PaySlips>>() {

                    @Override
                    public void onResponse(Call<ArrayList<PaySlips>> call, Response<ArrayList<PaySlips>> response) {

                        if (response.code() == 200||response.code() == 201||response.code() == 204)
                        {

                            if(response.body()!=null&&response.body().size()!=0){


                                PaySlips paySlips = response.body().get(response.body().size()-1);

                                if(paySlips!=null){



                                    mName.setText(""+paySlips.getEmployeeName());
                                    mDesignation.setText(""+paySlips.getDesignationName());
                                    mEId.setText(PreferenceHandler.getInstance(ViewPaySlipScreen.this).getCompanyName().substring(0,2)+""+paySlips.getEmployeeId());
                                    mPAN.setText(""+paySlips.getPANnumber());
                                    mMonth.setText(""+paySlips.getMonth()+" "+paySlips.getYear());
                                    mDOJ.setText(""+paySlips.getDateOfJoining());
                                    mDepartment.setText(""+paySlips.getDepartmentName());
                                    mLeaveTaken.setText(""+paySlips.getLeaveTaken());
                                    mBasic.setText(""+paySlips.getBasicPay());
                                    mHouse.setText(""+paySlips.getRentAllowance());
                                    mConvey.setText(""+paySlips.getConveyAllowance());
                                    mMedical.setText(""+paySlips.getMedicalAllowance());
                                    mPF.setText(""+paySlips.getPF());
                                    mESI.setText(""+paySlips.getESI());
                                    mLoan.setText(""+paySlips.getLoan());
                                    mPT.setText(""+paySlips.getProfessionalTax());
                                    mLeaves.setText(""+paySlips.getLeaves());
                                    mAdvance.setText(""+paySlips.getAdvance());

                                }
                            }else{
                                Toast.makeText(ViewPaySlipScreen.this, "No Slip on selected month", Toast.LENGTH_SHORT).show();
                            }




                        }else{

                            Toast.makeText(ViewPaySlipScreen.this, "No Slip on selected month", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<PaySlips>> call, Throwable t) {

                        Toast.makeText(ViewPaySlipScreen.this, "No Slip on selected month", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });
    }

    public void validate(){

        name = mName.getText().toString();
        design = mDesignation.getText().toString();
        eid = mEId.getText().toString();
        pan = mPAN.getText().toString();
        String monthValue = mMonth.getText().toString();
        //year = mYear.getText().toString();
        doj = mDOJ.getText().toString();
        dept = mDepartment.getText().toString();
        leaveTaken = mLeaveTaken.getText().toString();
        basic = mBasic.getText().toString();
        house = mHouse.getText().toString();
        convey = mConvey.getText().toString();
        medical = mMedical.getText().toString();
        vehicle = mVehicle.getText().toString();
        washing = mWashing.getText().toString();
        other = mOther.getText().toString();
        others = mOthers.getText().toString();
        pf = mPF.getText().toString();
        esi = mESI.getText().toString();
        loan = mLoan.getText().toString();
        pt = mPT.getText().toString();
        leaves = mLeaves.getText().toString();
        advance = mAdvance.getText().toString();

        if(name==null||name.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(design==null||design.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(eid==null||eid.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(pan==null||pan.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(monthValue==null||monthValue.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }/*else if(year==null||year.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }*/else if(doj==null||doj.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(dept==null||dept.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(leaveTaken==null||leaveTaken.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(basic==null||basic.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(house==null||house.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(convey==null||convey.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(medical==null||medical.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }/*else if(vehicle==null||vehicle.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(washing==null||washing.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(other==null||other.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }*//*else if(others==null||others.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }*/else if(pf==null||pf.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(esi==null||esi.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(loan==null||loan.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(pt==null||pt.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(leaves==null||leaves.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else if(advance==null||advance.isEmpty()){

            Toast.makeText(this, "Field should not empty", Toast.LENGTH_SHORT).show();
        }else{

            double basics=0,houses=0,conveys=0,medicals=0,vehicles=0,washings=0,otherss=0,othersss=0,pfs=0,esis=0,loans=0,pts=0,leavess=0,advances=0;

            try{

                basics = Double.parseDouble(basic);
                houses = Double.parseDouble(house);
                conveys = Double.parseDouble(convey);
                medicals = Double.parseDouble(medical);
//                vehicles = Double.parseDouble(vehicle);
                // washings = Double.parseDouble(washing);
                //otherss = Double.parseDouble(other);
                //  othersss = Double.parseDouble(others);

                pfs = Double.parseDouble(pf);
                esis = Double.parseDouble(esi);
                loans = Double.parseDouble(loan);
                pts = Double.parseDouble(pt);
                leavess = Double.parseDouble(leaves);
                advances = Double.parseDouble(advance);

                addition = basics+houses+conveys+medicals+vehicles+washings+otherss+othersss;
                deduction = pfs+esis+loans+pts+leavess+advances;

                PaySlips paySlips = new PaySlips();
                paySlips.setEmployeeName(name);
                paySlips.setDesignationName(design);
                paySlips.setEmployeeId(employeeId);
                paySlips.setPANnumber(pan);

                Date monthDate = new SimpleDateFormat("MM/yyyy").parse(monthValue);
                month = new SimpleDateFormat("MMM").format(monthDate);
                year = new SimpleDateFormat("yyyy").format(monthDate);

                paySlips.setMonth(month);
                paySlips.setYear(year);
                paySlips.setDateOfJoining(doj);
                paySlips.setDepartmentName(dept);
                paySlips.setLeaveTaken(leaveCount);
                paySlips.setBasicPay(basics);
                paySlips.setRentAllowance(houses);
                paySlips.setConveyAllowance(conveys);
                paySlips.setMedicalAllowance(medicals);
                paySlips.setPF(pfs);
                paySlips.setESI(esis);
                paySlips.setLoan(loans);
                paySlips.setProfessionalTax(pts);
                paySlips.setLeaves(leavess);
                paySlips.setAdvance(advances);
                paySlips.setCreatedBy(""+PreferenceHandler.getInstance(ViewPaySlipScreen.this).getUserFullName());
                paySlips.setCreatedDate(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));


                if(pfs==0){
                    pf="-";
                }
                if(esis==0){
                    esi="-";
                }
                if(loans==0){
                    loan="-";
                }
                if(pts==0){
                    pt="-";
                }
                if(leavess==0){
                    leaves="-";
                }
                if(advances==0){
                    advance="-";
                }

                net = addition - deduction;


                try{

                    if((invoicepdfFile==null||invoicepdfFile.isEmpty())){
                        boolean fileCreated = createPDFInvoice();
                        if(fileCreated){
                            sendEmailattacheInvoice();
                        }else{
                          //  Toast.makeText(CreatePaySlip.this, "File not generate but booking happened", Toast.LENGTH_SHORT).show();
                            createPDFInvoice();

                        }

                    }else if((invoicepdfFile!=null&&!invoicepdfFile.isEmpty())){

                        sendEmailattacheInvoice();

                    }else{
                        boolean fileCreated = createPDFInvoice();
                        if(fileCreated){
                            sendEmailattacheInvoice();
                        }else{
                            createPDFInvoice();
                        }
                        // Toast.makeText(BillDetails.this, "Booking Not done", Toast.LENGTH_SHORT).show();
                    }
                    //createPDFInvoice();

                }catch (Exception e){
                    e.printStackTrace();
                }


            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private boolean createPDFInvoice () throws Exception{

        Document doc = new Document();
        PdfWriter docWriter = null;
        initializeFonts();

        try {
            File sd = Environment.getExternalStorageDirectory();
            invoicePdf = System.currentTimeMillis() + ".pdf";

            File directory = new File(sd.getAbsolutePath()+"/Employee/Pdf/PaySlip/");
            //create directory if not exist
            if (!directory.exists() && !directory.isDirectory()) {
                directory.mkdirs();
            }

            invoicepdfFile = sd.getAbsolutePath()+"/Employee/Pdf/PaySlip/"+invoicePdf;

            File file = new File(directory, invoicePdf);
            String path = "docs/" + invoicepdfFilename;
            docWriter = PdfWriter.getInstance(doc , new FileOutputStream(file));
            doc.addAuthor("Lucida Hospitality Pvt Ltd");
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator("zingohotels.com");
            doc.addTitle("Invoice");
            doc.setPageSize(PageSize.LETTER);

            doc.open();
            PdfContentByte cb = docWriter.getDirectContent();

            boolean beginPage = true;
            int y = 0;


            if(beginPage){
                generateLayoutInvoice(doc, cb);
                printPageNumber(cb);
                //doc.newPage();
                //generateLayoutNextInvoice(doc, cb);
                // printPageNumber(cb);
            }

            // printPageNumber(cb);
            return  true;

        }
        catch (DocumentException dex)
        {
            dex.printStackTrace();
            return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally
        {
            if (doc != null)
            {
                doc.close();
            }
            if (docWriter != null)
            {
                docWriter.close();
            }

        }
    }

    private void initializeFonts(){


        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void generateLayoutInvoice(Document doc, PdfContentByte cb)  {

        try {

            cb.setLineWidth(1f);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy");
            SimpleDateFormat sdfs = new SimpleDateFormat("ddMMHHmm");
            SimpleDateFormat sdfd = new SimpleDateFormat("dd MMM");


            createHeadingsTitles(cb,25,760,companyName);
            createContentTitles(cb,25,740,city);
            createContentTitles(cb,25,720,state);
          /*  createContentTitles(cb,305,700,"Near JNC College, Bangalore-560095");
            createContentTitles(cb,335,680,"CIN: U55209KA2016PTC096968");*/
            //createContentTitles(cb,355,660,"Email: "+);
            createContentTitles(cb,25,640,"Web: "+websites);
            //createContentTitles(cb,390,620,"Mob: +91- 7065 651 651");

            createHeadingsTitles(cb,250,560,"Salary Slip");

            cb.rectangle(25,420,560,120);
            cb.stroke();

            createHeadings(cb,45,520,"Employee Name:");
            createHeadings(cb,45,500,"Designation:");
            createHeadings(cb,45,480,"Employee ID:");
            createHeadings(cb,45,460,"Income Tax PAN:");

            createContentTitles(cb,160,520,""+name);
            createContentTitles(cb,160,500,""+design);
            createContentTitles(cb,160,480,""+eid);
            createContentTitles(cb,160,460,""+pan);

            createHeadings(cb,390,520,"Month:");
            createHeadings(cb,390,500,"Year:");
            createHeadings(cb,390,480,"DOJ:");
            createHeadings(cb,390,460,"Department:");
            createHeadings(cb,390,440,"Leave Taken:");

            createContentTitles(cb,480,520,""+month);
            createContentTitles(cb,480,500,""+year);
            createContentTitles(cb,480,480,""+doj);
            createContentTitles(cb,480,460,""+dept);
            createContentTitles(cb,480,440,""+leaveTaken);

            cb.rectangle(50,160,510,240);
            cb.moveTo(210,400);
            cb.lineTo(210,160);
            cb.moveTo(310,400);
            cb.lineTo(310,160);
            cb.moveTo(450,400);
            cb.lineTo(450,160);
            cb.rectangle(50,380,510,20);
            cb.rectangle(50,160,510,40);
            cb.rectangle(310,200,250,40);
            cb.stroke();

            createHeadings(cb,80,385,"SALARY");
            createHeadings(cb,220,385,"AMOUNT Rs.");
            createHeadings(cb,320,385,"DEDUCTIONS");
            createHeadings(cb,460,385,"AMOUNT Rs.");

            createContentTitles(cb,60,365,"Basic Pay");
            createContentTitles(cb,60,345,"House Rent Allowance");
            createContentTitles(cb,60,325,"Conveyance Allowance");
            createContentTitles(cb,60,305,"Medical Allowance");
            createContentTitles(cb,60,285,"Vehicle Allowance");
            createContentTitles(cb,60,265,"Washing Allowance");
            createContentTitles(cb,60,245,"Other Allowance");


            createContentTitles(cb,320,365,"Provident Fund");
            createContentTitles(cb,320,345,"E.S.I");
            createContentTitles(cb,320,325,"Loan");
            createContentTitles(cb,320,305,"Advance");
            createContentTitles(cb,320,285,"Professional Tax");
            createContentTitles(cb,320,265,"Leaves");

            createHeadings(cb,220,365,""+basic);
            createHeadings(cb,220,345,""+house);
            createHeadings(cb,220,325,""+convey);
            createHeadings(cb,220,305,""+medical);
            createHeadings(cb,220,285,""+vehicle);
            createHeadings(cb,220,265,""+washing);
            createHeadings(cb,220,245,""+other);

            createHeadings(cb,460,365,""+pf);
            createHeadings(cb,460,345,""+esi);
            createHeadings(cb,460,325,""+loan);
            createHeadings(cb,460,305,""+advance);
            createHeadings(cb,460,285,""+pt);
            createHeadings(cb,460,265,""+leaves);

            createHeadings(cb,320,225,"Total Deductions");
            createHeadings(cb,460,225,""+new DecimalFormat("##,###.##").format(deduction));
            createHeadings(cb,80,180,"Gross Pay");
            createHeadings(cb,220,180,""+new DecimalFormat("##,###.##").format(addition));
            createHeadings(cb,350,180,"Net Pay");
            createHeadings(cb,460,180,""+new DecimalFormat("##,###.##").format(net));




            createContentTariff(cb,50,60, "*This is a computer generated salary slip and does not require a signature.",PdfContentByte.ALIGN_LEFT);


            //This is a computer generated invoice and does not require a signature.

            //Add logo_zingo
            /*Drawable d = getResources ().getDrawable (R.drawable.logo_zingo);
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            ByteArrayOutputStream streams = new ByteArrayOutputStream();
            getResizedBitmap(bitmap,75,75).compress(Bitmap.CompressFormat.JPEG, 100, streams);
            byte[] bitmapData = streams.toByteArray();

            Image companyLogo = Image.getInstance(bitmapData);
            // Image companyLogo = Image.getInstance("logo_zingo_zingo.png");
            companyLogo.setAbsolutePosition(25,650);
            companyLogo.scalePercent(150);
            doc.add(companyLogo);*/
        }


        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void printPageNumber(PdfContentByte cb){


        cb.beginText();
        cb.setFontAndSize(bfBold, 8);
        cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, "Page No. " + (pageNumber+1), 570 , 10, 0);
        cb.endText();

        pageNumber++;

    }

    private void createHeadingsTitles(PdfContentByte cb, float x, float y, String text){


        cb.beginText();
        cb.setFontAndSize(bfBold, 18);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();

    }

    private void createContentTariff(PdfContentByte cb, float x, float y, String text, int align){


        cb.beginText();
        cb.setFontAndSize(bf, 14);
        cb.showTextAligned(align, text.trim(), x , y, 0);
        cb.endText();

    }
    private void createHeadings(PdfContentByte cb, float x, float y, String text){


        cb.beginText();
        cb.setFontAndSize(bfBold, 12);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();

    }
    public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
    }

    private void createContentTitles(PdfContentByte cb, float x, float y, String text){


        cb.beginText();
        cb.setFontAndSize(bf, 14);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();

    }

    private void sendEmailattacheInvoice() throws Exception {
        String[] mailto = {email};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Pay Slip for"+month);
        emailIntent.putExtra(Intent.EXTRA_EMAIL,mailto);
        File root = Environment.getExternalStorageDirectory();
        String pathToMyAttachedFile = "/Employee/Pdf/PaySlip/"+invoicePdf;
        File file = new File(root, pathToMyAttachedFile);
        if (!file.exists() || !file.canRead()) {
            return;
        }
        Uri uri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            uri = FileProvider.getUriForFile(this, "app.zingo.employeemanagements.fileprovider", file);
        }else{
            uri = Uri.fromFile(file);
        }

        //Uri uri = Uri.fromFile(file);
        if(uri!=null){
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        }else{
            Toast.makeText(ViewPaySlipScreen.this, "File cannot access", Toast.LENGTH_SHORT).show();
        }
        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                ViewPaySlipScreen.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
