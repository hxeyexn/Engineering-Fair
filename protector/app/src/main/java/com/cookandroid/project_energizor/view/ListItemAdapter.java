package com.cookandroid.project_energizor.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cookandroid.project_energizor.R;

import java.util.ArrayList;

//실종자 찾기 ListItemAdapter
public class ListItemAdapter extends BaseAdapter {
    ArrayList<ListItem> items = new ArrayList<ListItem>();
    Context context;

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        context = viewGroup.getContext();
        ListItem listItem = items.get(position);

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_item, viewGroup, false);
        }


        TextView name = view.findViewById(R.id.name);
        TextView sex = view.findViewById(R.id.sex);
        TextView age = view.findViewById(R.id.age);
        TextView location = view.findViewById(R.id.position);
        TextView description = view.findViewById(R.id.description);


        name.setText(listItem.getName());
        sex.setText(listItem.getSex());
        age.setText(listItem.getAge());
        location.setText(listItem.getPosition());
        description.setText(listItem.getDescription());

        return view;
    }

    public void addItem(ListItem item) {
        items.add(item);
    }
}
