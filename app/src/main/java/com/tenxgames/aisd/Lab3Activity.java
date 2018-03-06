package com.tenxgames.aisd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

public class Lab3Activity extends AppCompatActivity {

    /**
     * Минимальное значение числа при случайном заполнении.
     */
    private static final int MIN_RANDOM_NUMBER = -1000;

    /**
     * Максимальное значение числа при случайном заполнении.
     */
    private static final int MAX_RANDOM_NUMBER = 1000;

    /**
     * Максимальное значение количества элементов массива при случайном заполнении.
     */
    private static final int MAX_RANDOM_AMOUNT = 10000;

    /**
     * Идентификатор контейнера для сортировки прямым включением.
     */
    private static final int DIRECTINC_CONTAINER = 1;

    /**
     * Идентификатор контейнера для сортировки прямым выбором.
     */
    private static final int DIRECTSEL_CONTAINER = 2;

    /**
     * Текущее состояние контейнеров(сумма идентификаторов).
     */
    private int selectedContainers;

    /**
     * Флаг синхронного заполнения.
     */
    private boolean isSyncFillingActivated;

    /// Событие смены фокуса для обеспечения синхронного заполнения
    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (isSyncFillingActivated) {
                String text = ((EditText) view).getText().toString();

                EditText box;
                box = findViewById(R.id.lab3DirectIncBox);
                box.setText(text);
                box = findViewById(R.id.lab3DirectSelBox);
                box.setText(text);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab3);

