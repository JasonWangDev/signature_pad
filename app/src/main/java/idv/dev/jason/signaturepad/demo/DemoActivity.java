package idv.dev.jason.signaturepad.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import idv.dev.jason.signaturepad.SignaturePadActivity;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {

    private File file;

    private Button btn;
    private ImageView iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);

        btn = findViewById(R.id.btn);
        iv = findViewById(R.id.iv);

        btn.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("TAG", "requestCode " + requestCode);
        Log.d("TAG", "resultCode " + resultCode);
        if (data != null)
        {
            Log.d("TAG", "data " + data.toString());
            file = new File(data.getStringExtra("KEY_FILE_PATH"));
            Log.d("TAG", file.toString());

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            iv.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, SignaturePadActivity.class);
        if (null != file && file.exists())
            intent.putExtra("KEY_FILE_PATH", file.getAbsolutePath());

        startActivityForResult(intent, 0x01);
    }

}
