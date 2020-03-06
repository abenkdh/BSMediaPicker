package uc.benkkstudio.bsmediapickerexample;

import androidx.appcompat.app.AppCompatActivity;
import uc.benkkstudio.bsmediapicker.BSMediaPicker;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new BSMediaPicker.Config().enableImage(false);
    }
}
