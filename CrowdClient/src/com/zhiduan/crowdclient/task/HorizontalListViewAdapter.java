package com.zhiduan.crowdclient.task;

  
import java.util.List;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.TaskImg;

import android.content.Context;  
import android.graphics.Bitmap;  
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;  
import android.media.ThumbnailUtils;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.ImageView;  
import android.widget.TextView;  
  
public class HorizontalListViewAdapter extends BaseAdapter{  
    private List<Bitmap>imgs;  
    private String[] mTitles;  
    private Context mContext;  
    private LayoutInflater mInflater;  
    Bitmap iconBitmap;  
    private int selectIndex = -1;  
  
    public HorizontalListViewAdapter(Context context, List<Bitmap>task_imgs){  
        this.mContext = context;  
        this.imgs = task_imgs;  
        mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);  
    }  
    @Override  
    public int getCount() {  
        return imgs.size();  
    }  
    @Override  
    public Object getItem(int position) {  
        return position;  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
  
        ViewHolder holder;  
        if(convertView==null){  
            holder = new ViewHolder();  
            convertView = mInflater.inflate(R.layout.horizontal_list_item, null);  
            holder.mImage=(ImageView)convertView.findViewById(R.id.img_list_item);  
            holder.mTitle=(TextView)convertView.findViewById(R.id.text_list_item);  
            convertView.setTag(holder);  
        }else{  
            holder=(ViewHolder)convertView.getTag();  
        }  
        if(position == selectIndex){  
            convertView.setSelected(true);  
        }else{  
            convertView.setSelected(false);  
        }  
          
       // holder.mTitle.setText(mTitles[position]);  
      //  iconBitmap = getPropThumnail(mIconIDs[position]);  
        holder.mImage.setImageBitmap(imgs.get(position));  
  
        return convertView;  
    }  
  
    private static class ViewHolder {  
        private TextView mTitle ;  
        private ImageView mImage;  
    }  
    private Bitmap getPropThumnail(int id){  
        Drawable d = mContext.getResources().getDrawable(id);  
        Bitmap b = drawableToBitmap(d);  
//      Bitmap bb = BitmapUtil.getRoundedCornerBitmap(b, 100);  
        int w = mContext.getResources().getDimensionPixelOffset(R.dimen.thumnail_default_width);  
        int h = mContext.getResources().getDimensionPixelSize(R.dimen.thumnail_default_height);  
        Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, w, h);  
          
        return thumBitmap;  
    }  
    public void setSelectIndex(int i){  
        selectIndex = i;  
    }  
    
    public static Bitmap drawableToBitmap(Drawable drawable) {  
        // 取 drawable 的长宽  
        int w = drawable.getIntrinsicWidth();  
        int h = drawable.getIntrinsicHeight();  
  
        // 取 drawable 的颜色格式  
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                : Bitmap.Config.RGB_565;  
        // 建立对应 bitmap  
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);  
        // 建立对应 bitmap 的画布  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, w, h);  
        // 把 drawable 内容画到画布中  
        drawable.draw(canvas);  
        return bitmap;  
    }  
}