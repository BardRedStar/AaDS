package com.tenxgames.aisd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Lab7Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab7);

        Button btn = findViewById(R.id.lab7Start);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CanvasView canvas = findViewById(R.id.lab7CanvasView);
                ArrayList<CanvasView.Node> nodes = canvas.getListNodes();

                TextView tw = findViewById(R.id.lab7Result);

                if (nodes.size() == 0)
                    tw.setText("Граф пустой!");
                else
                    tw.setText(Dijkstra(nodes));
            }
        });
    }

    public String Dijkstra(ArrayList<CanvasView.Node> nodes) {

        ArrayList<CanvasView.Node> stableNodes = new ArrayList<>();
        int[] D = new int[nodes.size()];
        stableNodes.add(nodes.get(0));
        int[] path = new int[nodes.size()];

        for (int i = 1; i < D.length; i++)
            D[i] = Integer.MAX_VALUE;

        for (CanvasView.Link link : nodes.get(0).links) {
            D[link.node2.id - 1] = link.weight;
        }

        int min;
        int min_index;

        for (int i = 0; i < nodes.size() - 1; i++) {

            min = Integer.MAX_VALUE;
            min_index = 0;

            for (int j = 0; j < nodes.size(); j++) {
                if (stableNodes.indexOf(nodes.get(j)) == -1) {
                    if (min > D[j]) {
                        min = D[j];
                        min_index = j;
                    }
                }
            }
            stableNodes.add(nodes.get(min_index));

            for (int j = 0; j < nodes.size(); j++) {
                if (stableNodes.indexOf(nodes.get(j)) == -1) {
                    if (nodes.get(min_index).isNodeLinked(nodes.get(j))) {
                        D[j] = Math.min(D[j], D[min_index]
                                + nodes.get(min_index).getLinkWeight(nodes.get(j)));

                        if (D[min_index] + nodes.get(min_index).getLinkWeight(nodes.get(j)) <= D[j]) {
                            path[j] = min_index;
                        }
                    }
                }
            }
        }

        if (D[nodes.size() - 1] == Integer.MAX_VALUE)
            return "До вершины " + nodes.size() + " невозможно добраться!";

        String res = String.valueOf(nodes.size());
        int ind = nodes.size() - 1;
        while (ind != 0) {
            res = (path[ind] + 1) + " " + res;
            ind = path[ind];
        }

        return "Кратчайший путь: " + res + "\nДлина пути: " + D[nodes.size() - 1];
    }
}
