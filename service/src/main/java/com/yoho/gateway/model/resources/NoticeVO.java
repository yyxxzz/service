package com.yoho.gateway.model.resources;

import java.util.List;

/**
 * 文字公告
 * Created by sunjiexiang on 2015/12/24.
 */
public class NoticeVO {

    private String open;

    private String time;

    private List<Data> list;

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Data> getList() {
        return list;
    }

    public void setList(List<Data> list) {
        this.list = list;
    }

    public static class Data {

        private String title;

        private String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}
