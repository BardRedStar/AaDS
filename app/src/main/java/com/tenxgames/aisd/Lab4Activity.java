package com.tenxgames.aisd;

import android.annotation.SuppressLint;
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

public class Lab4Activity extends AppCompatActivity {

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
     * Идентификатор контейнера для шейкерной сортировки.
     */
    private static final int SHAKERSORT_CONTAINER = 1;

    /**
     * Идентификатор контейнера для сортировки Шелла.
     */
    private static final int SHELLSORT_CONTAINER = 2;

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
                box = findViewById(R.id.lab4ShakerSortBox);
                box.setText(text);
                box = findViewById(R.id.lab4ShellSortBox);
                box.setText(text);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab4);

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

        cb = findViewById(R.id.lab4SyncFillingCheckBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isSyncFillingActivated = isChecked;
            }
        });

        cb = findViewById(R.id.lab4ShakerSortCheckBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                CheckBox cb = findViewById(R.id.lab4ShakerSortCheckBox);
                LinearLayout container = findViewById(R.id.lab4ShakerSortContainer);
                if (isChecked) {
                    container.setVisibility(View.VISIBLE);
                    selectedContainers += SHAKERSORT_CONTAINER;
                } else {
                    container.setVisibility(View.GONE);
                    selectedContainers -= SHAKERSORT_CONTAINER;
                }
            }
        });

        cb = findViewById(R.id.lab4ShellSortCheckBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                CheckBox cb = findViewById(R.id.lab4ShellSortCheckBox);
                LinearLayout container = findViewById(R.id.lab4ShellSortContainer);
                if (isChecked) {
                    container.setVisibility(View.VISIBLE);
                    selectedContainers += SHELLSORT_CONTAINER;
                } else {
                    container.setVisibility(View.GONE);
                    selectedContainers -= SHELLSORT_CONTAINER;
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
     * @see Lab4Activity#getRandomIntArray()
     * @see Lab4Activity#startSort(int[], int[])
     * @see Lab4Activity#isStringCorrect(char[])
     */
    private void initializeButtons() {
        Button btn;
        btn = findViewById(R.id.lab4StartButton);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText box;
                String shakerSortStr, shellSortStr;
                shakerSortStr = shellSortStr = null;
                Log.w("Lab4", "Selected Containers " + selectedContainers);
                if (isContainerActive(SHAKERSORT_CONTAINER)) {
                    box = findViewById(R.id.lab4ShakerSortBox);
                    shakerSortStr = box.getText().toString();
                    if (shakerSortStr.equals("") || !isStringCorrect(shakerSortStr.toCharArray())) {
                        Toast.makeText(getApplicationContext(),
                                "Ошибка ввода элементов в прямом включении!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (isContainerActive(SHELLSORT_CONTAINER)) {
                    box = findViewById(R.id.lab4ShellSortBox);
                    shellSortStr = box.getText().toString();
                    if (shellSortStr.equals("") || !isStringCorrect(shellSortStr.toCharArray())) {
                        Toast.makeText(getApplicationContext(),
                                "Ошибка ввода элементов в прямом выборе!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                startSort(parseStringtoArray(shakerSortStr), parseStringtoArray(shellSortStr));
            }
        });

        btn = findViewById(R.id.lab4RandomButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] shakerSortArray, shellSortArray, sharedArray;
                EditText box;
                TextView tw;

                shakerSortArray = shellSortArray = sharedArray = null;

                if (isSyncFillingActivated) {
                    sharedArray = getRandomIntArray();
                }

                if (isContainerActive(SHAKERSORT_CONTAINER)) {
                    if (isSyncFillingActivated)
                        shakerSortArray = Arrays.copyOf(sharedArray, sharedArray.length);
                    else
                        shakerSortArray = getRandomIntArray();

                    box = findViewById(R.id.lab4ShakerSortBox);
                    box.setText(arrayToString(shakerSortArray));

                    tw = findViewById(R.id.lab4ShakerSortAmount);
                    tw.setText("Количество элементов: " + shakerSortArray.length);
                }

                if (isContainerActive(SHELLSORT_CONTAINER)) {
                    if (isSyncFillingActivated)
                        shellSortArray = Arrays.copyOf(sharedArray, sharedArray.length);
                    else
                        shellSortArray = getRandomIntArray();

                    box = findViewById(R.id.lab4ShellSortBox);
                    box.setText(arrayToString(shellSortArray));

                    tw = findViewById(R.id.lab4ShellSortAmount);
                    tw.setText("Количество элементов: " + shellSortArray.length);
                }

                startSort(shakerSortArray, shellSortArray);
            }
        });

        btn = findViewById(R.id.lab4ClearButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /// Очистка полей результата
                clearResults();

                /// Очистка полей ввода
                EditText box;
                box = findViewById(R.id.lab4ShakerSortBox);
                box.setText("");
                box = findViewById(R.id.lab4ShellSortBox);
                box.setText("");
            }
        });
    }

    /**
     * Очищает {@link TextView поля результатов}
     *
     * @see TextView
     */
    private void clearResults()
    {
        TextView tw;
        tw = findViewById(R.id.lab4ShakerSortAmount);
        tw.setText("Количество элементов: ");
        tw = findViewById(R.id.lab4ShakerSortComps);
        tw.setText("Сравнения: ");
        tw = findViewById(R.id.lab4ShakerSortTime);
        tw.setText("Время (сек): ");
        tw = findViewById(R.id.lab4ShakerSortSwaps);
        tw.setText("Перестановки: ");
        tw = findViewById(R.id.lab4ShellSortAmount);
        tw.setText("Количество элементов: ");
        tw = findViewById(R.id.lab4ShellSortComps);
        tw.setText("Сравнения: ");
        tw = findViewById(R.id.lab4ShellSortTime);
        tw.setText("Время (сек): ");
        tw = findViewById(R.id.lab4ShellSortSwaps);
        tw.setText("Перестановки: ");
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
        box = findViewById(R.id.lab4ShakerSortBox);
        box.setOnFocusChangeListener(onFocusChangeListener);
        box = findViewById(R.id.lab4ShellSortBox);
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
     * Генерирует массив случайной длины (от 1 до {@link Lab4Activity#MAX_RANDOM_AMOUNT}) и
     * заполняет его числами от {@link Lab4Activity#MIN_RANDOM_NUMBER} до
     * {@link Lab4Activity#MAX_RANDOM_NUMBER} включительно.
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
     * @param shakerSortArray Массив для шейкерной сортировки.
     * @param shellSortArray  Массив для сортировки Шелла.
     */
    private void startSort(final int[] shakerSortArray, final int[] shellSortArray) {
        /// Если был передан массив для шейкерной сортировки
        if (shakerSortArray != null) {
            new Thread() {
                public void run() {
                    /// Инициализируем переменные для подсчёта сравнений и перемещений и
                    /// засекаем время
                    int comparsions = 0, swaps = 0;
                    long time = System.nanoTime();
                    int tmp;
                    /// Переменные для хранения правой и левой границ
                    int right = shakerSortArray.length - 1;
                    int left = 0;

                    /// Пока границы не встретятся
                    while (left < right) {
                        /// Проход слева направо методом "пузырька"
                        for (int i = left; i < right; i++) {
                            comparsions++;
                            if (shakerSortArray[i] > shakerSortArray[i + 1]) {
                                swaps++;
                                tmp = shakerSortArray[i];
                                shakerSortArray[i] = shakerSortArray[i + 1];
                                shakerSortArray[i + 1] = tmp;
                            }
                        }
                        /// Один элемент "всплыл", а значит надо подвинуть правую границу
                        right--;

                        /// Проход справа налево тем же способом
                        for (int i = right; i > left; i--) {
                            comparsions++;
                            if (shakerSortArray[i] < shakerSortArray[i - 1]) {
                                swaps++;
                                tmp = shakerSortArray[i];
                                shakerSortArray[i] = shakerSortArray[i - 1];
                                shakerSortArray[i - 1] = tmp;
                            }
                        }
                        /// Сдвигаем левую границу
                        left++;
                    }

                    /// Останавливаем таймер и записываем результаты на экране
                    final long finalTime = System.nanoTime() - time;
                    final int finalComparsions = comparsions;
                    final int finalSwaps = swaps;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText box;
                            box = findViewById(R.id.lab4ShakerSortBox);
                            box.setText(arrayToString(shakerSortArray));

                            TextView tw;
                            tw = findViewById(R.id.lab4ShakerSortComps);
                            tw.setText("Сравнения: " + finalComparsions);
                            tw = findViewById(R.id.lab4ShakerSortTime);
                            tw.setText("Время (сек): " + nanoToSec(finalTime));
                            tw = findViewById(R.id.lab4ShakerSortSwaps);
                            tw.setText("Перестановки: " + finalSwaps);
                        }
                    });
                }
            }.start();
        }

        /// Если был передан массив для сортировки Шелла
        if (shellSortArray != null) {
            new Thread() {
                public void run() {
                    /// Инициализируем переменные для подсчёта сравнений и перемещений и
                    /// засекаем время
                    int comparsions = 0, swaps = 0, i, j, k, tmp;
                    long time = System.nanoTime();
                    /// N - количество элементов в массиве
                    int N = shellSortArray.length;
                    /// Создаём группы через каждые k элементов.
                    /// Размер начинается с N/2 и уменьшается
                    for (k = N / 2; k > 0; k /= 2) {
                        /// Сортируем группу
                        for (i = k; i < N; i++) {
                            /// Запоминаем граничный элемент
                            tmp = shellSortArray[i];
                            /// Идём с конца через каждые k элементов и двигаем те, что больше tmp
                            for (j = i; j >= k; j -= k) {
                                comparsions++;
                                if (tmp < shellSortArray[j - k]) {
                                    swaps++;
                                    shellSortArray[j] = shellSortArray[j - k];
                                } else
                                    break;
                            }
                            /// Вставляем элемент
                            shellSortArray[j] = tmp;
                        }
                    }

                    /// Останавливаем таймер и записываем результаты на экране
                    final long finalTime = System.nanoTime() - time;
                    final int finalComparsions = comparsions;
                    final int finalSwaps = swaps;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText box;
                            box = findViewById(R.id.lab4ShellSortBox);
                            box.setText(arrayToString(shellSortArray));

                            TextView tw;
                            tw = findViewById(R.id.lab4ShellSortComps);
                            tw.setText("Сравнения: " + finalComparsions);
                            tw = findViewById(R.id.lab4ShellSortTime);
                            tw.setText("Время (сек): " + nanoToSec(finalTime));
                            tw = findViewById(R.id.lab4ShellSortSwaps);
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
     * @see Lab4Activity#SHAKERSORT_CONTAINER Контейнер шейкерной сортировки
     * @see Lab4Activity#SHELLSORT_CONTAINER Контейнер сортировки Шелла
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

    /**
     * Переводит наносекунды в секунды (целая часть отделяется запятой)
     *
     * @param time Число наносекунд
     * @return Строка с количеством секунд.
     * @see System#nanoTime()
     */
    @SuppressLint("DefaultLocale")
    private String nanoToSec(long time) {
        return String.format("%d,%09d", time / 1000000000, time % 1000000000);
    }
}
