package app.zingo.employeemanagements.WebApi;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public interface UploadApi {

    @Multipart
    //@POST("Upload/user/PostUserImage")
    @POST("Upload/user/PostProfileImage")
    Call<String> uploadProfileImages ( @Part MultipartBody.Part file , @Part ("UploadedImage") RequestBody name );
}
