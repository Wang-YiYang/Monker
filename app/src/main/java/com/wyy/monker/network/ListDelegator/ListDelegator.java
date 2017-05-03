package com.wyy.monker.network.ListDelegator;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.wyy.monker.R;
import com.wyy.monker.base.adapter.listview.DMListAdapter;
import com.wyy.monker.network.bean.BaseRefreshData;
import com.wyy.monker.network.common.Command;
import com.wyy.monker.network.observer.BaseRefreshListObserver;
import com.wyy.monker.utils.LogUtil;

import java.util.List;

/**
 * Created by tonychen on 15/10/19.
 */
public class ListDelegator<T> {
    private ListView mListView;
    private SwipeRefreshLayout mRefreshLayout;
    private LoadingFooter mFooter;
    private Activity mActivity;

    private View mEmptyView;
    private View mErrorView;
    private ViewGroup mParentViewGroup;
    private View mCurrentVisiableView;

    private DMListAdapter<T> mAdapter;


    // for emtpy view or error view
    private int mEmptyListImgResId = -1;
    private View.OnClickListener mErrorListButtonClickListener;


    //paging control
    private int mPage = 1;
    private int mPageSize = 10;


    private Command<T> mCommand;
    private Class<T> mClazz;


    public ListDelegator(ListView listView, SwipeRefreshLayout refreshLayout, Activity activity, Command<T> loadCommand, DMListAdapter<T> adapter,Class<T> clazz) {
        this(listView, refreshLayout, activity, loadCommand, adapter, activity.getLayoutInflater().inflate(R.layout.empty_list_view, null), activity.getLayoutInflater().inflate(R.layout.error_list_loading, null),clazz);
    }

    public ListDelegator(ListView listView, SwipeRefreshLayout refreshLayout, Activity activity, Command<T> loadCommand, DMListAdapter<T> adapter, View emptyView, View errorView, Class<T> clazz) {
        mListView = listView;
        mClazz=clazz;
        mParentViewGroup = (ViewGroup) mListView.getParent();
        mCurrentVisiableView = mListView;
        mRefreshLayout = refreshLayout;
        mActivity = activity;
        mFooter = new LoadingFooter(mActivity);
        mCommand = loadCommand;
        mEmptyView = emptyView;
        mErrorView = errorView;
        mAdapter = adapter;
        setupListView();
        setupRefreshLayout();
        setupEmptyView();
        setupErrorView();
    }


    public void beginHeaderRefreshing() {
        //进入页面初次手动开启加载请求
        beginRefreshAnimation();
        loadFirstPage();
    }

    public void beginFooterRefreshing() {
        loadNextPage();
    }

    public void switchToListview() {
        switchToView(mListView);
    }

    private void setupRefreshLayout() {
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新的请求
                loadFirstPage();
            }
        });
        //下拉刷新控件的配色
        mRefreshLayout.setColorSchemeColors(mActivity.getResources().getColor(R.color.colorPrimary));
    }

    private void setupListView() {

        //AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(mAdapter);
        //animationAdapter.setAbsListView(mListView);
        mListView.setAdapter(mAdapter);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //如果当前正在加载或是已经没有更多,则不继续请求
                boolean enable = false;
                if (mListView != null && mListView.getChildCount() > mListView.getFooterViewsCount() + mListView.getHeaderViewsCount()) {
                    boolean firstItemVisible = mListView.getFirstVisiblePosition() == 0;
                    if (firstItemVisible) {
                        View v = mListView.getChildAt(0);
                        int offset = (v == null) ? view.getPaddingTop() : v.getTop();
                        LogUtil.i("offset:" + offset + "  paddingtop:" + view.getPaddingTop());
                        if (offset == view.getPaddingTop()) {
                            enable = true;
                        }
                    }
                }
                LogUtil.i("List Delegator refreshlayout enabled = " + enable);
                if (mAdapter.getData().size() == 0) {
                    enable = true;
                }
                mRefreshLayout.setEnabled(enable);
                if (mFooter.getState() == LoadingFooter.State.Loading || mFooter.getState() == LoadingFooter.State.TheEnd) {
                    return;
                }

                //这里判断是否滑到了最低端
                if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount != 0 && mAdapter.getCount() > 0) {
                    loadNextPage();
                }
            }

        });
        mListView.addFooterView(mFooter.getView());
    }


    private void setupEmptyView() {
        Button button = (Button) mEmptyView.findViewById(R.id.btn_empty);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginHeaderRefreshing();
            }
        });

        ImageView iv_empty = (ImageView) mEmptyView.findViewById( R.id.iv_empty);
        if (mEmptyListImgResId != -1) {
            iv_empty.setImageResource(mEmptyListImgResId);
        }
    }

    private void setupErrorView() {
        Button button = (Button) mErrorView.findViewById(R.id.id_err_list_refresh_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginHeaderRefreshing();
            }
        });

    }

    private void switchToView(View toView) {
        if (mCurrentVisiableView == toView) {
            return;
        }
        int index = mParentViewGroup.indexOfChild(mCurrentVisiableView);
        mParentViewGroup.removeView(mCurrentVisiableView);
        mParentViewGroup.addView(toView, index, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        toView.setVisibility(View.VISIBLE);
        mCurrentVisiableView = toView;
    }

    private void beginRefreshAnimation() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
    }

    private void endRefreshAnimation() {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);

            }
        }, 1000);
    }


    //加载第一页,下拉刷新
    private void loadFirstPage() {
        LogUtil.i("loadFirstPage");
        mPage = 1;
        mCommand.execute(mPage, mPageSize, new BaseRefreshListObserver<T>(mActivity, mClazz) {
            @Override
            public void onHandleSuccess(BaseRefreshData response) {
                //Logger.e(response.getDataList().toString());
                List<T> dataList = response.getList();
                mAdapter.setData(dataList);
                mPage++;
                //如果size小于pageSize则说明没有更多了
                if (dataList.size() < mPageSize) {
                    mFooter.setState(LoadingFooter.State.TheEnd);
                } else {
                    mFooter.setState(LoadingFooter.State.Idle);
                }
                endRefreshAnimation();
            }

            @Override
            public void onError(Throwable e) {
                mFooter.setState(LoadingFooter.State.Idle);
                endRefreshAnimation();
            }
        });
    }

    //载入更多
    private void loadNextPage() {
        LogUtil.i("loadNextPage");
        mFooter.setState(LoadingFooter.State.Loading);

        mCommand.execute(mPage, mPageSize, new BaseRefreshListObserver<T>(mActivity, mClazz) {
            @Override
            public void onHandleSuccess(BaseRefreshData response) {
                List<T> dataList = response.getList();
                mAdapter.addData(dataList);
                mPage++;
                //如果size小于pageSize则说明没有更多了
                if (dataList.size() < mPageSize) {
                    mFooter.setState(LoadingFooter.State.TheEnd);
                } else {
                    mFooter.setState(LoadingFooter.State.Idle);
                }
            }

            @Override
            public void onError(Throwable e) {
                mFooter.setState(LoadingFooter.State.Idle);
            }
        });
    }

    /**
     * 设置空视图  的 空图像
     *
     * @param mEmptyListImgResId
     */
    public void setEmtypyImgResId(int mEmptyListImgResId) {
        this.mEmptyListImgResId = mEmptyListImgResId;
        setupEmptyView();
    }


    public void setCommand(Command<T> command) {
        mCommand = command;
    }
}
