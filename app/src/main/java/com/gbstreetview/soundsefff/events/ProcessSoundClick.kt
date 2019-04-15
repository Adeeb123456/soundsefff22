package com.webheavens.manishkprmaterialtabs.events

import com.webheavens.manishkprmaterialtabs.model.VoiceItemModel


class ProcessSoundClick(item:VoiceItemModel,posi:Int) {
      var voiceItemModel:VoiceItemModel?=null
    var pos=0

    init {
        voiceItemModel=item;
        pos=posi
    }
}