package com.cookandroid.project_energizor.view;

import android.icu.text.Transliterator;
import android.media.audiofx.AudioEffect;
import android.util.EventLogTags;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

//사용자 정보 model
public class User {

    //User DB
    public String UserName;
    public String Rrn;
    public String Phone;
    public String Nationality;
    public String Sex;
    public String Height;
    public String Weight;
    public String Blood;
    public String ProtectorName;
    public String ProtectorPhone;
    public String Age;
    public String Position;
    public String Description;

    public User() {}

    public User(String UserName, String Rrn, String Phone, String Nationality, String Sex,
                String Height, String Weight, String Blood, String ProtectorName,
                String ProtectorPhone, String Age, String Position, String Description) {
        this.UserName = UserName;
        this.Rrn = Rrn;
        this.Phone = Phone;
        this.Nationality = Nationality;
        this.Sex = Sex;
        this.Height = Height;
        this.Weight = Weight;
        this.Blood = Blood;
        this.ProtectorName = ProtectorName;
        this.ProtectorPhone = ProtectorPhone;
        this.Age = Age;
        this.Position = Position;
        this.Description = Description;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("UserName", UserName);
        result.put("Rrn", Rrn);
        result.put("Phone", Phone);
        result.put("Nationality", Nationality);
        result.put("Sex", Sex);
        result.put("Height", Height);
        result.put("Weight", Weight);
        result.put("Blood", Blood);
        result.put("ProtectorName", ProtectorName);
        result.put("ProtectorPhone", ProtectorPhone);
        result.put("Age", Age);
        result.put("Position", Position);
        result.put("Description", Description);

        return result;
    }

}
