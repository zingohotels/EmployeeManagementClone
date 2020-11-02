package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.StockOrderLogsModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StockOrderLogsApi {

    @GET ("StockOrderLogs")
    Call <ArrayList < StockOrderLogsModel >> getStockOrderLog ( @Body StockOrderLogsModel stockOrderLogsModel );

    @GET ("StockOrderLogs/{id}")
    Call <StockOrderLogsModel> getStockOrderLogById ( @Path ("id") int id , @Body StockOrderLogsModel stockOrderLogsModel );

    @PUT ("StockOrderLogs/{id}")
    Call <StockOrderLogsModel> updateStockOrderLogById ( @Path ("id") int id , @Body StockOrderLogsModel stockOrderLogsModel );

    @POST ("StockOrderLogs")
    Call <StockOrderLogsModel> getStockOrderLogs ( @Body StockOrderLogsModel stockOrderLogsModel );

    @DELETE ("StockOrderLogs/{id}")
    Call <StockOrderLogsModel> deleteStockOrderLogById ( @Path ("id") int id , @Body StockOrderLogsModel stockOrderLogsModel );

}
