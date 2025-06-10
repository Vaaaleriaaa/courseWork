package org.example;

import java.io.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {

        String folderLin = "src\\main\\java\\org\\example\\problems\\lin\\20\\";
        String folderVL = "src\\main\\java\\org\\example\\problems\\volumeLimit\\n_20\\";
        String folderCC = "src\\main\\java\\org\\example\\problems\\confComb\\n_20\\p_25_30\\";

        /*
        ////// ДЛЯ ОТЛАДКИ
        String folderCCtest = "src\\main\\java\\org\\example\\problems\\confComb\\n_5\\p_25_30\\";
        for (int i = 0; i < 1; i++) {
            File problemFile = new File(folderCCtest + "t" + i + ".txt");
            AssignmentProblemConflictCombination problem = new AssignmentProblemConflictCombination(problemFile);

            LocalSearch lc = new LocalSearch(problem);
            lc.localSearchRestartStep1p1();

        }
        */


        File file_result;


        /// Решаем с конфликтными комбинациями
        file_result = new File(folderCC + "LS1p1_cc.txt");
        try(BufferedWriter out = new BufferedWriter(new FileWriter(file_result, true))) {
            //out.write("Task CountIteration Time_LS_1p1 Desigion_LS_1p1 Status_SCIP Time_SCIP Desigion_SCIP ErrorRate_LS_1p1\n");
            out.newLine();
            out.newLine();
        }




        for (int i = 1; i < 30; i++) {

            File problemFile = new File(folderCC + "t" + i + ".txt");

            AssignmentProblemConflictCombination problem = new AssignmentProblemConflictCombination(problemFile);

            LocalSearch lc = new LocalSearch(problem);
            lc.localSearchRestartStep1p1();

            problem.solveTask("SCIP", problem.time_limit_milliseconds);

            try(BufferedWriter out = new BufferedWriter(new FileWriter(file_result, true))) {
                out.write(i + " " + lc.step + " " + lc.wall_time_ls + " " + lc.rec + " ");
                out.write(problem.resultStatus + " " + problem.wall_time + " " + problem.decisionSolverOrTools + " " + lc.getErrorRate() + "\n");

            }
            System.out.println("Task " + i + " solved!!!");

        }
    }
}



        /*
        try(BufferedWriter out = new BufferedWriter(new FileWriter(file_result))) {
            out.write("НомерЗадачи ИсходнаяПогрешность swap invert shuffle insertP insert swapK insertK\n");
        }





        // исследование всех окрестностей на 30 задачах, ищем оптимальное решение задачи с помощью одной из окрестностей и записываем погрешность
        for (int i = 29; i < 30; i++) {

            AssignmentProblemConflictCombination problem = new AssignmentProblemConflictCombination(new File(folder + "t" + i + ".txt"));
            LocalSearch lc = new LocalSearch(problem);
            lc.generateSolverPi();
            try(BufferedWriter out = new BufferedWriter(new FileWriter(file_result, true))) {
                out.write(i + " " + lc.getErrorRate() );
            }
            ArrayList<Integer> pi = new ArrayList<>();
            pi.addAll(lc.pi);
            int numSteps = 4000;
                for (int v = 0; v<7; v++){
                    System.out.println("PI MAIN " + pi);
                    System.out.println("PI MAIN " + pi);
                    System.out.println("PI MAIN " + pi);
                    try(BufferedWriter out = new BufferedWriter(new FileWriter(file_result, true))) {
                        out.write( " " + lc.researchNeighborhood( pi ,numSteps, v) );
                    }
                }
            try(BufferedWriter out = new BufferedWriter(new FileWriter(file_result, true))) {
                out.newLine();
            }
        }
   }
}
         */





        /*
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file_result))) {
            out.write("шапка\n"); // Time_LS_1p1 Desigion_LS_1p1 ErrorRate_LS_1p1
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


        for (int i = 0; i<30; i++){
            File fileProblem = new File(folder + "t" + i + ".txt");
            AssignmentProblemConflictCombination problem = new AssignmentProblemConflictCombination(fileProblem);
            try (BufferedWriter out = new BufferedWriter(new FileWriter(file_result, true))) {
                out.write("t"+ i + " " + problem.conflictPercent + " ");

                problem.solveTask("SCIP", problem.time_limit_milliseconds);

                LocalSearch localSearch = new LocalSearch(problem);
                localSearch.localSearchRestartStep1p1();
                out.write(localSearch.wall_time_ls + " " + localSearch.rec + " " + localSearch.getErrorRate());

                out.newLine();
                System.out.println("task " + i + " solved!");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }
}

         */

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