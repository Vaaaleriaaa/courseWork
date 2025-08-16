package org.example;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class AssignmentProblemVolumeLimit extends AbstractAssignmentProblem {
    public int[][] costArray; // матрица стоимости
    private int[] volumeLimit; // матрица ограничения на объемы
    private int[] appointmentLimit; // матрица ограничения на количество назначений на одну должность

    public int[][] costArrayExpand; // расширенная матрица стоимости
    public int nExpand; // количество должностей с расширением



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
        Loader.loadNativeLibraries();   // Загружает нативные библиотеки, необходимые для OR-Tools.

        nExpand = 0;
        for (int i = 0; i < n; i++){
            nExpand += appointmentLimit[i];
        }

        costArrayExpand = new int[nExpand][nExpand];
        for (int i = 0; i < n; i++) { // идем по строкам
            int column = 0;
            for (int post = 0; post < n; post++) {
                for (int al = 0; al < appointmentLimit[post]; al++) {
                    costArrayExpand[i][column] = costArray[i][post];
                    column++;
                }
            }
        }
        for (int i = n; i < nExpand; i++){
            for (int j = 0; j < nExpand; j++){
                costArrayExpand[i][j] = 0;
            }
        }

    }

    // чтобы посчитать значение целевой функции в классе LocalSearch
    @Override
    public int function(List<Integer> list){
        int f=0;
        for (int i=0; i<nExpand; i++){
            f += costArrayExpand[list.get(i)][i];

        }

        f -= punish(list);
        return f;
    }

    public int punish(List<Integer> list){
        int punish = 0;

        int column = 0;
        for (int post = 0; post < n; post++) {
            int postCost = 0;
            for (int al = 0; al < appointmentLimit[post]; al++) {
                postCost += costArrayExpand[list.get(column)][column];
                column++;
            }
            if (postCost > volumeLimit[post]){
                punish += 1000;
            }
        }

        return punish;
    }

    @Override
    public ArrayList<Integer> getInvalidList(List<Integer> pi) {
        ArrayList<Integer> invalidList = new ArrayList<>();

        int column = 0;
        for (int post = 0; post < n; post++) {
            int postCost = 0;
            for (int al = 0; al < appointmentLimit[post]; al++) {
                postCost += costArrayExpand[pi.get(column)][column];
                column++;
            }
            if (postCost > volumeLimit[post]){
                column -= appointmentLimit[post];
                for (int al = 0; al < appointmentLimit[post]; al++) {
                    if (costArrayExpand[pi.get(column)][column] > 0) {
                        invalidList.add(pi.get(column));
                    }
                    column++;
                }
            }
        }

        return invalidList;

    }

    @Override
    public int getN() {
        return nExpand;
    }



    // генерация задачи о назначении с сохранением в файл по заданному n и файлу...
    public static void generateAssignmentProblem(int n, boolean max, File file){
        try(BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            // Записываем значения n и max
            out.write(n + " " + max);
            out.newLine();
            out.newLine();

            // создание матрицы стоимости из случайных значений от 0 до 20
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    out.write(Integer.toString((new Random()).nextInt(20)));
                    out.write(" ");
                }
                out.newLine();
            }
            out.newLine();
            out.newLine();

            // создание матрицы ограничений на объемы из случайных значений
            for (int j = 0; j < n; j++) {
                out.write(Integer.toString(20*(1+(new Random()).nextInt(n))));
                out.write(" ");
            }
            out.newLine();
            out.newLine();

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
        for (int i = 0; i < nExpand; i++) {
            int elementAdd = Integer.MAX_VALUE; // Минимальное значение для текущей строки
            int indexElementAdd = -1; // Индекс элемента для добавления

            // Находим минимальное значение в строке costArray[i]
            for (int k = 0; k < nExpand; k++) {
                if (elementAdd > costArrayExpand[i][k]) {
                    elementAdd = costArrayExpand[i][k];
                    indexElementAdd = k;
                }
            }

            // Проверяем, что выбранный индекс еще не добавлен в список
            while (pi.contains(indexElementAdd)) {
                elementAdd = Integer.MAX_VALUE; // Сбрасываем минимальное значение
                indexElementAdd = -1; // Сбрасываем индекс

                // Находим следующее минимальное значение
                for (int k = 0; k < nExpand
                        ; k++) {
                    if (!pi.contains(k) && costArrayExpand[i][k] < elementAdd) {
                        elementAdd = costArrayExpand[i][k];
                        indexElementAdd = k;
                    }
                }

                // Если все индексы уже добавлены, выходим из цикла
                if (indexElementAdd == -1) {
                    break;
                }
            }

            // Добавляем найденный индекс в список
            if (indexElementAdd != -1) {
                pi.add(indexElementAdd);
            }
        }

        return pi;
    }


    @Override
    public ArrayList<Integer> generateSolverStart() {
        return solveTask("SCIP", 100);
    }

    // Решим задачу используя библиотеку OrTools
    public ArrayList<Integer> solveTask(String solver_name, long time){
        System.out.println("buildSolver");

        MPSolver solver = MPSolver.createSolver(solver_name);    // Создает решатель SCIP.
        if (solver == null) {   // Проверяет, удалось ли создать решатель.
            System.out.println("Could not create solver" + solver_name);     // Выводит сообщение об ошибке, если не удалось.
            return null;     // Завершает программу.
        }

        // Создадим переменные
        // x[i][j] is an array of 0-1 variables, which will be 1 if worker i is assigned to task j.
        MPVariable[][] x = new MPVariable[n][n];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                x[i][j] = solver.makeIntVar(0, 1, "");
            }
        }

        // Создадим ограничения
        // Каждый работник имеет одну должность
        for (int i = 0; i < n; ++i) {
            MPConstraint constraint = solver.makeConstraint(1, 1, "");
            for (int j = 0; j < n; ++j) {
                constraint.setCoefficient(x[i][j], 1);
            }
        }

        // Ограничение на суммарные затраты на должность
        for (int j = 0; j < n; ++j) {
            MPConstraint constraint = solver.makeConstraint(0, volumeLimit[j], "");
            for (int i = 0; i < n; ++i) {
                constraint.setCoefficient(x[i][j], costArray[i][j]);
            }
        }

        // Ограничение на количество работников на одной должности
        for (int j = 0; j < n; ++j) {
            MPConstraint constraint = solver.makeConstraint(0, appointmentLimit[j], "");
            for (int i = 0; i < n; ++i) {
                constraint.setCoefficient(x[i][j], 1);
            }
        }


        // Создадим целевую функцию
        MPObjective objective = solver.objective();
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                objective.setCoefficient(x[i][j], costArray[i][j]);
            }
        }

        // Фиксируем то, что мы решаем задачу на минимум или максимум
        if (max == false) {
            objective.setMinimization();
        }
        else{
            objective.setMaximization();
        }

        resultStatus = solver.solve();
        if (time == time_limit_milliseconds) {
            wall_time = solver.wallTime();
            decisionSolverOrTools = objective.value();

            /////ВРЕМЕННО ВЫВОДИМ РЕШЕНИЕ В КОНСОЛЬ /////
            System.out.println("Решение солвера\n");
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    if (x[i][j].solutionValue() > 0.5) {
                        System.out.print(j + " ");
                    }
                }
            }
            System.out.println();
            /////ВРЕМЕННО ВЫВОДИМ РЕШЕНИЕ В КОНСОЛЬ /////

        }
        else {

            ArrayList<Integer> pi = new ArrayList<>();
            int workerZero = n;
            for (int p=0; p<n; p++) {  // перебираем должности
                int solveA = 0;
                for (int i = 0; i < n; ++i) {
                    if (x[i][p].solutionValue() > 0.5) {
                        pi.add(i);
                        solveA++;
                    }
                }

                for (int a = solveA; a < appointmentLimit[p]; a++) {
                    pi.add(workerZero);
                    workerZero++;
                }

            }

            return pi;
        }
        return null;
    }
}
