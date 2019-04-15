package com.webheavens.manishkprmaterialtabs.soundTouch

import android.content.Context
import android.media.AudioFormat
import android.os.AsyncTask
import com.webheavens.manishkprmaterialtabs.repository.Repository
import com.webheavens.manishkprmaterialtabs.utils.AminUtils
import com.webheavens.manishkprmaterialtabs.utils.Constants
import io.github.junyuecao.soundtouch.SoundTouch
import kotlinx.android.synthetic.main.recording_layout.*
import org.koin.android.ext.android.inject
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.ArrayList
import com.webheavens.manishkprmaterialtabs.events.MessageEvent
import com.webheavens.manishkprmaterialtabs.events.ProcessSoundClick
import com.webheavens.manishkprmaterialtabs.model.ProcessSoundObj
import com.webheavens.manishkprmaterialtabs.model.VoiceItemModel
import com.webheavens.manishkprmaterialtabs.soundTouch.util.FileUtilsSoundT
import org.greenrobot.eventbus.EventBus



class ProcessSoundLogic() {
companion object {
    val SOUND_FRM_RECORD=1
    val SOUND_FRM_iMPORT=2
    val SOUND_FRM_TEXT=3
}

    var soundevent:ProcessSoundClick?=null
    var soundCategory=0;



    private var mSoundTouch : SoundTouch? = null
    var countSound:Int=0;

    private var mWavOutputStream: FileOutputStream? = null
    private val BUFFER_SIZE: Int = 4096
    private var mTempBuffer : ByteArray = ByteArray(BUFFER_SIZE)

    var soundFileList: ArrayList<File> = ArrayList();
var context:Context? = null
    var soundByteArray: ByteArray? = null

    var repo:Repository?=null


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

    var  importedSoundpath=""

    fun initSound(contx:Context,soundCategory: Int){
context=contx

        repo=Repository(contx)

        when(soundCategory) {
            SOUND_FRM_RECORD -> {
                var path = getTempFile()
                soundType = SounType.SOUNDFROMRECORD
                importedSoundpath = path.path

                readBytesFromImportedFile(importedSoundpath)

            }
            SOUND_FRM_TEXT -> {
                var path = FileUtilsSoundT(this!!.context!!).getTexttoSoundFilePath(this!!.context!!)
                soundType = SounType.SOUNDFROMTEXT
                importedSoundpath = path.path


                readBytesFromImportedFile(importedSoundpath)

            }
            SOUND_FRM_iMPORT -> {
                soundType = SounType.SOUNDFROMFILE
                Constants.importedSoundPath = importedSoundpath

                readBytesFromImportedFile(importedSoundpath)


            }

        }

    }

fun saveItem(){
   var list:ArrayList<VoiceItemModel>
    var list2:ArrayList<VoiceItemModel>
    list2= ArrayList()

    list= repo!!.getSavedVoiceItemList() as ArrayList<VoiceItemModel>
    for(i in list){
        if(i.soundName.equals(soundevent!!.voiceItemModel!!.soundName)){
            i.isProcessed=true
        }

        list2.add(i)
    }
  //  repo!!.saveTempSoundEffctList(list2)

}

    fun processSoundRx(sounObj:ProcessSoundObj){

    }



