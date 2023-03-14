package com.example.myapplication.slice;


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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainAbilitySlice extends AbilitySlice {
    private ListContainer mlistContainer;
    private Button mBtn;
    //。。。
    TextField mT;
    Button mBtn1;
    private PreferencesChangeCounter mCounter;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_page_listcontainer);
//        AbilitySlice.instance.finish();
        initListContainer();//初始化控件展示后点击就会进入编辑界面，
        addObserver();//可能会进入编辑界面，要注册一个数据库服务，用来监听数据库变化并更新组件
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    protected void onStop() {
        removeObserver();
        super.onStop();
    }

    private void initListContainer() {
        mlistContainer = (ListContainer) findComponentById(ResourceTable.Id_list_container);
        List<SampleItem> list = getData();//读取数据库中的内容并返回
        SampleItemProvider sampleItemProvider = new SampleItemProvider(list, this);//把含有数据的list放进管理器
        mlistContainer.setItemProvider(sampleItemProvider);//把管理器设置再控件中，完成数据的绑定，即listView控件能显示列表了
        //下面设定短暂点击的回调


        mlistContainer.setItemClickedListener(new ListContainer.ItemClickedListener() {
            @Override
            public void onItemClicked(ListContainer listContainer, Component component, int i, long l) {

                    SampleItem item = (SampleItem) listContainer.getItemProvider().getItem(i);//i相当于下标
                    startAbility(item.getmTitle());//这是有关重载方法，能够根据关键词去数据库找对应内容，在启动ability

            }
        });
        //长按回调
        mlistContainer.setItemLongClickedListener(new ListContainer.ItemLongClickedListener() {
            @Override
            public boolean onItemLongClicked(ListContainer listContainer, Component component, int i, long l) {
                SampleItem item = (SampleItem) listContainer.getItemProvider().getItem(i);
                showDeleteDialog(item.getmTitle());//展示删除信息
                return true;
            }
        });
        //点击按钮新建回调
        mBtn = (Button) findComponentById(ResourceTable.Id_new_record);
        //..
        mBtn1 = (Button)findComponentById(ResourceTable.Id_searchbutton);
        mT = (TextField)findComponentById(ResourceTable.Id_searchfield);
        mBtn1.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                String tmpSear  =  mT.getText();
                Preferences preferences = PreferencesHelper.getInstance().getPreference(getContext());
                if(tmpSear.equals("")){
                    return;
                }
                if(preferences.hasKey(tmpSear)){
                    Context context = getContext();
                    //这个是一个控件
                    CommonDialog commonDialog = new CommonDialog(context);
                    commonDialog.setTitleText("搜索到该Title备忘录，是否进入编辑");
                    //弹出对话框
                    //设置按钮与回调
                    commonDialog.setButton(1, "确定", new IDialog.ClickedListener() {
                        @Override
                        public void onClick(IDialog iDialog, int i) {
                            //获取偏好数据库
                            Preferences preferences = PreferencesHelper.getInstance().getPreference(getApplicationContext());

                            iDialog.hide();//弹框消失
                            Intent intent = new Intent();
                            Operation operation = new Intent.OperationBuilder()
                                    .withDeviceId("")
                                    .withBundleName(getBundleName())
                                    .withAbilityName("com.example.myapplication.EditAbility")
                                    .build();
                            intent.setOperation(operation);
                            intent.setParam("key", tmpSear);
                            intent.setParam("flag","F");



                            intent.setFlags(Intent.FLAG_ABILITY_NEW_MISSION);
                            startAbility(intent);
                        }
                    });
                    commonDialog.setButton(2, "取消", new IDialog.ClickedListener() {
                        @Override
                        public void onClick(IDialog iDialog, int i) {
                            iDialog.hide();
                        }
                    });
                    commonDialog.show();//弹框询问
                }
                else{
//                    new ToastDialog(getContext())
//                            .setText("搜索不到耶！！！")
//                            .setAlignment(LayoutAlignment.CENTER)
//                            .show();
                    //Preferences preferences = PreferencesHelper.getInstance().getPreference(getApplicationContext());
                    Map<String, String> records = (Map<String, String>) preferences.getAll();//从数据库拿出来是键值对
                    ArrayList<SampleItem> list = new ArrayList<>();
                    for (String key : records.keySet()) {//遍历键
                        if(key.contains(tmpSear)){
                            list.add(new SampleItem(key, records.getOrDefault(key, "")));
                        }
                    }
                    SampleItemProvider provider = (SampleItemProvider) mlistContainer.getItemProvider();
                    provider.refreshList(list);
                }
            }
        });
        mBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                startAbility("");
            }
        });
        Button allButton = (Button) findComponentById(ResourceTable.Id_sortAll);

        allButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
