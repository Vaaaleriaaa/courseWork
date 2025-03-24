package org.example.problemsVolumeLimit;

import org.example.AbstractAssignmentProblem;

import java.io.*;
import java.util.*;

public class AssignmentProblemVolumeLimit extends AbstractAssignmentProblem {
    public int[][] costArray; // матрица стоимости

    public List<Integer> pi; // оптимальное решение
    private int[] volumeLimit; // матрица ограничения на объемы
    private int[] appointmentLimit; // матрица ограничения на количество назначений на одно здание


    // Загрузка задачи из файла
    public AssignmentProblemVolumeLimit(File file){
        try(Scanner scanner = new Scanner(file)) {

            n = scanner.nextInt();
            if (n <= 0) {
                throw new IllegalArgumentException("n <= 0");
            }

            max = scanner.nextBoolean();

            costArray = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    costArray[i][j] = scanner.nextInt();
                }
            }

            volumeLimit = new int[n];
            for (int j = 0; j < n; j++) {
                volumeLimit[j] = scanner.nextInt();
            }

            appointmentLimit = new int[n];
            for (int j = 0; j < n; j++) {
                appointmentLimit[j] = scanner.nextInt();
            }

        }catch (IOException e) { System.out.println(e.getMessage()); }

    }

    // чтобы посчитать значение целевой функции в классе LocalSearch
    @Override
    public int function(List<Integer> list){
        int f=0;
        for (int i=0; i<n; i++){
            f += costArray[i][list.get(i)];
        }
        return f;
    }

    // генерация задачи о назначении
    public static void generateAssignmentProblem(){
        System.out.println("Генерация случайно задачи о назначении.\nВведите название файла, в который сохранить условие задачи: ");
        Scanner s = new Scanner(System.in);
        String fileNameCondition = "D:\\ярлыкиРабочегоСтола\\univer\\3course\\courseWork\\localSearch\\src\\main\\java\\org\\example\\problemsVolumeLimit\\" + s.nextLine() + ".txt";
        File fileCondition = new File(fileNameCondition);
        try(BufferedWriter out = new BufferedWriter(new FileWriter(fileCondition))) {
            System.out.println("Введите количество должностей: ");
            int n = s.nextInt();
            out.write(Integer.toString(n));
            out.newLine(); out.newLine();


            System.out.println("Введите 1, если задача на максимум, 0 - минимум: ");
            int  max = s.nextInt();
            if (max == 1){
                out.write("true");
            }
            else if (max == 0){
                out.write("false");
            }
            out.newLine(); out.newLine();

            // создание матрицы стоимости из случайных значений от 0 до 20
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    out.write(Integer.toString((new Random()).nextInt(20)));
                    out.write(" ");
                }
                out.newLine();
            }
            out.newLine();

            // создание матрицы ограничений на объемы из случайных значений
            for (int j = 0; j < n; j++) {
                out.write(Integer.toString(20*(1+(new Random()).nextInt(n))));
                out.write(" ");
            }
            out.newLine(); out.newLine();

            // создание матрицы ограничений на количество назначений в одно здание из случайных значений от 1 до n
            for (int j = 0; j < n; j++) {
                out.write(Integer.toString(1 + (new Random()).nextInt(n)));
                out.write(" ");
            }

        }catch(IOException e) { System.out.println(e.getMessage()); }
        System.out.println("Готово!");
    }

    // генерация задачи о назначении с сохранением в файл по заданному n и файлу...
    public void generateAssignmentProblem(int n, File file){
        try(BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            // Записываем значения n и max
            out.write(Integer.toString(n) + " " + Boolean.toString(max));
            out.newLine();
            // создание матрицы стоимости из случайных значений от 0 до 20
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    out.write(Integer.toString((new Random()).nextInt(20)));
                    out.write(" ");
                }
                out.newLine();
            }
            // создание матрицы ограничений на объемы из случайных значений
            for (int j = 0; j < n; j++) {
                out.write(Integer.toString(20*(1+(new Random()).nextInt(n))));
                out.write(" ");
            }

            // создание матрицы ограничений на количество назначений в одно здание из случайных значений от 1 до n
            for (int j = 0; j < n; j++) {
                out.write(Integer.toString(1 + (new Random()).nextInt(n)));
                out.write(" ");
            }
        }catch(IOException e) { System.out.println(e.getMessage()); }
    }


    // построим начальное решение из максимальных элементов в столбцах(столбцы не повторяются)
    @Override
    public ArrayList<Integer> generateSmartStart(){
        ArrayList<Integer> pi = new ArrayList<>(n);
        for (int i=0; i<n; i++) {
            int elemetAdd = costArray[i][0];
            Integer indexElementAdd = -1;
            for (int k=0; k<n; k++){
                if (elemetAdd > costArray[i][k]){
                    elemetAdd = costArray[i][k];
                }
            }

            for (int j=0; j<n; j++){
                if (!pi.contains(j)) {
                    if (costArray[i][j] > elemetAdd) {
                        elemetAdd = costArray[i][j];
                        indexElementAdd = j;
                    }
                }
            }
            pi.add(i, indexElementAdd);
        }
        return pi;
    }
}

