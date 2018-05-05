package com.tenxgames.aisd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tenxgames.aisd.sqlite.SQLiteHelper;
import com.tenxgames.aisd.sqlite.SortRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Lab5Activity extends AppCompatActivity {

    /// Переменные для работы с файлами
    File fileA, fileB, fileC;

    /// Максимальное значение элемента при рандомном заполнении
    private static final int MAX_RANDOM_NUMBER = 1000;

    /// Минимальное значение элемента при рандомном заполнении
    private static final int MIN_RANDOM_NUMBER = -1000;

    /// Максимальное число элементов в массиве при рандомном заполнении
    private static final int MAX_RANDOM_AMOUNT = 50;

    private SQLiteHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab5);

        db = new SQLiteHelper(this);

        /// Отклик кнопки "Поехали"
        Button btn = findViewById(R.id.lab5StartButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /// Получаем строку
                EditText text = findViewById(R.id.lab5InputBox);
                String buf = text.getText().toString();
                /// Убираем лишние пробелы по бокам
                buf = buf.trim();
                if (buf.equals("") || !isStringCorrect(buf.toCharArray())) {
                    //Если строка пустая или там есть символы, кроме цифр, выводим ошибку
                    Toast.makeText(getBaseContext(), "Ошибка ввода! Проверьте данные!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                /// Парсим строку в массив чисел и выводим на экран их количество
                TextView tw = findViewById(R.id.lab5NumElements);
                tw.setText("Количество элементов: " + getSequenceLength(buf));

                startSequenceSort(buf);
            }
        });

        /// Отклик кнопки "Рандом"
        final Button btn2 = findViewById(R.id.lab5RandomButton);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn2.setEnabled(false);
                /// Заполняем массив случайными числами и выводим количество
                String sequence = getRandomSequence();
                TextView tw = findViewById(R.id.lab5NumElements);
                tw.setText("Количество элементов: " + getSequenceLength(sequence));

                /// Обновляем поле ввода под новый массив
                EditText text = findViewById(R.id.lab5InputBox);
                text.setText(sequence);

                //Инициализируем поиск моды и медианы
                startSequenceSort(sequence);
                btn2.setEnabled(true);
            }
        });

        /// Отклик кнопки "Очистить"
        btn = findViewById(R.id.lab5ClearButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = findViewById(R.id.lab5InputBox);
                text.setText("");
            }
        });

        btn = findViewById(R.id.lab5SortHistoryButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SortsLogActivity.class);
                startActivity(intent);
            }
        });

        try {
            fileA = new File(this.getCacheDir().getPath() + "/file_a.txt");
            fileB = new File(this.getCacheDir().getPath() + "/file_b.txt");
            fileC = new File(this.getCacheDir().getPath() + "/file_c.txt");

            if (!fileA.exists())
                fileA = File.createTempFile("file_a", ".txt", this.getCacheDir());

            if (!fileB.exists())
                fileB = File.createTempFile("file_b", ".txt", this.getCacheDir());

            if (!fileC.exists())
                fileC = File.createTempFile("file_c", ".txt", this.getCacheDir());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Начинает сортировку, подготавливая данные, засекая время и т.д. Записывает лог сортировки в
     * базу данных.
     *
     * @param sequence Последовательность в виде строки
     */
    private void startSequenceSort(String sequence) {

        loadSequence(sequence);
        long time = System.nanoTime();

        String res = getSortedSequence();

        long endTime = System.nanoTime();

        TextView tw = findViewById(R.id.lab5Time);
        tw.setText("Время (сек): " + nanoToSec(endTime - time));
        tw = findViewById(R.id.lab5SortedSequence);
        tw.setText("Отсортированная последовательность: \n" +
                res);
        Date currentTime = Calendar.getInstance().getTime();
        db.addSortRecord(new SortRecord(0,
                currentTime.toString(),
                nanoToSec(endTime - time),
                sequence,
                res));
    }

    @Override
    protected void onDestroy() {
        if (db != null && db.isDatabaseOpen())
            db.closeDatabase();

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (db != null && db.isDatabaseOpen())
            db.closeDatabase();

        super.onStop();
    }

    /**
     * Загружает введенную последовательность в файл А.
     *
     * @param sequence Последовательность в виде строки
     */
    private void loadSequence(String sequence) {
        try {
            FileOutputStream f = new FileOutputStream(fileA);
            f.write(sequence.getBytes());
            f.flush();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает отсортированную последовательность, используя
     * введенную последовательность из файла А и файлы B,C для сортировки.
     *
     * @return Сортированная последовательность в виде строки или null в случае ошибки
     */
    private String getSortedSequence() {
        try {
            int i = 1;
            while (!isSequenceSorted()) {
                splitSequence(i);
                uniteSequences(i);
                i *= 2;
            }

            FileInputStream r = new FileInputStream(fileA);
            byte[] msg = new byte[r.available()];
            r.read(msg);
            r.close();
            return new String(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Разбивает последовательность из файла А в файлы B,C в соответствии с алгоритмом сортировки
     *
     * @param blockSize Текущий размер блока разбиения
     */
    private void splitSequence(int blockSize) {
        try {
            /// Открываем три потока. Файл А - входной, Файлы B,C - выходные.
            FileInputStream fisA = new FileInputStream(fileA);
            FileOutputStream fosB = new FileOutputStream(fileB);
            FileOutputStream fosC = new FileOutputStream(fileC);

            /// Флаг записи в определенный файл. Нечетный - файл B, Четный - файл C
            boolean odd = true;
            /// Переменная для хранения текущего символа из потока
            int symbol;
            /// Билдер для хранения числа посимвольно
            StringBuilder num = new StringBuilder();
            /// Текущий размер блока
            int currentBlockSize = 0;

            while ((symbol = fisA.read()) != -1) {
                /// Добавляем символ к числу
                num.append((char) symbol);

                /// Если найден пробел, значит число окончено и полностью считано
                if ((char) symbol == ' ') {
                    /// Увеличиваем текущий блок
                    currentBlockSize++;
                    /// Если конец блока не достигнут, то считываем дальше
                    if (currentBlockSize < blockSize)
                        continue;

                    /// Сбрасываем блок
                    currentBlockSize = 0;

                    /// Записываем в нужный файл. Если нечетный, то B, если четный, то C
                    if (odd)
                        fosB.write(num.toString().getBytes());
                    else
                        fosC.write(num.toString().getBytes());

                    /// Инверсируем флаг
                    odd = !odd;
                    /// Сбрасываем билдер для числа
                    num.setLength(0);

                }
            }
            /// Если число в конце файла считывалось, но до пробела не дошло, то в билдере должно
            /// что-то остаться
            if (num.length() != 0) {
                /// Записываем оставшееся число по флагу в файлы
                if (odd)
                    fosB.write(num.toString().getBytes());
                else
                    fosC.write(num.toString().getBytes());
            }

            /// Закрываем потоки
            fisA.close();
            fosB.flush();
            fosB.close();
            fosC.flush();
            fosC.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Объединяет последовательности из файлов B,C в соответствии с алгоритмом сортировки
     *
     * @param blockSize Текущий размер блока слияния
     */
    private void uniteSequences(int blockSize) {
        try {
            /// Открываем три потока. Файл А - выходной, Файлы B,C - входные.
            FileOutputStream fosA = new FileOutputStream(fileA);
            FileInputStream fisB = new FileInputStream(fileB);
            FileInputStream fisC = new FileInputStream(fileC);


            int symbol;
            /// Билдеры для чисел из файлов B и С
            StringBuilder strB = new StringBuilder();
            StringBuilder strC = new StringBuilder();
            /// Блоки для файлов B и С считаются отдельно
            int currentBlockSizeB = 0, currentBlockSizeC = 0;
            /// Текущие числа из файлов в формате чисел
            Integer numB = null, numC = null;
            /// Флаг требования чисел из нужного файла:
            /// 0 - числа не нужны
            /// 1 - Нужно из файла B
            /// 2 - Нужно из файла С
            /// 3 - Нужно из обоих файлов
            int needFrom = 3;
            /// Символ пробела. Нужен для корректной записи последовательности
            String space = "";
            /// Константы для выбора требуемого числа из файлов
            int SEQUENCE_B = 1, SEQUENCE_C = 2;
            while (true) {
                /// Если нужно число из файла В и текущий блок в файле B не достиг указанного
                if ((needFrom & SEQUENCE_B) > 0 && currentBlockSizeB != blockSize) {
                    /// Обнуляем число. Если файл кончился, то оно не считается и будет null
                    numB = null;
                    while ((symbol = fisB.read()) != -1) {
                        /// Если мы дошли до пробела, и перед ним не было еще пробела
                        if ((char) symbol == ' ' && strB.length() != 0) {
                            ///
                            numB = Integer.parseInt(strB.toString());
                            break;
                        } else
                            strB.append((char) symbol);
                    }

                    if (strB.length() > 0)
                        numB = Integer.parseInt(strB.toString());
                    currentBlockSizeB++;
                    strB.setLength(0);
                }

                if ((needFrom & SEQUENCE_C) > 0 && currentBlockSizeC != blockSize) {
                    numC = null;
                    while ((symbol = fisC.read()) != -1) {
                        if ((char) symbol == ' ' && strC.length() != 0) {
                            numC = Integer.parseInt(strC.toString());

                            break;
                        } else
                            strC.append((char) symbol);
                    }

                    if (strC.length() > 0)
                        numC = Integer.parseInt(strC.toString());

                    currentBlockSizeC++;
                    strC.setLength(0);
                }

                if (numB == null && numC == null) {
                    strB.setLength(0);
                    strC.setLength(0);
                    break;
                } else if (numB == null && numC != null) {

                    if (currentBlockSizeC < blockSize) {
                        while ((symbol = fisC.read()) != -1) {
                            if ((char) symbol == ' ' && strC.length() != 0) {
                                currentBlockSizeC++;
                                if (currentBlockSizeC != blockSize) {
                                    strC.append((char) symbol);
                                    continue;
                                }
                                break;
                            } else
                                strC.append((char) symbol);
                        }
                    }

                    if (strC.length() > 0)
                        strC.insert(0, ' ');

                    fosA.write((space + numC + strC.toString()).getBytes());
                    strC.setLength(0);
                    numB = numC = null;
                    needFrom = 3;
                    currentBlockSizeB = currentBlockSizeC = 0;

                } else if (numB != null && numC == null) {
                    if (currentBlockSizeB < blockSize) {
                        while ((symbol = fisB.read()) != -1) {
                            if ((char) symbol == ' ' && strB.length() != 0) {
                                currentBlockSizeB++;
                                if (currentBlockSizeB != blockSize) {
                                    strB.append((char) symbol);
                                    continue;
                                }
                                break;
                            } else
                                strB.append((char) symbol);
                        }
                    }
                    if (strB.length() > 0)
                        strB.insert(0, ' ');

                    fosA.write((space + numB + strB.toString()).getBytes());
                    strB.setLength(0);
                    numB = numC = null;
                    currentBlockSizeB = currentBlockSizeC = 0;
                    needFrom = 3;
                } else {
                    if (numB <= numC) {
                        fosA.write((space + String.valueOf(numB)).getBytes());
                        needFrom = SEQUENCE_B;
                        numB = null;
                    } else {
                        fosA.write((space + String.valueOf(numC)).getBytes());
                        needFrom = SEQUENCE_C;
                        numC = null;
                    }
                }
                space = " ";
            }
            fosA.flush();
            fosA.close();
            fisB.close();
            fisC.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверяет, отсортирована ли последовательность.
     *
     * @return true - отсортирована, false - не отсортирована.
     */
    private boolean isSequenceSorted() {
        try {
            FileInputStream r = new FileInputStream(fileA);
            int symbol = 0;
            StringBuilder str = new StringBuilder();
            int prev, current = Integer.MIN_VALUE;
            while ((symbol = r.read()) != -1) {
                if ((char) symbol == ' ' && str.length() != 0) {
                    prev = current;
                    current = Integer.parseInt(str.toString());
                    str.setLength(0);
                    if (current < prev)
                        return false;
                } else
                    str.append((char) symbol);
            }

            if (str.length() > 0) {
                prev = current;
                current = Integer.parseInt(str.toString());
                if (current < prev)
                    return false;
            }
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Функция парсинга строки в массив чисел (числа должны быть через пробел).
     *
     * @param sequence Строка для парсинга
     * @return Массив чисел из строки
     */
    public int getSequenceLength(String sequence) {
        int res = 0;
        for (int i = 0; i < sequence.length(); i++) {
            if (sequence.charAt(i) == ' ')
                res++;
        }
        res++;
        return res;
    }

    /**
     * Функция заполнения последовательности {@link Random случайными} числами.
     * Количество чисел - {@link Lab2Activity#MAX_RANDOM_AMOUNT},
     * максимальное значение элемента - {@link Lab2Activity#MAX_RANDOM_NUMBER}.
     *
     * @return Заполненная последовательность в виде строки
     * @see Random
     */
    public String getRandomSequence() {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        int amount = rnd.nextInt(MAX_RANDOM_AMOUNT + 1);
        for (int i = 0; i < amount; i++) {
            sb.append(rnd.nextInt(MAX_RANDOM_NUMBER - MIN_RANDOM_NUMBER + 1)
                    + MIN_RANDOM_NUMBER);
            sb.append(" ");
        }

        return sb.toString().trim();
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
