package com.cookandroid.project_energizor.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cookandroid.project_energizor.R;

import java.util.ArrayList;

//알림 리스트 adapter
public class NoticeAdapter extends BaseAdapter {

    private ArrayList<NoticeList> noticeListItem = new ArrayList<>();

    @Override
    public int getCount() {
        return noticeListItem.size();
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public NoticeList getItem(int position) {
        return noticeListItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notice_list, parent, false);
        }

        TextView noticeTitle = (TextView) convertView.findViewById(R.id.noticeTitle);
        TextView noticeDate = (TextView) convertView.findViewById(R.id.noticeDate);

        NoticeList noticeList = getItem(position);

        noticeTitle.setText(noticeList.getNoticeTitle());
        noticeDate.setText(noticeList.getNoticeDate());

        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem (String noticeTitle, String noticeDate){
        NoticeList item = new NoticeList();

        item.setNoticeTitle(noticeTitle);
        item.setNoticeDate(noticeDate);

        noticeListItem.add(item);
    }
}

