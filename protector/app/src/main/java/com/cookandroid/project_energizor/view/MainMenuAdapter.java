package com.cookandroid.project_energizor.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cookandroid.project_energizor.R;

//메인 페이지 메뉴 Adapter
public class MainMenuAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;

    int[] arrMenuImage;

    public MainMenuAdapter(Context context, int[] arrMenuImage) {

        this.context = context;
        this.arrMenuImage = arrMenuImage;
    }

    //자식 뷰(어댑터 뷰의 자식 뷰)들의 개수를 리턴하는 메소드
    @Override
    public int getCount() {
        return arrMenuImage.length;
    }

    //항목(자식 뷰의 내용을 갖는 객체) 중 하나를 리턴
    //int position은 리턴할 항목의 위치를 의미
    @Override
    public Object getItem(int position) {
        return arrMenuImage[position];
    }

    //어댑터가 갖는 항목의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return 0;
    }

    //각 그리드 칸마다 이미지뷰, 텍스트뷰 생성해서 보여줌
    //자식 뷰들 중 하나를 리턴하는 메소드, 값이 null이면, 자식 뷰를 생성
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (inflater == null) {
            //LayoutInflater(xml 리소스를 전개하여 뷰 객체를 만듬) 객체 사용할 준비
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (view == null) {
            //사전에 미리 선언해뒀던 레이아웃에 작성했던 xml의 메모리 객체가 삽입
            //인수 root는 생성된 뷰의 루트로 사용할 뷰, 리소스 내에 루트가 따로 있어 null 사용
            view = inflater.inflate(R.layout.mainpage_menu, null);
        }

        ImageView menuImage = view.findViewById(R.id.mainMenuImage);

        menuImage.setImageResource(arrMenuImage[position]);

        //메뉴 클릭 시 activity 전환
        menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == 0) {
                    Intent intent = new Intent(context, FirstActivity.class);
                    context.startActivity(intent);
                }
                else if(position == 1) {
                    Intent intent = new Intent(context, SecondActivity.class);
                    context.startActivity(intent);
                }
                else if(position == 2) {
                    Intent intent = new Intent(context, ThirdActivity.class);
                    context.startActivity(intent);
                }
                else if(position == 3) {
                    Intent intent = new Intent(context, FourthActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        return view;
    }
}
