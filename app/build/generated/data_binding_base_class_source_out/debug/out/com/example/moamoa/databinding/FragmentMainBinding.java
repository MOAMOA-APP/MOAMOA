// Generated by view binder compiler. Do not edit!
package com.example.moamoa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.moamoa.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentMainBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView FID;

  @NonNull
  public final TextView charge;

  @NonNull
  public final ToggleButton heart;

  @NonNull
  public final ImageView mainImage;

  @NonNull
  public final TextView mans;

  @NonNull
  public final TextView name;

  @NonNull
  public final TextView state;

  @NonNull
  public final TextView title;

  private FragmentMainBinding(@NonNull LinearLayout rootView, @NonNull TextView FID,
      @NonNull TextView charge, @NonNull ToggleButton heart, @NonNull ImageView mainImage,
      @NonNull TextView mans, @NonNull TextView name, @NonNull TextView state,
      @NonNull TextView title) {
    this.rootView = rootView;
    this.FID = FID;
    this.charge = charge;
    this.heart = heart;
    this.mainImage = mainImage;
    this.mans = mans;
    this.name = name;
    this.state = state;
    this.title = title;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.FID;
      TextView FID = ViewBindings.findChildViewById(rootView, id);
      if (FID == null) {
        break missingId;
      }

      id = R.id.charge;
      TextView charge = ViewBindings.findChildViewById(rootView, id);
      if (charge == null) {
        break missingId;
      }

      id = R.id.heart;
      ToggleButton heart = ViewBindings.findChildViewById(rootView, id);
      if (heart == null) {
        break missingId;
      }

      id = R.id.mainImage;
      ImageView mainImage = ViewBindings.findChildViewById(rootView, id);
      if (mainImage == null) {
        break missingId;
      }

      id = R.id.mans;
      TextView mans = ViewBindings.findChildViewById(rootView, id);
      if (mans == null) {
        break missingId;
      }

      id = R.id.name;
      TextView name = ViewBindings.findChildViewById(rootView, id);
      if (name == null) {
        break missingId;
      }

      id = R.id.state;
      TextView state = ViewBindings.findChildViewById(rootView, id);
      if (state == null) {
        break missingId;
      }

      id = R.id.title;
      TextView title = ViewBindings.findChildViewById(rootView, id);
      if (title == null) {
        break missingId;
      }

      return new FragmentMainBinding((LinearLayout) rootView, FID, charge, heart, mainImage, mans,
          name, state, title);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
