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
    public List<Integer> pi; // оптимальное решение
    public boolean max = true; // временный костыль

    // Загрузка задачи из файла
    public LinearAssignmentProblem(File file){
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
        }catch (IOException e) { System.out.println(e.getMessage()); }

        // сразу же найдем точное решение
        pi = Arrays.stream(OptimalAssignmentSolver.kuhnMunkres(costArray)).boxed().toList();
        fPi = function(pi);
        super.setN(n);
        super.setfPi(fPi);
    }

    // Считает значение целевой функции по решению заданному в виде List<Integer>
    // (используется в классе LocalSearch)
    @Override
    public int function(List<Integer> list){
        int f=0;
        for (int i=0; i<n; i++){
            f += costArray[i][list.get(i)];
            //  System.out.println("F: " + f);
        }
        return f;
    }

    // Создает задачу о назначении по введенному значению n
    public static void generateAssignmentProblem(){
        System.out.println("Генерация случайно задачи о назначении.\nВведите название файла, в который сохранить условие задачи: ");
        Scanner s = new Scanner(System.in);
        String fileNameCondition = "D:\\ярлыкиРабочегоСтола\\univer\\3course\\courseWork\\localSearch\\src\\main\\java\\org\\example" + s.nextLine() + ".txt";
        File fileCondition = new File(fileNameCondition);
        try(BufferedWriter out = new BufferedWriter(new FileWriter(fileCondition))) {
            System.out.println("Введите количество должностей: ");
            int n = s.nextInt();
            out.write(Integer.toString(n));
            out.newLine();

            System.out.println("Введите 1, если задача на максимум, 0 - минимум: ");
            int  max = s.nextInt();
            if (max == 1){
                out.write("true");
            }
            else if (max == 0){
                out.write("false");
            }
            out.newLine(); out.newLine();

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    out.write(Integer.toString((new Random()).nextInt(20)));
                    out.write(" ");
                }
                out.newLine();
            }
        }catch(IOException e) { System.out.println(e.getMessage()); }
        System.out.println("Готово!");
    }

    // Генерация задачи о назначении с сохранением в файл по заданному n и файлу...
    public static void generateAssignmentProblem(int n, File file){
        try(BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            out.write(Integer.toString(n));
            out.newLine();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    out.write(Integer.toString((new Random()).nextInt(20)));
                    out.write(" ");
                }
                out.newLine();
            }
        }catch(IOException e) { System.out.println(e.getMessage()); }
    }


    // Создает начальное решение из максимальных элементов в столбцах(столбцы не повторяются)
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


    // Решение задачи через библиотеку OrTools
    public void solveTask(){
        Loader.loadNativeLibraries();


        // Объявим решателем SCIP.
        // Есть еще несколько разных решателей, например, альтернативный PDLP, или GLOP для линейного программирования
        // Про решатели: https://developers.google.com/optimization/lp/lp_advanced?hl=ru
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            System.out.println("Could not create solver SCIP");
            return;
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
        // Задача на минимум
        objective.setMaximization();

        // Вызов решателя
        MPSolver.ResultStatus resultStatus = solver.solve();

        // Выведем решение
        // Check that the problem has a feasible solution.
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL
                || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            System.out.println("Total cost: " + objective.value() + "\n");
            /*for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    // Test if x[i][j] is 0 or 1 (with tolerance for floating point
                    // arithmetic).
                    if (x[i][j].solutionValue() > 0.5) {
                        System.out.println(
                                "Worker " + i + " assigned to task " + j + ".  Cost = " + costArray[i][j]);
                    }
                }
            }

             */
        } else {
            System.err.println("No solution found.");
        }
    }
}

