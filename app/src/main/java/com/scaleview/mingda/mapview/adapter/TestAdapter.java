package com.scaleview.mingda.mapview.adapter;

import android.content.Context;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.scaleview.mingda.mapview.R;
import com.scaleview.mingda.mapview.view.MapView;

/**
 * Created by 玉光 on 2017-9-26.
 */

public class TestAdapter extends MapView.Adapter<TestAdapter.TextHolder> {

    private Context context;

    public TestAdapter(Context context) {

        this.context = context;
    }

    @Override
    public TextHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextHolder(LayoutInflater.from(context).inflate(R.layout.item_test, parent, false));
    }

    @Override
    public void onBindViewHolder(TextHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.textView.setText(position + "");
    }

    @Override
    public PointF onBindPosition(int position) {
        return new PointF(2600 * position / 15+15, 1164 * position / 15+15);
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    public class TextHolder extends MapView.ViewHolder {

        public TextView textView;

        public TextHolder(final View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textview);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "我被点击了"+itemView.getTag(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
