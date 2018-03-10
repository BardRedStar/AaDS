package com.tenxgames.aisd;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn;
        LinearLayout layout = findViewById(R.id.mainLinearLayout);
        for (int i = 1; ; i++) {
            try {
                final Class activity = Class.forName(getPackageName()+".Lab" + i + "Activity");
                btn = new Button(this);
                LinearLayout.LayoutParams lp =
                        new LinearLayout.LayoutParams(dpToPx(250),dpToPx(50));

                lp.setMargins(0, dpToPx(24), 0, 0);
                btn.setLayoutParams(lp);
                btn.setText("Лабораторная работа " + i);
                btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btn.setTextColor(Color.WHITE);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), activity);
                        startActivity(intent);
                    }
                });
                layout.addView(btn);
            } catch (ClassNotFoundException e) {
                return;
            }
        }
    }

    /**
     * Переводит значение из DP в пиксели.
     *
     * @param dp Значение в DP.
     * @return Значение в пикселях.
     *
     * @see DisplayMetrics
     * @see Math
     */
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
