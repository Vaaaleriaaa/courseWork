package org.example;

import java.io.*;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {

        String folder = "D:\\ярлыкиРабочегоСтола\\univer\\3course\\courseWork\\localSearch\\src\\main\\java\\org\\example\\problems\\confComb\\n_10\\p_25_30\\";

        File file_result = new File(folder + "test.txt");

        try (BufferedWriter out = new BufferedWriter(new FileWriter(file_result))) {
            out.write("Task Status_SCIP Time_SCIP Desigion_SCIP Time_LS_1p1 Desigion_LS_1p1 ErrorRate_LS_1p1\n"); // Time_LS_1p1 Desigion_LS_1p1 ErrorRate_LS_1p1
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


        for (int i = 0; i<30; i++){
            File fileProblem = new File(folder + "t" + i + ".txt");
            AssignmentProblemConflictCombination problem = new AssignmentProblemConflictCombination(fileProblem);
            try (BufferedWriter out = new BufferedWriter(new FileWriter(file_result, true))) {
                out.write("t"+ i + " " + problem.conflictPercent + " ");

                problem.solveTask("SCIP", problem.time_limit_milliseconds);
                out.write(problem.resultStatus + " " + problem.wall_time + " " + problem.decisionSolverOrTools + " ");

                LocalSearch localSearch = new LocalSearch(problem);
                localSearch.localSearchRestartStep1p1();
                out.write(localSearch.wall_time_ls + " " + localSearch.rec + " " + localSearch.getErrorRate() + " ");

                out.newLine();
                System.out.println("task " + i + " solved!");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }
}

        //String file_result = folder + "\\A_TCC_25_60_n_" + 15 + ".txt";
        //HashSet<String> solvers = new HashSet<>();
        //Collections.addAll(solvers,  "SCIP", "CBC"); //"GLOP", "PDLP",
        // GLOP, PDLP - только для дробных переменных

        //String solver_name = "CBC";


        // Перебираем разные размерности задачи
        //for (int n = 15; n < 16; n += 5) {

/*
            try (BufferedWriter out = new BufferedWriter(new FileWriter(file_result))) {
                out.write("Task ConflictPercent Status_solve Time_Solver Desigion\n");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

 */


/*
            // Количество задач
            for (int i = 0; i < 10; i++) {
                int conflictPercent = (new Random()).nextInt(25, 31); // вернет случайное целое число от 25(включительно) до 31(исключительно)

                File file = new File(folder + "\\task_" + i + "_n_" + n + "p_" + conflictPercent + ".txt");
                AssignmentProblemConflictCombination.generateAssignmentProblem(n, true, conflictPercent, file);

                try (BufferedWriter out = new BufferedWriter(new FileWriter(file_result, true))) {

                    AssignmentProblemConflictCombination model = new AssignmentProblemConflictCombination(file);

                    model.solveTask(solver_name);
                    out.write("task_" + i + "_n" + n + " " + conflictPercent + " ");
                    out.write(model.resultStatus + " " + Long.toString(model.wall_time) + " " + model.decisionSolverOrTools + "\n");


                    //LocalSearch localSearch = new LocalSearch(model);
                    //localSearch.localSearchRestartStep();


                    //out.write(localSearch.wall_time_ls + " " + localSearch.rec + " " + localSearch.getErrorRate());
                    //out.newLine();

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("Задача " + i + " размерности " + n + " решена!");
            }

 */



/*
            // Количество задач
            for (int i = 0; i < 8; i++) {
                int conflictPercent = (new Random()).nextInt(31, 41); // вернет случайное целое число от 25(включительно) до 31(исключительно)

                File file = new File(folder + "\\task_" + (10+i) + "_n_" + n + "p_" + conflictPercent + ".txt");
                AssignmentProblemConflictCombination.generateAssignmentProblem(n, true, conflictPercent, file);

                try (BufferedWriter out = new BufferedWriter(new FileWriter(file_result, true))) {

                    AssignmentProblemConflictCombination model = new AssignmentProblemConflictCombination(file);

                    model.solveTask(solver_name);
                    out.write("task_" + (10+i) + "_n" + n + " " + conflictPercent + " ");
                    out.write(model.resultStatus + " " + Long.toString(model.wall_time) + " " + model.decisionSolverOrTools + "\n");


                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println( "Задача " + (10+i) + " размерности " + n + " решена!");
            }
            */

/*
            // Количество задач
            for (int i = 0; i < 10; i++) {
                int conflictPercent = (new Random()).nextInt(41, 51); // вернет случайное целое число от 25(включительно) до 31(исключительно)

                File file = new File(folder + "\\task_" + (20 + i) + "_n_" + n + "p_" + conflictPercent + ".txt");
                AssignmentProblemConflictCombination.generateAssignmentProblem(n, true, conflictPercent, file);

                try (BufferedWriter out = new BufferedWriter(new FileWriter(file_result, true))) {

                    AssignmentProblemConflictCombination model = new AssignmentProblemConflictCombination(file);

                    model.solveTask(solver_name);
                    out.write("task_" + (20 + i) + "_n" + n + " " + conflictPercent + " ");
                    out.write(model.resultStatus + " " + Long.toString(model.wall_time) + " " + model.decisionSolverOrTools + "\n");


                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("Задача " + (20 + i) + " размерности " + n + " решена!");
            }


        }
    }
}

 */