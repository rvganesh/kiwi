package com.sevenheaven.kiwi;

import android.graphics.BitmapRegionDecoder;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    TestView testView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        testView = (TestView) findViewById(R.id.test_view);
//        dispalyPattern(generateSolution());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static int[][] generateSolution() {
        final int gridNumber = 3;
        final int[][] gridPattern = new int[gridNumber*gridNumber][gridNumber*gridNumber];

        for(int loop1 = 0; loop1<gridNumber*gridNumber; loop1++) {

            for(int loop2 = 0;loop2<gridNumber*gridNumber;loop2++) {
                gridPattern[loop1][loop2] = (loop1*gridNumber + loop1/gridNumber + loop2) % (gridNumber*gridNumber) + 1;
            }
        }

        return gridPattern;
    }

    public static void dispalyPattern(int[][] matrix) {

        int rowLenth = matrix.length;
        int columnLength = matrix[0].length;

        for(int loop1 = 0;loop1<rowLenth;loop1++) {
            for(int loop2 = 0;loop2<columnLength;loop2++) {

                System.out.print(matrix[loop1][loop2]+" ");
            }
            System.out.println("");
        }
    }


}
