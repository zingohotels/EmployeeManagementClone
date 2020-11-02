package app.zingo.employeemanagements.WebApi;

import java.util.ArrayList;

import app.zingo.employeemanagements.model.BrandsModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BrandsApi {

    @GET ("Brands/GetBrandsByOrganizationId/{OrganizationId}")
    Call <ArrayList < BrandsModel >> getBrandsByOrganizationId ( @Path ("OrganizationId") int id );

    @GET ("Brands")
    Call <ArrayList < BrandsModel >> getBrand( @Path ("id") int id , @Body BrandsModel brandsModel );

    @GET ("Brands/{id}")
    Call < BrandsModel > getBrandsById ( @Path ("id") int id );

    @PUT ("Brands/{id}")
    Call < BrandsModel > updateBrandsById( @Path ("id") int id , @Body BrandsModel brandsModel );

    @POST ("Brands")
    Call < BrandsModel > addBrands( @Body BrandsModel brandsModel );

    @DELETE ("Brands/{id}")
    Call < BrandsModel > deleteBrands( @Path ("id") int id , @Body BrandsModel brandsModel );
}
