package com.example.myapplication.adapter;

import com.alibaba.fastjson.JSON;
import ohos.ai.cv.text.Text;

public class SampleItem {
    private String mTitle;//标题
    private String mString;//内容

    public SampleItem(String title, String str) {
        super();
        //data tmp = JSON.parseObject(str,data.class);
        mTitle = title;
        mString = str;//现在是真正的title与context
    }
    //下面是对标题和内容的读写
    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmString() {
        return mString;
    }

    public void setmString(String mString) {
        this.mString = mString;
    }
}
