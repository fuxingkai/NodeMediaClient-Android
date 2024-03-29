package cn.nodemedia.nodemediaclient;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cn.nodemedia.LivePublisher;
import cn.nodemedia.LivePublisher.LivePublishDelegate;

public class LivePublisherDemoActivity extends Activity implements OnClickListener, LivePublishDelegate {
	private SurfaceView sv;
	private Button micBtn, swtBtn, videoBtn, flashBtn, camBtn;
	private boolean isStarting = false;
	private boolean isMicOn = true;
	private boolean isCamOn = true;
	private boolean isFlsOn = true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encoder);
		isStarting = false;
		sv = (SurfaceView) findViewById(R.id.cameraView);
		micBtn = (Button) findViewById(R.id.button_mic);
		swtBtn = (Button) findViewById(R.id.button_sw);
		videoBtn = (Button) findViewById(R.id.button_video);
		flashBtn = (Button) findViewById(R.id.button_flash);
		camBtn = (Button) findViewById(R.id.button_cam);

		micBtn.setOnClickListener(this);
		swtBtn.setOnClickListener(this);
		videoBtn.setOnClickListener(this);
		flashBtn.setOnClickListener(this);
		camBtn.setOnClickListener(this);

		LivePublisher.init(this); // 1.初始化
		LivePublisher.setDelegate(this); // 2.设置事件回调
		
		/**
		 * 设置输出音频参数
		 * 码率 32kbps
		 * 使用HE-AAC ,部分服务端不支持HE-AAC,会导致发布失败
		 */
		LivePublisher.setAudioParam(32 * 1000, LivePublisher.AAC_PROFILE_HE);
	
		
		
		/**
		 * 设置输出视频参数
		 * 宽 640
		 * 高 360
		 * fps 15
		 * 码率 300kbps
		 * 使用main profile 
		 */
		LivePublisher.setVideoParam(640, 360, 15, 300 * 1000, LivePublisher.AVC_PROFILE_MAIN);
		
		/**
		 * 是否开启背景噪音抑制
		 */
		LivePublisher.setDenoiseEnable(true);
		
		/**
		 * 开始视频预览，
		 * cameraPreview ： 用以回显摄像头预览的SurfaceViewd对象，如果此参数传入null，则只发布音频
		 * interfaceOrientation ： 程序界面的方向，也做调整摄像头旋转度数的参数，
		 * camId： 摄像头初始id，LivePublisher.CAMERA_BACK 后置，LivePublisher.CAMERA_FRONE 前置
		 */
		LivePublisher.startPreview(sv,getWindowManager().getDefaultDisplay().getRotation(),LivePublisher.CAMERA_BACK); // 5.开始预览 如果传null 则只发布音频
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//如果程序UI没有锁定屏幕方向，旋转手机后，请把新的界面方向传入，以调整摄像头预览方向
		LivePublisher.setCameraOrientation(getWindowManager().getDefaultDisplay().getRotation());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LivePublisher.stopPreview();
		LivePublisher.stopPublish();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.button_mic:
			if (isStarting) {
				isMicOn = !isMicOn;
				LivePublisher.setMicEnable(isMicOn); //设置是否打开麦克风
				if (isMicOn) {
					handler.sendEmptyMessage(2101);
				} else {
					handler.sendEmptyMessage(2100);
				}
			}
			break;
		case R.id.button_sw:
			LivePublisher.switchCamera();// 切换前后摄像头
			LivePublisher.setFlashEnable(false);//关闭闪光灯,前置不支持闪光灯
			isFlsOn =false;
			flashBtn.setBackgroundResource(R.drawable.ic_flash_off);
			break;
		case R.id.button_video:
			if (isStarting) {
				LivePublisher.stopPublish();// 停止发布
			} else {
				/**
				 *设置视频发布的方向，此方法为可选，如果不调用，则输出视频方向跟随界面方向，如果特定指出视频方向，在startPublish前调用设置
				 *videoOrientation ： 视频方向
				 *VIDEO_ORI_PORTRAIT			home键在 下 的 9:16 竖屏方向
				 *VIDEO_ORI_LANDSCAPE			home键在 右 的16:9 横屏方向
				 *VIDEO_ORI_PORTRAIT_REVERSE	home键在 上 的9:16 竖屏方向
				 *VIDEO_ORI_LANDSCAPE_REVERSE	home键在 左的 16:9 横屏方向
				 */
				LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT);
				
				/**
				 * 开始视频发布
				 * rtmpUrl  rtmp流地址
				 */
				LivePublisher.startPublish((String)SharedPreUtil.get(LivePublisherDemoActivity.this, "pubUrl", "rtmp://192.168.0.10/live/demo")); // 开始发布
			}
			break;
		case R.id.button_flash:
			int ret = -1;
			if(isFlsOn) {
				ret = LivePublisher.setFlashEnable(false);
			}else {
				ret = LivePublisher.setFlashEnable(true);
			}
			if(ret == -1) {
				//无闪光灯,或处于前置摄像头,不支持闪光灯操作
			}else if(ret == 0) {
				//闪光灯被关闭
				flashBtn.setBackgroundResource(R.drawable.ic_flash_off);
				isFlsOn = false;
			}else {
				//闪光灯被打开
				flashBtn.setBackgroundResource(R.drawable.ic_flash_on);
				isFlsOn = true;
			}
			break;
		case R.id.button_cam:
			if (isStarting) {
				isCamOn = !isCamOn;
				LivePublisher.setCamEnable(isCamOn);
				if (isCamOn) {
					handler.sendEmptyMessage(2103);
				} else {
					handler.sendEmptyMessage(2102);
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onEventCallback(int event, String msg) {
		handler.sendEmptyMessage(event);
	}

	private Handler handler = new Handler() {
		// 回调处理
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 2000:
				Toast.makeText(LivePublisherDemoActivity.this, "正在发布视频", Toast.LENGTH_SHORT).show();
				break;
			case 2001:
				Toast.makeText(LivePublisherDemoActivity.this, "视频发布成功", Toast.LENGTH_SHORT).show();
				videoBtn.setBackgroundResource(R.drawable.ic_video_start);
				isStarting = true;
				break;
			case 2002:
				Toast.makeText(LivePublisherDemoActivity.this, "视频发布失败", Toast.LENGTH_SHORT).show();
				break;
			case 2004:
				Toast.makeText(LivePublisherDemoActivity.this, "视频发布结束", Toast.LENGTH_SHORT).show();
				videoBtn.setBackgroundResource(R.drawable.ic_video_stop);
				isStarting = false;
				break;
			case 2005:
				Toast.makeText(LivePublisherDemoActivity.this, "网络异常,发布中断", Toast.LENGTH_SHORT).show();
				break;
			case 2100:
				// mic off
				micBtn.setBackgroundResource(R.drawable.ic_mic_off);
				Toast.makeText(LivePublisherDemoActivity.this, "麦克风静音", Toast.LENGTH_SHORT).show();
				break;
			case 2101:
				// mic on
				micBtn.setBackgroundResource(R.drawable.ic_mic_on);
				Toast.makeText(LivePublisherDemoActivity.this, "麦克风恢复", Toast.LENGTH_SHORT).show();
				break;
			case 2102:
				// camera off
				camBtn.setBackgroundResource(R.drawable.ic_cam_off);
				Toast.makeText(LivePublisherDemoActivity.this, "摄像头传输关闭", Toast.LENGTH_SHORT).show();
				break;
			case 2103:
				// camera on
				camBtn.setBackgroundResource(R.drawable.ic_cam_on);
				Toast.makeText(LivePublisherDemoActivity.this, "摄像头传输打开", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

}
