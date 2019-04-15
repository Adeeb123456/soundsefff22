package io.github.junyuecao.androidsoundeffect

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioFormat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.webheavens.manishkprmaterialtabs.R
import com.webheavens.manishkprmaterialtabs.soundTouch.VoiceRecorder
import io.github.junyuecao.soundtouch.SoundTouch

import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.media.MediaRecorder
import android.os.*
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.narayanacharya.waveview.WaveView
import com.skyfishjy.library.RippleBackground
import com.webheavens.manishkprmaterialtabs.repository.Repository
import com.webheavens.manishkprmaterialtabs.soundTouch.util.FileUtilsSoundT
import com.webheavens.manishkprmaterialtabs.utils.AminUtils
import com.webheavens.manishkprmaterialtabs.utils.Constants
import com.webheavens.manishkprmaterialtabs.utils.TinyDB
import info.kimjihyok.ripplelibrary.VoiceRippleView
import kotlinx.android.synthetic.main.recording_layout.*
import org.koin.android.ext.android.inject
import java.util.*


class RecoedingActivity : AppCompatActivity(), VoiceRecorder.Callback,NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
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

        val TAG = "MainActivity1"
    private var mRecorder : VoiceRecorder? = null
    private var mSoundTouch : SoundTouch? = null
    private var mIsRecording = false
    private var mTestWavOutput: FileOutputStream? = null
    private val BUFFER_SIZE: Int = 4096
    private var mTempBuffer : ByteArray = ByteArray(BUFFER_SIZE)

    private var mPitch: Double = 1.0;
    private var mRate: Double = 1.0;
    var byteArrayInputFile: ByteArray? = null
    var soundByteArray: ByteArray? = null

    var soundFileList: ArrayList<File> = ArrayList();

    enum class SounType {
        SOUNDFROMFILE,
        SOUNDFROMTEXT,
        SOUNDFROMRECORD,
        ROBOTSOUND,
        ECHOSOUND
    }

    enum class LastSounType {
        SOUNDFROMFILE,
        SOUNDFROMTEXT,
        SOUNDFROMRECORD,
        ROBOTSOUND,
        ECHOSOUND
    }


    var soundType:SounType=SounType.SOUNDFROMRECORD;

    var lastSoundType:SounType=SounType.SOUNDFROMRECORD;


    override fun onVoiceStart() {
        mSoundTouch = SoundTouch()
        mSoundTouch?.setChannels(1)
        mSoundTouch?.setSampleRate(VoiceRecorder.SAMPLE_RATE)
        mTestWavOutput = getTestWavOutput()
        writeWavHeader(mTestWavOutput!!,
                AudioFormat.CHANNEL_IN_MONO,
                VoiceRecorder.SAMPLE_RATE,
                AudioFormat.ENCODING_PCM_16BIT);

      //  initOrGetFileOutputStreemList()


    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else{
            finish()
        }
    }

    var count:Int =0;







    override fun onVoice(data: ByteArray?, size: Int) {
        Log.d(TAG, "onVoice: $data, Size: $size")
      mSoundTouch?.setRate(mRate)
        mSoundTouch?.setPitch(mPitch)
        mSoundTouch?.putSamples(data, size)
        var bufferSize = 0
        do {
            bufferSize = mSoundTouch!!.receiveSamples(mTempBuffer, BUFFER_SIZE)
            if (bufferSize > 0) {
                mTestWavOutput?.write(mTempBuffer, 0, bufferSize)
            }
        } while (bufferSize != 0)


    }





