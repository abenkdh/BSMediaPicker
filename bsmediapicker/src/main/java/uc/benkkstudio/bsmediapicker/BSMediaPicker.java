package uc.benkkstudio.bsmediapicker;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import uc.benkkstudio.bsmediapicker.commons.adapters.ViewPagerAdapter;
import uc.benkkstudio.bsmediapicker.commons.models.Session;
import uc.benkkstudio.bsmediapicker.commons.models.enums.SourceType;
import uc.benkkstudio.bsmediapicker.commons.modules.PermissionModule;
import uc.benkkstudio.bsmediapicker.commons.ui.ToolbarView;
import uc.benkkstudio.bsmediapicker.utils.Constant;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import uc.benkkstudio.bsmediapicker.components.editor.EditorActivity;
import uc.benkkstudio.bsmediapicker.components.gallery.GalleryPickerFragment;

public class BSMediaPicker extends AppCompatActivity implements ToolbarView.OnClickTitleListener, ToolbarView.OnClickNextListener, ToolbarView.OnClickBackListener {
    TabLayout mMainTabLayout;
    ViewPager mMainViewPager;
    ToolbarView mToolbar;

    String _tabGallery;
    String _tabPhoto;
    String _tabVideo;
    private Session mSession = Session.getInstance();
    private HashSet<SourceType> mSourceTypeSet = new HashSet<>();
    public static final String MEDIA_RESULT = "MEDIA_RESULT";
    public static final String MEDIA_TYPE = "MEDIA_TYPE";
    public static void initalize(int video_upload_duration){
        Constant.video_upload_duration = video_upload_duration;
    }

    private void initViews() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();
        PermissionModule permissionModule = new PermissionModule(this);
        permissionModule.checkPermissions();

        mToolbar.setOnClickBackMenuListener(this)
                .setOnClickTitleListener(this)
                .setOnClickNextListener(this);

        final ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getListFragment());
        mMainViewPager.setAdapter(pagerAdapter);

        mMainTabLayout.addOnTabSelectedListener(getViewPagerOnTabSelectedListener());
        mMainViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mMainTabLayout));

        mMainViewPager.setCurrentItem(0);
        Constant.notifyMediaStore(this, Environment.getExternalStorageDirectory().getPath());
    }

    private TabLayout.ViewPagerOnTabSelectedListener getViewPagerOnTabSelectedListener() {
        return new TabLayout.ViewPagerOnTabSelectedListener(mMainViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                displayTitleByTab(tab);
                initNextButtonByTab(tab.getPosition());
            }
        };
    }

    private void displayTitleByTab(TabLayout.Tab tab) {
        if (tab.getText() != null) {
            String title = tab.getText().toString();
            mToolbar.setTitle(title);
        }
    }

    private void initNextButtonByTab(int position) {
        switch (position) {
            case 0:
                mToolbar.showNext();
                break;
            case 1:
                mToolbar.hideNext();
                break;
            case 2:
                mToolbar.hideNext();
                break;
            default:
                mToolbar.hideNext();
                break;
        }
    }

    private ArrayList<Fragment> getListFragment() {
        ArrayList<Fragment> fragments = new ArrayList<>();

        if (mSourceTypeSet.contains(SourceType.Gallery)) {
            fragments.add(GalleryPickerFragment.newInstance());
            mMainTabLayout.addTab(mMainTabLayout.newTab().setText(_tabGallery));
        }

        return fragments;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        mMainTabLayout = findViewById(R.id.mMainTabLayout);
        mMainTabLayout.setVisibility(View.GONE);
        mMainViewPager = findViewById(R.id.mMainViewPager);
        mToolbar = findViewById(R.id.mToolbar);
        _tabGallery = getString(R.string.tab_gallery);
        _tabPhoto = getString(R.string.tab_photo);
        _tabVideo = getString(R.string.tab_video);
        // If you want to start activity with custom Tab
        mSourceTypeSet.add(SourceType.Gallery);

        initViews();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClickBack() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    public void onClickNext() {
        // Fetch file to upload
        mSession.getFileToUpload();
        try {
            if(mSession.getFileToUpload().getPath().contains("mp4")){
                if(GalleryPickerFragment.player != null){
                    GalleryPickerFragment.player.setPlayWhenReady(false);
                }
                Constant.trimUrl = mSession.getFileToUpload().getPath();
                startActivityForResult(new Intent(BSMediaPicker.this, EditorActivity.class), 22346);
            } else {
                Intent result = new Intent();
                result.putExtra(MEDIA_RESULT, mSession.getFileToUpload().getPath());
                result.putExtra(MEDIA_TYPE, MediaType.IMAGE);
                setResult(RESULT_OK, result);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 22346){
            if(resultCode == RESULT_OK){
                Intent result = new Intent();
                result.putExtra(MEDIA_RESULT, data.getStringExtra("uri"));
                result.putExtra(MEDIA_TYPE, MediaType.VIDEO);
                setResult(RESULT_OK, result);
                finish();
            }
        }
    }

    @Override
    public void onClickTitle() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED, new Intent());
    }

    public static class Config{
        private boolean config;

        public Config() {
        }

        public BSMediaPicker.Config enableImage(boolean config) {
            MediaConstant.IMAGE_AND_VIDEO = config;
            return this;
        }
    }
}
