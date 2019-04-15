package com.gbstreetview.soundsefff.stouch

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.webheavens.manishkprmaterialtabs.databinding.*
import com.webheavens.manishkprmaterialtabs.adapters.MainPagerAdapter
import com.webheavens.manishkprmaterialtabs.events.MessageEvent
import com.webheavens.manishkprmaterialtabs.fragments.HomeFragment
import com.webheavens.manishkprmaterialtabs.fragments.SaveFragment
import com.webheavens.manishkprmaterialtabs.fragments.dummyFragment
import com.webheavens.manishkprmaterialtabs.model.ProcessSoundObj
import com.webheavens.manishkprmaterialtabs.model.VoiceItemModel
import com.webheavens.manishkprmaterialtabs.repository.Repository
import com.webheavens.manishkprmaterialtabs.soundTouch.ProcessSoundLogic
import com.webheavens.manishkprmaterialtabs.soundTouch.ProcessSoundRx
import com.webheavens.manishkprmaterialtabs.utils.*
import com.webheavens.manishkprmaterialtabs.views.SaveSoundsActivity


import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.view.*
import kotlinx.android.synthetic.main.popup_layout.view.*
import me.piruin.quickaction.QuickAction
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import java.io.File
import java.io.RandomAccessFile
import java.util.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

   internal var toolbar: Toolbar? =null
    lateinit internal var fab: FloatingActionButton
    lateinit internal var tabs: TabLayout
    lateinit  internal var pager: ViewPager

    lateinit internal var adapter: MainPagerAdapter
    internal var Titles = arrayOf<CharSequence>("Sound Effects", "Sound Library")




    val  ID_UP:Int = 1;
    val  ID_DOWN:Int = 2;
    val  ID_SEARCH = 3;
    val ID_INFO = 4;
    val  ID_ERASE = 5;
    val  ID_OK = 6;

    lateinit var  quickAction: QuickAction;
    lateinit var  quickIntent:QuickAction


   /* fun initToolTipDialogue(){



 //Config default color
  //  QuickAction.setDefaultColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
    QuickAction.setDefaultTextColor(Color.BLACK);

    var nextItem:ActionItem =  ActionItem(ID_DOWN, "Next           ");
        var  prevItem:ActionItem =  ActionItem(ID_UP, "Prev         ");
        var  searchItem:ActionItem =  ActionItem(ID_SEARCH, "Find     ");
        var  infoItem:ActionItem =  ActionItem(ID_INFO, "Info         ");
        var  eraseItem:ActionItem =  ActionItem(ID_ERASE, "Clear     ");
        var okItem:ActionItem  =  ActionItem(ID_OK, "OK");

    //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
  //  prevItem.setSticky(true);
   // nextItem.setSticky(true);



        //create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout
    //orientation
    quickAction =  QuickAction(this, QuickAction.VERTICAL);
    quickAction.setColorRes(R.color.white);
    //quickAction.setTextColorRes(R.color.gra);

    //set divider with color
        quickAction.setEnabledDivider(true);
   quickAction.setDividerColor(ContextCompat.getColor(this, R.color.dim_foreground_disabled_material_dark));
    //

    //set enable divider default is disable for vertical
    //quickAction.setEnabledDivider(true);
    //Note this must be called before addActionItem()

    //add action items into QuickAction
    quickAction.addActionItem(nextItem, prevItem);
    quickAction.setTextColor(Color.GRAY);

    quickAction.addActionItem(searchItem);
    quickAction.addActionItem(infoItem);
    quickAction.addActionItem(eraseItem);
    quickAction.addActionItem(okItem);

    //Set listener for action item clicked
    quickAction.setOnActionItemClickListener(object :QuickAction.OnActionItemClickListener {
        override fun onItemClick(item: ActionItem?) {
           var title:String  = item!!.getTitle();
            Toast.makeText(applicationContext, title+" selected", Toast.LENGTH_SHORT).show();
            if (!item.isSticky()) quickAction.remove(item);
        }

    });




                 var sendIntent:Intent =  Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");

        quickIntent =  QuickIntentAction(this)
                .setActivityIntent(sendIntent)
                .create();
        quickIntent.setAnimStyle(QuickAction.Animation.REFLECT);



    }*/









    fun initViews() {
        toolbar = view!!.findViewById(R.id.toolbar)
        fab = view!!.findViewById(R.id.fab)
        pager = view!!.findViewById(R.id.pager)
        tabs = view!!.findViewById(R.id.tabs)

        setSupportActionBar(toolbar)

        val drawer = view!!.drawer_layout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = view!!.findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
     // toolbar!!.navigationIcon=R.drawable.ic_navigation_drawer_icon2


        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_navigation_drawer_icon2)
        }
        //initToolTipDialogue()




        view!!.findViewById<View>(R.id.record).setOnClickListener {  }
    }


    fun initAnim() {
        Handler().postDelayed({ AminUtils.animateParallexImage(this@MainActivity, view!!.findViewById(R.id.header)) }, 100)

    }


