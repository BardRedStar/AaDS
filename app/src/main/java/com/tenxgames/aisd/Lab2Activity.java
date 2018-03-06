package com.tenxgames.aisd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Lab2Activity extends AppCompatActivity {

    /// Максимальное значение элемента при рандомном заполнении
    private static final int MAX_RANDOM_NUMBER = 1000;

    /// Максимальное число элементов в массиве при рандомном заполнении
    private static final int MAX_RANDOM_AMOUNT = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab2);
        setTitle("Лабораторная работа 2");

        /// Отклик кнопки "Поехали"
        Button btn = findViewById(R.id.lab2StartButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// Получаем строку
                EditText text = findViewById(R.id.lab2InputBox);
                String buf = text.getText().toString();
                /// Убираем лишние пробелы по бокам
                buf = buf.trim();
                if (buf.equals("") || !checkString(buf.toCharArray())) {
                    //Если строка пустая или там есть символы, кроме цифр, выводим ошибку
                    Toast.makeText(getBaseContext(), "Ошибка ввода! Проверьте данные!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                /// Парсим строку в массив чисел и выводим на экран их количество
                int[] nums = parseNums(buf);
                TextView tw = findViewById(R.id.lab2NumElements);
                tw.setText("Количество элементов: " + nums.length);

                //Инициализируем поиск моды и медианы
                startSearch(nums);
            }
        });

        /// Отклик кнопки "Рандом"
        btn = findViewById(R.id.lab2RandomButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /// Заполняем массив случайными числами и выводим количество
                int[] nums = randomNums();
                TextView tw = findViewById(R.id.lab2NumElements);
                tw.setText("Количество элементов: " + nums.length);

                /// Обновляем поле ввода под новый массив
                StringBuilder sNums = new StringBuilder();
                for (int i = 0; i < nums.length; i++) {
                    sNums.append(nums[i] + " ");
                }
                EditText text = findViewById(R.id.lab2InputBox);
                text.setText(sNums.toString());

                //Инициализируем поиск моды и медианы
                startSearch(nums);
            }
        });

        /// Отклик кнопки "Очистить"
        btn = findViewById(R.id.lab2ClearButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = findViewById(R.id.lab2InputBox);
                text.setText("");
            }
        });

    }

    /**
     * Функция парсинга строки в массив чисел (числа должны быть через пробел).
     *
     * @param sNums Строка для парсинга
     * @return Массив чисел из строки
     *
     */
    public int[] parseNums(String sNums) {
        String[] nums = sNums.split(" ");

        int[] res = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            res[i] = Integer.valueOf(nums[i]);
        }
        return res;
    }

    /**
     * Функция заполнения массива {@link Random случайными} числами.
     * Количество чисел - {@link Lab2Activity#MAX_RANDOM_AMOUNT},
     * максимальное значение элемента - {@link Lab2Activity#MAX_RANDOM_NUMBER}.
     *
     * @return     Массив случайных чисел
     *
     * @see Random
     */
    public int[] randomNums() {
        Random rnd = new Random();
        int[] res = new int[rnd.nextInt(MAX_RANDOM_AMOUNT) + 1];

        for (int i = 0; i < res.length; i++) {
            res[i] = rnd.nextInt(MAX_RANDOM_NUMBER) + 1;
        }

        return res;
    }

    /**
     * Функция поиска моды и медианы в массиве чисел с помощью отдельных потоков.
     *
     * @param nums Массив чисел для поиска
     */
    public void startSearch(final int[] nums) {
        /// Очищаем значения на экране
        TextView tw = findViewById(R.id.lab2Median);
        tw.setText("Медиана: ");
        tw = findViewById(R.id.lab2MedianTime);
        tw.setText("Время медианы (сек): ");
        tw = findViewById(R.id.lab2Mode);
        tw.setText("Мода: ");
        tw = findViewById(R.id.lab2ModeTime);
        tw.setText("Время моды (сек): ");

        /// Поиск моды
        new Thread() {
            @Override
            public void run() {
                /// Используем модификацию ArrayMap для целых чисел (SparseIntArray)
                SparseIntArray modeArray = new SparseIntArray();
                final StringBuilder mode = new StringBuilder();
                int max = Integer.MIN_VALUE;
                int cur;

                /// Замеряем время начала в наносекундах, чтобы видеть точную разницу
                final long startTime = System.nanoTime();


                /// Добавляем в наш ассоциативный массив числа из основного массива по очереди
                /// Ключом является само число, а значением - количество его повторений в массиве
                /// По ходу цикла ищем максимальное число вхождений чисел на текущий момент
                for (int i = 0; i < nums.length; i++) {
                    if (modeArray.indexOfKey(nums[i]) < 0) {
                        modeArray.append(nums[i], 1);
                        cur = 1;
                    } else {
                        cur = modeArray.get(nums[i]) + 1;
                        modeArray.put(nums[i], cur);
                    }
                    if (cur > max) max = cur;
                }
                /// На данном этапе имеем заполненный ассоциативный массив и макс. число вхождений
                /// Анализируем массив на это число и записываем через запятую в StringBuilder для вывода
                cur = 0;
                for (int i = 0; i < modeArray.size(); i++) {
                    if (modeArray.valueAt(i) == max) {
                        if (cur != 0)
                            mode.append(", ");

                        mode.append(modeArray.keyAt(i));
                        cur++;
                    }
                }
                /// Мода(-ы) найдена(-ы), снова замеряем время
                final long endTime = System.nanoTime();

                /// Пора бы вывести результаты. В этом же потоке нельзя использовать элементы интерфейса
                /// Сделаем это в потоке UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /// Выводим наш StringBuilder с модами
                        TextView tw = findViewById(R.id.lab2Mode);
                        tw.setText("Мода: " + mode.toString());
                        /// Посчитаем время. Оно в наносекундах, но мы выведем целую часть секунд
                        /// А через запятую выведем остаток
                        tw = findViewById(R.id.lab2ModeTime);
                        long resTime = endTime - startTime;
                        tw.setText("Время моды (сек): " + String.valueOf(resTime / 1000000000)
                                + "," + String.format("%09d", resTime % 1000000000));
                    }
                });
                /// Очищаем массив
                modeArray.clear();
            }
        }.start();

        /// Поиск медианы
        /// Так же запускаем отдельный поток
        new Thread() {
            @Override
            public void run() {
                /// Для поиска медианы воспользуемся обычной коллекцией ArrayList
                final ArrayList<Integer> medianArray = new ArrayList<>();
                final double median;
                /// Засекаем время в наносекундах
                final long startTime = System.nanoTime();

                /// Добавляем все элементы массива в коллекцию и сортируем
                for (int i = 0; i < nums.length; i++) {
                    medianArray.add(nums[i]);
                }
                /// Решил воспользоваться встроенными инструментами, т.к. с пузырьковой большое время
                Collections.sort(medianArray);

                /// Если количество элементов нечетное, то выводим центральный элемент
                /// А если чётное, то среднее арифметическое двух центральных
                if (nums.length % 2 == 1)
                    median = medianArray.get(nums.length / 2);
                else
                    median = (medianArray.get(nums.length / 2) + medianArray.get(nums.length / 2 - 1)) / 2.0;

                /// Снова засекаем время
                final long endTime = System.nanoTime();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /// Выводим получившиеся значения
                        /// Форматирование времени такое же, как и в моде
                        TextView tw = findViewById(R.id.lab2Median);
                        tw.setText("Медиана: " + median);
                        tw = findViewById(R.id.lab2MedianTime);
                        long resTime = endTime - startTime;
                        tw.setText("Время медианы (сек): " + String.valueOf(resTime / 1000000000)
                                + "," + String.format("%09d", resTime % 1000000000));

                        /// Выведем отсортированный массив в поле для ввода, чтобы было удобно
                        /// отслеживать результат
                        StringBuilder sNums = new StringBuilder();
                        for (int i = 0; i < medianArray.size(); i++) {
                            sNums.append(medianArray.get(i) + " ");
                        }
                        EditText text = findViewById(R.id.lab2InputBox);
                        text.setText(sNums.toString());

                        /// Очищаем массив
                        medianArray.clear();
                    }
                });
            }
        }.start();
    }

    /**
     * Проверяет, встречаются ли в строке символы, кроме пробелов и цифр
     *
     * @param nums Массив символов для проверки
     * @return true, если строка состоит из цифр и пробелов, иначе false
     *
     * @see Character#isDigit(char)
     */
    public boolean checkString(char[] nums) {
        for (int i = 0; i < nums.length; i++) {
            if (!Character.isDigit(nums[i]) && nums[i] != ' ')
                return false;
        }
        return true;
    }
}
