package com.example.moamoa.ui.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moamoa.Form;
import com.example.moamoa.R;
import com.example.moamoa.databinding.ActivityChatsBinding;
import com.example.moamoa.ui.account.User;
import com.example.moamoa.ui.chatlist.ChatListFragment;
import com.example.moamoa.ui.formdetail.FormdetailActivity;
import com.example.moamoa.ui.home.homelist_adapter;
import com.example.moamoa.ui.home.homelist_data;
import com.example.moamoa.ui.notifications.NotificationsAdapter;
import com.example.moamoa.ui.notifications.NotificationsData;
import com.example.moamoa.ui.search.SearchActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {
    //
    private ActivityChatsBinding binding;

    private List<ChatModel> chatModels = new ArrayList<>();
    private ArrayList<String> destinationUsers = new ArrayList<>();

    private String Chatroomname, Formid;

    private String UID, destinationuid;
    private String myNICK, destinationNICK;

    private ChatsFragment chatsFragment = new ChatsFragment();

    private FirebaseDatabase mDatabase;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<ChatsUserData> arrayList = new ArrayList<>();
    private ChatUserAdapter chatUserAdapter;
    private RecyclerView recyclerView;

    TextView TextView_mynick, TextView_mynation;
    TextView chatbar, TextView_destinationnick, TextView_destinationsnation;
    ImageView myPfImage, destinationPfImage;

    private final HashMap<Integer, String> langHashmap = new HashMap<>();
    static int select_lang = 0;

    final int DIALOG_EXITROOM = 1;
    final int DIALOG_SELECTLANG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        // 사용자의 UID, 닉네임 불러옴
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // FormdetailActivity와 ChatListFragment에서 값 받음
        Intent getIntent = getIntent();
        destinationuid = getIntent.getStringExtra("destinationUID");


        // 받은 값 ChatsFragment에 넘겨줌
        Bundle bundle = new Bundle();
        bundle.putString("destinationUID", destinationuid);
        chatsFragment.setArguments(bundle);
        /*
         * ChatsFragment로 값이 안넘어갔던 이유: xml에 fragmentcontainerview 있음
         * --> 값을 넘기기 전에 ChatsFragment가 만들어져 null값이 됨
         * 해결 위해 fragmentcontainerview 대신 framelayout 사용 후 밑 코드로 ChatsFragment 연결해줌
         */

        // 프래그먼트 매니저로 chatscontainer에 chatsFragment 연결해줌
        getSupportFragmentManager().beginTransaction().replace(R.id.chatscontainer, chatsFragment).commit();


        mDatabase = FirebaseDatabase.getInstance();
        //recyclerView = findViewById(R.id.chats_recyclerview_userinfo);

        //getUserList();

        // 해시맵에 사용언어 설정을 위한 string값 넣어줌
        langHashmap.put(0, "KOR");
        langHashmap.put(1, "ENG");
        langHashmap.put(2, "CHI");

        // 채팅방 이름 설정
        chatbar = findViewById(R.id.chatbarname);

        // 내 프로필
        TextView_mynick = (TextView) findViewById(R.id.chats_TextView_mynickname);
        TextView_mynation = (TextView) findViewById(R.id.chats_mynationality);
        myPfImage = (ImageView) findViewById(R.id.chats_myprofile_image);
        getuserprofile(UID);

        // 상대방 프로필
        TextView_destinationnick = (TextView) findViewById(R.id.chats_TextView_theirnickname);
        TextView_destinationsnation = (TextView) findViewById(R.id.chats_theirnationality);
        destinationPfImage = (ImageView) findViewById(R.id.chats_theirprofile_image);
        getuserprofile(destinationuid);

        /* 채팅방 이름 세팅
         * 그러니까 여기서 뭘해야되냐면... 일단 거기서도 채팅이 되고 채팅 리스트가 따로 있는 한 넘겨받아서 할순없음
         * 왜냐면 넘겨받은값은 폼에서 들어갈때는 몰라도 채팅 리스트에선 안넘겨받앗으니까 없는거잖아...
         * 그럼 거기 있는게 뭐냐면 일단 사람 아이디는 있는듯
         * 습 그럼 그냥 폼으로 나누겠다는 걸 없애고 그냥 사람이랑 채팅을 하고
         *
         * 정리
         * 공지--> 폼이 중요 (추후 제작) 폼 정보 넣어서 공지방 만듦
         * 그 외 문의채팅--> 폼 중요X 사람 정보만 있으면 됨
         * 그럼 폼 제목에 사람 이름 넣는걸로 하면 될듯 굿굿
         * */

        // 채팅창 메뉴
        findViewById(R.id.drawer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open right drawer
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.openDrawer(Gravity.RIGHT) ;
                    Log.d(this.getClass().getName(), "서랍 열기");
                }
                else{
                    drawer.closeDrawer(Gravity.RIGHT);
                    Log.d(this.getClass().getName(), "서랍 닫기");
                }
            }
        });

        findViewById(R.id.chats_btn_getoutofroom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_EXITROOM); // 다이얼로그 호출
            }
        });

        findViewById(R.id.chats_btn_selectlanguage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_SELECTLANG); // 다이얼로그 호출
            }
        });

        // 기본 툴바 숨김
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


    }

    private void getuserprofile(String string){
        FirebaseDatabase.getInstance().getReference().child("users").child(string).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (string.equals(UID)){
                    // 헤더에서 내 닉네임 보여줌
                    TextView_mynick.setText(user.nick);
                    TextView_mynation.setText(langHashmap.get(select_lang));

                    // 헤더에서 내 프사 보여줌
                    String myprofil_text = snapshot.child("image").getValue().toString();
                    FirebaseStorage.getInstance().getReference(myprofil_text)
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Activity context = (Activity) myPfImage.getContext();
                            if(context.isFinishing()) return;
                            Glide.with(myPfImage)
                                    .load(uri)
                                    .into(myPfImage);
                        }
                    });
                } else {
                    // 채팅방 이름 설정
                    chatbar.setText(user.nick);

                    // 헤더에서 상대방 닉네임 보여줌
                    TextView_destinationnick.setText(user.nick);

                    // 헤더에서 상대방 프사 보여줌
                    String destinationprofil_text = snapshot.child("image").getValue().toString();
                    FirebaseStorage.getInstance().getReference(destinationprofil_text)
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Activity context = (Activity) destinationPfImage.getContext();
                            if(context.isFinishing()) return;
                            Glide.with(destinationPfImage)
                                    .load(uri)
                                    .into(destinationPfImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    protected Dialog onCreateDialog(int id){
        Resources res = getResources();
        switch (id){
            case DIALOG_EXITROOM:
                androidx.appcompat.app.AlertDialog.Builder builder1
                        = new androidx.appcompat.app.AlertDialog.Builder(ChatsActivity.this);

                builder1.setTitle("채팅방 나가기")
                        .setMessage("채팅방을 나가시겠습니까?");

                builder1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                return builder1.create();

            case DIALOG_SELECTLANG:
                final String[] lang = res.getStringArray(R.array.selectlanguage);
                androidx.appcompat.app.AlertDialog.Builder builder2
                        = new androidx.appcompat.app.AlertDialog.Builder(ChatsActivity.this);

                builder2.setTitle("언어 선택");

                builder2.setSingleChoiceItems(lang, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ChatsActivity.this, lang[which], Toast.LENGTH_SHORT).show();
                        select_lang = Arrays.asList(lang).indexOf(lang[which]);
                        getuserprofile(UID);

                        dialog.dismiss(); // 누르면 바로 닫히는 형태
                    }
                });

                return builder2.create();
        }
        return super.onCreateDialog(id);
    }

    /*
    public void InitializeData(String profileimg, String nationality, String nick, String UID){
        ChatsUserData tmpdata = new ChatsUserData();
        tmpdata.setUID(UID);
        tmpdata.setUsernick(nick);
        tmpdata.setProfilepic(profileimg);
        tmpdata.setNationality(nationality);
        arrayList.add(tmpdata);
    }

    public void getUserList(){
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+UID)
                .equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            // 여기서 equalTo는 true까지의 방만 검색한다. (내가 소속된 방만 검색)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 데이터 받아오기 세팅
                chatModels.clear();
                for (DataSnapshot item : snapshot.getChildren()){
                    ChatModel chatModel = item.getValue(ChatModel.class); //채팅방 아래 데이터 가져옴
                    assert chatModel != null;
                    if (chatModel.users.containsKey(destinationuid)){   //destinationUID 있는지 체크
                        chatModels.add(item.getValue(ChatModel.class));
                        Log.e("TEST666", "chatModels"+chatModels);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        mDatabase.getReference().child("user").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

            }
        });
    }

     */
}