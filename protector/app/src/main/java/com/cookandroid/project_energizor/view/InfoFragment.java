package com.cookandroid.project_energizor.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.cookandroid.project_energizor.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

//내 정보 페이지(수정 및 삭제)
public class InfoFragment extends Fragment {

    private String Pk;
    private FirebaseAuth auth;
    String Age, Position, Description;
    ViewGroup rootView;
    TextView Id;
    EditText UserName, Rrn, Phone, Nationality, Sex, Height, Weight, Blood, ProtectorName, ProtectorPhone;
    Button DeleteUser, UpdateUser;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info, container, false);

        auth = FirebaseAuth.getInstance();

        Id = rootView.findViewById(R.id.Id);
        UserName = rootView.findViewById(R.id.uUserName);
        Rrn = rootView.findViewById(R.id.uRrn);
        Phone = rootView.findViewById(R.id.uPhone);
        Nationality = rootView.findViewById(R.id.uNationality);
        Sex = rootView.findViewById(R.id.uSex);
        Height = rootView.findViewById(R.id.uHeight);
        Weight = rootView.findViewById(R.id.uWeight);
        Blood = rootView.findViewById(R.id.uBlood);
        ProtectorName = rootView.findViewById(R.id.uProtectorName);
        ProtectorPhone = rootView.findViewById(R.id.uProtectorPhone);
        DeleteUser = rootView.findViewById(R.id.DeleteUser);
        UpdateUser = rootView.findViewById(R.id.UpdateUser);

        String id = auth.getCurrentUser().getEmail();
        readUser(id);

        Id.setText(id);

        //사용자 정보 삭제 버튼 눌렸을 때
        DeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(Id.getText().toString());

                Id.setText("");
                UserName.setText("");
                Rrn.setText("");
                Phone.setText("");
                Nationality.setText("");
                Sex.setText("");
                Height.setText("");
                Weight.setText("");
                Blood.setText("");
                ProtectorName.setText("");
                ProtectorPhone.setText("");

                Toast.makeText(getActivity(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        //사용자 정보 수정 버튼 눌렸을 때
        UpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser(Pk, UserName.getText().toString(), Rrn.getText().toString(), Phone.getText().toString(),
                        Nationality.getText().toString(), Sex.getText().toString(), Height.getText().toString(),
                        Weight.getText().toString(), Blood.getText().toString(), ProtectorName.getText().toString(),
                        ProtectorPhone.getText().toString(), Age, Position, Description);

                Toast.makeText(getActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    //DB에서 사용자 정보 불러옴
    private void readUser(String Id) {
        Query keyQuery = databaseReference.child("DataSet").orderByChild("ProtectorApp/Id").equalTo(Id);
        keyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Pk = childSnapshot.getKey();
                    Log.i("Pk", Pk);

                    Query userQuery = databaseReference.child("/DataSet/").child(Pk).child("/ProtectorApp/").child("/PersonalInformation/");
                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            UserName.setText(user.UserName);
                            Rrn.setText(user.Rrn);
                            Phone.setText(user.Phone);
                            Nationality.setText(user.Nationality);
                            Sex.setText(user.Sex);
                            Height.setText(user.Height);
                            Weight.setText(user.Weight);
                            Blood.setText(user.Blood);
                            ProtectorName.setText(user.ProtectorName);
                            ProtectorPhone.setText(user.ProtectorPhone);
                            Age = user.Age;
                            Position = user.Position;
                            Description = user.Description;

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //사용자 정보 삭제
    private void deleteUser(String Id) {
        Query keyQuery = databaseReference.child("DataSet").orderByChild("ProtectorApp/Id").equalTo(Id);
        keyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Pk = childSnapshot.getKey();
                    Log.i("Pk", Pk);

                    databaseReference.child("/DataSet/").child(Pk).removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //사용자 정보 저장
    private void updateUser(String Pk, String UserName, String Rrn, String Phone, String Nationality, String Sex, String Height, String Weight, String Blood, String ProtectorName, String ProtectorPhone, String Age, String Position, String Description) {
        User User = new User(UserName, Rrn, Phone, Nationality, Sex, Height, Weight, Blood, ProtectorName, ProtectorPhone, Age, Position, Description);
        Map<String, Object> userValues = User.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/DataSet/" + Pk + "/ProtectorApp/" + "/PersonalInformation/" , userValues);
        databaseReference.updateChildren(childUpdates);
    }

}

