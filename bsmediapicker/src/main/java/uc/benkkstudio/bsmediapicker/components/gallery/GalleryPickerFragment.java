package uc.benkkstudio.bsmediapicker.components.gallery;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import uc.benkkstudio.bsmediapicker.commons.models.Session;
import uc.benkkstudio.bsmediapicker.commons.modules.LoadMoreModule;
import uc.benkkstudio.bsmediapicker.commons.modules.LoadMoreModuleDelegate;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.material.appbar.AppBarLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uc.benkkstudio.bsmediapicker.MediaConstant;
import uc.benkkstudio.bsmediapicker.R;

/*
 * Created by Guillaume on 17/11/2016.
 */

public class GalleryPickerFragment extends Fragment implements GridAdapterListener, LoadMoreModuleDelegate {
    RecyclerView mGalleryRecyclerView;
    ImageView mPreview;
    SimpleExoPlayerView mPlayer;
    AppBarLayout mAppBarContainer;

    public static SimpleExoPlayer player;
    private static final String EXTENSION_JPG = ".jpg";
    private static final String EXTENSION_JPEG = ".jpeg";
    private static final String EXTENSION_PNG = ".png";
    private static final String EXTENSION_MP4 = ".mp4";
    private static final int PREVIEW_SIZE = 800;
    private static final int MARGING_GRID = 2;
    private static final int RANGE = 20;

    private Session mSession = Session.getInstance();
    private LoadMoreModule mLoadMoreModule = new LoadMoreModule();
    private GridAdapter mGridAdapter;
    private ArrayList<File> mFiles;
    private boolean isLoading = false;
    private int mOffset;
    private boolean isFirstLoad = true;

    public static GalleryPickerFragment newInstance() {
        return new GalleryPickerFragment();
    }

    private void initViews() {
        if (isFirstLoad) {
            mGridAdapter = new GridAdapter(getContext());
        }
        mGridAdapter.setListener(this);
        mGalleryRecyclerView.setAdapter(mGridAdapter);
        mGalleryRecyclerView.setHasFixedSize(true);
        mGalleryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mGalleryRecyclerView.addItemDecoration(addItemDecoration());
        mLoadMoreModule.LoadMoreUtils(mGalleryRecyclerView, this, getContext());
        mOffset = 0;
        fetchMedia();
    }

    private RecyclerView.ItemDecoration addItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view,
                                       RecyclerView parent, RecyclerView.State state) {
                outRect.left = MARGING_GRID;
                outRect.right = MARGING_GRID;
                outRect.bottom = MARGING_GRID;
                if (parent.getChildLayoutPosition(view) >= 0 && parent.getChildLayoutPosition(view) <= 3) {
                    outRect.top = MARGING_GRID;
                }
            }
        };
    }

    private void fetchMedia() {
        mFiles = new ArrayList<>();
        loadAllImageAndVideo();
        if (mFiles.size() > 0) {
            displayPreview(mFiles.get(0));
            mGridAdapter.setItems(getRangePets());
        }
        isFirstLoad = false;
    }

    private List<File> getRangePets() {
        return mFiles;
    }


    private void loadAllImageAndVideo() {
// Get relevant columns for use later.
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };
        String selection;
        if(MediaConstant.IMAGE_AND_VIDEO){
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        } else {
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        }

        Uri queryUri = MediaStore.Files.getContentUri("external");
        CursorLoader cursorLoader = new CursorLoader(
                getActivity(),
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );

        Cursor cursor = cursorLoader.loadInBackground();
        int data = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        while (cursor.moveToNext()) {
            mFiles.add(new File(cursor.getString(data)));
        }

    }


    private void displayPreview(File file) {
        Glide.with(Objects.requireNonNull(getContext()))
                .load(Uri.fromFile(file))
                .centerCrop()
                .into(mPreview);
        mPreview.setVisibility(file.getPath().contains("mp4") ? View.GONE : View.VISIBLE);
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            mPlayer.setPlayer(null);
            player = null;
        }
        if (file.getPath().contains("mp4")) {
            initExoPlayer(file);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initExoPlayer(File file) {
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
        mPlayer.setPlayer(player);
        MediaSource mediaSource = mediaSource(Uri.parse(file.getAbsolutePath()));
        player.prepare(mediaSource, true, false);
        player.setPlayWhenReady(true);
    }

    private MediaSource mediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(getActivity(), "Exoplayer-local")).
                createMediaSource(uri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gallery_picker_view, container, false);
        mGalleryRecyclerView = v.findViewById(R.id.mGalleryRecyclerView);
        mPreview = v.findViewById(R.id.mPreview);
        mPlayer = v.findViewById(R.id.mPlayer);
        mAppBarContainer = v.findViewById(R.id.mAppBarContainer);
        initViews();
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClickMediaItem(File file) {
        displayPreview(file);
        mSession.setFileToUpload(file);
        mAppBarContainer.setExpanded(true, true);
    }

    @Override
    public void shouldLoadMore() {
    }
}
