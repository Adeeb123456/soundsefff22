package com.gbstreetview.soundsefff.stouch;


import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.webheavens.manishkprmaterialtabs.model.ProcessSoundObj;
import com.webheavens.manishkprmaterialtabs.soundTouch.ProcessSoundLogic;

import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ProcessSoundRx {
    Context context;
    int sountCate;
    ProcessSoundObj[] soundObjsArray;
    ProcessSoundLogic soundLogic=new ProcessSoundLogic();

    public  void processSoundArray(Context context,int sountCate,ProcessSoundObj[] soundObjsArray){
        this.context=context;
        this.soundObjsArray=soundObjsArray;
        this.sountCate=sountCate;
        final String[] states = {"Lagos", "Abuja", "Imo", "Enugu"};
        Observable<ProcessSoundObj> statesObservable = Observable.fromArray(soundObjsArray);
        soundLogic.initSound(context,sountCate);
        statesObservable.flatMap(
                s -> Observable.create(getProcessedSound(s)).subscribeOn(Schedulers.computation())

        ).subscribe(pair -> Log.d("MainActivity", pair.first + " population is " + pair.second));
    }


int count=-1;
    private ObservableOnSubscribe<Pair> getProcessedSound(ProcessSoundObj soundObj) {
        return(emitter -> {
            Random r = new Random();
            count++;
            Log.d("MainActivity", "getPopulation() for "+ " called on " + Thread.currentThread().getName());



soundLogic.processSound(soundObj,count);
            emitter.onNext(new Pair(soundObj, r.nextInt(300000 - 10000) + 10000));
            emitter.onComplete();
        });
    }
}
