// Generated by view binder compiler. Do not edit!
package com.example.moamoa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.moamoa.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RecyclerviewChatsBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout LinearChatmsg;

  @NonNull
  public final CardView chatCardview;

  @NonNull
  public final TextView chatMsg;

  @NonNull
  public final TextView chatNickname;

  @NonNull
  public final LinearLayout chattingLayout;

  @NonNull
  public final LinearLayout messageLine;

  @NonNull
  public final CircleImageView profileImage;

  @NonNull
  public final TextView sendedTime;

  private RecyclerviewChatsBinding(@NonNull LinearLayout rootView,
      @NonNull LinearLayout LinearChatmsg, @NonNull CardView chatCardview,
      @NonNull TextView chatMsg, @NonNull TextView chatNickname,
      @NonNull LinearLayout chattingLayout, @NonNull LinearLayout messageLine,
      @NonNull CircleImageView profileImage, @NonNull TextView sendedTime) {
    this.rootView = rootView;
    this.LinearChatmsg = LinearChatmsg;
    this.chatCardview = chatCardview;
    this.chatMsg = chatMsg;
    this.chatNickname = chatNickname;
    this.chattingLayout = chattingLayout;
    this.messageLine = messageLine;
    this.profileImage = profileImage;
    this.sendedTime = sendedTime;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RecyclerviewChatsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RecyclerviewChatsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.recyclerview_chats, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RecyclerviewChatsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.Linear_chatmsg;
      LinearLayout LinearChatmsg = ViewBindings.findChildViewById(rootView, id);
      if (LinearChatmsg == null) {
        break missingId;
      }

      id = R.id.chat_cardview;
      CardView chatCardview = ViewBindings.findChildViewById(rootView, id);
      if (chatCardview == null) {
        break missingId;
      }

      id = R.id.chat_msg;
      TextView chatMsg = ViewBindings.findChildViewById(rootView, id);
      if (chatMsg == null) {
        break missingId;
      }

      id = R.id.chat_nickname;
      TextView chatNickname = ViewBindings.findChildViewById(rootView, id);
      if (chatNickname == null) {
        break missingId;
      }

      LinearLayout chattingLayout = (LinearLayout) rootView;

      id = R.id.message_line;
      LinearLayout messageLine = ViewBindings.findChildViewById(rootView, id);
      if (messageLine == null) {
        break missingId;
      }

      id = R.id.profile_image;
      CircleImageView profileImage = ViewBindings.findChildViewById(rootView, id);
      if (profileImage == null) {
        break missingId;
      }

      id = R.id.sended_time;
      TextView sendedTime = ViewBindings.findChildViewById(rootView, id);
      if (sendedTime == null) {
        break missingId;
      }

      return new RecyclerviewChatsBinding((LinearLayout) rootView, LinearChatmsg, chatCardview,
          chatMsg, chatNickname, chattingLayout, messageLine, profileImage, sendedTime);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