        /// Инициализация элементов управления
        initializeCheckBoxes();
        initializeButtons();
        initializeTextBoxes();
    }

    /**
     * Подписывает {@link CheckBox флажки} на
     * {@link CheckBox.OnCheckedChangeListener событие смены статуса} для синхронного заполнения
     * и переключения контейнеров
     *
     * @see CheckBox
     * @see CheckBox.OnCheckedChangeListener
     */
    private void initializeCheckBoxes() {
        CheckBox cb;

        cb = findViewById(R.id.lab3SyncFillingCheckBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isSyncFillingActivated = isChecked;
            }
        });

        cb = findViewById(R.id.lab3DirectIncCheckBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                CheckBox cb = findViewById(R.id.lab3DirectIncCheckBox);
                LinearLayout container = findViewById(R.id.lab3DirectIncContainer);
                if (isChecked) {
                    container.setVisibility(View.VISIBLE);
                    selectedContainers += DIRECTINC_CONTAINER;
                } else {
                    container.setVisibility(View.GONE);
                    selectedContainers -= DIRECTINC_CONTAINER;
                }
            }
        });

        cb = findViewById(R.id.lab3DirectSelCheckBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                CheckBox cb = findViewById(R.id.lab3DirectSelCheckBox);
                LinearLayout container = findViewById(R.id.lab3DirectSelContainer);
                if (isChecked) {
                    container.setVisibility(View.VISIBLE);
                    selectedContainers += DIRECTSEL_CONTAINER;
                } else {
                    container.setVisibility(View.GONE);
                    selectedContainers -= DIRECTSEL_CONTAINER;
                }
            }
        });
    }

    /**
     * Подписывает {@link Button кнопки} на {@link View.OnClickListener событие нажатия}
     * и обрабатывает логику после нажатия: проверку данных на корректность, генерацию массивов
     * чисел, инициализацию сортировок, очистку полей.
     *
     * @see Button
     * @see View.OnClickListener
     * @see Lab3Activity#getRandomIntArray()
     * @see Lab3Activity#startSort(int[], int[])
     * @see Lab3Activity#isStringCorrect(char[])
     */
    private void initializeButtons() {
        Button btn;
        btn = findViewById(R.id.lab3StartButton);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText box;
                String directIncStr, directSelStr;
                directIncStr = directSelStr = null;
                Log.w("Lab3", "Selected Containers " + selectedContainers);
                if (isContainerActive(DIRECTINC_CONTAINER)) {
                    box = findViewById(R.id.lab3DirectIncBox);
                    directIncStr = box.getText().toString();
                    if (directIncStr.equals("") || !isStringCorrect(directIncStr.toCharArray())) {
                        Toast.makeText(getApplicationContext(),
                                "Ошибка ввода элементов в прямом включении!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (isContainerActive(DIRECTSEL_CONTAINER)) {
                    box = findViewById(R.id.lab3DirectSelBox);
                    directSelStr = box.getText().toString();
                    if (directSelStr.equals("") || !isStringCorrect(directSelStr.toCharArray())) {
                        Toast.makeText(getApplicationContext(),
                                "Ошибка ввода элементов в прямом выборе!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                startSort(parseStringtoArray(directIncStr), parseStringtoArray(directSelStr));
            }
        });

        btn = findViewById(R.id.lab3RandomButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] directIncArray, directSelArray, sharedArray;
                EditText box;
                TextView tw;

                directIncArray = directSelArray = sharedArray = null;

                if (isSyncFillingActivated) {
                    sharedArray = getRandomIntArray();
                }

                if (isContainerActive(DIRECTINC_CONTAINER)) {
                    if (isSyncFillingActivated)
                        directIncArray = Arrays.copyOf(sharedArray, sharedArray.length);
                    else
                        directIncArray = getRandomIntArray();

                    box = findViewById(R.id.lab3DirectIncBox);
                    box.setText(arrayToString(directIncArray));

                    tw = findViewById(R.id.lab3DirectIncAmount);
                    tw.setText("Количество элементов: " + directIncArray.length);
                }

                if (isContainerActive(DIRECTSEL_CONTAINER)) {
                    if (isSyncFillingActivated)
                        directSelArray = Arrays.copyOf(sharedArray, sharedArray.length);
                    else
                        directSelArray = getRandomIntArray();

                    box = findViewById(R.id.lab3DirectSelBox);
                    box.setText(arrayToString(directSelArray));

                    tw = findViewById(R.id.lab3DirectSelAmount);
                    tw.setText("Количество элементов: " + directSelArray.length);
                }

                startSort(directIncArray, directSelArray);
            }
        });

        btn = findViewById(R.id.lab3ClearButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /// Очистка полей результата
                TextView tw;
                tw = findViewById(R.id.lab3DirectIncAmount);
                tw.setText("Количество элементов: ");
                tw = findViewById(R.id.lab3DirectIncComps);
                tw.setText("Сравнения: ");
                tw = findViewById(R.id.lab3DirectIncTime);
                tw.setText("Время (сек): ");
                tw = findViewById(R.id.lab3DirectIncSwaps);
                tw.setText("Перестановки: ");
                tw = findViewById(R.id.lab3DirectSelAmount);
                tw.setText("Количество элементов: ");
                tw = findViewById(R.id.lab3DirectSelComps);
                tw.setText("Сравнения: ");
                tw = findViewById(R.id.lab3DirectSelTime);
                tw.setText("Время (сек): ");
                tw = findViewById(R.id.lab3DirectSelSwaps);
                tw.setText("Перестановки: ");


                /// Очистка полей ввода
                EditText box;
                box = findViewById(R.id.lab3DirectIncBox);
                box.setText("");
                box = findViewById(R.id.lab3DirectSelBox);
                box.setText("");
            }
        });
    }

    /**
     * Подписывает {@link EditText текстовые поля} на
     * {@link View.OnFocusChangeListener событие смены фокуса} для синхронного заполнения.
     *
     * @see EditText
     * @see View.OnFocusChangeListener
     */
    private void initializeTextBoxes() {
        EditText box;
        box = findViewById(R.id.lab3DirectIncBox);
        box.setOnFocusChangeListener(onFocusChangeListener);
        box = findViewById(R.id.lab3DirectSelBox);
        box.setOnFocusChangeListener(onFocusChangeListener);
    }

    /**
     * Составляет массив чисел из введенной (корректной) строки.
     *
     * @param nums Строка с числами через пробел.
     * @return Массив чисел, содержащихся в строке.
     */
    private int[] parseStringtoArray(String nums) {
        if (nums != null) {
            String[] buf = nums.split(" ");
            int[] res = new int[buf.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = Integer.parseInt(buf[i]);
            }
            return res;
        }
        return null;
    }

    /**
     * Генерирует массив случайной длины (от 1 до {@link Lab3Activity#MAX_RANDOM_AMOUNT}) и
     * заполняет его числами от {@link Lab3Activity#MIN_RANDOM_NUMBER} до
     * {@link Lab3Activity#MAX_RANDOM_NUMBER} включительно.
     *
     * @return Сгенерированный массив чисел.
     */
    private int[] getRandomIntArray() {
        Random rnd = new Random();
        int[] res = new int[rnd.nextInt(MAX_RANDOM_AMOUNT) + 1];

        for (int i = 0; i < res.length; i++) {
            res[i] = rnd.nextInt(MAX_RANDOM_NUMBER - MIN_RANDOM_NUMBER + 1)
                    + MIN_RANDOM_NUMBER;
        }
        return res;
    }

    /**
     * Создаёт строку из элементов массива, размещая их через пробел.
     *
     * @param array Массив для перевода в строку.
     * @return Строку в виде элементов массива, расположенных через пробел.
     */
    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i] + " ");
        }
        return sb.toString();
    }

    /**
     * Инициализирует сортировку введенных массивов, используя отдельные потоки.
     *
     * @param directIncArray Массив для сортировки прямым включением.
     * @param directSelArray Массив для сортировки прямым выбором.
     */
    private void startSort(final int[] directIncArray, final int[] directSelArray) {
        if (directIncArray != null) {
            new Thread() {
                public void run() {
                    int comparsions = 0, swaps = 0;
                    long time = System.nanoTime();
                    int tmp;
                    for (int i = 1; i < directIncArray.length; i++) {
                        tmp = directIncArray[i];
                        for (int j = 0; j < i; j++) {
                            comparsions++;
                            if (tmp < directIncArray[j]) {
                                for (int x = i; x > j; x--) {
                                    swaps++;
                                    directIncArray[x] = directIncArray[x - 1];
                                }
                                directIncArray[j] = tmp;
                                break;
                            }
                        }
                    }

                    final long finalTime = System.nanoTime() - time;
                    final int finalComparsions = comparsions;
                    final int finalSwaps = swaps;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText box;
                            box = findViewById(R.id.lab3DirectIncBox);
                            box.setText(arrayToString(directIncArray));

                            TextView tw;
                            tw = findViewById(R.id.lab3DirectIncComps);
                            tw.setText("Сравнения: " + finalComparsions);
                            tw = findViewById(R.id.lab3DirectIncTime);
                            tw.setText("Время (сек): " + finalTime);
                            tw = findViewById(R.id.lab3DirectIncSwaps);
                            tw.setText("Перестановки: " + finalSwaps);
                        }
                    });
                }
            }.start();
        }


        if (directSelArray != null) {
            new Thread() {
                public void run() {
                    int comparsions = 0, swaps = 0;
                    long time = System.nanoTime();
                    int max, max_index;
                    int edge = directSelArray.length;
                    while (edge != 0) {
                        max = directSelArray[0];
                        max_index = 0;
                        for (int i = 0; i < edge; i++) {
                            if (directSelArray[i] >= max) {
                                comparsions++;
                                max = directSelArray[i];
                                max_index = i;
                            }
                        }

                        edge--;

                        if (max_index == edge)
                            continue;

                        for (int i = max_index; i < edge; i++) {
                            swaps++;
                            directSelArray[i] = directSelArray[i + 1];
                        }

                        directSelArray[edge] = max;
                    }

                    final long finalTime = System.nanoTime() - time;
                    final int finalComparsions = comparsions;
                    final int finalSwaps = swaps;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText box;
                            box = findViewById(R.id.lab3DirectSelBox);
                            box.setText(arrayToString(directSelArray));

                            TextView tw;
                            tw = findViewById(R.id.lab3DirectSelComps);
                            tw.setText("Сравнения: " + finalComparsions);
                            tw = findViewById(R.id.lab3DirectSelTime);
                            tw.setText("Время (сек): " + finalTime);
                            tw = findViewById(R.id.lab3DirectSelSwaps);
                            tw.setText("Перестановки: " + finalSwaps);
                        }
                    });
                }
            }.start();
        }
    }

    /**
     * Проверяет, активен ли блок сортировки по его идентификатору (см. константы).
     *
     * @param containerId Идентификатор контейнера.
     * @return true, если контейнер активен на данный момент, и false, если нет.
     *
     * @see Lab3Activity#DIRECTINC_CONTAINER Контейнер сортировки прямым включением
     * @see Lab3Activity#DIRECTSEL_CONTAINER Контейнер сортировки прямым выбором
     */
    private boolean isContainerActive(int containerId) {
        return ((selectedContainers & containerId) > 0);
    }

    /**
     * Проверяет, встречаются ли в строке символы, кроме пробелов, цифр
     * и знака "-" (после которого обязательно должна быть цифра).
     *
     * @param nums Массив символов для проверки.
     * @return true, если строка состоит из цифр и пробелов, иначе false.
     *
     * @see Character#isDigit(char)
     */
    private boolean isStringCorrect(char[] nums) {
        for (int i = 0; i < nums.length; i++) {
            if (!Character.isDigit(nums[i])
                    && nums[i] != ' '
                    && (nums[i] != '-' || i == nums.length - 1 || !Character.isDigit(nums[i + 1])))
                return false;
        }
        return true;
    }
}
