package c.top3haui.themcuahang;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by VanCuong on 04/04/2017.
 */

public class ActivityNext extends AppCompatActivity implements SingleUploadBroadcastReceiver.Delegate {
    EditText ed_tinh, ed_quan, ed_dc;
    Button btn_send,btn_file;
    ImageView img_bg;
    Items items = new Items();
    private int PICK_IMAGE_REQUEST = 1;
    String UPLOAD_URL = "http://goigas.96.lt/cuahang/upload.php";
    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    String link_image;
    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;
    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        ed_tinh = (EditText) findViewById(R.id.ed_tinh);
        ed_quan = (EditText) findViewById(R.id.ed_quan);
        btn_send = (Button) findViewById(R.id.btn_send);
        ed_dc = (EditText) findViewById(R.id.ed_dc);
        img_bg = (ImageView) findViewById(R.id.img_bg);
        if (Build.VERSION.SDK_INT >= 23)
        requestStoragePermission();
        final Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("data");

        items = (Items) bundle.getSerializable("item");
        img_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMultipart();

            }
        });

    }

    public void callAddVolley(Items items) {
        final String ten = items.getTen();
        final String user_id = items.getUser_id();
        final String loaigas = items.getLoaigas();
        final String motagia = items.getMotagia();
        final String sdt = items.getSdt();
        final String chucuahang = items.getChucuahang();
        final String diadiem = ed_dc.getText().toString().trim() + "," + ed_quan.getText().toString().trim() + ","
                + ed_tinh.getText().toString().trim();
        final String latlng = items.getLatlng()+"0";
        String url = "http://goigas.96.lt/cuahang/create_cuahang.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Post", response);
                //phan tich json
                try {
                    JSONObject obj = new JSONObject(response);

                    String name = obj.getString("success");
                    if (name.equals("1")) {
                        Toast.makeText(getApplicationContext(), "Gui thanh cong", Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(getApplicationContext(), "Gui loi", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("loi", e.getMessage());
                }
//                Snackbar snackbar = Snackbar
//                        .make(findViewById(R.id.activity_next), "Thêm thành công!", Snackbar.LENGTH_SHORT);
//                snackbar.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Add new ", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ten", ten);
                params.put("loaigas", loaigas);
                params.put("motagia", motagia);
                params.put("sdt", sdt);
                params.put("chucuahang", chucuahang);
                params.put("latlng", latlng);
                params.put("diadiem", diadiem);
                params.put("user_id", user_id);
                params.put("link_img", getLink_image());
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void uploadMultipart() {
        //getting name for the image

        uploadReceiver.register(this);
        //getting the actual path of the image
        final String path = getRealPathFromURI(filePath);
        final String name = path.substring(path.lastIndexOf("/") + 1);
        //Uploading code

        try {
            String uploadId = UUID.randomUUID().toString();
            uploadReceiver.setDelegate(this);
            uploadReceiver.setUploadID(uploadId);

            UploadNotificationConfig ntf = new UploadNotificationConfig();
            ntf.setTitle("Upload ảnh");

            ntf.setCompletedMessage("Tải lên ảnh thành công");
            ntf.setErrorMessage("Tải lên ảnh thất bại, vui lòng kiểm tra lại mạng");
            //Creating a multi part request
            String ul = new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("name", name) //Adding text parameter to the request
                    .setNotificationConfig(ntf)
                    .setMaxRetries(0)
                    .startUpload(); //Starting the upload


        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //  handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                String name = getPath(filePath).substring(getPath(filePath).lastIndexOf("/") + 1);
                String name1 = getRealPathFromURI(filePath).substring(getPath(filePath).lastIndexOf("/") + 1);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                img_bg.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//
private String getRealPathFromURI(Uri contentURI) {
    String result;
    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
    if (cursor == null) { // Source is Dropbox or other similar local file path
        result = contentURI.getPath();
    } else {
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        result = cursor.getString(idx);
        cursor.close();
    }
    return result;
}
    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

//        cursor = getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//        String path=null;
//        try {
//        cursor.moveToFirst();
//         path= cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//        cursor.close();
//        }
//        catch(Exception e) {
//            Log.e("Path Error", e.toString());
//        }
//        return path;
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        cursor = getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {

    }

    @Override
    public void onError(Exception exception) {

    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {

        try {

            JSONObject response = new JSONObject(new String(serverResponseBody, "UTF-8"));
            String link = response.getString("url");
          setLink_image(link);
            callAddVolley(items);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String getLink_image() {
        return link_image;
    }

    public void setLink_image(String link_image) {
        this.link_image = link_image;
    }

    @Override
    public void onCancelled() {
    }
}
