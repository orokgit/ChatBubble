package com.javapapers.android.chat;

/**
 * Created by orok on 14.10.2014.
 */
import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatSCA extends SimpleCursorAdapter {

    private int layout;

    public ChatSCA(Context _context, int _layout, Cursor _cursor, String[] _from, int[] _to, int _flag) {
        super(_context, _layout, _cursor, _from, _to, _flag);
        layout = _layout;
    }

    //связывает данные с view на которые указывает курсор
    @Override
    public void bindView(View view, Context _context, Cursor _cursor) {

        String from_user = _cursor.getString(_cursor.getColumnIndex(MyContactsProvider.MESSAGE_FROM_USER));
        String to_user = _cursor.getString(_cursor.getColumnIndex(MyContactsProvider.MESSAGE_TO_USER));
        String content = _cursor.getString(_cursor.getColumnIndex(MyContactsProvider.MESSAGE_CONTENT));
        Long date = _cursor.getLong(_cursor.getColumnIndex(MyContactsProvider.MESSAGE_DATE)) / 1000;

        TextView tvFromUser = (TextView) view.findViewById(R.id.from_user);
        TextView tvToUser = (TextView) view.findViewById(R.id.to_user);
        TextView tvContent = (TextView) view.findViewById(R.id.content);
        TextView tvDate = (TextView) view.findViewById(R.id.date);

        tvFromUser.setText(from_user);
        tvToUser.setText(to_user);
        tvContent.setText(content);

        //SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        tvDate.setText(format.format(new Date(date)));

        if (from_user.equals(UsersData.getInstance().getUserUuid(1)))
        {
            tvContent.setBackgroundResource(R.drawable.bubble_a);

            RelativeLayout.LayoutParams paramsTvContent = (RelativeLayout.LayoutParams)tvContent.getLayoutParams();
            paramsTvContent.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            paramsTvContent.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            //paramsTvContent. removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            tvContent.setLayoutParams(paramsTvContent);

            RelativeLayout.LayoutParams paramsTvDate = (RelativeLayout.LayoutParams)tvDate.getLayoutParams();
            paramsTvDate.addRule(RelativeLayout.END_OF, 0);
            paramsTvDate.addRule(RelativeLayout.START_OF, tvContent.getId());
            //paramsTvDate.removeRule(RelativeLayout.END_OF);
            tvDate.setLayoutParams(paramsTvDate);
        }
        else
        {
            tvContent.setBackgroundResource(R.drawable.bubble_b);

            RelativeLayout.LayoutParams paramsTvContent = (RelativeLayout.LayoutParams)tvContent.getLayoutParams();
            paramsTvContent.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            paramsTvContent.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            //paramsTvContent.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tvContent.setLayoutParams(paramsTvContent);


            RelativeLayout.LayoutParams paramsTvDate = (RelativeLayout.LayoutParams)tvDate.getLayoutParams();
            paramsTvDate.addRule(RelativeLayout.END_OF, tvContent.getId());
            paramsTvDate.addRule(RelativeLayout.START_OF, 0);
            //paramsTvDate.removeRule(RelativeLayout.START_OF);
            tvDate.setLayoutParams(paramsTvDate);
        }
    }

    //сoздаёт нвоую view для хранения данных на которую указывает курсор
    @Override
    public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, parent, false);
        return view;
    }

}