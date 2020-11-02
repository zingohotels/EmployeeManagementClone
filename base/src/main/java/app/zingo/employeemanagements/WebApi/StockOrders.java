package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;


import app.zingo.employeemanagements.model.StockOrdersModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StockOrders {

    @GET ("StockOrders")
    Call <ArrayList < StockOrdersModel >> getStockOrder ( );

    @GET ("StockOrders/{id}")
    Call < StockOrdersModel > getStockOrderById( @Path ("id") int id , @Body StockOrdersModel stockOrdersModel );

    @PUT ("StockOrders/{id}")
    Call < StockOrdersModel > updateStockOrderById( @Path ("id") int id , @Body StockOrdersModel stockOrdersModel );

    @POST ("StockOrders")
    Call < StockOrdersModel > addStockOrders( @Body StockOrdersModel stockOrdersModel );

    @DELETE ("StockOrders/{id}")
    Call < StockOrdersModel > deleteStockOrdersById( @Path ("id") int id , @Body StockOrdersModel stockOrdersModel );

}
