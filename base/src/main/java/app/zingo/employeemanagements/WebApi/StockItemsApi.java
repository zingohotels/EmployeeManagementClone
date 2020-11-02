package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.StockItemModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StockItemsApi {

    @GET ("StockItems/GetStockItemsByOrganizationId/{OrganizationId}")
    Call <ArrayList < StockItemModel >> getStockItemsByOrganizationId ( @Path ("OrganizationId") int id );

    @GET ("StockItems/GetStockItemsByStockCategoryId/{StockCategoryId}")
    Call <ArrayList <StockItemModel>> getStockItemsByStockCategoryId ( @Path ("StockCategoryId") int id , StockItemModel stockItemModel );

    @GET ("StockItems/GetStockItemsByBrandId/{BrandId}")
    Call <ArrayList <StockItemModel>> getStockItemsByBrandId ( @Path ("id") int id , StockItemModel stockItemModel );

    @GET ("StockItems/GetStockItemsByStockSubCategoryId/{StockSubCategoryId}")
    Call <ArrayList <StockItemModel>> getStockItemsByStockSubCategoryId ( @Path ("StockSubCategoryId") int id );

    @GET ("StockItems/GetStockItemsByBrandIdAndStockCategoryId/{BrandId}/{StockCategoryId}")
    Call <ArrayList <StockItemModel>> getStockItemsByBrandIdAndStockCategoryId ( @Path ("StockCategoryId") int id , StockItemModel stockItemModel );

    @GET ("StockItems/GetStockItemsByBrandIdAndStockSubCategoryId/{BrandId}/{StockSubCategoryId}")
    Call <ArrayList <StockItemModel>> getStockItemsByBrandIdAndStockSubCategoryId ( @Path ("StockSubCategoryId") int id , StockItemModel stockItemModel );

    @GET ("StockItems")
    Call <ArrayList <StockItemModel>> getStockItem ( @Body StockItemModel stockItemModel );

    @GET ("StockItems/{id}")
    Call <StockItemModel> getStockItemsById ( @Path ("id") int id , @Body StockItemModel stockItemModel );

    @PUT ("StockItems/{id}")
    Call <StockItemModel> updateStockItemsById ( @Path ("id") int id , @Body StockItemModel stockItemModel );

    @POST ("StockItems")
    Call <StockItemModel> addStockItems ( @Body StockItemModel stockItemModel );

    @DELETE ("StockItems/{id}")
    Call <StockItemModel> deleteStockItemsById ( @Path ("id") int id , @Body StockItemModel stockItemModel );

}
