package org.example;
import org.example.problemsConfComb.AssignmentProblemConflictCombination;
import org.example.problemsVolumeLimit.AssignmentProblemVolumeLimit;
import org.example.problemsVolumeLimit.MathModelVolumeLimit;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {

        String folder = "D:\\ярлыкиРабочегоСтола\\univer\\3course\\courseWork\\localSearch\\src\\main\\java\\org\\example\\";
        LinearAssignmentProblem.generateAssignmentProblem(10, new File(folder + "t0_10.txt"));

        /*LinearAssignmentProblem linearAssignmentProblem = new LinearAssignmentProblem(new File(folder + "t0_10.txt"));
        System.out.println("Решаем задачу t1!!!");
        System.out.println("Решение методом Kuhn-Munkres: " + linearAssignmentProblem.function(linearAssignmentProblem.pi));
        System.out.println("Решаем через мат. модель: ");
        MathModel model = new MathModel(new File(folder + "t1_10.txt"));
        model.solveTask();
        System.out.println();
        LocalSearch lc = new LocalSearch(linearAssignmentProblem);
        lc.training();
        lc.localSearchRestart();
        System.out.println("Решение локального поиска с перезапуском: " + lc.rec);

         */








        /*
        //Сгенерируем задачу о назначении с конфликтными комбинациями
        //AssignmentProblemConflictCombination.generateAssignmentProblem();
        // Решим ее!!!
        AssignmentProblemConflictCombination model = new AssignmentProblemConflictCombination(new File(folder + "\\problemsConfComb\\tcc1.txt"));
        model.solveTask();

         */


        /*
        // сгенерируем задачу о назначении с ограничением на объемы
        //  AssignmentProblemVolumeLimit.generateAssignmentProblem();
        // Решим ее!!!
        MathModelVolumeLimit model = new MathModelVolumeLimit(new File(folder + "\\problemsVolumeLimit\\tvl1.txt"));
        model.solveTask();

         */

        /* MathModel model = new MathModel(new File(folder + "\\problems\\exp\\t0.txt"));
        model.solveTask();

        LinearAssignmentProblem linearAssignmentProblem0 = new LinearAssignmentProblem(new File(folder + "\\problems\\exp\\t0.txt"));
        System.out.println(linearAssignmentProblem0.function(linearAssignmentProblem0.pi));
         */

        /*
        // исследование 5 окрестностей на 1 задаче
        LinearAssignmentProblem linearAssignmentProblem0 = new LinearAssignmentProblem(new File(folder + "\\problems\\exp\\t0.txt"));
        LocalSearch lc0 = new LocalSearch(linearAssignmentProblem0);
        lc0.researchAllNeighborhood(1000, 1); // запускаем исследование на 1000 итерации, записывать результат будем на каждой из итерации

        // исследование всех окрестностей на 30 задач, ищем оптимальное решение задачи с помощью одной из окрестностей и записываем погрешность
        try(BufferedWriter out = new BufferedWriter(new FileWriter(folder + "\\problems\\exp\\researchNeighborhoodsOn30Tasks.txt"))) {
            out.write("НомерЗадачи swap invert shuffle insertP insert\n");
            for (int i = 0; i < 30; i++) {
                LinearAssignmentProblem linearAssignmentProblem = new LinearAssignmentProblem(new File(folder + "\\problems\\exp\\t" + i + ".txt"));
                LocalSearch lc = new LocalSearch(linearAssignmentProblem);
                int numSteps = 1000;
                int v = 0;
                switch (v) {
                    case (0):
                        out.write(i + " " + lc.researchNeighborhood(numSteps, v));
                        v++;
                    case (1):
                        out.write(i + " " + lc.researchNeighborhood(numSteps, v));
                        v++;
                    case (2):
                        out.write(i + " " + lc.researchNeighborhood(numSteps, v));
                        v++;
                    case (3):
                        out.write(i + " " + lc.researchNeighborhood(numSteps, v));
                        v++;
                    case (4):
                        out.write(i + " " + lc.researchNeighborhood(numSteps, v) + "\n");
                }
            }
        } catch (IOException e) { System.out.println(e.getMessage());}

        // исследование алгоритма поиска с перезапуском на 30 задачах
        try(BufferedWriter out = new BufferedWriter(new FileWriter(folder + "\\problems\\exp\\researchRestart.txt"))) {
            out.write("НомерЗадачи Погрешность\n");
            for (int i = 0; i < 30; i++) {
                LinearAssignmentProblem linearAssignmentProblem = new LinearAssignmentProblem(new File(folder + "\\problems\\exp\\t" + i + ".txt"));
                LocalSearch lc = new LocalSearch(linearAssignmentProblem);
                lc.training();
                lc.localSearchRestart();
                out.write(i + " " + lc.getErrorRate() + "\n");
            }
        } catch (IOException e) { System.out.println(e.getMessage());}


        // исследование алгоритма поиска с перезапуском на квадратичных задачых
        try(BufferedWriter out = new BufferedWriter(new FileWriter(folder + "\\problems\\QAP\\researchQRandomStart20000.txt"))) {
            out.write("Задача Погрешность\n");
            for (int i = 0; i < 8; i++) {
                QuadraticAbstractAssignmentProblem quadraticAssignmentProblem = new QuadraticAbstractAssignmentProblem(new File(folder + "\\problems\\QAP\\qt" + i + ".txt"));

                LocalSearchMin localSearchMin = new LocalSearchMin(quadraticAssignmentProblem);
                localSearchMin.training();
                localSearchMin.localSearchRestart();
                out.write("qt" + i + " " + localSearchMin.getErrorRate() + "\n");
            }
            QuadraticAbstractAssignmentProblem quadraticAssignmentProblem = new QuadraticAbstractAssignmentProblem(new File(folder + "\\problems\\QAP\\qt" + 8 + ".txt"));

            LocalSearchMin localSearchMax = new LocalSearchMin(quadraticAssignmentProblem);
            localSearchMax.training();
            localSearchMax.localSearchRestart();
            out.write("qt" + 8 + " " + localSearchMax.getErrorRate() + "\n");
        } catch (IOException e) { System.out.println(e.getMessage());}

         */
    }
}
