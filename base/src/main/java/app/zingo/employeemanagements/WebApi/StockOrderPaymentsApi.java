package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.StockOrderPaymentsModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StockOrderPaymentsApi {

    @GET ("StockOrderPayments")
    Call <ArrayList < StockOrderPaymentsModel >> getStockOrderPayment ( @Body StockOrderPaymentsModel stockOrderPaymentsModel );

    @GET ("StockOrderPayments/{id}")
    Call <StockOrderPaymentsModel> getStockOrderPaymentById ( @Path ("id") int id , @Body StockOrderPaymentsModel stockOrderPaymentsModel );

    @PUT ("StockOrderPayments/{id}")
    Call <StockOrderPaymentsModel> updateStockOrderPaymentById ( @Path ("id") int id , @Body StockOrderPaymentsModel stockOrderPaymentsModel );

    @POST ("StockOrderPayments")
    Call <StockOrderPaymentsModel> getStockOrderPayments ( @Body StockOrderPaymentsModel stockOrderPaymentsModel );

   @DELETE ("StockOrderPayments/{id}")
   Call <StockOrderPaymentsModel> deleteStockOrderPayments ( @Body StockOrderPaymentsModel stockOrderPaymentsModel );


}
