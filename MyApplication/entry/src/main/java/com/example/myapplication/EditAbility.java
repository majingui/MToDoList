package com.example.myapplication;
import com.example.myapplication.slice.EditAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.IAbilityContinuation;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;

public class EditAbility extends Ability implements IAbilityContinuation {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(EditAbilitySlice.class.getName());
    }
    @Override
    public boolean onStartContinuation() {
        return true;
    }

    @Override
    public boolean onSaveData(IntentParams intentParams) {
        return true;
    }

    @Override
    public boolean onRestoreData(IntentParams intentParams) {
        return true;
    }

    @Override
    public void onCompleteContinuation(int i) {
    }
}
