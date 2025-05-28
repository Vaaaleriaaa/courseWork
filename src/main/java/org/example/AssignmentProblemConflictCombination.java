package org.example;

import com.google.ortools.Loader;
import com.google.ortools.*;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import org.example.AbstractAssignmentProblem;
import org.example.OptimalAssignmentSolver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AssignmentProblemConflictCombination extends AbstractAssignmentProblem {
    public int[][] costArray; // матрица стоимости

    public int conflictPercent; // количество конфликтных работников и работ в процентах
    private int nw; // количество множеств конфликтных работников
    private int np; // количество множеств связанных работ
    private Set<Integer>[] setsWorker; // множества конфликтных работников
    private Set<Integer>[] setsPost; // множества связанных работ

    private int punishment = 500; // при проверке ограничений, каждая конфликтующая пара считается дважды, поэтому наказание будет в 2 раза больше, т.е. 1000


    public long time_limit_milliseconds = 1800000 * 4; // 1800000 - 30 минут, 7200000 - 2 часа
    public long wall_time; // время, за которое solver нашел решение

    public MPSolver.ResultStatus resultStatus; // Статус найденного решения(допустимое, оптимальное, задача некорректная и т.д. )

    // Создает задачу о назначении по введенному значению n, max, conflictPercent и file
    public static void generateAssignmentProblem(int n, boolean max,  int conflictPercent, File file){
        Scanner s = new Scanner(System.in);
        try(BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            // Записываем количество должностей и работников
            out.write(Integer.toString(n) + " " + Boolean.toString(max) + " " + conflictPercent);

            out.newLine(); out.newLine();

            // Генерируем и записываем значения матрицы стоимости
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    out.write(Integer.toString((new Random()).nextInt(20)));
                    out.write(" ");
                }
                out.newLine();
            }
            out.newLine();

            // Генерируем и записываем количество комбинация конфликтных работников
            int nw = conflictPercent * n * (n-1) / 200;
            // out.write(Integer.toString(1+(new Random()).nextInt(n/2.)));
            out.write(nw + " ");

            // Генерируем и записываем количество комбинация связанных работ
            int np = conflictPercent * n * (n-1) / 200;
            // out.write(Integer.toString(1+(new Random()).nextInt(n/2.)));
            out.write(np +" ");

            out.newLine();

            // Генерируем и записываем значения множеств конфликтных работников
            // Создаем массив из множеств конфликтных работников
            Set<Integer>[] setsWorker = new Set[nw];
            Set<Set<Integer>> uniqueSets = new HashSet<>(); // Вспомогательное множество для отслеживания уникальных множеств
            Random random = new Random(); // Единый генератор случайных чисел
            for (int i = 0; i < nw; i++) {
                Set<Integer> tempSet;
                do {
                    tempSet = new HashSet<>();
                    // Генерируем 2 уникальных элемента
                    while (tempSet.size() < 2) {
                        tempSet.add(random.nextInt(n));
                    }
                } while (uniqueSets.contains(tempSet)); // Повторяем, если множество уже существует

                // Сохраняем уникальное множество
                uniqueSets.add(tempSet);
                setsWorker[i] = tempSet;

                // Записываем в файл
                out.write(2 + " "); // Длина множества
                for (int item : tempSet) {
                    out.write(item + " ");
                }
                out.newLine();
            }
            out.newLine();

            // Генерируем и записываем значения множеств связанных работ
            // Создаем массив из множеств связанных работ
            Set<Integer>[] setsPost = new Set[np];
            for (int i = 0; i < np; i++) {
                Set<Integer> tempSet;
                do {
                    tempSet = new HashSet<>();
                    // Генерируем 2 уникальных элемента
                    while (tempSet.size() < 2) {
                        tempSet.add(random.nextInt(n));
                    }
                } while (uniqueSets.contains(tempSet)); // Повторяем, если множество уже существует

                // Сохраняем уникальное множество
                uniqueSets.add(tempSet);
                setsPost[i] = tempSet;

                // Записываем в файл
                out.write(2 + " "); // Длина множества
                for (int item : tempSet) {
                    out.write(item + " ");
                }
                out.newLine();
            }

        }catch(IOException e) { System.out.println(e.getMessage()); }
    }

    // Загрузка задачи из файла
    public AssignmentProblemConflictCombination(File file){
        try(Scanner scanner = new Scanner(file)) {
            n = scanner.nextInt();
            if (n <= 0) {
                throw new IllegalArgumentException("n <= 0");
            }

            max = scanner.nextBoolean();
            conflictPercent = scanner.nextInt();

            costArray = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    costArray[i][j] = scanner.nextInt();
                }
            }

            nw = scanner.nextInt();
            if (nw <= 0) {
                throw new IllegalArgumentException("Отсутствуют конфликтные работники, скорее всего это классическая задача о назначении");
            }
            np = scanner.nextInt();
            if (np <= 0) {
                throw new IllegalArgumentException("Отсутствуют связанные работы, скорее всего это классическая задача о назначении");
            }

            setsWorker = new Set[nw];
            for (int i = 0; i < nw; i++){
                setsWorker[i] = new HashSet<>();
                int sizeSetWorker = scanner.nextInt();
                for(int j=0; j < sizeSetWorker; j++) {
                    setsWorker[i].add(scanner.nextInt());
                }
            }

            setsPost = new Set[np];
            for (int i = 0; i < np; i++){
                setsPost[i] = new HashSet<>();
                int sizeSetsPost = scanner.nextInt();
                for(int j=0; j < sizeSetsPost; j++) {
                    setsPost[i].add(scanner.nextInt());
                }
            }
        }catch (IOException e) { System.out.println(e.getMessage()); }
    }

    // Считает значение целевой функции по решению заданному в виде List<Integer>
    // (используется в классе LocalSearch)
    @Override
    public int function(List<Integer> list){
        int f=0;
        for (int i=0; i<n; i++){
            f += costArray[i][list.get(i)];
        }
        f -= punish(list);
        return f;
    }

    // Создает начальное решение из максимальных элементов в столбцах(столбцы не повторяются)
    @Override
    public ArrayList<Integer> generateSmartStart() {
        return null;
    }

    // Нет ли в назначении конфликтных комбинаций(конфликтные работники назначены вместе на связанные должности)
    private int punish(List<Integer> list){
        int punish = 0;

        for (int i = 0; i < nw; i++) { // Проходим по всем конфликтующим группам работников
            for (int j = 0; j < np; j++) { // Проходим по всем связанным группам должностей

                for (int itemW1 : setsWorker[i]) { // Перебираем работников из конфликтующей группы
                    for (int itemP1 : setsPost[j]) { // Перебираем должности из связанной группы
                        for (int itemW2 : setsWorker[i]) { // Перебираем работников из конфликтующей группы
                            for (int itemP2 : setsPost[j]) { // Перебираем должности из связанной группы

                                //Пропускаем, если работник пытается устроиться на ту же должность (в этом нет конфликта)
                                if (itemW1 == itemW2 && itemP1 == itemP2) continue;

                                else if ( list.get(itemW1) == itemP1 && list.get(itemW2) == itemP2){
                                    punish += punishment;
                                }
                            }
                        }
                    }
                }
            }
        }

        return punish;
    }

    @Override
    public ArrayList<Integer> generateSolverStart(){
        // Вызов решателя
        return solveTask("SCIP", 1000); // Запускаем на секунду

    }

    // Решим задачу используя библиотеку OrTools
    public ArrayList<Integer> solveTask(String solver_name, long time){
        System.out.println("solveTask");
        Loader.loadNativeLibraries();   // Загружает нативные библиотеки, необходимые для OR-Tools.

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

        // На каждую должность назначен 1 работник
        for (int j = 0; j < n; ++j) {
            MPConstraint constraint = solver.makeConstraint(1, 1, "");
            for (int i = 0; i < n; ++i) {
                constraint.setCoefficient(x[i][j], 1);
            }
        }

        // Нельзя назначать конфликтных работников на связанные должности
        for (int i = 0; i < nw; i++) { // Проходим по всем конфликтующим группам работников
            for (int j = 0; j < np; j++) { // Проходим по всем связанным группам должностей

                for (int itemW1 : setsWorker[i]) { // Перебираем работников из конфликтующей группы
                    for (int itemP1 : setsPost[j]) { // Перебираем должности из связанной группы
                        for (int itemW2 : setsWorker[i]) { // Перебираем работников из конфликтующей группы
                            for (int itemP2 : setsPost[j]) { // Перебираем должности из связанной группы

                                //Пропускаем, если работник пытается устроиться на ту же должность (в этом нет конфликта)
                                if (itemW1 == itemW2 && itemP1 == itemP2) continue;

                                //Создаем ограничение, которое гарантирует, что не может быть назначено сразу два работника из конфликтной группы на две связанные должности.
                                MPConstraint constraint = solver.makeConstraint(0, 1, ""); // Меньше или равно 1

                                constraint.setCoefficient(x[itemW1][itemP1], 1); // Если этот работник назначен на эту должность
                                constraint.setCoefficient(x[itemW2][itemP2], 1); // ...и этот работник назначен на эту должность

                                //System.out.println("Ограничение: " + itemW1 + " на " + itemP1 + " И " + itemW2 + " на " + itemP2 + " <= 1");
                            }
                        }
                    }
                }
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
        if (!max) {
            objective.setMinimization();
        }
        else{
            objective.setMaximization();
        }

        // Вызов решателя
        solver.setTimeLimit(time);

        if (time == time_limit_milliseconds) {
            resultStatus = solver.solve();
            wall_time = solver.wallTime();
            decisionSolverOrTools = objective.value();
        }

        ArrayList<Integer> pi = new ArrayList<>();

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (x[i][j].solutionValue() > 0.5) {
                    pi.add(j);
                }
            }
        }

        return pi;

        /*
        // Выведем решение
        // Check that the problem has a feasible solution.
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL
                || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            System.out.println("Total cost: " + objective.value() + "\n");
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    if (x[i][j].solutionValue() > 0.5) {
                        System.out.print(j + " ");
                    }
                }
            }
        } else {
            System.err.println("No solution found.");
        }

         */

    }


}

