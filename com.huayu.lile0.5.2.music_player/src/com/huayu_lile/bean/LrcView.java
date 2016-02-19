package com.huayu_lile.bean;

import java.util.ArrayList;
import java.util.List;

import com.huayu.lile.music_player.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class LrcView extends TextView {

	private float width; // 歌词视图宽度
	private float height; // 歌词视图高度
	private float height_dynamic;
	private Paint currentPaint; // 当前画笔对象
	private Paint notCurrentPaint; // 非当前画笔对象
	private float textHeight = 50; // 文本高度
	private float textSize = 35; // 文本大小
	private int index = 0; // list集合下标
	private List<LrcContent> mLrcList;
	private int test = 0;

	public void setmLrcList(List<LrcContent> mLrcList) {

		this.mLrcList = mLrcList;
	}

	public LrcView(Context context) {
		super(context);
		init();

	}

	public LrcView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();

	}

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

	}

	// mLrcList和画笔初始化
	private void init() {

		Log.d("tag", "LrcView-init-execute");
		mLrcList = new ArrayList<LrcContent>();
		setFocusable(true); // 设置可对焦

		// 高亮部分
		currentPaint = new Paint();
		currentPaint.setAntiAlias(true); // 设置抗锯齿，让文字美观饱满
		currentPaint.setTextAlign(Paint.Align.CENTER);// 设置文本对齐方式

		// 非高亮部分
		notCurrentPaint = new Paint();
		notCurrentPaint.setAntiAlias(true);
		notCurrentPaint.setTextAlign(Paint.Align.CENTER);

	}

	@Override
	protected void onDraw(Canvas canvas) {

		test++;
		Log.d("tag", "LrcView-ondraw-execute1-test::" + test);
		if (test == 0) {
			test = 1;
			Log.d("tag", "execute---ondraw-test=1");
		}

		Log.d("tag", "LrcView-ondraw-execute2");
		// super.onDraw(canvas);
		if (canvas == null) {
			Log.d("tag", "LrcView-ondraw的canvas==null");
			return;
		}
		try {

			currentPaint.setColor(getResources().getColor(R.color.BurlyWood));
			notCurrentPaint.setColor(getResources().getColor(R.color.Coral));

			currentPaint.setTextSize(45);
			currentPaint.setTypeface(Typeface.SANS_SERIF);

			notCurrentPaint.setTextSize(textSize);
			notCurrentPaint.setTypeface(Typeface.SANS_SERIF);

			Log.d("tag", "mLrcList.size(): "+mLrcList.size());
			// 过滤没有歌词文件的情况
			if (mLrcList.size()==0){
				canvas.drawText("木有歌词文件                  ",
						width / 2, height/2, currentPaint);
				canvas.drawText("       赶紧去下载...",
						width / 2, height/2+45, currentPaint);
				Log.d("tag", "setText(\n...木有歌词文件，\n                 赶紧去下载...)");
				return;
			}
			canvas.drawText(mLrcList.get(index).getLrcStr(), width / 2,
					height_dynamic / 2, currentPaint);

			float tempY = height_dynamic / 2;
			// 画出本句之前的句子
			for (int i = index - 1; i >= 0; i--) {
				// 向上推移
				tempY = tempY - textHeight;
				canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY,
						notCurrentPaint);
			}
			tempY = height_dynamic / 2;
			// 画出本句之后的句子
			for (int i = index + 1; i < mLrcList.size(); i++) {
				// 往下推移
				tempY = tempY + textHeight;
				canvas.drawText(mLrcList.get(i).getLrcStr(), width / 2, tempY,
						notCurrentPaint);
			}

		} catch (Exception e) {
			Log.d("tag", "LrcView---Exception");
			e.printStackTrace();

			
		}
		

	}

	/**
	 * 当view大小改变的时候调用的方法 ，给width,height赋值
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.width = w;
		this.height = h;
		this.height_dynamic = h;
	}

	public void setIndex(int index) {
		if (index == this.index) {
			this.height_dynamic = this.height_dynamic - 4;
			return;
		}
		this.index = index;
		this.height_dynamic = this.height;
	}

}