fun OnVoiceEndDummy(){
    mSoundTouch?.release()
    try {
        mTestWavOutput?.close()
        mTestWavOutput = null
    } catch (e: IOException) {
        e.printStackTrace()
    }
    updateWavHeader(getTempFile())

    showProgressBar()


    readBytesFromFile()

   // processSound()

    soundProcessEnd()
    }


    override fun onVoiceEnd() {

        if(!isDummyStopRecording){
            recordingTxt.text=" Tab to start Recording"
            mSoundTouch?.release()
            try {
                mTestWavOutput?.close()
                mTestWavOutput = null
            } catch (e: IOException) {
                e.printStackTrace()
            }
            updateWavHeader(getTempFile())

            showProgressBar()


            readBytesFromFile()

          //  processSound()

            soundProcessEnd()
        }



    }




    fun showProgressBar(){
        runOnUiThread({
            hideRcordingBtns()
            progressContainer0.visibility=View.VISIBLE
        })

    }


    fun hideProgressBar(){
        runOnUiThread({
            showRcordingBtns()
            progressContainer0.visibility=View.GONE
        })



    }

fun hideRcordingBtns(){
    runOnUiThread({
        recording_container.visibility=View.GONE
    })

}

    fun showRcordingBtns(){
        runOnUiThread({
            recording_container.visibility=View.VISIBLE
        })

    }




    fun releaseSoundTouch(){
        for(i in 1..4){
           soundtList?.get(i).release()
            try {
                fileOutList.get(i)?.close()
              //  mTestWavOutput = null
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermissions():Boolean{
      if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
          requestPermissions( arrayOf(RECORD_AUDIO),100)

          return false;
      }else{
          return true;
      }

    }


    fun checkPermissionsStorage(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),102)
        }

    }

    fun checkPermissionsStorageWrit(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),103)
        }

    }


var voice_ripple_view: VoiceRippleView? = null
    fun initVoiceRipple(){

// set view related settings for ripple view
        voice_ripple_view?.setRippleColor(ContextCompat.getColor(this, R.color.colorPrimary))
        voice_ripple_view?.setRippleSampleRate(info.kimjihyok.ripplelibrary.Rate.LOW)
        voice_ripple_view?.setRippleDecayRate(info.kimjihyok.ripplelibrary.Rate.HIGH)
        voice_ripple_view?.setBackgroundRippleRatio(1.4)

// set recorder related settings for ripple view
        voice_ripple_view?.setMediaRecorder(MediaRecorder())
        voice_ripple_view?.setOutputFile(getTempFile().absolutePath)
        voice_ripple_view?.setAudioSource(MediaRecorder.AudioSource.MIC)
        voice_ripple_view?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        voice_ripple_view?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        // set inner icon for record and recording
        voice_ripple_view!!.setRecordDrawable(ContextCompat.getDrawable(this, R.drawable.ic_record), ContextCompat.getDrawable(this, R.drawable.ic_record));
        voice_ripple_view!!.setIconSize(30);

    }



    fun initSounRecorder(){
        mRecorder = VoiceRecorder(this)

    }

    fun startRecordSoundAd(){
        recordingTxt.text="Recording.."
        //recordSound.setImageResource(R.drawable.ic_record_press)
        mRecorder?.start()


    }


    fun stopRecordSoundAd(){

        //recordSound.setImageResource(R.drawable.ic_record_press)


        mRecorder?.stop()


    }


    var  importedSoundpath=""
    val repo:Repository by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording_main2)
      //  checkPermissionsStorageWrit()
        //checkPermissionsStorage()
       // checkPermissions()
        // initVoiceRipple()
