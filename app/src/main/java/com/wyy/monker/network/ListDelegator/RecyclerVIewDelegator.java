package com.wyy.monker.network.ListDelegator;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wyy.monker.R;
import com.wyy.monker.base.adapter.recycleView.BaseRecycerAdapter;
import com.wyy.monker.base.adapter.recycleView.DividerItemDecoration;
import com.wyy.monker.network.bean.BaseM;
import com.wyy.monker.network.bean.BaseRefreshData;
import com.wyy.monker.network.common.Command;
import com.wyy.monker.network.observer.BaseRefreshListObserver;
import com.wyy.monker.utils.LogUtil;
import com.wyy.monker.utils.ScreenUtil;
import com.wyy.monker.utils.SizeUtil;

import java.util.List;




public class RecyclerVIewDelegator<T extends BaseM> {
    private RecyclerView mListView;
    private SwipeRefreshLayout mRefreshLayout;
    private LoadingFooter mFooter;
    private Context mContext;

    private View mEmptyView;
    private View mErrorView;
    private ViewGroup mParentViewGroup;
    private View mCurrentVisiableView;

    private BaseRecycerAdapter<T> mAdapter;


    // for emtpy view or error view
    private int mEmptyListPicResId = -1;
    private int mEmptyListButtonTitleStringResId = -1;
    private View.OnClickListener mEmptyListButtonClickListener;
    private View.OnClickListener mErrorListButtonClickListener;


    //paging control
    private int mPage = 1;
    private int mPageSize = 10;

