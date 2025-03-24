package org.example.problemsVolumeLimit;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MathModelVolumeLimit {
    private int n; // количество зданий и цехов
    public boolean max; // Задача на максимум(true) или на минимум(false)
    private int[][] costArray; // матрица стоимости
    private int[] volumeLimit; // матрица ограничений на объемы
    private int[] appointmentLimit; // матрица ограничений на количество назначений на одно здание


    public MathModelVolumeLimit(File file){
        try(Scanner scanner = new Scanner(file)) {
            n = scanner.nextInt();
            if (n <= 0) {
                throw new IllegalArgumentException("n <= 0");
            }

            max = scanner.nextBoolean();
            System.out.println("DONE 1");

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

    public void solveTask(){
        Loader.loadNativeLibraries();   // Загружает нативные библиотеки, необходимые для OR-Tools.

        // Объявим решателем SCIP.
        // Есть еще несколько разных решателей, например, альтернативный PDLP, или GLOP для линейного программирования
        // Про решатели: https://developers.google.com/optimization/lp/lp_advanced?hl=ru
        MPSolver solver = MPSolver.createSolver("SCIP");    // Создает решатель SCIP.
        if (solver == null) {   // Проверяет, удалось ли создать решатель.
            System.out.println("Could not create solver SCIP");     // Выводит сообщение об ошибке, если не удалось.
            return;     // Завершает программу.
        }

        // Создадим переменные
        // x[i][j] is an array of 0-1 variables, which will be 1 if worker i is assigned to task j.
        MPVariable[][] x = new MPVariable[n][n];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                x[i][j] = solver.makeIntVar(0, 1, "Работник i назначен на должность j");
            }
        }

        // Создадим ограничения

        // Каждый работник может быть назначен на одну должность
        for (int i = 0; i < n; ++i) {
            MPConstraint constraint = solver.makeConstraint(1, 1, "");
            for (int j = 0; j < n; ++j) {
                constraint.setCoefficient(x[i][j], 1);
            }
        }

        // Суммарное ограничение на суммарные затраты на работников на должности
        for (int j = 0; j < n; ++j) {
            MPConstraint constraint = solver.makeConstraint(0, volumeLimit[j], "");
            for (int i = 0; i < n; ++i) {
                constraint.setCoefficient(x[i][j], costArray[i][j]);
            }
        }

        // На каждую должность назначено не больше appointmentLimit[j] работников
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
        // Задача на минимум
        if (max == false) {
            objective.setMinimization();
        }
        else{
            objective.setMaximization();
        }

        // Вызов решателя
        MPSolver.ResultStatus resultStatus = solver.solve();

        // Выведем решение
        // Check that the problem has a feasible solution.
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL
                || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            System.out.println("Total cost: " + objective.value() + "\n");
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    if (x[i][j].solutionValue() == 1) {
                        System.out.print(j + " ");
                    }
                }
            }
        } else {
            System.err.println("No solution found.");
        }
    }

}

