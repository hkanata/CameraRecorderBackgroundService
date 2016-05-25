package br.com.opba.br.com.opba.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RecorderOpbaCamService extends Service implements TextureView.SurfaceTextureListener, MediaRecorder.OnInfoListener {

    //Log Tag
    private static final String TAG = "LOG_OP";

    //Quality recorders
    private static final Boolean BEST_QUALITY = Boolean.FALSE;

    //Arquive Parameters
    private static final String FOLDER_NAME       = "OpMovies";
    private static final String ARQUIVE_NAME      = "rec_";
    private static final String ARQUIVE_EXTENSION = ".mp4";


    //OBS: Must be higther or equals 5 seconds (5000)
    //1000 = 1 Segundo
    //5000 = 5 Segundos
    //1000*60 = 1 Minuto
    private static final int DURATION = 5000;

    //To Camera Back: Camera.CameraInfo.CAMERA_FACING_BACK
    //To Camera Front: Camera.CameraInfo.CAMERA_FACING_FRONT
    private static final int CAMERA_FACE = Camera.CameraInfo.CAMERA_FACING_FRONT;

    //Camera
    private static Camera mServiceCamera;

    //Media Recorder
    private MediaRecorder mMediaRecorder;

    //Texture View
    private TextureView mTextureView;

    //Windows manager dynamic
    private WindowManager mWindowManager;

    //Inflater
    public LayoutInflater minflater;

    @Override
    public void onCreate() {
        //Initialize Service
        Log.i(TAG, "Initialize Service On Create");
        super.onCreate();

        //Check current camera faces
        if(!isCameraAvailable()){
            Log.i(TAG, "Cameras doenst exists FACING = " + CAMERA_FACE);
            return;
        }

        //Initialize
        initializeAll();
    }

    /*
    * Facade function
    * */
    public void initializeAll(){
        createSurfaceTexture();
        if(!startRecording()){
            Log.i(TAG, "Erros Ocurs when started the camera recorder");
        }
    }

    /*
    * Create surface and initial variables
    * */
    public void createSurfaceTexture() {

        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);
        minflater = (LayoutInflater)getSystemService (LAYOUT_INFLATER_SERVICE);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        FrameLayout mParentView = new FrameLayout(getApplicationContext());
        final WindowManager.LayoutParams param = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);
        param.width = 352;
        param.height = 288;
        mWindowManager.addView(mParentView, param);
        mParentView.addView(mTextureView);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("OP", "ON BIND SERVICE");
        return null;
    }

    @Override
    public void onDestroy() {
        //CallOne time when clicked in button stop
        stopRecording();
        super.onDestroy();
        Log.i(TAG, "On Destroy");
    }

    /*
    * Start Recording and set parameters
    * */
    public boolean startRecording(){
        try {

            //Set camera faces
            mServiceCamera = Camera.open(CAMERA_FACE);
            Camera.Parameters params = mServiceCamera.getParameters();
            mServiceCamera.setParameters(params);

            //Setting texture
            mServiceCamera.setPreviewTexture(new SurfaceTexture(1));
            mServiceCamera.startPreview();

            //Lock and unlock camera
            mServiceCamera.lock();
            mServiceCamera.unlock();

            //Media recorder
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setCamera(mServiceCamera);

            //Setting video and audio sources
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            //Check video duration
            if( DURATION >= 5000 ) {
                mMediaRecorder.setMaxDuration(DURATION);
            }

            //Setting listenner duration
            mMediaRecorder.setOnInfoListener(this);

            //Recorder on best Quality
            if( BEST_QUALITY ) {
                mMediaRecorder.setAudioEncodingBitRate(196608);
                mMediaRecorder.setVideoEncodingBitRate(15000000);
                mMediaRecorder.setVideoSize(640, 480);
                mMediaRecorder.setVideoFrameRate(16);
                //mMediaRecorder.setVideoSize(640,480);
                //mMediaRecorder.setVideoEncodingBitRate(3000000);
            }

            //Setting output directory
            mMediaRecorder.setOutputFile(getPathDirectory());

            //Prepare and start recorder
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            return true;
        }catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /*
    * Path Directory to Create folder
    * */
    private String getPathDirectory() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        File file   = new File(path+File.separator+FOLDER_NAME+File.separator);

        if( !file.exists() ) {file.mkdirs();}

        return file.getAbsolutePath() + "/" + getArquiveName();
    }

    /*
    * Getting arquive name
    * */
    private String getArquiveName(){
        Date dataAgora = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.format(dataAgora);

        Calendar dataAgoraCalendar = Calendar.getInstance();
        dataAgoraCalendar = sdf.getCalendar();

        int dia = dataAgoraCalendar.get(Calendar.DATE);
        int mes = dataAgoraCalendar.get(Calendar.MONTH);
        int ano = dataAgoraCalendar.get(Calendar.YEAR);

        int hora = dataAgoraCalendar.get(Calendar.HOUR_OF_DAY);
        int minuto = dataAgoraCalendar.get(Calendar.MINUTE);
        int segundo = dataAgoraCalendar.get(Calendar.SECOND);

        String actualDate = String.valueOf(dia) + "_" + String.valueOf(mes) + "_" + String.valueOf(ano);
        String actualHour = String.valueOf(hora) + "_" + String.valueOf(minuto) + "_" + String.valueOf(segundo);
        String completeLogDate = actualDate+"_"+actualHour;


        /*return ARQUIVE_NAME +
                date.toString().replace(" ", "_")
                        .replace(":", "_") + ARQUIVE_EXTENSION;*/
        return ARQUIVE_NAME + completeLogDate + ARQUIVE_EXTENSION;
    }

    /*
    * Stop recorder
    * */
    public void stopRecording() {
        Log.i(TAG, "Recording Stopped");
        try {
            mServiceCamera.reconnect();

            mMediaRecorder.stop();
            mMediaRecorder.reset();

            mServiceCamera.stopPreview();
            mMediaRecorder.release();

            mServiceCamera.release();
            mServiceCamera = null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
    * Check CAMERAS FACES
    * */
    private boolean isCameraAvailable() {
        int cameraCount = 0;
        boolean isFrontCameraAvailable = false;
        cameraCount = Camera.getNumberOfCameras();
        while (cameraCount > 0) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount--;
            Camera.getCameraInfo(cameraCount, cameraInfo);
            if (cameraInfo.facing == CAMERA_FACE) {
                isFrontCameraAvailable = true;
                break;
            }
        }
        return isFrontCameraAvailable;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Call all time when service is calling
        Log.i(TAG, "On Start Command");
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "SURFACE IS NOW AVAILABLE");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if( mServiceCamera != null ) {
            mServiceCamera.setPreviewCallback(null);
            mServiceCamera.stopPreview();
            mServiceCamera.release();
        }
        Log.i(TAG, "On Surface Texture Destroyed");
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            Log.v(TAG, "Maximum Duration Reached");
            //mr.stop();
            stopRecording();
            createSurfaceTexture();
            startRecording();
        }
    }

}