initRippleBg()
initViews()

        // Example of a call to a native method


        Handler().postDelayed({
            if(checkPermissions()){
                initSounRecorder()
                // mIsRecording=true;
                // startRecordSoundAd()
            }
        },800)



        if(intent!=null){
            try {

                        soundType=SounType.SOUNDFROMRECORD
Constants.CURRENT_SOUND_CATEG=Constants.SOUND_FRM_RECORD

                TinyDB.getInstance(applicationContext).putInt(Constants.CURRENT_SOUND_CATEGSp,
                        Constants.SOUND_FRM_RECORD)
                hideProgressBar()
                showRcordingBtns()

            }catch (e:Exception){
                hideProgressBar()
                showRcordingBtns()
                e.printStackTrace()
            }


        }





        recordSound.setOnClickListener {

            mIsRecording = !mIsRecording
            if (mIsRecording) {
                rippleBackground!!.startRippleAnimation();
                startRecordSoundAd()
                startTimer()
            startWave()
            } else {

                if(isDummyStopRecording){
                    isDummyStopRecording=false
                    OnVoiceEndDummy()
                }

                rippleBackground!!.stopRippleAnimation();
                stopRecordSoundAd()
                pauseTimer()
                hideWav()
            }


          /*  mIsRecording = !mIsRecording
            if (mIsRecording) {
               // start.text = "Stop"
                mRecorder?.start()
            } else {
               // start.text = "Start"
                mRecorder?.stop()
            }*/

        }


           // readBytesFromFile()

          //  processSound()




    }



    var isDummyStopRecording=false;

    fun stopRecordingDummy(){
        isDummyStopRecording=true;
      // mIsRecording=false
       // rippleBackground!!.stopRippleAnimation();
        stopRecordSoundAd()
      //  pauseTimer()
       // hideWav()
    }


    private fun getTestWavOutput(): FileOutputStream? {
        val s = getTempFile()

        try {
            val os = FileOutputStream(s)
            return os
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(grantResults!=null&&grantResults.size>0){
            if(requestCode==100)
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                initSounRecorder()
               // startRecordSoundAd()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mIsRecording = false
  //  start.text = "Start"
        mRecorder?.stop()
    }





    var soundtList: ArrayList<SoundTouch> = ArrayList();
    var fileOutList: ArrayList<FileOutputStream> = ArrayList();










    private fun getTempFileTextToVoice() = File(getExternalFilesDir(null), Constants.textCreatedSoundN)
    private fun getTempFileImported() = File(getExternalFilesDir(null), Constants.recordedSoundName)


    private fun getTempFile() = File(getExternalFilesDir(null), Constants.recordedSoundName)

    /**
     * Writes the proper 44-byte RIFF/WAVE header to/for the given stream
     * Two size fields are left empty/null since we do not yet know the final stream size

     * @param out         The stream to write the header to
     * *
     * @param channelMask An AudioFormat.CHANNEL_* mask
     * *
     * @param sampleRate  The sample rate in hertz
     * *
     * @param encoding    An AudioFormat.ENCODING_PCM_* value
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeWavHeader(out: OutputStream, channelMask: Int, sampleRate: Int, encoding: Int) {
        val channels: Short
        when (channelMask) {
            AudioFormat.CHANNEL_IN_MONO -> channels = 1
            AudioFormat.CHANNEL_IN_STEREO -> channels = 2
            else -> throw IllegalArgumentException("Unacceptable channel mask")
        }

        val bitDepth: Short
        when (encoding) {
            AudioFormat.ENCODING_PCM_8BIT -> bitDepth = 8
            AudioFormat.ENCODING_PCM_16BIT -> bitDepth = 16
            AudioFormat.ENCODING_PCM_FLOAT -> bitDepth = 32
            else -> throw IllegalArgumentException("Unacceptable encoding")
        }

        writeWavHeader(out, channels, sampleRate, bitDepth)
    }

    /**
     * Writes the proper 44-byte RIFF/WAVE header to/for the given stream
     * Two size fields are left empty/null since we do not yet know the final stream size

     * @param out        The stream to write the header to
     * *
     * @param channels   The number of channels
     * *
     * @param sampleRate The sample rate in hertz
     * *
     * @param bitDepth   The bit depth
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeWavHeader(out: OutputStream, channels: Short, sampleRate: Int, bitDepth: Short) {
        // Convert the multi-byte integers to raw bytes in little endian format as required by the spec
        val littleBytes = ByteBuffer
                .allocate(14)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(channels)
                .putInt(sampleRate)
                .putInt(sampleRate * channels.toInt() * (bitDepth / 8))
                .putShort((channels * (bitDepth / 8)).toShort())
                .putShort(bitDepth)
                .array()

        // Not necessarily the best, but it's very easy to visualize this way
        out.write(byteArrayOf(
                // RIFF header
                'R'.toByte(), 'I'.toByte(), 'F'.toByte(), 'F'.toByte(), // ChunkID
                0, 0, 0, 0, // ChunkSize (must be updated later)
                'W'.toByte(), 'A'.toByte(), 'V'.toByte(), 'E'.toByte(), // Format
                // fmt subchunk
                'f'.toByte(), 'm'.toByte(), 't'.toByte(), ' '.toByte(), // Subchunk1ID
                16, 0, 0, 0, // Subchunk1Size
                1, 0, // AudioFormat
                littleBytes[0], littleBytes[1], // NumChannels
                littleBytes[2], littleBytes[3], littleBytes[4], littleBytes[5], // SampleRate
                littleBytes[6], littleBytes[7], littleBytes[8], littleBytes[9], // ByteRate
                littleBytes[10], littleBytes[11], // BlockAlign
                littleBytes[12], littleBytes[13], // BitsPerSample

               /* littleBytes[13], littleBytes[12], // NumChannels
                littleBytes[11], littleBytes[10], littleBytes[9], littleBytes[8], // SampleRate
                littleBytes[7], littleBytes[6], littleBytes[5], littleBytes[4], // ByteRate
                littleBytes[3], littleBytes[2], // BlockAlign
                littleBytes[1], littleBytes[0], // BitsPerSample*/


                // data subchunk
                'd'.toByte(), 'a'.toByte(), 't'.toByte(), 'a'.toByte(), // Subchunk2ID
                0, 0, 0, 0)// Subchunk2Size (must be updated later)
        )
    }



    /*ositions	Sample Value	Description
1 - 4	"RIFF"	Marks the file as a riff file. Characters are each 1 byte long.
5 - 8	File size (integer)	Size of the overall file - 8 bytes, in bytes (32-bit integer). Typically, you'd fill this in after creation.
9 -12	"WAVE"	File Type Header. For our purposes, it always equals "WAVE".
13-16	"fmt "	Format chunk marker. Includes trailing null
17-20	16	Length of format data as listed above
21-22	1	Type of format (1 is PCM) - 2 byte integer
23-24	2	Number of Channels - 2 byte integer
25-28	44100	Sample Rate - 32 byte integer. Common values are 44100 (CD), 48000 (DAT). Sample Rate = Number of Samples per second, or Hertz.
29-32	176400	(Sample Rate * BitsPerSample * Channels) / 8.
33-34	4	(BitsPerSample * Channels) / 8.1 - 8 bit mono2 - 8 bit stereo/16 bit mono4 - 16 bit stereo
35-36	16	Bits per sample
37-40	"data"	"data" chunk header. Marks the beginning of the data section.
41-44	File size (data)	Size of the data section.
Sample values are given above for a 16-bit stereo source.
*/



    /**
     * Updates the given wav file's header to include the final chunk sizes

     * @param wav The wav file to update
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun updateWavHeader(wav: File) {
        val sizes = ByteBuffer
                .allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                // There are probably a bunch of different/better ways to calculate
                // these two given your circumstances. Cast should be safe since if the WAV is
                // > 4 GB we've already made a terrible mistake.
                .putInt((wav.length() - 8).toInt()) // ChunkSize
                .putInt((wav.length() - 44).toInt()) // Subchunk2Size
                .array()

        var accessWave: RandomAccessFile? = null

        try {
            accessWave = RandomAccessFile(wav, "rw")
          //  accessWave.re
            // ChunkSize
            accessWave.seek(4)
            accessWave.write(sizes, 0, 4)

            // Subchunk2Size
            accessWave.seek(40)
            accessWave.write(sizes, 4, 4)



        } catch (ex: IOException) {
            // Rethrow but we still close accessWave in our finally
            throw ex
        } finally {
            if (accessWave != null) {
                try {
                    accessWave.close()
                } catch (ex: IOException) {
                    //
                }

            }
        }
    }


    fun funEchoVoice(file:File) {
        //Clone original Bytes

        val bytesTemp = soundByteArray
        val temp = bytesTemp!!.clone()
        var randomAccessFile: RandomAccessFile? = null
        try {

            randomAccessFile= RandomAccessFile(file,"rw")



            //seek to skip 44 bytes
            randomAccessFile!!.seek(44)
            //Echo
            val N = 44100 / 8
            for (n in N + 1 until bytesTemp.size) {
                bytesTemp[n] = (temp[n] + .5 * temp[n - N]).toByte()
            }
            randomAccessFile.write(bytesTemp)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    fun reverseVoice(file:File) {
        //Clone original Bytes

        val bytesTemp = soundByteArray
        val temp = bytesTemp!!.clone()
        var randomAccessFile: RandomAccessFile? = null
        try {

            /*    when(soundType){
                    SounType.SOUNDFROMTEXT ->{
                        randomAccessFile = RandomAccessFile(getTempFileTextToVoice(), "rw")
                    }
                    SounType.SOUNDFROMRECORD ->{
                        randomAccessFile = RandomAccessFile(getTempFile(), "rw")
                    }
                    SounType.SOUNDFROMFILE->{
                        randomAccessFile= RandomAccessFile(getRobotSoundFilePath(),"rw")
                    }

                    SounType.ECHOSOUND->{
                        randomAccessFile= RandomAccessFile(getEchooSound(),"rw")
                    }

                }*/

            randomAccessFile= RandomAccessFile(file,"rw")



            //seek to skip 44 bytes
            randomAccessFile!!.seek(44)
            //Echo
            val N = 44100 / 8
            /*for (n in N + 1 until bytesTemp.size) {
                bytesTemp[n] = (temp[n] + .5 * temp[n - N]).toByte()
            }*/
            var countReverse=bytesTemp.size-1
            for(n in N+1 until bytesTemp.size){

                bytesTemp[n]=temp[countReverse]
                countReverse--

            }


            randomAccessFile.write(bytesTemp)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }




