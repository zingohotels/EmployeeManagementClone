package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.StockOrderPersonInfoModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StockOrderPersonInfoesApi {

    @GET ("StockOrderPersonInfoes")
    Call <ArrayList <StockOrderPersonInfoModel>> getStockOrderPersonInfoModel ( @Body StockOrderPersonInfoModel stockOrderPersonInfoModel );

    @GET ("StockOrderPersonInfoes/{id}")
    Call <StockOrderPersonInfoModel> getStockOrderPersonInfoById ( @Path ("id") int id , @Body StockOrderPersonInfoModel stockOrderPersonInfoModel );

    @PUT ("StockOrderPersonInfoes/{id}")
    Call <StockOrderPersonInfoModel> updateStockOrderPersonInfoById ( @Path ("id") int id , @Body StockOrderPersonInfoModel stockOrderPersonInfoModel );

    @POST ("StockOrderPersonInfoes")
    Call <StockOrderPersonInfoModel> getStockOrderPersonInfo ( @Body StockOrderPersonInfoModel stockOrderPersonInfoModel );


    @DELETE ("StockOrderPersonInfoes/{id}")
    Call <StockOrderPersonInfoModel> deleteStockOrderPersonInfoById ( @Path ("id") int id , @Body StockOrderPersonInfoModel stockOrderPersonInfoModel );


}
