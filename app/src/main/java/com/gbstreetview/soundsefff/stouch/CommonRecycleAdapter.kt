package com.gbstreetview.soundsefff.stouch

import android.app.Activity
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.media.MediaPlayer
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.ybq.android.spinkit.SpinKitView
import com.webheavens.manishkprmaterialtabs.MainActivity
import com.webheavens.manishkprmaterialtabs.R
import com.webheavens.manishkprmaterialtabs.databinding.RecycleViewCommonItemsBinding

import com.webheavens.manishkprmaterialtabs.model.VoiceItemModel
import com.webheavens.manishkprmaterialtabs.utils.AminUtils
import com.webheavens.manishkprmaterialtabs.utils.SoundPlayer
import com.webheavens.manishkprmaterialtabs.viewModel.VoiceItemViewModel
import com.webheavens.manishkprmaterialtabs.events.MessageEvent
import com.webheavens.manishkprmaterialtabs.events.ProcessSoundClick
import com.webheavens.manishkprmaterialtabs.repository.Repository
import com.webheavens.manishkprmaterialtabs.utils.Constants
import org.greenrobot.eventbus.EventBus




class CommonRecycleAdapter() :
        RecyclerView.Adapter<CommonRecycleAdapter.ViewHolder>(), View.OnClickListener, SoundPlayer.SoundPlayerCallBacks {

    internal var context: Context? = null
    internal var currentItemPosition = 0
   lateinit internal var soundPlayer: SoundPlayer
    var itemsData: ArrayList<VoiceItemModel>? = ArrayList()
    internal var player: MediaPlayer? = null
    lateinit var activity:Activity

    public  var adapterType:Int=0
  public  companion object {

      var soundID = 0
    }

  constructor( activity: Activity?, itemsDataa: List<VoiceItemModel>?) : this() {
this.activity= activity!!
      itemsData=itemsDataa as ArrayList<VoiceItemModel>;
      initPlayer()
  }

    fun initPlayer() {
        try {
            soundPlayer = SoundPlayer(context as Activity?, this!!.itemsData!![0], this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

   /* init {
        itemsData=itemsDataa as ArrayList<VoiceItemModel>;
        initPlayer()
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonRecycleAdapter.ViewHolder {

        val itemLayoutView = LayoutInflater.from(parent.context).inflate(R.layout.recycle_view_common_items, null)
        context=parent.context


        return ViewHolder(itemLayoutView)

    }


var vh:ViewHolder?=null
var playPos:Int=0;
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        currentItemPosition = position
        vh=viewHolder
//viewHolder.setViewModel(VoiceItemViewModel(itemsData!!.get(position)))
        if (this!!.itemsData!![position].isPalying) {
            viewHolder.playBtn.setImageResource(R.drawable.group_20_2)
        } else {
            viewHolder.playBtn.setImageResource(R.drawable.group_20)

        }

        if (this!!.itemsData!![position].isProgressBar) {
            viewHolder.progree!!.visibility=View.VISIBLE
        } else {
           // viewHolder.playBtn.setImageResource(R.drawable.group_20)
            viewHolder.progree!!.visibility=View.INVISIBLE

        }



        viewHolder.soundNameTv.text = this!!.itemsData!![position].soundName




        if (this!!.itemsData!![position].thumIconLocal != 0) {
            viewHolder.thumbIcon.setImageResource(this!!.itemsData!![position].thumIconLocal)
        } else {
            loadImagefrmUrl(viewHolder)
        }
     //   viewHolder.optionButton.setImageResource(this!!.itemsData!![position].optionBtnIcon)


        viewHolder.playBtn.setOnClickListener {
            currentItemPosition = position
            playPos=position
            animateZoomInOutOnce(viewHolder.playBtn)
            android.os.Handler().postDelayed({
                //  clearAllPreviousPlayedBtns(viewHolder.playBtn);
                playAndChangeImageBgOnButtonClick(viewHolder.playBtn)

            }, 200)


        }



        viewHolder.playBtn.tag = "pause"

        if(adapterType==1){
            viewHolder.optionButton.visibility=View.INVISIBLE
        }else{
            viewHolder.optionButton.visibility=View.VISIBLE
            viewHolder.optionButton.setOnClickListener{view->
                (context as MainActivity).showItemsInDialogue(view,itemsData!!.get(position))
            }
        }



        // viewHolder.playBtn.setImageResource(itemsData.get(position).getPlayBtnIcon());


    }

    var savItemPos=0

    override fun onClick(view: View) {

        when (view.id) {
            R.id.playBtn -> {

            }

            R.id.optionButton -> {

                (context as MainActivity).showItemsInDialogue(view,itemsData!!.get(savItemPos))
            }
        }/*clearAllPreviousPlayedBtns();
                playAndChangeImageBgOnButtonClick(view);
                animateZoomInOutOnce(view);*/
    }



    fun playSound()  {
        try {


        stopSound()
        // soundPlayer.playSound(itemsData.get(currentItemPosition).getFrequency());
        val file = this!!.itemsData!![currentItemPosition].filePath
        if (file.exists()) {
            player = MediaPlayer.create(context, Uri.fromFile(file))

            player!!.setOnErrorListener { mediaPlayer, i, i1 ->
                Log.d("debug", "error $i $i1")

                false
            }
            player!!.start()
        }

        player!!.setOnCompletionListener {
            try {
                vh!!.playBtn!!.tag = "pause"
                vh!!.playBtn!!.setImageResource(R.drawable.group_20)
                this!!.itemsData!![playPos].isPalying = false
                stopSound()
                notifyItemChanged(playPos)
            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }

        }
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun pauseSound() {
        if (player != null) {
            if (player!!.isPlaying) {
                player!!.pause()
            }
        }
    }

    fun stopSound() {
        if (player != null) {
            if (player!!.isPlaying) {
                //player.stop();
                player!!.reset()
                player!!.release()
                player = null
            }
        }
    }

    var imageView:ImageView?=null
    fun playAndChangeImageBgOnButtonClick(view: View) {
        imageView = view as ImageView
        if(! itemsData!![currentItemPosition].isProcessed){
            for( i in itemsData!!.indices){
                if(i!=currentItemPosition)
                    itemsData!![i].isProgressBar=false;
                else
                    itemsData!![i].isProgressBar=true;



            }
            notifyItemChanged(currentItemPosition)

            var click:ProcessSoundClick=ProcessSoundClick(itemsData!![currentItemPosition],currentItemPosition)
           click.voiceItemModel!!.soundCategory=Constants.CURRENT_SOUND_CATEG

            ProcessSound(context, Repository(this!!.context!!),click)
                   .initAndProcessSoundAdapter(object :CommonRecycleAdapter.ProcessCallBak{
                       override fun onProcess(evn: MessageEvent) {
                           playSoundEvent(evn.pos)
                       }

                   })

            //EventBus.getDefault().post(click)
        }else{


            for( i in itemsData!!.indices){
                if(i!=currentItemPosition)
                    itemsData!![i].isPalying=false;



            }
            if (this!!.itemsData!![currentItemPosition].isPalying ) {
                imageView!!.tag = "pause"
                imageView!!.setImageResource(R.drawable.group_20)
                this!!.itemsData!![currentItemPosition].isPalying = false
                stopSound()
            } else {
                imageView!!.tag = "play"
                imageView!!.setImageResource(R.drawable.group_20_2)
                this!!.itemsData!![currentItemPosition].isPalying = true
                playSound()


            }


            notifyDataSetChanged()

        }





    }

    interface ProcessCallBak{
      fun  onProcess(evn:MessageEvent)
    }


fun playSoundEvent(pos:Int){
    for( i in itemsData!!.indices){
        if(i!=currentItemPosition){
            itemsData!![i].isPalying=false;
           // itemsData!![i].isProcessed=false;
        }

        else{
            itemsData!![i].isPalying=true;
            itemsData!![i].isProcessed=true;
        }

    }

  //  imageView!!.tag = "play"
    vh!!.playBtn.tag= "play"
    vh!!.playBtn!!.setImageResource(R.drawable.group_20_2)
    this!!.itemsData!![currentItemPosition].isPalying = true
    this!!.itemsData!![currentItemPosition].isProgressBar=false
    notifyDataSetChanged()
    playSound()
}
    fun animateZoomInOutOnce(view: View) {
        android.os.Handler().postDelayed({ AminUtils.zoomInOutOnce(context, view) }, 100)

    }


    fun performedActionOnPlayButton() {

    }


    fun performActionOnOptionButton() {

    }

    override fun onPlay() {

    }

    override fun onPause() {

    }

    override fun onStop() {

    }

    override fun onReady() {

    }


    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView) {
         var thumbIcon: ImageView
       var playBtn: ImageView
        var soundNameTv: TextView
       var optionButton: ImageView
        var binding: RecycleViewCommonItemsBinding? =null
var progree:SpinKitView?=null
        init {


            soundNameTv = itemLayoutView.findViewById(R.id.soundNameTv)
            optionButton = itemLayoutView.findViewById(R.id.optionButton)
            playBtn = itemLayoutView.findViewById(R.id.playBtn)
            thumbIcon = itemLayoutView.findViewById(R.id.thumbIcon)
            progree=itemLayoutView.findViewById(R.id.spin_kit)

            if(binding==null){
                binding=DataBindingUtil.bind(itemLayoutView)
            }

        }


        fun setViewModel(viewItem:VoiceItemViewModel){
            binding!!.voiceItemVModel=viewItem
        }

        fun unbind() {
            if (binding != null) {
                binding!!.unbind(); // Don't forget to unbind
            }
        }

    }

    fun loadImagefrmUrl(viewHolder: ViewHolder) {

    }

    override fun getItemCount(): Int {
        return itemsData!!.size
    }






    fun updateAll(data:List<VoiceItemModel>){
if(itemsData!=null){
    itemsData!!.clear()
    itemsData!!.addAll(data)
}


    }

}