var view:View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
   // setContentView(R.layout.activity_main2)
        //  ButterKnife.bind(this);


view=bind()
        initViews()

        initAnim()
        setUpTabs()
        setUpClick()

        slideUpAnim(micContain)
        tts.creatAndSaveSound("say hello",null)

      if(TinyDB.getInstance(applicationContext).getBoolean(Constants.isAppFirstTimeRunKey)){

      }else{
          TinyDB.getInstance(applicationContext).putBoolean(Constants.isAppFirstTimeRunKey,true)

          Handler().postDelayed({


              mic_record.performClick()
          },400)

      }
    }

    lateinit var binding: ActivityMain2Binding
    fun bind(): View? {
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main2)


         return binding.root

    }

    internal fun setUpClick() {


        try{


        fab.setOnClickListener { view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show() }

    popupMenu.setOnClickListener({view->
        showPopup(this, this!!.p!!);

      // quickAction.show(view)
    })
        mic_record.setOnClickListener({
            view->
            startRecordingActivity()
        })

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    val fragmentList = ArrayList<Fragment>()
    internal fun setUpTabs() {
        fragmentList.add(HomeFragment())
        adapter = MainPagerAdapter(this.supportFragmentManager, Titles, Titles.size,fragmentList)
        pager.adapter = adapter
        tabs.setupWithViewPager(pager)






/*        spaceTabLayout.initialize(pager, supportFragmentManager, fragmentList)

        spaceTabLayout.setTabOneOnClickListener(View.OnClickListener {
            val snackbar = Snackbar
                    .make(containerLayout, "Welcome to SpaceTabLayout", Snackbar.LENGTH_SHORT)

            snackbar.show()
        })

        spaceTabLayout.setOnClickListener(View.OnClickListener { Toast.makeText(application, "" + spaceTabLayout.getCurrentPosition(), Toast.LENGTH_SHORT).show() })*/

        //  setupTabIcons();
    }

    private fun setupTabIcons() {
        tabs.getTabAt(0)!!.setIcon(R.mipmap.ic_launcher)
        tabs.getTabAt(1)!!.setIcon(R.mipmap.ic_launcher)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  menuInflater.inflate(R.menu.main2, menu)
        return false

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
           startActivity(Intent(applicationContext,SaveSoundsActivity::class.java))
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    var isPicSelect=false;
    var soundUrl=""
    var importedf: File? =null



    fun startSoundEffectActivity(soundPath:String){
       var int: Intent= Intent(this,RecoedingActivity::class.java);

        int.putExtra(Constants.PROCESS_SOUND_PATH,soundPath)

        startActivity(int)
    }



    @RequiresApi(Build.VERSION_CODES.M)
    fun requestReadSoundfile() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                 requestPermissions(
                         arrayOf(READ_EXTERNAL_STORAGE),
                  Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
        } else {

            openAudioGallery()
        }

    }





    var soundByteArray: ByteArray? = null
    fun readBytesFromFile(filePath:String){
        soundByteArray=readUsingRandomAccesFile(filePath);
    }
    fun readUsingRandomAccesFile(path:String):ByteArray{
        val randomAccessFile = RandomAccessFile(path, "r")

        var document= ByteArray(randomAccessFile.length().toInt())

// Line changed
        randomAccessFile.readFully(document)

        return  document
    }



    private fun getTempFile() = File(getExternalFilesDir(null), Constants.recordedSoundName)






    fun updateSoundListAdapter() {


    }


    // The method that displays the popup_png.
    private fun showPopup(context: Activity, p: Point) {
        val popupWidth = resources.getDimensionPixelOffset(R.dimen.popup_w)
        val popupHeight = resources.getDimensionPixelOffset(R.dimen.popup_h2)

        // Inflate the popup_layout.xml
        //val viewGroup = context.findViewById(R.id.popupMenu) as LinearLayout
        val layoutInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.popup_layout, null)

        // Creating the PopupWindow
        val popup = PopupWindow(context)
        popup.contentView = layout
       popup.width = popupWidth
      popup.height = popupHeight
        popup.isFocusable = true

        // Some offset to align the popup_png a bit to the right, and a bit down, relative to button's position.
        var OFFSET_X = (p.x-resources.getDimensionPixelOffset(R.dimen.popupxoffset))
        val OFFSET_Y = 60

        popup.setBackgroundDrawable(BitmapDrawable())
         assignToolTipClickListner(popup,layout)

        popup.showAtLocation(popupMenu, Gravity.NO_GRAVITY,  OFFSET_X, OFFSET_Y)


    }

    //The "x" and "y" position of the "Show Button" on screen.
    var p: Point? = null
    override fun onWindowFocusChanged(hasFocus: Boolean) {

        try{
            var location:IntArray = IntArray(2)
            popupMenu.getLocationOnScreen(location)

            p =  Point();
            p!!.x = location[0];
            p!!.y = location[1];
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }


    }



    fun assignToolTipClickListner( popup:PopupWindow,layout:View){
       layout.textView1.setOnClickListener({

           popup.dismiss()

           requestReadSoundfile()
       })
        layout.text2.setOnClickListener({

            popup.dismiss()

            showCreateSoundDialogue(layout.text2)

        })

        layout.textView3.setOnClickListener({
            val repo:Repository by inject()
            repo.clearVoiceData()

            popup.dismiss()
     EventBus.getDefault().post( MessageEvent());
        })

    }

    val tts:TextToSoundGenerator by inject()
    fun showCreateSoundDialogue(view:View){
      DialogueUtil.showCreatSoundDialogue(this@MainActivity,view,object:DialogueUtil.CreateSoundDiaLogCall{
          override fun onCreateSoundFrmTxr(str: String) {





          }

      });
    }



    fun openAudioGallery(){
       /* val videoIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(videoIntent, "Select Audio"), Constants.SELECT_AUDIE_REQ_CODE)*/


     val intent_upload =  Intent();
       intent_upload.setType("audio/*");
       intent_upload.setAction(Intent.ACTION_GET_CONTENT)
       startActivityForResult(Intent.createChooser(intent_upload, "Select Audio"), Constants.SELECT_AUDIE_REQ_CODE)



    }




    fun showPopup2(){
        val inflater:LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.popup_layout,null)

        // Initialize a new instance of popup_png window
        val popupWindow = PopupWindow(
                view, // Custom view to show in popup_png window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup_png window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        // Set an elevation for the popup_png window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 10.0F
        }





        // Get the widgets reference from custom view
        val tv = view.findViewById<TextView>(R.id.textView1)
      //  val buttonPopup = view.findViewById<Button>(R.id.button_popup)

        // Set click listener for popup_png window's text view
        tv.setOnClickListener{
            // Change the text color of popup_png window's text view
            tv.setTextColor(Color.RED)
        }

        // Set a click listener for popup_png's button widget


        // Set a dismiss listener for popup_png window
        popupWindow.setOnDismissListener {
            Toast.makeText(applicationContext,"Popup closed",Toast.LENGTH_SHORT).show()
        }


        // Finally, show the popup_png window on app
       // TransitionManager.beginDelayedTransition(popupMenu)
        popupWindow.showAtLocation(
              popupMenu, // Location to display popup_png window
                Gravity.TOP, // Exact position of layout to display popup_png
                0, // X offset
                0 // Y offset
        )
    }



    fun slideDownAnim(view:View){
        AminUtils.slideDownAnim(this,view,object: AminUtils.AnimationCalllback{
            override fun onAnimationEnds() {
                micContain.visibility=View.GONE
            }

        })
    }


    fun slideUpAnim(view:View){
        micContain.visibility=View.VISIBLE
        AminUtils.slideUpAnim(this,view,null)
    }



    fun showMicContainer(){
        slideUpAnim(micContain)
    }


    fun hideMicContainer(){
        slideDownAnim(micContain)
    }



    //startActivities

    fun startRecordingActivity(){

        val intent = Intent(this, RecoedingActivity::class.java)
        // create the transition animation - the images in the layouts
        // of both activities are defined with android:transitionName="robot"
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptions
                    .makeSceneTransitionAnimation(this, mic_record as View, "robot")

             startActivityForResult(intent, Constants.SELECT_AUDIE_REQ_CODE,options.toBundle())
        } else {
             startActivityForResult(Intent(applicationContext, RecoedingActivity::class.java),Constants.SELECT_AUDIE_REQ_CODE)
        }
        // start the new activity

    }


    fun showItemsInDialogue(view:View,soundItem:VoiceItemModel){


        val repo: Repository by inject()

        DialogueUtil.showDialogue(this@MainActivity,view,soundItem)
    }


   var soundItem:VoiceItemModel?=null

    fun dialogueItemClick(pos:Int,soundItem:VoiceItemModel){
        this.soundItem=soundItem
       // Toast.makeText(applicationContext," pos "+pos,Toast.LENGTH_SHORT).show()

       // val repo: Repository by inject()

        if(pos==0){
            shareChooser(soundItem);

        }else
            if(pos==1){

                try{
                    Thread(Runnable {
                        var list=ArrayList<VoiceItemModel>()

                        list=TinyDB.getInstance(applicationContext).getListObject(Constants.SPKEY_SAVED_RECORDING,VoiceItemModel::class.java)

                        list.add(soundItem)

                        TinyDB.getInstance(applicationContext).putListObject(Constants.SPKEY_SAVED_RECORDING,list)

                        if(applicationContext!=null||!isFinishing)
                            runOnUiThread { Toast.makeText(applicationContext,"Recording saved",Toast.LENGTH_SHORT).show() }

                    }).start()
                }catch (e:java.lang.Exception){
                    e.printStackTrace()
                }

            }
            else
                if(pos==2){
                    writePermissionNoti(this)

                }
                else
                    if(pos==3){
                        if(checkPermissionsStorageWrit()){
                            writePermissionNoti(this)

                          //  showDailogue("The notification ringtone "+resources.getString(R.string.app_name)+" has been created.\n\nPlease, set it on next screen")
                        }
                    }else
                        if(pos==4){


                        }

    }


    fun showDailogue(msg:String){
        var dial:AlertDialog.Builder=AlertDialog.Builder(MainActivity@this)

        dial.setMessage(msg)
        dial.setPositiveButton("ok",DialogInterface.OnClickListener({
            v,c->
            startActivityForResult( Intent(android.provider.Settings.ACTION_SOUND_SETTINGS), 0);
        }))

        dial.show()
    }



    fun checkPermissionsStorageWrit():Boolean{
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE),103)
            return false
        }else{
            return  true
        }

    }





    fun generateEffectsFormSoundFile(){

        var int:Intent=Intent(this,RecoedingActivity::class.java)
      int.putExtra(Constants.textToVoiceEffectIntentKey,"")
        TinyDB.getInstance(applicationContext).putInt(Constants.CURRENT_SOUND_CATEGSp,
                Constants.SOUND_FRM_TEXT)

        var event=MessageEvent()
    EventBus.getDefault().post(event)

     //   int.putExtra(Constants.PROCESS_SOUND_PATH,"msWord")

      //ad  startActivity(int)





    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        try{
            if(grantResults!=null&&grantResults.size>0){
                if(requestCode==103) {


                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        writePermission(this)

                    }

                }
                if(requestCode==1004) {


                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        writePermissionNoti(this)

                    }

                }

                if (requestCode == CODE_WRITE_SETTINGS_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utils.setRingtone(this,soundItem!!.filePath)
                }

                if(requestCode==Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
                    openAudioGallery()
                }

            }
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }


    }
