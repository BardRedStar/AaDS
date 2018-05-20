package com.tenxgames.aisd;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Lab8Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab8);

        Button btn = findViewById(R.id.lab8Start);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CanvasView canvas = findViewById(R.id.lab8CanvasView);
                ArrayList<CanvasView.Node> nodes = canvas.getListNodes();

                if (nodes.size() == 0)
                    return;

                ArrayList<CanvasView.Node> tree = Prima(nodes);

                Intent intent = new Intent(Lab8Activity.this, GraphResultActivity.class);
                intent.putExtra("nodes", treeToJSON(tree));
                startActivity(intent);
            }
        });
    }

    /**
     * Строит остовной граф минимальной стоимости, начиная с вершины 1
     *
     * @param nodes Лист узлов со связями, описывающих текущий граф
     *
     * @return Лист узлов со связями, описывающий остовной граф минимальной стоимости
     */
    public ArrayList<CanvasView.Node> Prima(ArrayList<CanvasView.Node> nodes) {
        ArrayList<CanvasView.Node> tree = new ArrayList<>();

        tree.add(new CanvasView.Node(nodes.get(0).id, nodes.get(0).x, nodes.get(0).y));
        int min;
        int min_node;
        CanvasView.Link min_link;
        while (tree.size() != nodes.size()) {
            min = Integer.MAX_VALUE;
            min_link = null;
            min_node = 0;
            for (int i = 0; i < tree.size(); i++) {
                for (CanvasView.Link link : nodes.get(tree.get(i).id - 1).links) {
                    boolean isAlreadyLinked = false;
                    for (int j = 0; j < tree.size(); j++) {
                        if (tree.get(j).id == link.node2.id) {
                            isAlreadyLinked = true;
                            break;
                        }
                    }
                    if (min > link.weight && !isAlreadyLinked) {
                        min_node = i;
                        min = link.weight;
                        min_link = link;
                    }
                }
            }

            if (min_link != null) {
                tree.add(new CanvasView.Node(min_link.node2.id, min_link.node2.x, min_link.node2.y));
                tree.get(tree.size() - 1).linkNode(tree.get(min_node), min_link.weight);
                tree.get(min_node).linkNode(tree.get(tree.size() - 1), min_link.weight);
            } else break;
        }

        return tree;
    }

    /**
     * Переводит лист узлов со связями в формат JSON
     *
     * @param tree Лист узлов со связями
     *
     * @return Строка JSON с содержанием листа узлов
     */
    public String treeToJSON(ArrayList<CanvasView.Node> tree) {
        try {
            JSONArray res = new JSONArray();
            JSONObject obj, linkObj;
            JSONArray linksArray;

            for (CanvasView.Node node : tree) {
                obj = new JSONObject();
                obj.put("id", node.id);
                obj.put("x", node.x);
                obj.put("y", node.y);
                linksArray = new JSONArray();
                for (CanvasView.Link link : node.links) {
                    linkObj = new JSONObject();
                    linkObj.put("node", link.node2.id);
                    linkObj.put("weight", link.weight);
                    linksArray.put(linkObj);
                }
                obj.put("links", linksArray);
                res.put(obj);
            }

            return res.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
