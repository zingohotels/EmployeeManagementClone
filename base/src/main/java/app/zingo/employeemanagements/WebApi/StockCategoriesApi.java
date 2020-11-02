package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.StockCategoryModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StockCategoriesApi {

    @GET ("StockCategories/GetStockCategoryByOrganizationId/{OrganizationId}")
    Call <ArrayList < StockCategoryModel >> getStockCategoryByOrganizationId ( @Path ("OrganizationId") int id );

    @GET ("StockCategories")
    Call <ArrayList < StockCategoryModel >> getStockCategory( @Body StockCategoryModel stockCategoryModel );

    @GET ("StockCategories/{id}")
    Call < StockCategoryModel > getStockCategoryById( @Path ("id") int id , @Body StockCategoryModel stockCategoryModel );

    @PUT ("StockCategories/{id}")
    Call < StockCategoryModel > updateStockCategoryById( @Path ("id") int id , @Body StockCategoryModel stockCategoryModel );

    @POST ("StockCategories")
    Call < StockCategoryModel > addStockCategory( @Body StockCategoryModel stockCategoryModel );

    @DELETE ("StockCategories/{id}")
    Call < StockCategoryModel > deleteStockCategoryById( @Path ("id") int id , @Body StockCategoryModel stockCategoryModel );

}
