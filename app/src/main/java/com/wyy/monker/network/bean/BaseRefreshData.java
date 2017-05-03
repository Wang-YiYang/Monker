package com.wyy.monker.network.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/27.
 */

public class BaseRefreshData<T> extends BaseM{


    /**
     * list : [{"activityImg":"https://www.dreammove.cn/Uploads/Picture/2017-04-26/59002884f2e42.jpg","detailUrl":"https://www.dreammove.cn/bm/bm/id/HD1493179639846664/#/HD1493179639846664","endTime":1493178840,"id":"HD1493179639846664","readCount":0,"startTime":1493092440,"status":2,"statusName":"已结束","title":"还在纠结怎么跟90后相处？90后CEO在纠结要不要招\u201c老年人\u201d\u2026\u2026","uid":0,"userCount":0},{"activityImg":"https://www.dreammove.cn/Uploads/Picture/2017-04-26/5900294aaf6f5.jpg","detailUrl":"https://www.dreammove.cn/bm/bm/id/HD1493180211179514/#/HD1493180211179514","endTime":1491019920,"id":"HD1493180211179514","readCount":0,"startTime":1490933520,"status":2,"statusName":"已结束","title":"《新媒体运营专场：彻底更新新媒体思维》","uid":0,"userCount":0},{"activityImg":"https://www.dreammove.cn/Uploads/Picture/2017-03-08/58bf804bc3964.jpg","detailUrl":"https://www.dreammove.cn/bm/bm/id/HD1488945280549043/#/HD1488945280549043","endTime":1489068000,"id":"HD1488945280549043","readCount":5,"startTime":1488945240,"status":2,"statusName":"已结束","title":"如何利用创投大数据辅助股权投资","uid":0,"userCount":3},{"activityImg":"https://www.dreammove.cn/Uploads/Picture/2016-11-23/5835469304212.jpg","detailUrl":"https://www.dreammove.cn/bm/bm/id/HD1479888501322043/#/HD1479888501322043","endTime":1480867140,"id":"HD1479888501322043","readCount":348,"startTime":1480831200,"status":2,"statusName":"已结束","title":"聚募投资人探营活动第一站 威代环境科技公司","uid":0,"userCount":7}]
     * page : {"current_page":1,"per_page":10,"total_count":4,"total_page":1}
     */

    private PageBean page;
    private List<T> list;

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public static class PageBean {
        /**
         * current_page : 1
         * per_page : 10
         * total_count : 4
         * total_page : 1
         */

        private int current_page;
        private int per_page;
        private int total_count;
        private int total_page;

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public int getPer_page() {
            return per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }

        public int getTotal_page() {
            return total_page;
        }

        public void setTotal_page(int total_page) {
            this.total_page = total_page;
        }
    }

}
