package com.tenxgames.aisd;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Random;

public class SwapsTableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swaps_table);

        int[] swapsArray = getIntent().getExtras().getIntArray("swaps");
        int[] numbersArray = getIntent().getExtras().getIntArray("numbers");

        if (swapsArray != null && numbersArray != null) {
            TableLayout table = findViewById(R.id.swapsTable);
            TableRow row;
            TextView tw;

            for (int i = 0; i < numbersArray.length; i++) {
                row = new TableRow(this);
                row.setGravity(Gravity.CENTER);
                tw = new TextView(this);
                tw.setText(""+numbersArray[i]);
                tw.setTextSize(20);

                if ((i != (numbersArray.length - 1)) && (i != 0)){
                    if (numbersArray[i] == numbersArray[i+1])
                        tw.setTextColor(generateNextColor());
                    else if (numbersArray[i] == numbersArray[i-1])
                        tw.setTextColor(generateNextColor());
                }
                else if (i == 0 && numbersArray[0] == numbersArray[1])
                    tw.setTextColor(generateNextColor());
                else if (i == numbersArray.length-1 && numbersArray[i] == numbersArray[i-1])
                    tw.setTextColor(generateNextColor());


                row.addView(tw);
                tw = new TextView(this);
                tw.setText(" "+swapsArray[i]);
                tw.setTextSize(20);
                row.addView(tw);

                table.addView(row);
            }
        }
    }

    private int generateNextColor()
    {
        Random rnd = new Random();

        return Color.rgb(rnd.nextInt(254)+1,
                rnd.nextInt(254)+1, rnd.nextInt(254)+1);
    }
}