var echoedSoundSounter=0
    var echoFileList= listOf<File>()

    fun processEchoSoundTask(){

    }


    class EchoSounTask: AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {



            return null
        }

    }

    fun reEchoSound(){
        val bytesTemp = soundByteArray
        val temp = bytesTemp!!.clone()
        var randomAccessFile: RandomAccessFile? = null
        try {

            when(soundType){
                SounType.SOUNDFROMTEXT ->{
                    randomAccessFile = RandomAccessFile(getTempFileTextToVoice(), "rw")
                }
                SounType.SOUNDFROMRECORD ->{
                    randomAccessFile = RandomAccessFile(getTempFile(), "rw")
                }

            }



            //seek to skip 44 bytes
            randomAccessFile!!.seek(44)
            //Echo
            val N = 44100 / 8
            for (n in N + 1 until bytesTemp.size) {
                bytesTemp[n] = (temp[n] + .5 * temp[n - N]).toByte()
            }




            randomAccessFile.write(bytesTemp)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun readUsingRandomAccesFile(path:String):ByteArray{
        val randomAccessFile = RandomAccessFile(path, "r")

        var document= ByteArray(randomAccessFile.length().toInt())

// Line changed
        randomAccessFile.readFully(document)

        return  document
    }


    fun getfilepath(itmNmbr:Int):File{



      var  soundFile:File= File(getExternalFilesDir(null),repo.voicenames[itmNmbr]+".wav")
        soundFileList.add(soundFile)
        return soundFile;

    }



    private fun getTestWavOutput2(): FileOutputStream? {
        val s = getfilepath(countSound)

        try {
            val os = FileOutputStream(s)
            return os
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    private fun getTempFile2() = File(getExternalFilesDir(null), "mysoundad.wav")



    private fun getRobotSoundFilePath()=File(getExternalFilesDir(null),repo.voicenames[4]+".wav")


    fun getEchooSound()=File(getExternalFilesDir(null),repo.voicenames[1]+".wav")


    fun getReverseSound()=File(getExternalFilesDir(null),repo.voicenames[7]+".wav")











    public fun updateWavHeaderLoop(){
        for(file in soundFileList){
            updateWavHeader2(file)
        }
    }




    public  fun changeVoiceTextToVoice(data:ByteArray,size:Int,pitch:Double,rate:Double){
        Log.d(TAG, "onVoice: $data, Size: $size")
        //init
        mSoundTouch = SoundTouch()
        mSoundTouch?.setChannels(1)
        mSoundTouch?.setSampleRate(19000)
        mTestWavOutput = getTestWavOutput2()
        writeWavHeader(mTestWavOutput!!,
                AudioFormat.CHANNEL_IN_MONO,
                21000,
                AudioFormat.ENCODING_PCM_16BIT);



        //2nd
        mSoundTouch?.setRate(pitch)
        mSoundTouch?.setPitch(rate)
        mSoundTouch?.putSamples(data, size)
        var bufferSize = 0
        do {
            bufferSize = mSoundTouch!!.receiveSamples(mTempBuffer, BUFFER_SIZE)
            if (bufferSize > 0) {
                mTestWavOutput?.write(mTempBuffer, 0, bufferSize)
            }
        } while (bufferSize != 0)


        // updateWavHeader2(getTempFile2())

        updateWavHeaderLoop()
    }


    public  fun changeVoiceImportedSound(data:ByteArray,size:Int,pitch:Double,rate:Double){
        Log.d(TAG, "onVoice: $data, Size: $size")
        //init
        mSoundTouch = SoundTouch()
        mSoundTouch?.setChannels(1)
        mSoundTouch?.setSampleRate(48000)
        mTestWavOutput = getTestWavOutput2()
        writeWavHeader(mTestWavOutput!!,
                AudioFormat.CHANNEL_IN_MONO,
                48000,
                AudioFormat.ENCODING_PCM_16BIT);



        //2nd
        mSoundTouch?.setRate(pitch)
        mSoundTouch?.setPitch(rate)
        mSoundTouch?.putSamples(data, size)
        var bufferSize = 0
        do {
            bufferSize = mSoundTouch!!.receiveSamples(mTempBuffer, BUFFER_SIZE)
            if (bufferSize > 0) {
                mTestWavOutput?.write(mTempBuffer, 0, bufferSize)
            }
        } while (bufferSize != 0)


        // updateWavHeader2(getTempFile2())

        updateWavHeaderLoop()
    }


  public  fun changeVoice(data:ByteArray,size:Int,pitch:Double,rate:Double){
        Log.d(TAG, "onVoice: $data, Size: $size")
      Log.d(TAG, "onVoice: $data, Size: $size")
      //init
      mSoundTouch = SoundTouch()
      mSoundTouch?.setChannels(1)
      mSoundTouch?.setSampleRate(VoiceRecorder.SAMPLE_RATE)
      mTestWavOutput = getTestWavOutput2()
      writeWavHeader(mTestWavOutput!!,
              AudioFormat.CHANNEL_IN_MONO,
              VoiceRecorder.SAMPLE_RATE,
              AudioFormat.ENCODING_PCM_16BIT);



      //2nd
      mSoundTouch?.setRate(pitch)
      mSoundTouch?.setPitch(rate)
      mSoundTouch?.putSamples(data, size)
      var bufferSize = 0
      do {
          bufferSize = mSoundTouch!!.receiveSamples(mTempBuffer, BUFFER_SIZE)
          if (bufferSize > 0) {
              mTestWavOutput?.write(mTempBuffer, 0, bufferSize)
          }
      } while (bufferSize != 0)


      // updateWavHeader2(getTempFile2())

      updateWavHeaderLoop()
    }


    @Throws(IOException::class)
    private fun updateWavHeader2(wav: File) {
        val sizes = ByteBuffer
                .allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                // There are probably a bunch of different/better ways to calculate
                // these two given your circumstances. Cast should be safe since if the WAV is
                // > 4 GB we've already made a terrible mistake.
                .putInt((wav.length() - 8).toInt()) // ChunkSize
                .putInt((wav.length() - 44).toInt()) // Subchunk2Size
                .array()

        var accessWave: RandomAccessFile? = null

        try {
            accessWave = RandomAccessFile(wav, "rw")
            //  accessWave.re
            // ChunkSize
            accessWave.seek(4)
            accessWave.write(sizes, 0, 4)

            // Subchunk2Size
            accessWave.seek(40)
            accessWave.write(sizes, 4, 4)
        } catch (ex: IOException) {
            // Rethrow but we still close accessWave in our finally
            throw ex
        } finally {
            if (accessWave != null) {
                try {
                    accessWave.close()
                } catch (ex: IOException) {
                    //
                }

            }
        }
    }


    var countSound:Int=0;
    fun processSound(){
lastSoundType=soundType



  /*  if(countSound==Constants.caveSoundPos){
        soundType=SounType.ECHOSOUND
    }
    else
        if(Constants.robotSoundPos==countSound) {
            soundType = SounType.ROBOTSOUND
        }
        else{
            soundType=lastSoundType
        }*/




        if(countSound<repo.voicenames!!.size){
            ProcessSoundTask().execute()

        }else{
            soundProcessEnd()
            funEchoVoice(getEchooSound())
            reverseVoice(getReverseSound())
        }
    }

    fun soundProcessEnd(){
        val repo:Repository by inject()

repo.saveTempSoundEffctList()

        hideProgressBar()
Constants.CURRENT_SOUND_CATEG=Constants.SOUND_FRM_RECORD

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        }else{
            finish()
        }
    }




    inner class ProcessSoundTask() : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {



            when(soundType){
                SounType.SOUNDFROMRECORD ->{
                    changeVoice(soundByteArray!!, soundByteArray?.size!!,Constants.soundFrequencyArray[countSound],Constants.soundBitRateArray[countSound])

                }
                SounType.SOUNDFROMFILE ->{
                    changeVoiceImportedSound(soundByteArray!!, soundByteArray?.size!!,Constants.frequencyArrayImport[countSound],Constants.soundBitRateArrayImported[countSound])

                }
                SounType.SOUNDFROMTEXT -> {
                    changeVoiceTextToVoice(soundByteArray!!, soundByteArray?.size!!,Constants.frequencyArrayText[countSound],Constants.soundBitRateArrayText[countSound])

                }
            }


            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            var effectName=repo.voicenames[countSound]
            var img=repo.thumbnails[countSound]
            //animateRightToRight(effectName,0)

            tvEffectsName.text="Processing  "+effectName
            effectsImg.setImageResource(img)
            AminUtils.spalshAnimation(applicationContext,effectsImg,object: AminUtils.AnimationCalllback {
                override fun onAnimationEnds() {
                    countSound++
                    processSound()
                }

            })
AminUtils.zoomInOut(applicationContext,tvEffectsName)
           // AminUtils.zoomInOut(applicationContext,effectsImg)


         /*   AminUtils.spalshAnimation(applicationContext,tvEffectsName,object: AminUtils.AnimationCalllback {
                override fun onAnimationEnds() {

                }

            })*/


        }




    }


    fun animateRightToRight(effectName:String,drawa:Int){
        tvEffectsName.text="Please wait Processing  "+effectName
        AminUtils.rightToLeftAnim(this,effectsImg,object: AminUtils.AnimationCalllback {
            override fun onAnimationEnds() {
            }

        })
    }

    fun readBytesFromFile(){
        soundByteArray=readUsingRandomAccesFile(getTempFile().path);
    }



    fun readBytesFromImportedFile(filePath:String){
        soundByteArray=readUsingRandomAccesFile(filePath);
    }

    var rippleBackground: RippleBackground?=null
    fun initRippleBg(){
        rippleBackground =findViewById(R.id.content);

    }

    fun startWave(){
        waveView!!.visibility=View.VISIBLE

        waveView!!.play()
    }


    fun hideWav(){
        waveView.pause()
        waveView!!.visibility=View.GONE
    }



    fun initWaterWaves(){
       var  sine:WaveView = findViewById(R.id.waveView);
        sine.setBackgroundColor(Color.GRAY);
        sine.setWaveColor(Color.WHITE);
        sine.setNumberOfWaves(3);
        sine.setFrequency(2.0f);
        sine.setAmplitude(5.0f);
        sine.setPhaseShift(-0.05f);
        sine.setDensity(5.0f);
       /* sine.setPrimaryLineWidth(3.0f);
        sine.setSecondaryLineWidth(1.0f)*/;
        sine.setWaveXAxisPositionMultiplier(0.5f)
    }




 /*
    class secondTask : TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                   long millis = System.currentTimeMillis() - starttime;
                   int seconds = (int) (millis / 1000);
                   int minutes = seconds / 60;
                   seconds     = seconds % 60;

                   text2.setText(String.format("%d:%02d", minutes, seconds));
                }
            });
        }
   };*/


    internal var starttime: Long = 0
    //this  posts a message to the main thread from our timertask
    //and updates the textfield
    internal val h = Handler(Handler.Callback {
        val millis = System.currentTimeMillis() - starttime
        var seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        seconds = seconds % 60


        false
    })
    //runs without timer be reposting self
    internal var h2 = Handler()
    internal var run: Runnable = object : Runnable {

        override fun run() {
            val millis = System.currentTimeMillis() - starttime
            var seconds = (millis / 1000).toInt()
            val minutes = seconds / 60
            seconds = seconds % 60

            //text3.text = String.format("%02d:%02d", minutes, seconds)

            timerTv.text=String.format("%02d:%02d:%02d",0,minutes,seconds)

            if(seconds>28){
                stopRecordingDummy()
            }

            h2.postDelayed(this, 500)
        }
    }


    internal var timer = Timer()

    //tells handler to send a message
    internal inner class firstTask : TimerTask() {

        override fun run() {
            h.sendEmptyMessage(0)
        }
    }

    //tells activity to run on ui thread
    internal inner class secondTask : TimerTask() {

        override fun run() {
            runOnUiThread {
                val millis = System.currentTimeMillis() - starttime
                var seconds = (millis / 1000).toInt()
                val minutes = seconds / 60
                seconds = seconds % 60


            }
        }
    }







    fun startTimer(){
        starttime = System.currentTimeMillis()
        timer = Timer()
        timer.schedule(firstTask(), 0, 500)
        timer.schedule(secondTask(), 0, 500)
        h2.postDelayed(run, 0)
    }



    fun pauseTimer(){

            timer.cancel()
            timer.purge()
            h2.removeCallbacks(run)


    }


    internal var toolbar: Toolbar? =null
    lateinit internal var fab: FloatingActionButton
    lateinit internal var tabs: TabLayout
    lateinit  internal var pager: ViewPager

    fun initViews() {
        toolbar = findViewById(R.id.toolbar)


        setSupportActionBar(toolbar)


        supportActionBar!!.setTitle("Record Voice Effects")
       /* val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()*/

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        // toolbar!!.navigationIcon=R.drawable.ic_navigation_drawer_icon2
        toolbar!!.setNavigationOnClickListener({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition()
            }else{
                finish()
            }
        })

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        }



        //initToolTipDialogue()





    }
}
