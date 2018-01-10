package com.zhiduan.crowdclient.im;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.util.NetUtils;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.message.MessageActivity;

public class ConversationListFragment extends EaseConversationListFragment {

	private TextView errorText;

	@Override
	protected void initView() {
		super.initView();
		View errorView = (LinearLayout) View.inflate(getActivity(),
				R.layout.em_chat_neterror_item, null);
		errorItemContainer.addView(errorView);
		
		errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
		setImmerseLayout();
	}
	
	protected void setImmerseLayout() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getActivity().getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}
	
	@Override
	protected void setUpView() {
		super.setUpView();
		// register context menu
		registerForContextMenu(conversationListView);
		conversationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EMConversation conversation = conversationListView.getItem(position);
				String username = conversation.conversationId();
				if (username.equals(EMClient.getInstance().getCurrentUser()))
					Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
				else {
					// start chat acitivity
					Intent intent = new Intent(getActivity(), ChatActivity.class);
					if (conversation.isGroup()) {
						if (conversation.getType() == EMConversationType.ChatRoom) {
							// it's group chat
							intent.putExtra(ImConstant.EXTRA_CHAT_TYPE, ImConstant.CHATTYPE_CHATROOM);
						} else {
							intent.putExtra(ImConstant.EXTRA_CHAT_TYPE, ImConstant.CHATTYPE_GROUP);
						}

					}
					Log.i("hexiuhui--", "username" + username);
					// it's single chat
					intent.putExtra(ImConstant.EXTRA_USER_ID, username);
					startActivity(intent);
				}
			}
		});
		
		notify_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MessageActivity.class);
				startActivity(intent);
			}
		});
		
//		friends_layout.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(getActivity(), ImContactActivity.class);
//				startActivity(intent);
//			}
//		});
		
		mBackLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		mTitleTxt.setText("消息");
		
		super.setUpView();
		// end of red packet code
	}

	@Override
	protected void onConnectionDisconnected() {
		super.onConnectionDisconnected();
		if (NetUtils.hasNetwork(getActivity())) {
			errorText.setText(R.string.can_not_connect_chat_server_connection);
		} else {
			errorText.setText(R.string.the_current_network);
		}
	}
}