//                new ToastDialog(getContext())
//                        .setText("搜索不到耶！！！")
//                        .setAlignment(LayoutAlignment.CENTER)
//                        .show();
                Preferences preferences = PreferencesHelper.getInstance().getPreference(getApplicationContext());
                Map<String, String> records = (Map<String, String>) preferences.getAll();//从数据库拿出来是键值对
                ArrayList<SampleItem> list = new ArrayList<>();
                for (String key : records.keySet()) {//遍历键
                    list.add(new SampleItem(key, records.getOrDefault(key, "")));
                }
                SampleItemProvider provider = (SampleItemProvider) mlistContainer.getItemProvider();

                provider.refreshList(list);
                new ToastDialog(getContext())
                        .setText("已为您展示全部类别")
                        .setAlignment(LayoutAlignment.CENTER)
                        .show();
            }
        });
        Button studyButton = (Button) findComponentById(ResourceTable.Id_sortstudy);
        studyButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                Preferences preferences = PreferencesHelper.getInstance().getPreference(getApplicationContext());
                Map<String, String> records = (Map<String, String>) preferences.getAll();//从数据库拿出来是键值对
                Preferences preferencesSort = PreferencesHelper.getInstance().getPreferenceSort(getApplicationContext());
                Map<String, String> recordsSort = (Map<String, String>) preferencesSort.getAll();
                ArrayList<SampleItem> list = new ArrayList<>();
                for (String key : records.keySet()) {//遍历键
                    if(recordsSort.get(key).equals("study")){
                        list.add(new SampleItem(key, records.getOrDefault(key, "")));
                    }
                }
                SampleItemProvider provider = (SampleItemProvider) mlistContainer.getItemProvider();
                provider.refreshList(list);
                new ToastDialog(getContext())
                        .setText("已为您展示学习类别")
                        .setAlignment(LayoutAlignment.CENTER)
                        .show();
            }
        });
        Button lifeButton = (Button) findComponentById(ResourceTable.Id_sortLife);
        lifeButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                Preferences preferences = PreferencesHelper.getInstance().getPreference(getApplicationContext());
                Map<String, String> records = (Map<String, String>) preferences.getAll();//从数据库拿出来是键值对
                Preferences preferencesSort = PreferencesHelper.getInstance().getPreferenceSort(getApplicationContext());
                Map<String, String> recordsSort = (Map<String, String>) preferencesSort.getAll();
                ArrayList<SampleItem> list = new ArrayList<>();
                for (String key : records.keySet()) {//遍历键
                    if(recordsSort.get(key).equals("life")){
                        list.add(new SampleItem(key, records.getOrDefault(key, "")));
                    }
                }
                SampleItemProvider provider = (SampleItemProvider) mlistContainer.getItemProvider();
                provider.refreshList(list);
                new ToastDialog(getContext())
                        .setText("已为您展示生活类别")
                        .setAlignment(LayoutAlignment.CENTER)
                        .show();
            }
        });
        Button workButton = (Button) findComponentById(ResourceTable.Id_sortWork);
        workButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                Preferences preferences = PreferencesHelper.getInstance().getPreference(getApplicationContext());
                Map<String, String> records = (Map<String, String>) preferences.getAll();//从数据库拿出来是键值对
                Preferences preferencesSort = PreferencesHelper.getInstance().getPreferenceSort(getApplicationContext());
                Map<String, String> recordsSort = (Map<String, String>) preferencesSort.getAll();
                ArrayList<SampleItem> list = new ArrayList<>();
                for (String key : records.keySet()) {//遍历键
                    if(recordsSort.get(key).equals("work")){
                        list.add(new SampleItem(key, records.getOrDefault(key, "")));
                    }
                }
                SampleItemProvider provider = (SampleItemProvider) mlistContainer.getItemProvider();
                provider.refreshList(list);
                new ToastDialog(getContext())
                        .setText("已为您展示工作类别")
                        .setAlignment(LayoutAlignment.CENTER)
                        .show();
            }
        });
    }

    private ArrayList<SampleItem> getData() {//创建列表
        Preferences preferences = PreferencesHelper.getInstance().getPreference(getContext());
        //访问数据库，生成键值对
        Map<String, String> records = (Map<String, String>) preferences.getAll();//从数据库拿出来是键值对
        ArrayList<SampleItem> list = new ArrayList<>();
        for (String key : records.keySet()) {//遍历键
            list.add(new SampleItem(key, records.getOrDefault(key, "")));
        }
        return list;
    }
    //下面两个函数与数据库有关
    private void addObserver() {
        Preferences preferences = PreferencesHelper.getInstance().getPreference(getContext());
        mCounter = new PreferencesChangeCounter();//启用数据库数据变化监听回调
        preferences.registerObserver(mCounter);//注册数据库服务
    }

    private void removeObserver() {//移除与注销数据库服务
        Preferences preferences = PreferencesHelper.getInstance().getPreference(getContext());
        preferences.unregisterObserver(mCounter);
    }
    //根据关键词去启动ability，这是有关重载方法
    private void startAbility(String key) {
        String flag;
        if(key==""){
            flag="T";
        }
        else{
            flag = "F";
        }
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName(getBundleName())
                .withAbilityName("com.example.myapplication.EditAbility")
                .build();
        intent.setOperation(operation);
        intent.setParam("key", key);
        intent.setParam("flag",flag);
        intent.setFlags(Intent.FLAG_ABILITY_NEW_MISSION);
        startAbility(intent);
    }
    //发生改变时能够刷新
    private class PreferencesChangeCounter implements Preferences.PreferencesObserver {
        @Override
        public void onChange(Preferences preferences, String key) {//当偏好数据库中数据改变时回调，会传入发生改变的key
            BaseItemProvider itemProvider = mlistContainer.getItemProvider();//获取listContainer里面的条目管理器
            if (itemProvider instanceof SampleItemProvider) {//如果是关于备忘录项的管理类（也就是刚刚设定的）
                ((SampleItemProvider) itemProvider).refreshList(getData());//说明它就是刚刚设置的SampleItemProvider，
                // 它管理着list的展示，调用它去刷新listContainer控件，先强制转换类型，再去调用刷新函数，传进的参数位getDAta的结果。getData会重新从数据库中加载数据
            }
        }
    }

    public void showDeleteDialog(final String key) {
        if (key == null || "".equals(key)) {
            return;
        }
        Context context = getContext();
        //这个是一个控件
        CommonDialog commonDialog = new CommonDialog(context);
        Component layout = LayoutScatter.getInstance(this).parse(ResourceTable.Layout_mydialog,null,false);

        //commonDialog.show();
        Component.ClickedListener listener = new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                String name = ((Button)component).getText();

                if(name.equals("删除条目")){

                    Preferences preferences = PreferencesHelper.getInstance().getPreference(getApplicationContext());
                    preferences.delete(key);
                    preferences.flush();
                    Preferences preferencesSort = PreferencesHelper.getInstance().getPreferenceSort(getApplicationContext());
                    preferencesSort.delete(key);
                    preferencesSort.flush();
                    Preferences preferencesTag = PreferencesHelper.getInstance().getPreferenceTag(getApplicationContext());
                    preferencesTag.delete(key);
                    preferencesTag.flush();
                    new ToastDialog(getContext())
                            .setText("已删除")
                            .setAlignment(LayoutAlignment.CENTER)
                            .show();
                }
                else if(name.equals("取消标记")){
                    Preferences preferencesSort = PreferencesHelper.getInstance().getPreferenceSort(getApplicationContext());
                    preferencesSort.putString(key,"all");
                    preferencesSort.flush();
                    new ToastDialog(getContext())
                            .setText("已取消标记")
                            .setAlignment(LayoutAlignment.CENTER)
                            .show();
                }
                else if(name.equals("取消操作")){
                    Preferences preferencesTag = PreferencesHelper.getInstance().getPreferenceTag(getApplicationContext());
                    preferencesTag.putString(key,"1");
                    preferencesTag.flush();//数据库一旦改变回立刻刷新list，不用手动
//                    SampleItemProvider provider = (SampleItemProvider) mlistContainer.getItemProvider();
//                    provider.refreshList(list);
                    new ToastDialog(getContext())
                            .setText("无操作")
                            .setAlignment(LayoutAlignment.CENTER)
                            .show();
                }
                else if(name.equals("标记学习")){
                    Preferences preferencesSort = PreferencesHelper.getInstance().getPreferenceSort(getApplicationContext());
                    preferencesSort.putString(key,"study");
                    preferencesSort.flush();
                    new ToastDialog(getContext())
                            .setText("已标记为学习类别")
                            .setAlignment(LayoutAlignment.CENTER)
                            .show();
                }
                else if(name.equals("标记生活")){
                    Preferences preferencesSort = PreferencesHelper.getInstance().getPreferenceSort(getApplicationContext());
                    preferencesSort.putString(key,"life");
                    preferencesSort.flush();
                    new ToastDialog(getContext())
                            .setText("已标记为生活类别")
                            .setAlignment(LayoutAlignment.CENTER)
                            .show();
                }
                else if(name.equals("标记工作")){
                    Preferences preferencesSort = PreferencesHelper.getInstance().getPreferenceSort(getApplicationContext());
                    preferencesSort.putString(key,"work");
                    preferencesSort.flush();
                    new ToastDialog(getContext())
                            .setText("已标记为工作类别")
                            .setAlignment(LayoutAlignment.CENTER)
                            .show();
                }
                commonDialog.hide();
            }

        };
        Button delete = (Button)layout.findComponentById(ResourceTable.Id_delete);//要指定layout里的控件
        delete.setClickedListener(listener);
        Button cancel = (Button)layout.findComponentById(ResourceTable.Id_cancel);
        cancel.setClickedListener(listener);
        Button nodo = (Button)layout.findComponentById(ResourceTable.Id_nodo);
        nodo.setClickedListener(listener);
        Button study = (Button)layout.findComponentById(ResourceTable.Id_study);
        study.setClickedListener(listener);
        Button life = (Button)layout.findComponentById(ResourceTable.Id_life);
        life.setClickedListener(listener);
        Button work = (Button)layout.findComponentById(ResourceTable.Id_work);
        work.setClickedListener(listener);
        commonDialog.setTransparent(true);
        commonDialog.setContentCustomComponent(layout);
        commonDialog.show();

    }
}
