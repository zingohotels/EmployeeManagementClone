package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.StockOrderDetailsModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StockOrderDetailsApi {

    @GET ("StockOrderDetails")
    Call <ArrayList < StockOrderDetailsModel >> getStockOrderDetail( @Body StockOrderDetailsModel stockOrderDetailsModel );

    @GET ("StockOrderDetails/{id}")
    Call < StockOrderDetailsModel > getStockOrderDetailById( @Path ("id") int id , @Body StockOrderDetailsModel stockOrderDetailsModel );

    @PUT ("StockOrderDetails/{id}")
    Call < StockOrderDetailsModel > updateStockOrderDetailById( @Path ("id") int id , @Body StockOrderDetailsModel stockOrderDetailsModel );

    @POST ("StockOrderDetails")
    Call < StockOrderDetailsModel > getStockOrderDetails( @Body StockOrderDetailsModel stockOrderDetailsModel );

    @DELETE ("StockOrderDetails/{id}")
    Call < StockOrderDetailsModel > deleteStockOrderDetails( @Path ("id") int id , @Body StockOrderDetailsModel stockOrderDetailsModel );

}
