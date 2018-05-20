package com.tenxgames.aisd;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import java.util.ArrayList;

public class CanvasView extends View {

    /// Радиус круга вершины
    final float NODE_RADIUS = 80.0f;

    /// Радиус круга выделенной вершины (зеленого)
    final float SELECTED_NODE_RADIUS = NODE_RADIUS * 1.2f;

    /// Толщина линии ребра
    final float LINE_RADIUS = 20.0f;

    /// Размер текста внутри круга вершины
    final float TEXT_SIZE = NODE_RADIUS;

    public ArrayList<Node> listNodes;
    private Paint lineBrush, circleBrush, circleSelectionBrush,
            textBrush, weightBrush, orientedLineBrushFrom, orientedLineBrushTo;
    public Node selectedNode1, selectedNode2;
    boolean isWeightEnabled;
    boolean isGraphOriented;
    private Rect textBounds;

    private AlertDialog dialogWeight;

    public CanvasView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CanvasView);
        isWeightEnabled = attributes.getBoolean(R.styleable.CanvasView_enable_weight, false);
        isGraphOriented = attributes.getBoolean(R.styleable.CanvasView_oriented, false);
        attributes.recycle();

        listNodes = new ArrayList<>();

        /// Инициализация кисти для рисования рёбер
        lineBrush = new Paint();
        lineBrush.setColor(getResources().getColor(R.color.colorPrimary));
        lineBrush.setStrokeWidth(LINE_RADIUS);

        orientedLineBrushFrom = new Paint();
        orientedLineBrushFrom.setColor(Color.RED);
        orientedLineBrushFrom.setStrokeWidth(LINE_RADIUS);

        orientedLineBrushTo = new Paint();
        orientedLineBrushTo.setColor(Color.GREEN);
        orientedLineBrushTo.setStrokeWidth(LINE_RADIUS);

        /// Инициализация кисти для рисования круга вершины
        circleBrush = new Paint();
        circleBrush.setColor(getResources().getColor(R.color.colorPrimary));
        circleBrush.setStyle(Paint.Style.FILL);

        /// Инициализация кисти для рисования круга выделенной вершины
        circleSelectionBrush = new Paint();
        circleSelectionBrush.setARGB(255, 140, 240, 140);
        circleSelectionBrush.setStyle(Paint.Style.FILL);

        /// Инициализация кисти для рисования текста
        textBrush = new Paint();
        textBrush.setColor(Color.WHITE);
        textBrush.setTextSize(TEXT_SIZE);

        /// Инициализация кисти для рисования цифр веса
        weightBrush = new Paint();
        weightBrush.setColor(Color.BLACK);
        weightBrush.setStyle(Paint.Style.FILL_AND_STROKE);
        weightBrush.setStrokeWidth(5.0f);
        weightBrush.setTextSize(60.0f);

        textBounds = new Rect();

        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.weight_dialog, null);
        /// Диалоговое окно для установки веса
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Установите вес")
                .setView(dialogView)
                .setCancelable(true)
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText txt = dialogView.findViewById(R.id.dialogWeight);
                        String sWeight = txt.getText().toString();
                        if (sWeight.isEmpty()) {
                            /// Если вес пустой, снимаем выделение и перерисовываем
                            selectedNode1 = null;
                            selectedNode2 = null;
                            invalidate();
                            return;
                        }
                        /// Получаем вес и создаем связь
                        int weight = Integer.parseInt(sWeight);

                        if (!isGraphOriented)
                            selectedNode2.linkNode(selectedNode1, weight);

                        selectedNode1.linkNode(selectedNode2, weight);

                        selectedNode1 = null;
                        selectedNode2 = null;

                        txt.setText("");
                        invalidate();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        /// Если окно было закрыто
                        selectedNode1 = null;
                        selectedNode2 = null;
                        invalidate();
                    }
                });
        dialogWeight = builder.create();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        String tmp;
        for (Node n : listNodes) {
            for (Link link : n.links) {

                /// Условие для одноразового построения связи (связь двусторонняя)

                /// Рисуем ребро
                if (!isGraphOriented) {
                    if (link.node1.id < link.node2.id)
                        canvas.drawLine(n.x, n.y, link.node2.x, link.node2.y, lineBrush);
                }
                else
                {
                    /// Середина отрезка
                    float halfX = (link.node1.x+link.node2.x)/2f;
                    float halfY = (link.node1.y+link.node2.y)/2f;
                    canvas.drawLine(n.x, n.y, halfX, halfY, orientedLineBrushFrom);
                    canvas.drawLine(halfX, halfY, link.node2.x, link.node2.y, orientedLineBrushTo);
                }
                /// Если вес включен, то пишем и его
                if (isWeightEnabled) {
                    if (isGraphOriented || link.node1.id < link.node2.id) {
                        /// Середина отрезка
                        float halfX = (link.node1.x + link.node2.x) / 2f;
                        float halfY = (link.node1.y + link.node2.y) / 2f;
                        /// Перпендикулярный вектор
                        float perpX = link.node2.y - link.node1.y;
                        float perpY = -(link.node2.x - link.node1.x);

                        /// Получаем длину перпендикулярного вектора
                        float length = getDistanceBetweenPoints(0, 0, perpX, perpY);

                        ///Нормируем вектор и откладываем его от середины отрезка
                        perpX = (perpX / length) * 60.0f + halfX;
                        perpY = (perpY / length) * 60.0f + halfY;

                        /// Пишем текст
                        tmp = String.valueOf(link.weight);
                        textBrush.getTextBounds(tmp, 0, tmp.length(), textBounds);
                        canvas.drawText(String.valueOf(link.weight),
                                perpX - textBrush.measureText(tmp) / 2f,
                                perpY + textBounds.height() / 2f,
                                weightBrush);
                    }
                }
            }
        }

        /// Рисуем выделенные вершины
        if (selectedNode1 != null)
            canvas.drawCircle(selectedNode1.x, selectedNode1.y, SELECTED_NODE_RADIUS,
                    circleSelectionBrush);

        if (selectedNode2 != null)
            canvas.drawCircle(selectedNode2.x, selectedNode2.y, SELECTED_NODE_RADIUS,
                    circleSelectionBrush);

        /// Рисуем обычные вершины и текст в них
        for (Node n : listNodes) {
            canvas.drawCircle(n.x, n.y, NODE_RADIUS, circleBrush);

            tmp = String.valueOf(n.id);
            textBrush.getTextBounds(tmp, 0, tmp.length(), textBounds);

            canvas.drawText(tmp, n.x - textBrush.measureText(tmp) / 2f,
                    n.y + textBounds.height() / 2f, textBrush);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        /// Нажатие на экран
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();
            for (Node n : listNodes) {
                ///Получаем расстояние между точкой нажатия и существующими вершинами
                /// Если нажатие было на вершину
                if (getDistanceBetweenPoints(x, y, n.x, n.y) < NODE_RADIUS) {

                    /// Если вершина уже выбрана
                    if (selectedNode1 == n) {
                        /// Снимаем выделение и перерисовываем
                        selectedNode1 = null;
                        invalidate();
                        return true;
                    }

                    /// Если вершина еще не выбрана
                    if (selectedNode1 == null)
                        ///Выделяем её
                        selectedNode1 = n;
                    else {
                        /// Если уже есть одна выбранная вершина и она не связана с текущей
                        if (!selectedNode1.isNodeLinked(n)) {
                            /// Если вес включен
                            if (isWeightEnabled) {
                                /// Вызываем диалоговое окно с выбором веса
                                selectedNode2 = n;
                                dialogWeight.show();
                            }
                            else {
                                /// Иначе создаем связь
                                selectedNode1.linkNode(n);
                                if (!isGraphOriented) {
                                    n.linkNode(selectedNode1);
                                }
                                selectedNode1 = null;
                            }
                        }
                    }
                    /// Перерисовываем
                    invalidate();
                    return true;
                } else if (getDistanceBetweenPoints(x, y, n.x, n.y) < NODE_RADIUS * 2) {
                    /// Если расстояние меньше 2 радиусов вершины, то пропускаем
                    return true;
                }
            }

            /// Добавляем вершину на поле и перерисовываем
            listNodes.add(new Node(listNodes.size() + 1, x, y));
            invalidate();
        }
        return true;
    }

    /**
     * Получает расстояние между точками на плоскости
     *
     * @param x1 Абсцисса первой точки
     * @param y1 Ордината первой точки
     * @param x2 Абсцисса второй точки
     * @param y2 Ордината второй точки
     *
     * @return Расстояние между точками
     */
    private float getDistanceBetweenPoints(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * Получает текущий лист вершин
     *
     * @return Лист вершин
     */
    public ArrayList<Node> getListNodes() {
        return listNodes;
    }

    /**
     * Устанавливает лист вершин
     *
     * @param listNodes Лист вершин для замены
     */
    public void setListNodes(ArrayList<Node> listNodes){
        this.listNodes = listNodes;
    }

    /**
     * Класс вершины
     */
    public static class Node {
        public float x; //Абсцисса
        public float y; //Ордината
        public int id; // Идентификатор вершины
        public ArrayList<Link> links; // Лист ребер для вершины

        public Node(int id, float x, float y) {
            this.id = id;
            this.x = x;
            this.y = y;
            links = new ArrayList<>();
        }

        /**
         * Проверяет, связана ли текущая вершина с данной.
         *
         * @param node Вершина для проверки связи.
         *
         * @return true - связана, false - нет.
         */
        public boolean isNodeLinked(Node node) {
            for (Link l : links)
                if (l.node1 == node || l.node2 == node)
                    return true;

            return false;
        }

        public int getLinkWeight(Node node)
        {
            for (Link l : links)
                if (l.node1 == node || l.node2 == node)
                    return l.weight;

            return Integer.MAX_VALUE;
        }

        /**
         * Связывает текущую вершину с данной
         *
         * @param node Вершина для создания связи с текущей
         */
        public void linkNode(Node node) {
            for (int i = 0; i < links.size(); i++) {
                if (node.id < links.get(i).node2.id) {
                    links.add(i, new Link(this, node));
                    return;
                }
            }
            links.add(new Link(this, node));
        }

        /**
         * Связать текущую вершину с данной с учётом веса
         *
         * @param node Вершина для создания связи с текущей
         * @param weight Вес связи
         */
        public void linkNode(Node node, int weight) {
            for (int i = 0; i < links.size(); i++) {
                if (node.id < links.get(i).node2.id) {
                    links.add(i, new Link(this, node, weight));
                    return;
                }
            }
            links.add(new Link(this, node, weight));
        }
    }

    /**
     * Класс связи (ребро)
     */
    public static class Link{
        public Node node1; // Первая вершина
        public Node node2; // Вторая вершина
        public int weight; // Вес

        public Link(Node node1, Node node2, int weight) {
            this.node1 = node1;
            this.node2 = node2;
            this.weight = weight;
        }

        public Link(Node node1, Node node2) {
            this.node1 = node1;
            this.node2 = node2;
            this.weight = 0;
        }

        /**
         * Получает противоположную вершину в зависимости от данной
         *
         * @param node Известная вершина
         *
         * @return Противоположная вершина
         */
        public Node getAnotherNode(Node node) {
            return (node == node1) ? node2 : node1;
        }
    }
}

