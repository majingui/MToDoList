package com.example.myapplication.slice;

import com.example.myapplication.ResourceTable;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.ListContainer;
import ohos.agp.components.TextField;
import com.example.myapplication.PreferencesHelper;
import com.example.myapplication.ResourceTable;
import com.example.myapplication.adapter.SampleItem;
import com.example.myapplication.adapter.SampleItemProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.IDialog;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.data.preferences.Preferences;

import java.util.*;
import java.util.concurrent.Delayed;

public class FirstShowSlice extends AbilitySlice{
    public static AbilitySlice instance = null;

// onCreate方法中赋值

    @Override
    public void onStart(Intent intent) {
        instance = this;
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_first_show);
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Timer timer=new Timer();
        TimerTask tast=new TimerTask() {
            @Override
            public void run(){
                present(new MainAbilitySlice(),new Intent());
                terminate();
            }
        };
        timer.schedule(tast, 1500);//10秒后
        //terminate();
        //terminateAbility();

//        Thread myThread = new Thread();
//        try {
//            myThread.sleep(1500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
    @Override
    public void onInactive() {
        //super.onInactive();

    }
    @Override
    public void onActive() {
        super.onActive();
    }
    @Override
    public void onBackground(){
        //super.onBackground();
        this.onStop();

    }
    @Override
    public void onForeground(Intent intent) {
        //super.onForeground(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

