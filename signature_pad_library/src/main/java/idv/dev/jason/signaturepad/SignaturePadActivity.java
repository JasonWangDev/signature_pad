package idv.dev.jason.signaturepad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jason on 2017/11/14.
 */

public class SignaturePadActivity extends AppCompatActivity
                                  implements SignaturePad.OnSignedListener,
                                             View.OnClickListener {

    public static final int REQUEST_CODE = 0x01;

    public static final int RESULT_CODE = 0x01;

    public static final String KEY_FILE_PATH = "KEY_FILE_PATH";

    private boolean isSigned;

    private SignaturePad signaturePad;

    private Button btnCancel;
    private Button btnClear;
    private Button btnSubmit;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signature_pad);

        signaturePad = findViewById(R.id.signature_pad);

        btnCancel = findViewById(R.id.btn_cancel);
        btnClear = findViewById(R.id.btn_clear);
        btnSubmit = findViewById(R.id.btn_submit);

        setSignatureImageFormIntentExtra();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerListener();
    }

    @Override
    protected void onPause() {
        unregisterListener();

        super.onPause();
    }


    @Override
    public void onStartSigning() { }

    @Override
    public void onSigned() {
        isSigned = true;
    }

    @Override
    public void onClear() {
        isSigned = false;
    }


    @Override
    public void onClick(View view) {
        if (R.id.btn_cancel == view.getId())
            finish();
        else if (R.id.btn_clear == view.getId())
            signaturePad.clear();
        else if (R.id.btn_submit == view.getId())
        {
            if (isSigned)
            {
                Bitmap bitmap = signaturePad.getTransparentSignatureBitmap();
                File file = bitmapToFile(bitmap);
                if (null != file)
                {
                    Intent intent = new Intent();
                    intent.putExtra(KEY_FILE_PATH, file.getAbsolutePath());

                    setResult(RESULT_CODE, intent);

                    finish();
                }
            }
        }
    }


    private void registerListener() {
        signaturePad.setOnSignedListener(this);

        btnCancel.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void unregisterListener() {
        signaturePad.setOnSignedListener(null);

        btnCancel.setOnClickListener(null);
        btnClear.setOnClickListener(null);
        btnSubmit.setOnClickListener(null);
    }


    private void setSignatureImageFormIntentExtra() {
        String filePath = getIntent().getStringExtra(KEY_FILE_PATH);
        if (null != filePath)
        {
            Bitmap bitmap = filePathToBitmap(filePath);
            if (null != bitmap)
                signaturePad.setSignatureBitmap(bitmap);
        }
    }

    private Bitmap filePathToBitmap(String path) {
        if (null == path)
            return null;

        File file = new File(path);
        if (null == file || !file.exists())
            return null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    private File bitmapToFile(Bitmap bitmap) {
        if (null == bitmap)
            return null;

        File file = new File(getCacheDir(), "temp.png");
        try
        {
            file.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);

            byte[] bitmapArr = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapArr);
            fos.flush();
            fos.close();

            return file;
        }
        catch (IOException e)
        {
            return null;
        }
    }

}
