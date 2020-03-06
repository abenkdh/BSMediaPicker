package uc.benkkstudio.bsmediapicker.components.video;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import uc.benkkstudio.bsmediapicker.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CaptureVideoFragment extends Fragment {

    public static CaptureVideoFragment newInstance() {
        return new CaptureVideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.capture_video_view, container, false);
    }

}
