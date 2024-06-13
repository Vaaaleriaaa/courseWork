package org.example;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {

        String folder = "D:\\ярлыкиРабочегоСтола\\3course\\courceWork\\localSearch\\src\\main\\java\\org\\example\\";

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
        }

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
        }
    }
}
