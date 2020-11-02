package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.StockItemPricingModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StockItemPricingsApi {

    @GET ("StockItemPricings")
    Call <ArrayList< StockItemPricingModel >> getStockItemPricing ( );

    @GET ("StockItemPricings/{id}")
    Call <StockItemPricingModel> getStockItemPricingById ( @Path ("id") int id );

    @PUT ("StockItemPricings/{id}")
    Call <StockItemPricingModel> updateStockItemPricingById ( @Path ("id") int id , @Body StockItemPricingModel stockItemPricingModel );

    @POST ("StockItemPricings")
    Call <StockItemPricingModel> getStockItemPricings ( @Body StockItemPricingModel stockItemPricingModel );



    @DELETE ("StockItemPricings/{id}")
    Call <StockItemPricingModel> deleteStockItemPricingById ( @Path ("id") int id);

}
