package com.example.moamoa.ui.formdetail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moamoa.R;
import com.example.moamoa.ui.chats.ChatsActivity;
import com.example.moamoa.ui.dashboard.MultiImageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FormdetailActivity extends Activity implements OnMapReadyCallback {
    private DatabaseReference mDatabase;
    String image;
    String k;
    int count;
    String FID;
    String FUID;
    RecyclerView recyclerView;
    private static final String TAG = "MultiImageActivity";
    ArrayList<String> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체
    MultiImageAdapter1 adapter;  // 리사이클러뷰에 적용시킬 어댑터

    // 지도
    private MapView mapView;
    private static NaverMap naverMap;
    Marker marker = new Marker();

    private FirebaseStorage firebaseStorage;
    private FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        FID = intent.getStringExtra("FID");
        FUID = intent.getStringExtra("UID_dash");
        if (user.getUid().equals(FUID)) {
            Intent intent1 = new Intent(this, DetailCreaterSideActivity.class);
            intent1.putExtra("FID", FID);
            intent1.putExtra("FUID", FUID);
            startActivity(intent1);
            finish();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formdetail);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        Intent intent = getIntent();
        FID = intent.getStringExtra("FID");
        Button chat_btn = (Button) findViewById(R.id.detail_chat_btn);   //채팅하기 버튼
        Button party_btn_0 = (Button) findViewById(R.id.detail_party_btn_0); //참여하기 버튼
        Button party_btn_1 = (Button) findViewById(R.id.detail_party_btn_1); //참여취소 버튼
        recyclerView =  (RecyclerView)findViewById(R.id.recyclerView_form);                           // 이미지를 보여줄 리사이클러뷰
        mDatabase.child("party").child(FID).child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()) {   //이미 참여중인 게시글인 경우
                    party_btn_0.setVisibility(View.GONE);
                    party_btn_1.setVisibility(View.VISIBLE);
                }
            }
        });

        TextView state1 = (TextView)findViewById(R.id.formstate_1);
        TextView state2 = (TextView)findViewById(R.id.formstate_2);
        View line1 = (View)findViewById(R.id.formstate_line1);
        View line2 = (View)findViewById(R.id.formstate_line2);
        mDatabase.child("form").child(FID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                int state = Integer.parseInt(task.getResult().child("state").getValue().toString());
                Log.e("asdf",task.getResult().child("state").getValue().toString());
                if(state>=1) {
                    state1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.second_green)));
                    line1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.main_orange)));
                    if (state == 2) {
                        state2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.main_green)));
                        line2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.second_green)));
                    }
                }
            }
        });

        printpage();

        ArrayList<NoticeData> noticeData = new ArrayList();
        ListView listView = (ListView) FormdetailActivity.this.findViewById(R.id.detail_notion);
        ScrollView scrollView = (ScrollView) FormdetailActivity.this.findViewById(R.id.detail_scroll);
        NoticeAdapter myAdapter = new NoticeAdapter(FormdetailActivity.this,noticeData);
        TextView no = (TextView)  FormdetailActivity.this.findViewById(R.id.no_notice_text);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mDatabase.child("form").child(FID).child("notice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    noticeData.clear();
                    listView.setVisibility(View.VISIBLE);
                    no.setVisibility(View.GONE);
                    int count = (int) snapshot.getChildrenCount();
                    int x = 0;
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        NoticeData listdata = new NoticeData();
                        listdata.setSubject((String) dataSnapshot.child("n_subject").getValue());
                        listdata.setText((String) dataSnapshot.child("n_text").getValue());
                        listdata.setDate((String) dataSnapshot.child("n_date").getValue());
                        listdata.setSide(1);
                        listdata.setFid(FID);
                        listdata.setNumb(Integer.parseInt(dataSnapshot.getKey().toString()));
                        noticeData.add(listdata);
                        x++;
                        if(x==count){
                            listView.setAdapter(myAdapter);
                        }
                    }
                }else{
                    listView.setVisibility(View.GONE);
                    no.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("xjdzzwh9wk"));
        mapView = (MapView) findViewById(R.id.mv);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                mDatabase.child("form").child(FUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // FUID -> ChatsFragment에서 destinationUID로 사용

                        // 밑에꺼... 무슨코드인지모르겠음
                        // point=dataSnapshot.child("point").getValue().toString();

                        if (user.getUid().equals(FUID)) {
                            // 본인의 폼에서는 채팅하기 누를 수 없음
                            Toast.makeText(getApplicationContext(), "내 게시글입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            // ChatActivity로 UID 넘김 (destinationUID)
                            Intent intent = new Intent(FormdetailActivity.this, ChatsActivity.class);
                            intent.putExtra("destinationUID", FUID);
                            intent.putExtra("FID", FID);

                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("", "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        });

        party_btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("form").child(FID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        int state = Integer.parseInt(task.getResult().child("state").getValue().toString());
                        int temp =  Integer.parseInt(task.getResult().child("parti_num").getValue().toString())-1;
                        if (state == 0) {
                            mDatabase.child("form").child(FID).child("parti_num").setValue(temp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDatabase.child("party").child(FID).child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            AlertDialog.Builder AddParty_alert = new AlertDialog.Builder(FormdetailActivity.this);
                                            AddParty_alert.setMessage("정말 참여를 취소하시겠습니까?")
                                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            printpage();
                                                            party_btn_0.setVisibility(View.VISIBLE);
                                                            party_btn_1.setVisibility(View.GONE);
                                                            Toast.makeText(getApplicationContext(), "참여 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                        }
                                                    }).show();
                                        }
                                    });
                                }
                            });

                        }else {
                            Toast.makeText(getApplicationContext(), "거래 진행 중에는 취소하실 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        //참가 버튼
        party_btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("form").child(FID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        int state = Integer.parseInt(task.getResult().child("state").getValue().toString());
                        if (state == 0) {
                            int max = Integer.parseInt(String.valueOf(task.getResult().child("max_count").getValue()));
                            int now = Integer.parseInt(String.valueOf(task.getResult().child("parti_num").getValue()));
                            if (max > now) {
                                AlertDialog.Builder AddParty_alert = new AlertDialog.Builder(FormdetailActivity.this);
                                AddParty_alert.setTitle("거래에 참여하시겠습니까?")
                                        .setMessage("참여인원이 마감되거나 마감기간이 종료되면 거래가 진행됩니다.\n" +
                                                "거래가 진행되면 참여를\n" + "취소할 수 없습니다.\n")
                                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                HashMap<String, Object> childUpdates = new HashMap<>();
                                                childUpdates.put(user.getUid(), getToday());
                                                mDatabase.child("party").child(FID).updateChildren(childUpdates);
                                                mDatabase.child("form").child(FID).child("parti_num").setValue(now + 1);
                                                Toast.makeText(getApplicationContext(), "참여되었습니다", Toast.LENGTH_SHORT).show();
                                                party_btn_0.setVisibility(View.GONE);
                                                party_btn_1.setVisibility(View.VISIBLE);
                                                printpage();
                                            }
                                        })
                                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                            }
                                        }).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "참여 인원이 꽉 찼습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "이미 거래가 진행 중인 게시글 입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        ToggleButton btn_heart = findViewById(R.id.heart);
        mDatabase.child("heart").child(user.getUid()).child(FID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    k=dataSnapshot.getKey();

                    if (dataSnapshot.getValue().equals("true")) {

                        btn_heart.setBackgroundResource(R.drawable.full_heart);
                    } else {
                        btn_heart.setBackgroundResource(R.drawable.empty_heart);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {    }
        });

        //찜하기버튼
        btn_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("heart").child(user.getUid()).child(FID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {    }
                });
                if ( btn_heart.isChecked()){mDatabase.child("heart").child(user.getUid()).child(FID).setValue("true");}
                else if( ! btn_heart.isChecked()){mDatabase.child("heart").child(user.getUid()).child(FID).setValue("false");}
            }
            ;
        });
        return;
    }

    @Override
    public void onBackPressed() {
        mDatabase.child("form").child(FID).child("count").setValue(count+1);

        finish();
    }
    private String getToday(){
        SimpleDateFormat mFormat1 = new SimpleDateFormat("yyyyMMddhhmmss");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);

        return mFormat1.format(mDate);
    }

    public void getImage(int photo_num){



        uriList.clear();
        for (int i = 0; i < photo_num; i++){
            String imageUri = "photo/"+FID+"_"+(i+1)+".png";  // 선택한 이미지들의 uri를 가져온다.

            try {
                uriList.add(imageUri);  //uri를 list에 담는다.

            } catch (Exception e) {
                Log.e(TAG, "File select error", e);
            }
        }

        adapter = new MultiImageAdapter1(uriList, this);
        recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));     // 리사이클러뷰 수평 스크롤 적용

    }
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        Geocoder geocoder = new Geocoder(this);


        mDatabase.child("form").child(FID).child("addr_co").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().getValue().toString().length()!=0){


                    String addr_co = task.getResult().getValue().toString();
                    String[] PointArray = addr_co.split(",");
                    String latitude = PointArray[0]; // 경도
                    String longitude = PointArray[1]; // 위도
                    LatLng point1 = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    marker.setPosition(point1);
                    marker.setMap(naverMap);
                    // 해당 좌표로 화면 줌
                    CameraPosition cameraPosition = new CameraPosition(point1, 16);

                    naverMap.setCameraPosition(cameraPosition);



                }else{

                    mapView.setVisibility(View.GONE);
                }

            }
        });
    }


    private void printpage(){
        mDatabase.child("form").child(FID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String subject = dataSnapshot.child("subject").getValue().toString();
                    String text = dataSnapshot.child("text").getValue().toString();

                    DecimalFormat myFormatter = new DecimalFormat("###,###");
                    String cost         = myFormatter.format(dataSnapshot.child("cost").getValue());
                    String category     = dataSnapshot.child("category_text").getValue().toString();
                    String max_count    = dataSnapshot.child("max_count").getValue().toString();
                    String today        = dataSnapshot.child("today").getValue().toString();
                    String deadline     = dataSnapshot.child("deadline").getValue().toString();
                    String state        = dataSnapshot.child("state").getValue().toString();
                    String express      = dataSnapshot.child("express").getValue().toString();
                    String address      = dataSnapshot.child("address").getValue().toString();
                    String addr_detail   = dataSnapshot.child("addr_detail").getValue().toString();
                    int photo_num   = Integer.parseInt(dataSnapshot.child("photo_num").getValue().toString());

                    int count_party= Integer.parseInt(dataSnapshot.child("parti_num").getValue().toString()) ;

                    Resources res = getResources();
                    String[] cat = res.getStringArray(R.array.category);
                    category=cat[Integer.parseInt(category)];

//                    image=dataSnapshot.child("image").getValue().toString().replace(".png","_1.png");
                    count=Integer.parseInt(dataSnapshot.child("count").getValue().toString());

                    UserFind(FUID);
                    String numb ="";
                    if(max_count.equals("1000")){
                        numb="∞";
                    }else{
                        numb = count_party+"/"+max_count;
                    }
                    Initializeform(subject,category,text,cost,numb,today,deadline,address,addr_detail,express,count,state);
                    getImage(photo_num);
                    //                    StorageReference pathReference = firebaseStorage.getReference(image);
//
//
//                    ImageView mainImage = (ImageView) findViewById(R.id.mainImage);
//                    FormdetailActivity activity = (FormdetailActivity) mainImage.getContext();
//                    pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            if (activity.isFinishing()) return;
//                            Glide.with(mainImage)
//                                    .load(uri)
//                                    .into(mainImage);
//                        }
//                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }

    private void UserFind(String FUID){
        mDatabase.child("users").child(FUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                TextView name_tv    = (TextView) findViewById(R.id.detail_nick);
                ImageView profile   = (ImageView) findViewById(R.id.detail_profile);
                TextView local_detail_text   = (TextView) findViewById(R.id.detail_local);
                String profile_text = task.getResult().child("image").getValue().toString();
                String name         = task.getResult().child("nick").getValue().toString();

                if(task.getResult().child("area").exists()){
                    String local_detail = task.getResult().child("area").getValue().toString();
                    local_detail_text.setText(local_detail);
                }
                name_tv.setText(name);
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference pathReference  = firebaseStorage.getReference(profile_text);
                FormdetailActivity activity1    = (FormdetailActivity) profile.getContext() ;
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (activity1.isFinishing()) return;
                        Glide.with(profile)
                                .load(uri)
                                .into(profile);
                    }
                });
            }
        });
    }

    private void Initializeform
            (String subject,String category,String text,String cost,String numb,
             String today,String deadline,String address,String addr_detail, String express,Integer count,String state)
    {
        TextView subject_text   = (TextView) findViewById(R.id.detail_subject);
        TextView category_text  = (TextView) findViewById(R.id.detail_category);
        TextView text_text      = (TextView) findViewById(R.id.detail_textarea);
        TextView cost_text      = (TextView) findViewById(R.id.detail_cost);
        TextView max_count_text = (TextView) findViewById(R.id.detail_party_num);
        TextView start          = (TextView) findViewById(R.id.detail_startdate);
        TextView deadlines      = (TextView) findViewById(R.id.detail_deadline);
        TextView express_text   = (TextView) findViewById(R.id.detail_express);
        TextView count_text     = (TextView) findViewById(R.id.detail_counttext);
        TextView state_text     = (TextView) findViewById(R.id.detail_state);
        TextView address_text       = (TextView) findViewById(R.id.address);
        TextView addr_detail_text   = (TextView) findViewById(R.id.detail_address);

        switch(state){
            case "0":
                state_text.setText("[참여모집]");
                state_text.setTextColor(Color.parseColor("#F1A94E"));
                break;
            case "1":
                state_text.setText("[거래진행]");
                state_text.setTextColor(Color.parseColor("#274E13"));
                break;
            case "2":
                state_text.setText("[거래완료]");
                state_text.setTextColor(Color.parseColor("#4C4C4C"));
                break;
        }

        if(numb.equals("∞")){
            numb="인원제한없음";
            max_count_text.setTextSize(15);
        }
        subject_text.setText(subject);
        category_text.setText(category);
        text_text.setText(text);
        cost_text.setText(cost);
        start.setText(today.substring(2,4)+"년 "+today.substring(4,6)+"월 "+today.substring(6,8)+"일");
        deadlines.setText(deadline.substring(2,4)+"년 "+deadline.substring(4,6)+"월 "+deadline.substring(6,8)+"일");
        max_count_text.setText(numb);
        express_text.setText(express);
        count_text.setText("조회 "+count);
        address_text.setText(address);
        addr_detail_text.setText(addr_detail);


    }
}

