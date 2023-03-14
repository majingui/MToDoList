package com.example.myapplication.adapter;

public class data {
    String title;
    String context;


    public data(String title,String context){//构造方法没有返回值
        this.title = title;
        this.context = context;
    }
    void setTitle(String newTitle){
        this.title = newTitle;
    }
    void setContext(String newContext){
        this.context = newContext;
    }
    String getTitle(){
        return this.title;
    }
    String getContext(){
        return this.context;
    }
}
