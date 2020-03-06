package uc.benkkstudio.bsmediapicker.commons.modules;

import android.content.Context;

import com.bumptech.glide.Glide;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class LoadMoreModule {

    private RecyclerView recyclerView;
    private LoadMoreModuleDelegate delegate;

    private static final Object TAG = new Object();
    private static final int SETTLING_DELAY = 300;

    private Glide glide = null;
    private Runnable mSettlingResumeRunnable = null;

    public void LoadMoreUtils(RecyclerView r, LoadMoreModuleDelegate d, Context context) {
        this.recyclerView = r;
        this.delegate = d;

        if (glide == null) {
            glide = Glide.get(context.getApplicationContext());
        }

        addListener();
    }

    private void addListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (llm.findFirstVisibleItemPosition() >= (llm.getItemCount() / 5)) {
                    delegate.shouldLoadMore();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerView.removeCallbacks(mSettlingResumeRunnable);

                } else if (scrollState == RecyclerView.SCROLL_STATE_SETTLING) {
                    recyclerView.postDelayed(mSettlingResumeRunnable, SETTLING_DELAY);
                }
            }
        });
    }

}
