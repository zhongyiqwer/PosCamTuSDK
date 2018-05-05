package com.psy.my;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.xdu.poscam.R;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.psy.db.DataAccess;
import com.psy.util.Utils;

import org.lasque.tusdk.EntryActivity;

public class SplashScreenActivity extends  Activity {

	public SharedPreferences sp;
	private boolean premmsion ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(SplashScreenActivity.this);
		this.setContentView(R.layout.splash_screen);

		requestPermissions();
		if (premmsion){
			start();
		}
	}

	private void start(){
		final Intent intent = new Intent();
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				sp = getSharedPreferences("flagSP", 0);
				SharedPreferences.Editor editor1 = sp.edit();

				if (sp.getInt("flag", 0) == 0) {
					DataAccess access = new DataAccess();
					access.initDatabase(SplashScreenActivity.this);
					editor1.putInt("flag", 1);
					editor1.commit();

					intent.setClass(SplashScreenActivity.this, GuideActivity.class);
					finish();
					SplashScreenActivity.this.startActivity(intent);

				}else {

					//if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
					// your code using Camera API here - is between 1-20
					intent.setClass(SplashScreenActivity.this, EntryActivity.class);
					finish();
					SplashScreenActivity.this.startActivity(intent);
//					} else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//						// your code using Camera2 API here - is api 21 or higher
//						Common.display(SplashScreenActivity.this,"暂不支持安卓5.0及以上版本...");
//					}

				}

			}
		};
		timer.schedule(task, 1400);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void requestPermissions(){
		//6.0运行时权限
		List<String> permissionList = new ArrayList<>();
		if (ActivityCompat.checkSelfPermission(SplashScreenActivity.this, Manifest.permission.CAMERA) !=
				PackageManager.PERMISSION_GRANTED ) {
			permissionList.add(Manifest.permission.CAMERA);
		}
		if (ActivityCompat.checkSelfPermission(SplashScreenActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
				PackageManager.PERMISSION_GRANTED){
			permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
		}
		if (!permissionList.isEmpty()){
			premmsion = false;
			String [] permissions = permissionList.toArray(new String[permissionList.size()]);
			ActivityCompat.requestPermissions(SplashScreenActivity.this,permissions,1);
		}else {premmsion = true;}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode){
			case 1:
				if (grantResults.length>0){
					for (int result:grantResults) {
						if (result != PackageManager.PERMISSION_GRANTED){
							//Toast.makeText(SplashScreenActivity.this,"没有权限",Toast.LENGTH_SHORT).show();
							//showToast.showToastShort("没有权限");
							//killAllActivities();
							startAppSettings();
							return;
						}
					}
					start();

				}else {
					//showToast.showToastShort("未知错误");
					//killAllActivities();
				}
				break;
			default:
		}
	}
	public void startAppSettings(){
		try {
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:"+ getPackageName()));
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

