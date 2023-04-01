package com.cookandroid.project_energizor.view;

import java.util.HashMap;
import java.util.Map;

//실종신고 model
public class Report  {

    public String Id;
    public int PW;
    public Boolean LostWarningFlag;
    public String LostWarningTime;

    public Report() {}

    public Report(String Id, int PW, boolean LostWarningFlag, String LostWarningTime) {
        this.Id = Id;
        this.PW = PW;
        this.LostWarningFlag = LostWarningFlag;
        this.LostWarningTime = LostWarningTime;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Id", Id);
        result.put("PW", PW);
        result.put("LostWarningFlag", LostWarningFlag);
        result.put("LostWarningTime", LostWarningTime);

        return result;
    }
}

