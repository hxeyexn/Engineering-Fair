package com.cookandroid.project_energizor.view;

import java.util.HashMap;
import java.util.Map;

//NoticeMsg model
public class NoticeMsg {

    public Boolean NoticeMsg;

    public NoticeMsg() {}

    public NoticeMsg(boolean NoticeMsg) {
        this.NoticeMsg = NoticeMsg;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("NoticeMsg", NoticeMsg);

        return result;
    }
}

