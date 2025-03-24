package org.example.problemsConfComb;

import com.google.ortools.Loader;
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
    private int nw; // количество множеств конфликтных работников
    private int np; // количество множеств связанных работ
    private Set<Integer>[] setsWorker; // множества конфликтных работников
    private Set<Integer>[] setsPost; // множества связанных работ

    // Создает задачу о назначении по введенному значению n
    public static void generateAssignmentProblem(){
        System.out.println("Генерация случайно задачи о назначении с конфликтными комбинациями.\nВведите название файла, в который сохранить условие задачи: ");
        Scanner s = new Scanner(System.in);
        String fileNameCondition = "D:\\ярлыкиРабочегоСтола\\univer\\3course\\courseWork\\localSearch\\src\\main\\java\\org\\example\\problemsConfComb\\" + s.nextLine() + ".txt";
        File fileCondition = new File(fileNameCondition);
        try(BufferedWriter out = new BufferedWriter(new FileWriter(fileCondition))) {
            System.out.println("Введите количество должностей: ");
            int n = s.nextInt();

            // Записываем количество должностей и работников
            out.write(Integer.toString(n));
            out.write(" ");

            // Генерируем и записываем количество комбинация конфликтных работников
            int nw = 1;
            // out.write(Integer.toString(1+(new Random()).nextInt(n/2.)));
            out.write(nw + " ");

            // Генерируем и записываем количество комбинация связанных работ
            int np = 1;
            // out.write(Integer.toString(1+(new Random()).nextInt(n/2.)));
            out.write(np +" ");

            System.out.println("Введите 1, если задача на максимум, 0 - минимум: ");
            int  max = s.nextInt();
            if (max == 1){
                out.write("true");
            }
            else if (max == 0){
                out.write("false");
            }
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

            // Генерируем и записываем значения множеств конфликтных работников
            // Создаем массив из множеств конфликтных работников
            Set<Integer>[] setsWorker = new Set[nw];
            for (int i = 0; i < nw; i++) {
                // Запишем длину множества
                out.write(2 + " ");
                // Создаем каждое множество и добавляем в него по 2 работника
                setsWorker[i] = new HashSet<>();
                while (setsWorker[i].size() < 2) {
                    setsWorker[i].add((new Random()).nextInt(n));
                }
                // Записываем получившиеся множество в файл
                for (int item: setsWorker[i]){
                    out.write(item + " ");
                }
                out.newLine();
            }
            out.newLine();

            // Генерируем и записываем значения множеств связанных работ
            // Создаем массив из множеств связанных работ
            Set<Integer>[] setsPost = new Set[np];
            for (int i = 0; i < np; i++) {
                // Запишем длину множества
                out.write(2 + " ");
                // Создаем каждое множество и добавляем в него по 2 работника
                setsPost[i] = new HashSet<>();
                while (setsPost[i].size() < 2) {
                    setsPost[i].add((new Random()).nextInt(n));
                }
                // Записываем получившиеся множество в файл
                for (int item: setsPost[i]){
                    out.write(item + " ");
                }
                out.newLine();
            }

        }catch(IOException e) { System.out.println(e.getMessage()); }
        System.out.println("Готово!");
    }

    // Загрузка задачи из файла
    public AssignmentProblemConflictCombination(File file){
        try(Scanner scanner = new Scanner(file)) {
            n = scanner.nextInt();
            if (n <= 0) {
                throw new IllegalArgumentException("n <= 0");
            }
            nw = scanner.nextInt();
            if (nw <= 0) {
                throw new IllegalArgumentException("Отсутствуют конфликтные работники, скорее всего это классическая задача о назначении");
            }
            np = scanner.nextInt();
            if (np <= 0) {
                throw new IllegalArgumentException("Отсутствуют связанные работы, скорее всего это классическая задача о назначении");
            }
            max = scanner.nextBoolean();
            costArray = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    costArray[i][j] = scanner.nextInt();
                }
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
        return f;
    }

    @Override
    public ArrayList<Integer> generateSmartStart() {
        return null;
    }

    // Решим задачу используя библиотеку OrTools
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

        /* // Нельзя назначать конфликтных работников на связанные должности
        for (int i = 0; i<nw; i++){
            for (int j=0; j < np; j++){
                for(int itemW: setsWorker[i]){
                    for( int itemP: setsPost[j]){
                        MPConstraint constraint = solver.makeConstraint(0, 1, "");
                        constraint.setCoefficient(x[itemW][itemP], 1);
                    }
                }
            }
        }

         */




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
                    if (x[i][j].solutionValue() > 0.5) {
                        System.out.print(j + " ");
                    }
                }
            }
        } else {
            System.err.println("No solution found.");
        }
    }
}