    inner class ProcessSoundTask() : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {



            when(soundType){
                RecoedingActivity.SounType.SOUNDFROMRECORD ->{
                    changeVoice(soundByteArray!!, soundByteArray?.size!!, Constants.soundFrequencyArray[countSound], Constants.soundBitRateArray[countSound])

                }
                RecoedingActivity.SounType.SOUNDFROMFILE ->{
                    changeVoiceImportedSound(soundByteArray!!, soundByteArray?.size!!, Constants.frequencyArrayImport[countSound], Constants.soundBitRateArrayImported[countSound])

                }
                RecoedingActivity.SounType.SOUNDFROMTEXT -> {
                    changeVoiceTextToVoice(soundByteArray!!, soundByteArray?.size!!, Constants.frequencyArrayText[countSound], Constants.soundBitRateArrayText[countSound])

                }
            }
saveItem()

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

                    countSound++
                    //processSound()
var event=MessageEvent()
      event.pos=countSound
            event.isProcessSound=true
            EventBus.getDefault().post(event)

            // AminUtils.zoomInOut(applicationContext,effectsImg)


            /*   AminUtils.spalshAnimation(applicationContext,tvEffectsName,object: AminUtils.AnimationCalllback {
                   override fun onAnimationEnds() {

                   }

               })*/


        }





    }



    public  fun changeVoiceTextToVoice(data:ByteArray,size:Int,pitch:Double,rate:Double){

       // Log.d(TAG, "onVoice: $data, Size: $size")
        //init
        mSoundTouch = SoundTouch()
        mSoundTouch?.setChannels(1)
        mSoundTouch?.setSampleRate(19000)
        mWavOutputStream = getTestWavOutput2()
        writeWavHeader(mWavOutputStream!!,
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
                mWavOutputStream?.write(mTempBuffer, 0, bufferSize)
            }
        } while (bufferSize != 0)


        // updateWavHeader2(getTempFile2())

        updateWavHeaderLoop()
    }
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




    public  fun changeVoiceImportedSound(data:ByteArray,size:Int,pitch:Double,rate:Double){
       // Log.d(TAG, "onVoice: $data, Size: $size")
        //init
        mSoundTouch = SoundTouch()
        mSoundTouch?.setChannels(1)
        mSoundTouch?.setSampleRate(48000)
        mWavOutputStream = getTestWavOutput2()
        writeWavHeader(mWavOutputStream!!,
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
                mWavOutputStream?.write(mTempBuffer, 0, bufferSize)
            }
        } while (bufferSize != 0)


        // updateWavHeader2(getTempFile2())

        updateWavHeaderLoop()
    }
    public fun updateWavHeaderLoop(){
        for(file in soundFileList){
            updateWavHeader2(file)
        }
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

    public  fun changeVoice(data:ByteArray,size:Int,pitch:Double,rate:Double){

        //init
        mSoundTouch = SoundTouch()
        mSoundTouch?.setChannels(1)
        mSoundTouch?.setSampleRate(VoiceRecorder.SAMPLE_RATE)
        mWavOutputStream = getTestWavOutput2()
        writeWavHeader(mWavOutputStream!!,
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
                mWavOutputStream?.write(mTempBuffer, 0, bufferSize)
            }
        } while (bufferSize != 0)


        // updateWavHeader2(getTempFile2())

        updateWavHeaderLoop()
    }


    fun getfilepath(itmNmbr:Int):File{



        var  soundFile:File= File(context!!.getExternalFilesDir(null),repo!!.voicenames[itmNmbr]+".wav")
        soundFileList.add(soundFile)
        return soundFile;

    }


    fun readBytesFromFile(){
        soundByteArray=readUsingRandomAccesFile(getTempFile().path);
    }
    fun readUsingRandomAccesFile(path:String):ByteArray{
        val randomAccessFile = RandomAccessFile(path, "r")

        var document= ByteArray(randomAccessFile.length().toInt())

// Line changed
        randomAccessFile.readFully(document)

        return  document
    }


    fun readBytesFromImportedFile(filePath:String){
        soundByteArray=readUsingRandomAccesFile(filePath);
    }



    private fun getTempFileTextToVoice() = File(context!!.getExternalFilesDir(null), Constants.textCreatedSoundN)
    private fun getTempFileImported() = File(context!!.getExternalFilesDir(null), Constants.recordedSoundName)


    private fun getTempFile() = File(context!!.getExternalFilesDir(null), Constants.recordedSoundName)



    fun processSound(processSoundObj: ProcessSoundObj,coun:Int) {
        lastSoundType = soundType
countSound=coun

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
        changeVoice(soundByteArray!!, soundByteArray!!.size, processSoundObj.pitch, processSoundObj.rate)


        when (soundType) {
            RecoedingActivity.SounType.SOUNDFROMFILE -> {
                changeVoice(soundByteArray!!, soundByteArray!!.size, processSoundObj.pitch, processSoundObj.rate)

            }
            RecoedingActivity.SounType.SOUNDFROMFILE -> {
                changeVoiceImportedSound(soundByteArray!!, soundByteArray?.size!!,
                        Constants.frequencyArrayImport[countSound], Constants.soundBitRateArrayImported[countSound])

            }
            RecoedingActivity.SounType.SOUNDFROMTEXT -> {
                changeVoiceTextToVoice(soundByteArray!!, soundByteArray?.size!!, Constants.frequencyArrayText[countSound], Constants.soundBitRateArrayText[countSound])

            }
        }
    }

    fun getEchooSound()=File(context!!.getExternalFilesDir(null),repo!!.voicenames[1]+".wav")


    fun getReverseSound()=File(context!!.getExternalFilesDir(null),repo!!.voicenames[7]+".wav")



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


            randomAccessFile!!.write(bytesTemp)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }






    fun soundProcessEnd(){
       // val repo: Repository by inject()

        repo!!.saveTempSoundEffctList()


    }
}