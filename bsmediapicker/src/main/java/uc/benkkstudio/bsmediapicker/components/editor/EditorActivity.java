package uc.benkkstudio.bsmediapicker.components.editor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import uc.benkkstudio.bsmediapicker.utils.Constant;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;
import uc.benkkstudio.bsmediapicker.R;

public class EditorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        K4LVideoTrimmer videoTrimmer = findViewById(R.id.timeLine);
        videoTrimmer.setVideoURI(Uri.parse(Constant.trimUrl));
        videoTrimmer.setDestinationPath(getExternalCacheDir().getAbsolutePath() + File.separator);
        videoTrimmer.setMaxDuration(Constant.video_upload_duration);
        videoTrimmer.setOnTrimVideoListener(new OnTrimVideoListener() {
            @Override
            public void getResult(Uri uri) {
                Intent result = new Intent();
                result.putExtra("uri", uri.toString());
                setResult(RESULT_OK, result);
                finish();
            }

            @Override
            public void cancelAction() {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED, new Intent());
    }
}
