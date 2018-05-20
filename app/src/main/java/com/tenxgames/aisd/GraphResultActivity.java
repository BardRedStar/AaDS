package com.tenxgames.aisd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GraphResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_result);
        setTitle("Остовной граф минимальной стоимости");
        String jsonTree = getIntent().getStringExtra("nodes");
        CanvasView canvas = findViewById(R.id.resultCanvasView);

        canvas.setListNodes(treeFromJSON(jsonTree));
        canvas.invalidate();
    }

    public ArrayList<CanvasView.Node> treeFromJSON(String jsonTree) {
        try {
            ArrayList<CanvasView.Node> res = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonTree);
            JSONObject jsonNode;
            CanvasView.Node node;


            JSONArray[] jsonLinks = new JSONArray[jsonArray.length()];


            for (int i=0;i<jsonArray.length();i++) {
                jsonNode = new JSONObject(jsonArray.get(i).toString());
                node = new CanvasView.Node(jsonNode.getInt("id"),
                                 (float) jsonNode.getDouble("x"),
                                 (float) jsonNode.getDouble("y"));
                res.add(node);
                jsonLinks[i] = jsonNode.getJSONArray("links");
            }

            int nodeId;
            for (int i=0;i<jsonLinks.length;i++) {
                for (int j=0;j<jsonLinks[i].length();j++) {
                    jsonNode = new JSONObject(jsonLinks[i].get(j).toString());
                    nodeId = jsonNode.getInt("node");
                    for (int x=0;x<res.size();x++) {
                        if (res.get(x).id == nodeId) {
                            nodeId = x;
                            break;
                        }
                    }

                    res.get(i).linkNode(res.get(nodeId), jsonNode.getInt("weight"));
                }
            }

            return res;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
