package com.example.myapplication.adapter;

import com.example.myapplication.PreferencesHelper;
import com.example.myapplication.ResourceTable;//改包名与引用的包名
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;
import ohos.agp.components.element.PixelMapElement;
import ohos.agp.components.element.ShapeElement;
import ohos.data.preferences.Preferences;
import ohos.global.resource.NotExistException;
import ohos.global.resource.Resource;

import java.io.IOException;
import java.util.List;

public class SampleItemProvider extends BaseItemProvider {
    private List<SampleItem> mList;
    private AbilitySlice mSlice;//这是一个界面类（并不是界面一定要写出来的，
    // 写出来的其实是java的类，只是他继承了ability）

    public SampleItemProvider(List<SampleItem> list, AbilitySlice slice) {
        super();//这个SampleItemProvider应该是管理List控件的一个类，container只是一个容器
        mList = list;
        mSlice = slice;
    }

    @Override//获取列表长度
    public int getCount() {
        return mList == null ? 0 :mList.size();
    }

    @Override//根据下标获取列表的SampleItem项目
    public Object getItem(int i) {
        if (mList != null && i >= 0 && i < mList.size()){
            return mList.get(i);
        }
        return null;
    }

    @Override//获取id，此处id就是下标
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        //这个回调函数考研由container来用 用于为list每个item设置自定义样式
        Component cpt;
        if (component == null) {
            //cpt他是控件类的一个实例，用它来得到一项的一个控件类
            cpt = LayoutScatter.getInstance(mSlice).parse(ResourceTable.Layout_item_sample, null, false);//这是一个内部组件，
            //它放在listcontainer这个外部组件中，嵌套
        } else {
            cpt = component;
        }
        //这个函数用于根据点击的按钮是哪一项来生成改项目的编辑界面
        SampleItem sampleItem = mList.get(i);
        Text text = (Text) cpt.findComponentById(ResourceTable.Id_title);
        text.setText(sampleItem.getmTitle());

        Text content = (Text) cpt.findComponentById(ResourceTable.Id_content);
        content.setText(sampleItem.getmString());
        Text mtext = (Text)cpt.findComponentById(ResourceTable.Id_finish);
        Button myButton = cpt.findComponentById(ResourceTable.Id_tag);
        Preferences preferencesTag = PreferencesHelper.getInstance().getPreferenceTag(mSlice.getApplicationContext());
        String tag = preferencesTag.getString(sampleItem.getmTitle(),"");
        if(tag.equals("0")){
            ShapeElement element1 = new ShapeElement();
            Resource b = null;
            try {
                b= cpt.getResourceManager().getResource(ResourceTable.Media_r);
                mtext.setText("未完成");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NotExistException e) {
                e.printStackTrace();
            }
            PixelMapElement p = new PixelMapElement(b);
            myButton.setAroundElements(p,null,null,null);
        }
        else{
            ShapeElement element1 = new ShapeElement();
            Resource b = null;
            try {
                b= cpt.getResourceManager().getResource(ResourceTable.Media_t);
                mtext.setText("已完成");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NotExistException e) {
                e.printStackTrace();
            }
            PixelMapElement p = new PixelMapElement(b);
            myButton.setAroundElements(p,null,null,null);
        }
//        Button myButton = cpt.findComponentById(ResourceTable.Id_tag);
        myButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                Preferences preferencesTag = PreferencesHelper.getInstance().getPreferenceTag(content.getContext());
                String tag = preferencesTag.getString(sampleItem.getmTitle(),"");
                if(tag.equals("0")){
                    ShapeElement element1 = new ShapeElement();
                    Resource b = null;
                    try {
                        b= cpt.getResourceManager().getResource(ResourceTable.Media_t);
                        mtext.setText("已完成");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NotExistException e) {
                        e.printStackTrace();
                    }
                    PixelMapElement p = new PixelMapElement(b);
                    myButton.setAroundElements(p,null,null,null);
                    preferencesTag.putString(sampleItem.getmTitle(),"1");
                    preferencesTag.flush();
                }
                else{
                    ShapeElement element1 = new ShapeElement();
                    Resource b = null;
                    try {
                        b= cpt.getResourceManager().getResource(ResourceTable.Media_r);
                        mtext.setText("未完成");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NotExistException e) {
                        e.printStackTrace();
                    }
                    PixelMapElement p = new PixelMapElement(b);
                    myButton.setAroundElements(p,null,null,null);
                    preferencesTag.putString(sampleItem.getmTitle(),"0");
                    preferencesTag.flush();
                }
            }
        });
        return cpt;//返回一个动态设定的一项的一个控件类
    }
    //刷新
    public void refreshList(List<SampleItem> list) {
        mList = list;
        notifyDataChanged();
    }
}

