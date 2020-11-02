package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.StockSubCategoryModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StockSubCategoriesApi {

    @GET ("StockSubCategories/GetStockSubCategoryByOrganizationId/{OrganizationId}")
    Call <ArrayList <StockSubCategoryModel>> getStockSubCategoryByOrganizationId ( @Path ("OrganizationId") int id );

    @GET ("StockSubCategories/GetStockSubCategoriesByStockCategoryId/{StockCategoryId}")
    Call <ArrayList < StockSubCategoryModel >> getStockSubCategoriesByStockCategoryId ( @Path ("StockCategoryId") int id );

    @GET ("StockSubCategories")
    Call <ArrayList <StockSubCategoryModel>> getStockSubCategories ( @Body StockSubCategoryModel stockSubCategoryModel );

    @GET ("StockSubCategories/{id}")
    Call <StockSubCategoryModel> getStockSubCategoryById ( @Body StockSubCategoryModel stockSubCategoryModel );

    @PUT ("StockSubCategories/{id}")
    Call <StockSubCategoryModel> updateStockSubCategoriesById ( @Path ("id") int id , @Body StockSubCategoryModel stockSubCategoryModel );

    @POST ("StockSubCategories")
    Call <StockSubCategoryModel> addStockSubCategory ( @Body StockSubCategoryModel stockSubCategoryModel );

    @DELETE ("StockSubCategories/{id}")
    Call <StockSubCategoryModel> deleteStockSubCategoriesById ( @Path ("id") int id , @Body StockSubCategoryModel stockSubCategoryModel );

}
