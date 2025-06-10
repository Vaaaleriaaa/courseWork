package org.example;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.io.*;
import java.util.*;

// Классическая задача о назначении

public class LinearAssignmentProblem extends AbstractAssignmentProblem {
    public int[][] costArray; // матрица стоимости

    // Загрузка задачи из файла
    public LinearAssignmentProblem(File file) {
        try (Scanner scanner = new Scanner(file)) {

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
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Loader.loadNativeLibraries();

    }

    // Считает значение целевой функции по решению заданному в виде List<Integer>
    // (используется в классе LocalSearch)
    @Override
    public int function(List<Integer> list) {
        int f = 0;
        for (int i = 0; i < n; i++) {
            f += costArray[list.get(i)][i];
        }
        return f;
    }

    // Генерация задачи о назначении с сохранением в файл по заданному n и файлу...
    public static void generateAssignmentProblem(int n, boolean max, File file) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            out.write(Integer.toString(n) + " " + Boolean.toString(max));
            out.newLine();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    out.write(Integer.toString((new Random()).nextInt(20)));
                    out.write(" ");
                }
                out.newLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    // Создает начальное решение из максимальных элементов в столбцах(столбцы не повторяются)
    @Override
    public ArrayList<Integer> generateSmartStart() {
        ArrayList<Integer> pi = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            int elementAdd = Integer.MAX_VALUE; // Минимальное значение для текущей строки
            int indexElementAdd = -1; // Индекс элемента для добавления

            // Находим минимальное значение в строке costArray[i]
            for (int k = 0; k < n; k++) {
                if (elementAdd > costArray[i][k]) {
                    elementAdd = costArray[i][k];
                    indexElementAdd = k;
                }
            }

            // Проверяем, что выбранный индекс еще не добавлен в список
            while (pi.contains(indexElementAdd)) {
                elementAdd = Integer.MAX_VALUE; // Сбрасываем минимальное значение
                indexElementAdd = -1; // Сбрасываем индекс

                // Находим следующее минимальное значение
                for (int k = 0; k < n; k++) {
                    if (!pi.contains(k) && costArray[i][k] < elementAdd) {
                        elementAdd = costArray[i][k];
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
        return solveTask("SCIP", 2000);
    }

    public ArrayList<Integer> getInvalidList(List<Integer> pi) {
        return null;
    }


    // Решение задачи через библиотеку OrTools
    public ArrayList<Integer> solveTask(String solver_name, long time) {

        // Объявим решателем
        MPSolver solver = MPSolver.createSolver(solver_name);
        if (solver == null) {
            System.out.println("Could not create solver " + solver_name);
            return null;
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
        // Each worker is assigned to at most one task.
        for (int i = 0; i < n; ++i) {
            MPConstraint constraint = solver.makeConstraint(1, 1, "");
            for (int j = 0; j < n; ++j) {
                constraint.setCoefficient(x[i][j], 1);
            }
        }
        // Each task is assigned to exactly one worker.
        for (int j = 0; j < n; ++j) {
            MPConstraint constraint = solver.makeConstraint(1, 1, "");
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
        } else {
            objective.setMaximization();
        }

        resultStatus = solver.solve();
        if (time == time_limit_milliseconds) {
            wall_time = solver.wallTime();
            decisionSolverOrTools = objective.value();
        } else {
            resultStatus = solver.solve();
            if (time == time_limit_milliseconds) {
                wall_time = solver.wallTime();
                decisionSolverOrTools = objective.value();
            } else {
                ArrayList<Integer> pi = new ArrayList<>();
                for (int i = 0; i < n; ++i) {
                    for (int j = 0; j < n; ++j) {
                        if (x[i][j].solutionValue() > 0.5) {
                            pi.add(j);
                            System.out.println(j);
                        }
                    }
                }
                return pi;
            }
        }
        return null;
    }

}

