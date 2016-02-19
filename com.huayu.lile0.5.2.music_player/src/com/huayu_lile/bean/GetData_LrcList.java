package com.huayu_lile.bean;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

public class GetData_LrcList {
	private List<LrcContent> lrcList; // List集合存放歌词内容对象
	private LrcContent mLrcContent; // 声明一个歌词内容对象

	/**
	 * 无参构造函数用来实例化对象
	 */
	public GetData_LrcList() {
		mLrcContent = new LrcContent();
		lrcList = new ArrayList<LrcContent>();
		Log.e("LRC", "success  " + "GetData_LrcList");
	}

	/**
	 * 读取歌词
	 * 
	 * @param path
	 * @return
	 */
	public String readLRC(String path) {
		// 定义一个StringBuilder对象，用来存放歌词内容
		StringBuilder stringBuilder = new StringBuilder();

		File f = new File(path.replace(".mp3", ".lrc"));


		if(!f.exists()){
			lrcList = new ArrayList<LrcContent>();
			Log.e("---readLRC", "!f.exists()此曲不存在歌词");
			return "此曲不存在歌词";}
		try {
			// 创建一个文件输入流对象
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String s = "";

			while ((s = br.readLine()) != null) {
				// 替换字符
				s = s.replace("[", "");
				s = s.replace("]", "@");
				Log.e("LRC", "success" + s);
				// 分离“@”字符
				String splitLrcData[] = s.split("@");
				if (splitLrcData.length > 1) {
					mLrcContent.setLrcStr(splitLrcData[1]);
					Log.e("LRC", "success" + splitLrcData[1]);
					// 处理歌词取得歌曲的时间
					int lrcTime = time2Str(splitLrcData[0]);

					mLrcContent.setLrcTime(lrcTime);

					// 添加进列表数组
					lrcList.add(mLrcContent);
					//Log.e("LRC", "success" + lrcList.size());
					// 新创建歌词内容对象
					mLrcContent = new LrcContent();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			stringBuilder.append("木有歌词文件，赶紧去下载！...");
		} catch (IOException e) {
			e.printStackTrace();
			stringBuilder.append("木有读取到歌词哦！");
		}
		return stringBuilder.toString();
	}

	private String GetCharset(File file) {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		try
		{
			boolean checked = false;
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1)
				return charset;
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE)
			{
				charset = "UTF-16LE";
				checked = true;
			}
			else if (first3Bytes[0] == (byte) 0xFE
					&& first3Bytes[1] == (byte) 0xFF)
			{
				charset = "UTF-16BE";
				checked = true;
			}
			else if (first3Bytes[0] == (byte) 0xEF
					&& first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF)
			{
				charset = "UTF-8";
				checked = true;
			}
			bis.reset();
			if (!checked)
			{
				int loc = 0;
				while ((read = bis.read()) != -1)
				{
					loc++;
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF) 
						break;
					if (0xC0 <= read && read <= 0xDF)
					{
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) 
							continue;
						else
							break;
					}
					else if (0xE0 <= read && read <= 0xEF)
					{
						read = bis.read();
						if (0x80 <= read && read <= 0xBF)

						{
							read = bis.read();
							if (0x80 <= read && read <= 0xBF)
							{
								charset = "UTF-8";
								break;
							}
							else
								break;
						}
						else
							break;
					}
				}
			}
			bis.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return charset;
	}

	/**
	 * 解析歌词时间 歌词内容格式如下： [00:02.32]陈奕迅 [00:03.43]好久不见 [00:05.22]歌词制作 王涛
	 * 
	 * @param timeStr
	 * @return
	 */
	public int time2Str(String timeStr) {
		timeStr = timeStr.replace(":", ".");
		timeStr = timeStr.replace(".", "@");

		String timeData[] = timeStr.split("@"); // 将时间分隔成字符串数组

		// 分离出分、秒并转换为整型
		int minute = Integer.parseInt(timeData[0]);
		int second = Integer.parseInt(timeData[1]);
		int millisecond = Integer.parseInt(timeData[2]);

		// 计算上一行与下一行的时间转换为毫秒数
		int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
		return currentTime;
	}

	public List<LrcContent> getLrcList() {
		return lrcList;
	}
}
