package com.zd.musictorelax.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.zd.musictorelax.activity.R;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * 初始化数据库
 * 
 * @author  
 * 
 */
public class initDb {
	Context context;
	public static String Path = Environment.getExternalStorageDirectory()
			+ "/";
	public static String dbName = "test1";
	public initDb(Context context) {
		this.context = context;
	}

	/**
	 * 导入静态数据库
	 * @param path
	 */
	public void copyDb(String path) {
		File file = new File(Path);
		if (!file.exists()) {
			file.mkdirs();
		}
		File file2 = new File(file, dbName);
		InputStream iStream = context.getResources().openRawResource(R.raw.test);
		try {
			FileOutputStream fos = new FileOutputStream(file2);
			byte[] buffer = new byte[1024];
			int count = 0;
			// 将静态数据库文件拷贝到目的地
			while ((count = iStream.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.close();
			fos.close();
			Log.i("sysout","本地数据库加载成功");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("sysout","本地数据库加载失败");
			e.printStackTrace();
		}
	}
}
