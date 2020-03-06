package uc.benkkstudio.bsmediapicker.components.gallery;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import uc.benkkstudio.bsmediapicker.commons.modules.ReboundModule;
import uc.benkkstudio.bsmediapicker.commons.modules.ReboundModuleDelegate;
import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.ref.WeakReference;

import uc.benkkstudio.bsmediapicker.R;

public class MediaItemView extends RelativeLayout implements ReboundModuleDelegate {
    ImageView mMediaThumb, image_play;
    private Context context;
    private File mCurrentFile;
    private ReboundModule mReboundModule = ReboundModule.getInstance(this);
    private WeakReference<MediaItemViewListener> mWrListener;

    void setListener(MediaItemViewListener listener) {
        this.mWrListener = new WeakReference<>(listener);
    }

    public MediaItemView(Context context) {
        super(context);
        this.context = context;
        View v = View.inflate(context, R.layout.media_item_view, this);
        mMediaThumb = v.findViewById(R.id.mMediaThumb);
        image_play = v.findViewById(R.id.image_play);
    }

    public void bind(File file) {
        mCurrentFile = file;
        mReboundModule.init(mMediaThumb);
        image_play.setVisibility(file.getAbsoluteFile().toString().contains("mp4") ? VISIBLE : GONE);
            Glide.with(getContext())
                    .asBitmap()
                    .load(Uri.fromFile(file))
                    .placeholder(R.drawable.placeholder_media)
                    .error(R.drawable.placeholder_error_media)
                    .into(mMediaThumb);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public void onTouchActionUp() {
        mWrListener.get().onClickItem(mCurrentFile);
    }
}