    //最后一个的位置
    private int[] lastPositions;
    //最后一个可见的item的位置
    private int lastVisibleItemPosition;

    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }

    private LAYOUT_MANAGER_TYPE layoutManagerType;


    private int maxItemNumber = Integer.MAX_VALUE;//设置最大的item数量限制
    private Command<T> mCommand;

    private Class<T> mClazz;


    public RecyclerVIewDelegator(RecyclerView listView, SwipeRefreshLayout refreshLayout, Context context, Command<T> loadCommand, BaseRecycerAdapter<T> adapter,Class<T> clazz) {
        this(listView, refreshLayout, context, loadCommand, adapter, LayoutInflater.from(context).inflate(R.layout.empty_list_view, null), LayoutInflater.from(context).inflate(R.layout.error_list_loading, null),clazz);
    }

    public RecyclerVIewDelegator(RecyclerView listView, SwipeRefreshLayout refreshLayout, Context context, Command<T> loadCommand, BaseRecycerAdapter<T> adapter, View emptyView, View errorView, Class clazz) {
        mListView = listView;
        mParentViewGroup = (ViewGroup) mListView.getParent();
        mCurrentVisiableView = mListView;
        mClazz=clazz;
        mRefreshLayout = refreshLayout;
        mContext = context;
        mFooter = new LoadingFooter(mContext);
        mCommand = loadCommand;
        mEmptyView = emptyView;
        mErrorView = errorView;
        mAdapter = adapter;
        setupListView();
        setupRefreshLayout();
        setupEmptyView();
        setupErrorView();
    }

    //设置每页的数量
    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
    }

    public void setmCommand(Command<T> mCommand) {
        this.mCommand = mCommand;
    }

    public void beginHeaderRefreshing() {
        //进入页面初次手动开启加载请求
        beginRefreshAnimation();
        loadFirstPage();
    }

    public void beginFooterRefreshing() {
        loadNextPage();
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
        mRefreshLayout.setColorSchemeColors(mContext.getResources().getColor(R.color.colorPrimary));
    }

    //設置divider的宽度
    public void setDividerHeight(int height) {

        mListView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST, height, mAdapter.isAddHead(), mAdapter.isAddFoot()));
    }

    private void setupListView() {
        mListView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        //设置Item增加、移除动画
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setAdapter(mAdapter);
        //上拉刷新
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int state) {
                super.onScrollStateChanged(recyclerView, state);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                //Logger.e("列表滚动数据  " + " state " + state + "visibleItemCount  " + visibleItemCount + "  lastVisibleItemPosition  " + lastVisibleItemPosition + "  totalItemCount  " + totalItemCount);//5
                if (visibleItemCount > 0 && state == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1) {
                    if (mAdapter.getCount() >= mPageSize)
                        beginFooterRefreshing();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //上拉为正  下拉为负
                //Logger.e("onScrolled   " + dx + "   " + dy + "  appStatus  " + appStatus);
                RecyclerView.LayoutManager layoutManager = getLayoutManager();
                if (layoutManagerType == null) {
                    if (layoutManager instanceof LinearLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
                    } else {
                        throw new RuntimeException(
                                "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
                    }
                }

                switch (layoutManagerType) {
                    case LINEAR:
                        lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        break;
                    case GRID:
                        lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                        break;
                    case STAGGERED_GRID:
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                        if (lastPositions == null) {
                            lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                        }
                        staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                        lastVisibleItemPosition = findMax(lastPositions);
                        break;
                }
            }
        });
        mAdapter.addFootView(mFooter.getView());

        mAdapter.setOnItemClickListener(new BaseRecycerAdapter.OnItemClick() {
            @Override
            public void onClick(int position) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(mAdapter.getDataList().get(position));
                }
            }
        });
    }

    //寻找最大值
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 设置排序方式
     */
    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        mListView.setLayoutManager(layout);
    }


    /**
     * 获取排序管理者
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return mListView.getLayoutManager();
    }

    private void setupEmptyView() {
        //图片
        ImageView iv_empty = (ImageView) mEmptyView.findViewById(R.id.iv_empty);
        if (mEmptyListPicResId != -1) {
            iv_empty.setImageResource(mEmptyListPicResId);
        }
        //按钮
        Button button = (Button) mEmptyView.findViewById( R.id.btn_empty);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beginHeaderRefreshing();
                }
            });
        }
        //空视图
        RelativeLayout relativeLayout = (RelativeLayout) mEmptyView.findViewById(R.id.empty_listview);
        if (relativeLayout == null) {
            return;
        }
        NestedScrollView.LayoutParams layoutParams = (NestedScrollView.LayoutParams) relativeLayout.getLayoutParams();
        int height = ScreenUtil.getScreenHeight() - SizeUtil.dp2px(64);
        //Logger.e("高度" + height + "   高度/3   " + (height / 3));
        //Logger.e("dp  " + WindowsUtils.px2dip((height / 3)));
        layoutParams.setMargins(0, (height / 3), 0, 0);
    }

    private void setupErrorView() {
        //按鈕
        Button id_err_list_refresh_btn = (Button) mErrorView.findViewById(R.id.id_err_list_refresh_btn);
        id_err_list_refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginHeaderRefreshing();
            }
        });
        //错误视图
        RelativeLayout relativeLayout = (RelativeLayout) mErrorView.findViewById( R.id.error_listview);
        NestedScrollView.LayoutParams layoutParams = (NestedScrollView.LayoutParams) relativeLayout.getLayoutParams();
        int height = ScreenUtil.getScreenHeight() - SizeUtil.dp2px(64);
        //Logger.e("高度" + height + "   高度/3   " + (height / 3));
        //Logger.e("dp  " + WindowsUtils.px2dip((height / 3)));
        layoutParams.setMargins(0, (height / 3), 0, 0);
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
        mPage = 1;
//        getEntityClass();
        mCommand.execute(mPage, mPageSize, new BaseRefreshListObserver<T>(mContext,mClazz) {
            @Override
            public void onHandleSuccess(BaseRefreshData t) {
                Log.d("tag", t.toString());
                List dataList = t.getList();
                mAdapter.setDataToAdapter(dataList);
                mPage++;
                //如果size小于pageSize则说明没有更多了
                if (dataList.size() <= mPageSize) {
                    mFooter.setState(LoadingFooter.State.TheEnd);
                } else {
                    mFooter.setState(LoadingFooter.State.Idle);
                }
                endRefreshAnimation();
                if (mAdapter.getCount() == 0) {
                    switchToEmtpyView();
                } else {
                    switchToListView();
                }

                if (onFirstLoad != null) {
                    onFirstLoad.onSucess(t.getPage().getTotal_count());
                }
            }

            @Override
            public void onError(Throwable e) {
                mFooter.setState(LoadingFooter.State.Idle);
                switchToEmtpyView();
            }
        });
    }


    //载入更多
    private void loadNextPage() {
        LogUtil.i("loadNextPage");
        mFooter.setState(LoadingFooter.State.Loading);
        mCommand.execute(mPage, mPageSize, new BaseRefreshListObserver<T>(mContext, mClazz) {
            @Override
            public void onHandleSuccess(BaseRefreshData t) {
                Log.d("tag", t.toString());
                if (mAdapter.getCount() > maxItemNumber) {
                    return;
                }
                List dataList = t.getList();
                if (dataList.size() != 0) {
                    mPage++;
                    mAdapter.addData(dataList);
                }
                //如果size小于pageSize则说明没有更多了
                if (dataList.size() < mPageSize) {
                    mFooter.setState(LoadingFooter.State.TheEnd);
                } else {
                    mFooter.setState(LoadingFooter.State.Idle);
                }
                if (mAdapter.getCount() == 0) {
                    switchToEmtpyView();
                } else {
                    switchToListView();
                }
            }

            @Override
            public void onError(Throwable e) {
                mFooter.setState(LoadingFooter.State.Idle);
                switchToEmtpyView();
            }
        });
    }


    public void setEmptyListPicResId(int mEmptyListPicResId) {
        this.mEmptyListPicResId = mEmptyListPicResId;
        setupEmptyView();
    }


    public void setCommand(Command<T> command) {
        mCommand = command;
    }

    public interface OnItemClickListener<T> {
        void OnItemClick(T item);
    }

    public interface OnFirstLoad {
        void onSucess(int total);
    }

    //点击事件
    private OnItemClickListener<T> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //完成事件
    private OnFirstLoad onFirstLoad;

    public void setOnFirstLoad(OnFirstLoad onFirstLoad) {
        this.onFirstLoad = onFirstLoad;
    }

    //转换成ListView
    private void switchToListView() {
        switchToView(mListView);
    }

    //转换成EmtpyVIew
    private void switchToEmtpyView() {
        switchToView(mEmptyView);
    }

    //转换成ErrorView
    private void switchToErrorView() {
        switchToView(mErrorView);
    }

    //转换View视图
    private void switchToView(View toView) {
        if (mCurrentVisiableView == toView) {
            return;
        }
        int index = mParentViewGroup.indexOfChild(mCurrentVisiableView);
        mParentViewGroup.removeView(mCurrentVisiableView);
        mParentViewGroup.addView(toView, index, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //mParentViewGroup.addView(toView, index);
        toView.setVisibility(View.VISIBLE);
        mCurrentVisiableView.setVisibility(View.VISIBLE);
        mCurrentVisiableView = toView;
    }


}
