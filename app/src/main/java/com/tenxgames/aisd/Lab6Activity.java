package com.tenxgames.aisd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Lab6Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab6);

        Button btn = findViewById(R.id.lab6StartBFS);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CanvasView canvas = findViewById(R.id.lab6CanvasView);
                ArrayList<CanvasView.Node> nodes = canvas.getListNodes();

                TextView tw = findViewById(R.id.lab6Result);
                tw.setText("Обход в ширину: " + BFS(nodes));
            }
        });

    }

    /**
     * Производит обход графа в ширину
     *
     * @param nodes Лист текущих вершин со связями
     *
     * @return Строка, в которой располагаются номера мершин через пробел в порядке обхода
     */
    public String BFS(ArrayList<CanvasView.Node> nodes) {
        ///Очередь
        Queue<CanvasView.Node> queue = new LinkedList<>();
        /// Массив флагов для обозначения просмотренных вершин
        boolean[] watched = new boolean[nodes.size()+1];

        /// Первая вершина сразу просмотрена и в очереди
        queue.add(nodes.get(0));
        watched[nodes.get(0).id] = true;
        CanvasView.Node current;
        String res = "";

        /// Пока очередь не пустая
        while (queue.size() != 0) {
            /// Вынимаем вершину из очереди и записываем в результат
            current = queue.poll();
            res = res + current.id + " ";

            /// Для всех вершин, связанных с текущей
            for (CanvasView.Link link : current.links) {
                /// Если она еще не просмотрена
                if (!watched[link.node2.id]) {
                    /// Добавляем в очередь и ставим, как просмотренную
                    queue.add(link.node2);
                    watched[link.node2.id] = true;
                }
            }
        }
        return res;
    }


}
