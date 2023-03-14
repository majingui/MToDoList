package com.example.myapplication;


import com.example.myapplication.slice.FirstShowSlice;
import com.example.myapplication.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(FirstShowSlice.class.getName());
        super.addActionRoute("action.MainAbility",MainAbilitySlice.class.getName());
        //super.setMainRoute(MainAbilitySlice.class.getName());
        requestPermission();
    }

    private void requestPermission() {
        String[] permissions = {
                "ohos.permission.DISTRIBUTED_DATASYNC"
        };//[为什么只有一个权限]
        List<String> applyPermissions = new ArrayList<>();
        for (String element : permissions) {
            if (verifySelfPermission(element) != 0) {
                if (canRequestPermission(element)) {
                    applyPermissions.add(element);
                }
            }
        }
        requestPermissionsFromUser(applyPermissions.toArray(new String[0]), 0);
    }
}