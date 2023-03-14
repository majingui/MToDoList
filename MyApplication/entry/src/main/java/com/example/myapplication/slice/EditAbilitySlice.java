package com.example.myapplication.slice;

import com.example.myapplication.PreferencesHelper;
import com.example.myapplication.ResourceTable;
import com.example.myapplication.adapter.data;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityContinuation;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.IDialog;
import ohos.agp.window.dialog.ListDialog;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.example.myapplication.adapter.SampleItemProvider;
import com.example.myapplication.adapter.SampleItem;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;

public class EditAbilitySlice extends AbilitySlice implements IAbilityContinuation {
    Text mTitle;//标题控件
    TextField mDetail;//输入框，即内容
    Button mContinue;//迁移按钮
    //下面是两个缓存字符串
    String mCacheKey;
    String mCacheContent;
    String oldKey;
    String flag;
    //这个界面涉及到流转
    @Override
    public void onStart(Intent intent) {
//        Text a = (Text)findComponentById(ResourceTable.Id_content);
//        a.setText("根据"+mTitle+"搜索到以下结果");




        this.flag = intent.getStringParam("flag");
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_edit);
        mTitle = (Text) findComponentById(ResourceTable.Id_title);
        mDetail = (TextField) findComponentById(ResourceTable.Id_detail);
        Button save = (Button) findComponentById(ResourceTable.Id_save);//定义一个保存按钮
        save.setClickedListener(new Component.ClickedListener() {//保存并写入数据库
            @Override
            public void onClick(Component component) {
                saveRecord();
            }
        });
        mContinue = (Button) findComponentById(ResourceTable.Id_move);//流转按钮
        mContinue.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                checkDevice();//检查设备并弹窗
            }
        });
        if (intent != null) {//下面这一片能处理当是主动启动与被动启动时的缓存初始化
            Preferences preferences = PreferencesHelper.getInstance().getPreference(getApplicationContext());
            String key;
            String content;
            if (mCacheKey != null) {//是被迁移的
                key = mCacheKey;
                content = mCacheContent;
            } else {//不是被迁移的
                key = intent.getStringParam("key");//不空时
                if (key == null || "".equals(key)) {//当key时空时
                    key = "Title"+preferences.getAll().size();
                    if (preferences.hasKey(key)) {//分布式数据库中有该键时
                        key = "Title"+preferences.getAll().size()+"_copy";
                    }
                }
                content = preferences.getString(key, "");
            }
            //设置标题的Text控件与内容Textfield控件的内容
            mTitle.setText(key);
            mDetail.setText(content);
            oldKey = key;
        }
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    private void saveRecord() {
        Preferences preferences = PreferencesHelper.getInstance().getPreference(getApplicationContext());


        //新增逻辑
        String key = oldKey;
        if(flag.equals("T")){
            if(preferences.hasKey(mTitle.getText())){
                CommonDialog commonDialog = new CommonDialog(getContext());
                commonDialog.setTitleText("该标题已存在，请重新设置！");
                commonDialog.setButton(1, "知道了", new IDialog.ClickedListener() {
                    @Override
                    public void onClick(IDialog iDialog, int i) {

                        iDialog.hide();//弹框消失
                    }
                });
//                commonDialog
                commonDialog.show();//弹框询问
                return;
            }
        }
        else{
            if(mTitle.getText().equals(key)){
                preferences.delete(key);
                preferences.flush();
            }
            else if(preferences.hasKey(mTitle.getText())){
                CommonDialog commonDialog = new CommonDialog(getContext());
                commonDialog.setTitleText("该标题已存在，请重新设置！");
                //弹出对话框
                //设置按钮与回调
                commonDialog.setButton(1, "知道了", new IDialog.ClickedListener() {
                    @Override
                    public void onClick(IDialog iDialog, int i) {

                        iDialog.hide();//弹框消失
                    }
                });
                commonDialog.show();//弹框询问
                return;
            }
            else{
                preferences.delete(key);
                preferences.flush();
            }
        }
//========================================================================================

        preferences.putString(mTitle.getText(), mDetail.getText());//把缓存写入数据库
        preferences.flushSync();
        Preferences preferencesSort = PreferencesHelper.getInstance().getPreferenceSort(getApplicationContext());
        Preferences preferencesTag = PreferencesHelper.getInstance().getPreferenceTag(getApplicationContext());
        preferencesSort.putString(mTitle.getText(), "all");//把缓存写入数据库
        preferencesSort.flushSync();
        preferencesTag.putString(mTitle.getText(), "0");//把缓存写入数据库
        preferencesTag.flushSync();
        terminateAbility();

    }

    private void checkDevice() {
        // 通过FLAG_GET_ONLINE_DEVICE标记获得在线设备列表
        List<DeviceInfo> deviceInfoList = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
        if (deviceInfoList.size() < 1) {
            showTip(this, "无在网设备");
        } else {
            showDeviceChooser(deviceInfoList);//展示设备
        }
    }

    private static void showTip(Context context, String text) {//showtip函数就是toast
        ToastDialog toastDialog = new ToastDialog(context);
        toastDialog.setText(text);
        toastDialog.show();
    }

    private void showDeviceChooser(List<DeviceInfo> deviceInfoList) {
        ListDialog dialog = new ListDialog(this);//创建一个ListDialog，是一个列表的对话框，本项目还用了另一种叫警示对话框，用来展示确定与取消
        String[] names = new String[deviceInfoList.size()];
        for(int i = 0; i<deviceInfoList.size();i++) {
            names[i] = deviceInfoList.get(i).getDeviceName();
        }
        dialog.setItems(names);//设置对话框列表内容
        dialog.setOnSingleSelectListener(new IDialog.ClickedListener() {//这里传入的是IDialog
            @Override
            public void onClick(IDialog iDialog, int i) {
                DeviceInfo info = deviceInfoList.get(i);//获取设备信息
                try {
                    // 开始任务迁移
                    continueAbility();
                } catch (IllegalStateException | UnsupportedOperationException e) {
                }
                dialog.hide();
            }
        });
        // 点击后迁移到指定设备
        dialog.setListener(new ListContainer.ItemClickedListener() {//这里传入的是ListContainer
            @Override
            public void onItemClicked(ListContainer listContainer, Component component, int i, long l) {
                DeviceInfo info = deviceInfoList.get(i);
                try {
                    // 开始任务迁移
                    continueAbility();
                } catch (IllegalStateException | UnsupportedOperationException e) {
                }
                dialog.hide();
            }
        }, null , null);
        dialog.show();//展示弹窗
    }


    @Override
    public boolean onStartContinuation() {//当发起方迁移时回调
        return true;
    }

    //下面两个函数在发生迁移时在发起方和接受方分别回调
    @Override
    public boolean onSaveData(IntentParams intentParams) {//发起迁移端保存数据
        intentParams.setParam("title", mTitle.getText());
        intentParams.setParam("content", mDetail.getText());
        return true;
    }

    @Override
    public boolean onRestoreData(IntentParams intentParams) {//接受迁移端恢复数据，也就是要先给两个缓存变量赋值
        mCacheKey = getIntentString(intentParams, "title");
        mCacheContent = getIntentString(intentParams, "content");
        this.flag  = getIntentString(intentParams,"flag");
        return true;
    }

    private String getIntentString(IntentParams intentParams, String key) {//从intent中拿数据
        Object value = intentParams.getParam(key);
        if ((value != null) && (value instanceof String)) {
            return (String) value;
        }
        return null;
    }

    @Override
    public void onCompleteContinuation(int i) {//当发起迁移方结束迁移时结束接收方的ability
        terminateAbility();
    }
}