val  CODE_WRITE_SETTINGS_PERMISSION=150

    fun writePermissionNoti(context: Activity) {
        val permission: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context)
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED
        }
        if (permission) {
            Utils.setRingtone(this,soundItem!!.filePath)
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + context.packageName)
                context.startActivityForResult(intent, CODE_WRITE_SETTINGS_PERMISSION)
            } else {
                ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.WRITE_SETTINGS), CODE_WRITE_SETTINGS_PERMISSION)
            }
        }
    }
    fun writePermission(context: Activity) {
        val permission: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context)
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED
        }
        if (permission) {
            Utils.setRingtone(this,soundItem!!.filePath)
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + context.packageName)
                context.startActivityForResult(intent, CODE_WRITE_SETTINGS_PERMISSION)
            } else {
                ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.WRITE_SETTINGS), CODE_WRITE_SETTINGS_PERMISSION)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 102) {

            }
            if (requestCode == Constants.SELECT_AUDIE_REQ_CODE) {
                var uri:Uri=data!!.data

                try {
                    var  uriString: String = uri.toString();
                    var  myFile:File =  File(uriString);
                    //    String path = myFile.getAbsolutePath();
                    var displayName: String ?= null;
                    var path2: String = FileUtils.getPath(this,uri)
                    importedf =  File(path2);
                    var fileSizeInBytes:Long = importedf!!.length();
                    var fileSizeInKB:Long = fileSizeInBytes / 1024;
                    var fileSizeInMB:Long = fileSizeInKB / 1024;
                    if (fileSizeInMB > 8) {
                        Toast.makeText(this, "sorry file size is large", Toast.LENGTH_SHORT).show();

                        isPicSelect=false

                    } else {
                        soundUrl = path2;
                        isPicSelect = true;

                        //  readBytesFromFile(soundUrl)
                        Constants.importedSoundPath=soundUrl
                        TinyDB.getInstance(applicationContext).putInt(Constants.CURRENT_SOUND_CATEGSp,
                                Constants.SOUND_FRM_iMPORT)

                        var event=MessageEvent()
                        EventBus.getDefault().post(event)

                        //startSoundEffectActivity(soundUrl)


                    }
                } catch ( e:Exception) {
                    //handle exception
                    isPicSelect=false

                    Toast.makeText(this, "Unable to process,try again", Toast.LENGTH_SHORT).show();
                }


            }



            if(requestCode==Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
                openAudioGallery()
            }
        }


        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION && Settings.System.canWrite(this)){
            Log.d("TAG", "MainActivity.CODE_WRITE_SETTINGS_PERMISSION success");
            Utils.setRingtone(this,soundItem!!.filePath)
        }

        if(requestCode==Constants.SELECT_AUDIE_REQ_CODE){
          //  startRxSoundProcess()
        }

    }







    fun shareChooser(soundItem:VoiceItemModel) {



        val f1 = File(getFilesDir(), soundItem.soundName+".mp3")
      //  Util.saveToFile(f1, trace)

        FileUtils.copyFile(soundItem.filePath,f1)

        val shareIntent1 = Intent()
        shareIntent1.action = Intent.ACTION_SEND
        shareIntent1.putExtra(Intent.EXTRA_STREAM, FileProvider
                .getUriForFile(this, "myAuth", f1))
        shareIntent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent1.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        shareIntent1.type = "audio/mp3"

        startActivity(Intent.createChooser(shareIntent1, null))
    }

    fun startRxSoundProcess(){
        var process: ProcessSoundRx
        process= ProcessSoundRx()




        process.processSoundArray(applicationContext, ProcessSoundLogic.SOUND_FRM_RECORD,getSoundObjArray())


    }

    fun getSoundObjArray(): Array<ProcessSoundObj?> {
        val processSoundObjsArrray = arrayOfNulls<ProcessSoundObj>(Constants.soundBitRateArray.size)

        for (i in 0 until Constants.soundBitRateArray.size) {

            processSoundObjsArrray[i] = ProcessSoundObj(0,
                    Constants.soundFrequencyArray[i],
                    Constants.soundBitRateArray[i])
        }

        return processSoundObjsArrray
    }




}
