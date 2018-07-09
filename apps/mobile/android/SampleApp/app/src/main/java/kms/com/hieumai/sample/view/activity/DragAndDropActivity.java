package kms.com.hieumai.sample.view.activity;

import android.support.v7.app.ActionBarActivity;
import android.content.ClipData;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import kms.kms.hieumai.sample.R;

public class DragAndDropActivity extends ActionBarActivity {

    LinearLayout area1, area2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop);
        area1 = (LinearLayout) findViewById(R.id.area1);
        area2 = (LinearLayout) findViewById(R.id.area2);

        TypedArray arrayResources = getResources().obtainTypedArray(
                R.array.resicon);

        for (int i = 0; i < arrayResources.length(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(arrayResources.getDrawable(i));
            imageView.setOnLongClickListener(myOnLongClickListener);
            area1.addView(imageView);
        }

        arrayResources.recycle();

        area1.setOnDragListener(myOnDragListener);
        area2.setOnDragListener(myOnDragListener);
    }

    OnLongClickListener myOnLongClickListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            ClipData data = ClipData.newPlainText("", "");
            DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            //v.setVisibility(View.INVISIBLE);
            return true;
        }

    };

    OnDragListener myOnDragListener = new OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    LinearLayout oldParent = (LinearLayout) view.getParent();
                    oldParent.removeView(view);
                    LinearLayout newParent = (LinearLayout) v;
                    newParent.addView(view);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                default:
                    break;
            }
            return true;
        }

    };
}