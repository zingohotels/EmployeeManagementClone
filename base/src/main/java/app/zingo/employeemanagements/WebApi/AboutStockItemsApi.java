package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.AboutStockItemModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AboutStockItemsApi {

    @GET ("AboutStockItems")
    Call <ArrayList <AboutStockItemModel>> getAboutStockItem ( @Body AboutStockItemModel aboutStockItemModel );

    @GET ("AboutStockItems/{id}")
    Call <AboutStockItemModel> getAboutStockItemsById ( @Path ("id") int id , @Body AboutStockItemModel aboutStockItemModel );

    @PUT ("AboutStockItems/{id}")
    Call <AboutStockItemModel> updateAboutStockItemsById ( @Path ("id") int id , @Body AboutStockItemModel aboutStockItemModel );

    @POST ("AboutStockItems")
    Call <AboutStockItemModel> getAboutStockItems ( @Body AboutStockItemModel aboutStockItemModel );

    @DELETE ("AboutStockItems")
    Call <AboutStockItemModel> deleteAboutStockItemsById ( @Path ("id") int id , @Body AboutStockItemModel aboutStockItemModel );

}
