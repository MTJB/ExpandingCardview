package com.example.mbrpc.expandingcardview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private CardView cardViewExpanded;
    private ImageView toggle;
    private TextView passed, warn, fail;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggle = (ImageView)findViewById(R.id.imageViewExpand);
        cardViewExpanded = (CardView)findViewById(R.id.card_graph_expanded);
        cardViewExpanded.setVisibility(View.GONE);
        // Handler to expand/contract card view
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardViewExpanded.getVisibility() == View.GONE) {
                    expand();
                }
                else {
                    collapse();
                }
            }
        });

        // Add graph to webView
        final String myUrl = "file:///android_asset/chart.html";
        WebView webViewCondensed = (WebView)findViewById(R.id.webViewCondensed);
        webViewCondensed.getSettings().setJavaScriptEnabled(true);
        webViewCondensed.loadUrl(myUrl);

        // Text view listeners
        passed = (TextView)findViewById(R.id.passed);
        warn = (TextView)findViewById(R.id.warning);
        fail = (TextView)findViewById(R.id.fail);
        DetailsListener listener = new DetailsListener(passed, warn, fail);
        passed.setOnTouchListener(listener);
        warn.setOnTouchListener(listener);
        fail.setOnTouchListener(listener);
    }

    private void expand() {
        // Set visible
        cardViewExpanded.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        cardViewExpanded.measure(widthSpec, heightSpec);

        ValueAnimator animator = slideAnimator(0, cardViewExpanded.getMeasuredHeight());
        animator.start();
        toggle.setImageResource(R.drawable.less);
        rotate(-180.0f);
    }

    private void collapse() {
        int finalHeight = cardViewExpanded.getHeight();
        ValueAnimator animator = slideAnimator(finalHeight, 0);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                toggle.setImageResource(R.drawable.more);
                rotate(180.0f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //Height=0, but it set visibility to GONE
                cardViewExpanded.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.start();
    }

    private ValueAnimator slideAnimator(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Update height
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = cardViewExpanded.getLayoutParams();
                layoutParams.height = value;
                cardViewExpanded.setLayoutParams(layoutParams);
            }
        });

        return animator;
    }

    private void rotate(float angle) {
        Animation animation = new RotateAnimation(0.0f, angle, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(250);
        toggle.startAnimation(animation);
    }

    public class DetailsListener implements View.OnTouchListener {
        private TextView _passed, _warning, _fail;

        public DetailsListener(TextView _passed, TextView _warning, TextView _fail) {
            _passed = passed;
            _warning = warn;
            _fail = fail;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO switch screen to "drill down"
            switch(v.getId()) {
                case R.id.passed:
                    Toast.makeText(MainActivity.this, "Passed Clicked",
                            Toast.LENGTH_LONG).show();
                    break;
                case R.id.warning:
                    Toast.makeText(MainActivity.this, "Warning Clicked",
                            Toast.LENGTH_LONG).show();
                    break;
                case R.id.fail:
                    Toast.makeText(MainActivity.this, "Fail Clicked",
                            Toast.LENGTH_LONG).show();
                    break;
            }
            return true;
        }
    }
}
