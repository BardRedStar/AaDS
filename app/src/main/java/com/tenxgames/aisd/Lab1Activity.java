package com.tenxgames.aisd;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class Lab1Activity extends AppCompatActivity {

    /// Максимальная длина строки при рандомном заполнении
    private static final int STRING_MAX_SIZE = 100;

    /// Алфавит для рандомного заполнения
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
            "0123456789!@#$%^&*()АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя =+-,;.:[]{}|\"\'\\";

    /// Генерация случайных чисел
    private Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab1);
        setTitle("Лабораторная работа 1");

        /// Кнопка "Поехали"
        Button btn = findViewById(R.id.lab1StartButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /// Получаем строки из полей
                EditText txt = findViewById(R.id.lab1StringBox);
                String str = txt.getText().toString();
                txt = findViewById(R.id.lab1SubstringBox);
                String substr = txt.getText().toString();

                /// Убираем пробелы по бокам
                str = str.trim();
                substr = substr.trim();

                /// Проверка на корректность ввода
                if (str.equals("") || substr.equals("")) {
                    Toast.makeText(getApplicationContext(), "Ошибка ввода! Проверьте данные!", Toast.LENGTH_LONG).show();
                    return;
                }

                /// Начинаем поиск, в зависимости от чувствительности к регистру
                CheckBox cb = findViewById(R.id.lab1CaseSensitiveCheckBox);
                startSearch(str, substr, cb.isChecked());
            }
        });

        /// Кнопка "Рандом"
        btn = findViewById(R.id.lab1RandomButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /// Генерируем рандомную строку
                String str = getRandomString();
                /// Выбираем из строки подстроку случайной длины
                String substr = getRandomSubstring(str);

                /// Выводим их на экран
                EditText txt = findViewById(R.id.lab1StringBox);
                txt.setText(str);
                txt = findViewById(R.id.lab1SubstringBox);
                txt.setText(substr);

                /// Начинаем поиск, в зависимости от чувствительности к регистру
                CheckBox cb = findViewById(R.id.lab1CaseSensitiveCheckBox);
                startSearch(str, substr, cb.isChecked());
            }
        });

        /// Кнопка "Очистить"
        btn = findViewById(R.id.lab1ClearButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /// Очищаем все поля
                EditText txt = findViewById(R.id.lab1StringBox);
                txt.setText("");
                txt = findViewById(R.id.lab1SubstringBox);
                txt.setText("");
                TextView tw = findViewById(R.id.lab1Result);
                tw.setText("");
            }
        });
    }

    /**
     * Генерирует случайную строку длиной от 0 до STRING_MAX_SIZE из символов ALPHABET
     *
     * @return     Сгенерированная строка
     */
    public String getRandomString() {
        int length = rnd.nextInt(STRING_MAX_SIZE) + 1;
        StringBuilder res = new StringBuilder();

        for (int i = 0; i < length; i++) {
            res.append(ALPHABET.charAt(rnd.nextInt(ALPHABET.length())));
        }
        return res.toString();
    }

    /**
     * Выбирает подстроку длиной от 1 до размера строки из исходной строки
     *
     * @param str Исходная строка
     * @return Подстрока случайной длины
     */
    public String getRandomSubstring(String str) {
        int length = rnd.nextInt(str.length()) + 1;
        int startIndex = rnd.nextInt(str.length() - length);
        return str.substring(startIndex, startIndex + length);
    }

    /**
     * Подготавливает данные к поиску
     *
     * @param originalStr Строка для поиска
     * @param originalSubstr Строка образа
     * @param caseSensitive Чувствительность к регистру: true - да, false - нет.
     */
    public void startSearch(String originalStr, String originalSubstr, boolean caseSensitive) {

        String str, substr;

        if (caseSensitive) {
            /// Если поиск чувствителен к регистру, то оставляем строки, как есть
            str = originalStr;
            substr = originalSubstr;
        } else {
            /// Если не чувствителен, то переводим их в нижний регистр
            str = originalStr.toLowerCase();
            substr = originalSubstr.toLowerCase();
        }

        TextView tw;
        /// Проводим поиск и обрабатываем результат
        /// result - позиция первого вхождения найденной строки (или -1, если не найдена)
        int result = getKMPResult(str, substr);
        if (result == -1) {
            tw = findViewById(R.id.lab1Result);
            tw.setText("Совпадений не найдено!");
            return;
        }

        /// Форматируем текст для вывода.
        /// Найденная область будет выделена жирным, чёрным и немного бОльшим шрифтом
        SpannableString buf = new SpannableString(originalStr);
        buf.setSpan(new StyleSpan(Typeface.BOLD), result, result + substr.length(), 0);
        buf.setSpan(new ForegroundColorSpan(Color.BLACK), result, result + substr.length(), 0);
        buf.setSpan(new RelativeSizeSpan(1.1f), result, result + substr.length(), 0);

        /// Выводим текст
        tw = findViewById(R.id.lab1Result);
        tw.setText(buf);
    }

    /**
     * Ищет образ в строке по КМП-алгоритму и возвращает позицию первого вхождения
     *
     * @param str Строка для поиска
     * @param substr Строка образа
     * @return Позиция первого вхождения или -1, если образ не найден
     */
    public int getKMPResult(String str, String substr) {

        /// Получаем таблицу сдвигов
        int[] d = getD(substr);

        /// offset - смещение
        /// i - позиция начала просмотра строки
        int offset = 0, i;
        while (true) {
            i = offset;

            /// Если от позиция просмотра до конца строки осталось меньше длины образа,
            /// то образ не найден
            if (str.length() - i < substr.length()) {
                return -1;
            }

            /// Смотрим совпадение символов. Если не совпал, то определяем смещение
            for (int j = 0; j < substr.length(); j++) {
                if (str.charAt(offset + j) != substr.charAt(j)) {
                    offset += j - d[j];
                    break;
                }

                /// Если цикл дошёл до конца, то образ найден
                if (j == substr.length() - 1)
                    return i;
            }
        }
    }

    /**
     * Вычисляет таблицу сдвигов на основе образа
     *
     * @param substr Строка образа
     * @return Одномерный массив чисел - таблица сдвигов
     */
    public int[] getD(String substr) {
        /// Создаём массив длиной, равной длине подстроки
        int[] d = new int[substr.length()];
        /// Первый (нулевой) элемент всегда равен -1
        d[0] = -1;
        int currentStreak, begin;

        /// Вспомогательная коллекция для хранения позиций элементов, равных первому (нулевому)
        /// Далее будем называть их ключами, но нулевой символ не учитываем
        ArrayList<Integer> points = new ArrayList<>();

        for (int i = 1; i < d.length; i++) {

            /// Рассматриваем уникальные случаи

            if (substr.charAt(i) == substr.charAt(0)) {
                /// Если текущий символ равен начальному, ставим ему значение -1
                d[i] = -1;
                /// Запоминаем ключ
                points.add(i);
                continue;
            }

            if (points.size() == 0) {
                /// После нулевого элемента должны идти нули, если не найден ни один ключ
                d[i] = 0;
                continue;
            }

            if (d[i - 1] == 0) {
                /// После любого нулевого элемента идёт ноль до следующего ключа
                d[i] = 0;
                continue;
            }

            /// Если случай не уникальный, то вычисляем по алгоритму
            /// Часть строки совпадает с началом, если она начинается с ключа
            for (int j = 0; j < points.size(); j++) {

                /// Обнуляем счётчик длины и получаем индекс из ключа
                currentStreak = 0;
                begin = points.get(j);

                /// Проходим от текущего ключа до исследуемого элемента
                for (int x = begin; x < i; x++) {
                    if (substr.charAt(x) == substr.charAt(x - begin)) {
                        /// Если символы совпадают, увеличиваем счётчик
                        currentStreak++;
                    } else break;
                }

                /// Если подстрока от ключа до символа полностью совпала
                if (currentStreak == i - begin) {
                    /// Записываем длину
                    d[i] = currentStreak;
                    break;
                } else d[i] = 0;
                /// Иначе, длина равна 0
            }
        }
        return d;
    }
}
