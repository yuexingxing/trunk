package com.zhiduan.crowdclient.menuindex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.menuindex.filterlistview.FixedGridView;
import com.zhiduan.crowdclient.menuindex.filterlistview.ImageManager;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.OrderUtil;

/**
 * Description 选择分类
 * Author: hexiuhui
 */
public class ClassifyActivity extends BaseActivity {

	private int [] orderType = {OrderUtil.EXPRESS_TYPE, OrderUtil.ERRANDS_TYPE, OrderUtil.TASK_TYPE, OrderUtil.PRODUCT_TYPE};
	private FixedGridView mFixedGridView;
	
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_classify, ClassifyActivity.this);
		setTitle("选择分类");
	}

	@Override
	public void initView() {
		mFixedGridView = (FixedGridView) findViewById(R.id.gv_classify);
	}

	@Override
	public void initData() {
		List<ClassifyEntity> classifyList = new ArrayList<ClassifyEntity>();
		classifyList.add(new ClassifyEntity("快递", R.drawable.classification_express_delivery));
		classifyList.add(new ClassifyEntity("跑腿", R.drawable.dlassification_running_errands));
		classifyList.add(new ClassifyEntity("任务", R.drawable.classification_task));
		classifyList.add(new ClassifyEntity("商品", R.drawable.classification_commodity));
		
        ClassifyAdapter adapter = new ClassifyAdapter(ClassifyActivity.this, classifyList);
        mFixedGridView.setAdapter(adapter);
        
        mFixedGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent exIntent = new Intent(ClassifyActivity.this, ExperienceAdditionActivity.class);
				exIntent.putExtra("categoryId", orderType[position]);
				exIntent.putExtra("ruleCode", "");
				startActivity(exIntent);
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 被系统回收时保存myapplication全局变量值
		GlobalInstanceStateHelper.saveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// 恢复被系统回收后的myapplication值
		GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);

		initView();
	}
	
	
	class ClassifyAdapter extends BaseAdapter {

		private Context context;
		private List<ClassifyEntity> list;

		public ClassifyAdapter(Context context, List<ClassifyEntity> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.item_classify, null);
				holder = new ViewHolder();

				holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ClassifyEntity entity = list.get(position);
			ImageManager mImageManager = new ImageManager(context);
			
			holder.tv_title.setText(entity.getTitle());
			
			mImageManager.loadResImage(entity.getResId(), holder.iv_image);

			return convertView;
		}

		class ViewHolder {
			public ImageView iv_image;
			public TextView tv_title;
		}
	}
	
	class ClassifyEntity implements Serializable {

	    private String title;
	    private int resId;
	    public int getResId() {
			return resId;
		}
	    
	    public ClassifyEntity() {
	    }

	    public ClassifyEntity(String title, int resId) {
	        this.title = title;
	        this.resId = resId;
	    }

		public void setResId(int resId) {
			this.resId = resId;
		}

	    public String getTitle() {
	        return title;
	    }

	    public void setTitle(String title) {
	        this.title = title;
	    }
	}
}
