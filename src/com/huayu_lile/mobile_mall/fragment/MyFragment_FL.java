package com.huayu_lile.mobile_mall.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.com.huayu.lile.mobile_mall.R;
import com.google.gson.reflect.TypeToken;
import com.huayu_lile.mobile_mall.bean.CategoryMenu;
import com.huayu_lile.mobile_mall.bean.CategoryMenu.CategoryItem;
import com.huayu_lile.mobile_mall.bean.Constants;
import com.huayu_lile.mobile_mall.bean.ListViewForScrollView;
import com.lib.json.JSONUtils;
import com.lib.uil.UILUtils;
import com.lib.volley.HTTPUtils;
import com.lib.volley.VolleyListener;

public class MyFragment_FL extends Fragment {
	private ArrayList<CategoryMenu> menuList = new ArrayList<CategoryMenu>();
	private List<CategoryItem> categoryitem;
	private View layout;
	private CategoryListAdapter mListAdapter;
	private CategoryGridAdapter mGridAdapter;
	private int selectedPosition;
	private ScrollView mScrollView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		return inflater.inflate(R.layout.fragment_fx, null, false);
	}

	/**
	 * 解析JsonArray获取菜单数据
	 */
	private void initData() {
		/*
		 * // 异步下载JSON HTTPUtils.getVolley(getActivity(),
		 * Constants.URL.MENUJSON, new VolleyListener() {
		 * 
		 * @Override public void onResponse(String arg0) { Type type = new
		 * TypeToken<ArrayList<CategoryMenu>>() { }.getType();
		 * ArrayList<CategoryMenu> list = JSONUtils .parseJSONArray(arg0, type);
		 * menuList.addAll(list); mListAdapter.notifyDataSetChanged();
		 * mGridAdapter.notifyDataSetChanged();
		 * layout.findViewById(R.id.progressBar1).setVisibility( View.GONE); }
		 * 
		 * @Override public void onErrorResponse(VolleyError arg0) { // TODO
		 * 自动生成的方法存根
		 * 
		 * } });
		 */
		String menujson = null;
		try {
			InputStream is = getActivity().getAssets().open("menujson.txt");
			int size;
			size = is.available();
			// Read the entire asset into a local byte buffer.
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();

			// Convert the buffer into a string.
			menujson = new String(buffer, "GB2312");

		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		Log.d("tag", menujson);
		Type type = new TypeToken<ArrayList<CategoryMenu>>() {
		}.getType();
		ArrayList<CategoryMenu> list = JSONUtils.parseJSONArray(menujson, type);
		menuList.addAll(list);
		mListAdapter.notifyDataSetChanged();
		mGridAdapter.notifyDataSetChanged();
		layout.findViewById(R.id.progressBar1).setVisibility(View.GONE);

	}

	private void initListView() {
		final ListViewForScrollView listView = (ListViewForScrollView) layout
				.findViewById(R.id.listView_category);
		mListAdapter = new CategoryListAdapter();
		listView.setAdapter(mListAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (selectedPosition == position) {
					return;
				}
				mScrollView.smoothScrollTo(0, position * view.getHeight());
				selectedPosition = position;
				mListAdapter.notifyDataSetChanged();
				mGridAdapter.notifyDataSetChanged();
			}
		});
	}

	class CategoryListAdapter extends BaseAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View inflate = getActivity().getLayoutInflater().inflate(
					R.layout.item_category_list, null);
			TextView tvCategoryList = (TextView) inflate
					.findViewById(R.id.tv_category_list);
			inflate.setTag(tvCategoryList);
			String categoryType = menuList.get(position).getCategory();
			tvCategoryList.setText(categoryType);
			if (selectedPosition == position) {
				inflate.setBackgroundResource(R.drawable.drawable_common_category_list_s);
				tvCategoryList.setTextColor(getResources()
						.getColor(R.color.red));
			} else {
				inflate.setBackgroundResource(R.drawable.drawable_common_category_list);
				tvCategoryList.setTextColor(getResources().getColor(
						R.color.text));

			}
			return inflate;
		}

		@Override
		public int getCount() {
			return menuList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	class GridViewHolder {
		ImageView imgCategoryGrid;
		TextView tvCategoryGrid;
	}

	class CategoryGridAdapter extends BaseAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View inflate = null;
			GridViewHolder holder = null;
			if (convertView == null) {
				holder = new GridViewHolder();
				inflate = getActivity().getLayoutInflater().inflate(
						R.layout.item_category_grid, null);
				holder.imgCategoryGrid = (ImageView) inflate
						.findViewById(R.id.img_category_grid);
				holder.tvCategoryGrid = (TextView) inflate
						.findViewById(R.id.tv_category_grid);
				inflate.setTag(holder);
			} else {
				inflate = convertView;
				holder = (GridViewHolder) inflate.getTag();
			}
			holder.tvCategoryGrid.setText(menuList.get(selectedPosition)
					.getCategoryitem().get(position).getTypename());
			UILUtils.displayImage(getActivity(), menuList.get(selectedPosition)
					.getCategoryitem().get(position).getImgurl(),
					holder.imgCategoryGrid);
			return inflate;
		}

		@Override
		public int getCount() {
			if (menuList.size() > 0) {
				categoryitem = menuList.get(selectedPosition).getCategoryitem();
				return categoryitem.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}
}
