package com.example.moamoa.ui.mypage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.moamoa.Form;
import com.example.moamoa.R;
import com.example.moamoa.databinding.ListviewCateogorylistBinding;
import com.example.moamoa.ui.category.CustomListView;
import com.example.moamoa.ui.formdetail.FormdetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */

//생선한 공동구매
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private ListviewCateogorylistBinding binding;  //empty_forms를 viewpager에 binding


    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        int pos = getArguments().getInt(ARG_SECTION_NUMBER);

        binding = ListviewCateogorylistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //empty_forms의 인기순, 최신순..등등 감춤
        LinearLayout layout = root.findViewById(R.id.search_option);
        layout.setVisibility(View.GONE); // 해당 뷰를 안 보여줌(공간마저 감춤)

        //추가
        ListView listView;
        listView = root.findViewById(R.id.listview);
        ArrayList<Form> listViewData = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("form").addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listViewData.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Form listData = snapshot.getValue(Form.class);
                    Log.d("확인","루트 : "+user.getUid());



                    if (listData.UID_dash.equals(user.getUid())&& listData.getstate()==0 && pos==1) {
                        Log.d("확인","실행: "+listData.UID_dash);
                        listViewData.add(listData);
                    }
                    if (listData.UID_dash.equals(user.getUid())&& listData.getstate()==1 && pos==1) {
                        Log.d("확인","실행: "+listData.UID_dash);
                        listViewData.add(listData);
                    }

                    if (listData.UID_dash.equals(user.getUid())&& listData.getstate()==2 && pos==2) {
                        Log.d("확인","실행: "+listData.UID_dash);
                        listViewData.add(listData);
                    }



                    //viewpager에 리스트 띄워줌
                    ListAdapter oAdapter = new CustomListView(listViewData);
                    listView.setAdapter(oAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String FID = listViewData.get(position).FID;
                String title = listViewData.get(position).subject;

                //인텐트 선언 및 정의


                Intent intent = new Intent(getContext(), FormdetailActivity.class);
                //입력한 input값을 intent로 전달한다.
                intent.putExtra("FID", FID);
                //액티비티 이동
                startActivity(intent);
                //Toast.makeText (getContext(), "FID : "+FID, Toast.LENGTH_SHORT).show ();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
//

