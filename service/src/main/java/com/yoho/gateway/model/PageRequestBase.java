package com.yoho.gateway.model;

public class PageRequestBase {

    private int page = 1;
    private int size = 10;
    //起始
    private int start = 0;

    public int getStart() {
        if(page>0&&size>0){
            this.start = (page-1)*size;
        }else {
            this.start = 0;
        }
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        if (page <= 1) {
            page = 1;
        }
        this.page = page;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "PageRequest [page=" + page + ", size=" + size + ", start="
                + start + "]";
    }
}
