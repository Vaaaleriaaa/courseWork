package org.example;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        String folder = "D:\\ярлыкиРабочегоСтола\\3course\\courceWork\\localSearch\\src\\main\\java\\org\\example\\problems\\exp\\";
        try(BufferedWriter out = new BufferedWriter(new FileWriter(folder + "RESULTS.txt"))) {
            for (int i=0; i<30; i++) {
                out.write("T" + i + ": ");
                AssignmentProblem.generateAssignmentProblem(20, new File(folder + "t" + i + ".txt"));
                AssignmentProblem assignmentProblem = new AssignmentProblem(new File(folder + "t" + i + ".txt"));
                LocalSearch lc = new LocalSearch(assignmentProblem);
                lc.localSearch(out);
                out.write(" OPT: " + assignmentProblem.fPi  + " ");
                out.newLine();
            }
        }catch(IOException e) { System.out.println(e.getMessage()); }

/*
        File fileNameProblem = new File(new String(folder + "test1.txt"));
        AssignmentProblem assignmentProblem = new AssignmentProblem(fileNameProblem);
        LocalSearch lc1 = new LocalSearch(assignmentProblem);
        System.out.println("OPT: " + assignmentProblem.fPi);
        System.out.println("Pi: " + assignmentProblem.pi);

        String fileNameResult = new String(folder + "results\\" + "Result" + "Test1.txt");
        File fileResult = new File(fileNameResult);
        try(BufferedWriter out = new BufferedWriter(new FileWriter(fileResult))) {
            out.flush();
            lc1.localSearch(out);
        }catch(IOException e) { System.out.println(e.getMessage()); }






 */

    }




}

/*
 File fileNameProblem = new File("test1.txt");
        AssignmentProblem assignmentProblem = new AssignmentProblem(fileNameProblem);
        LocalSearch localSearch = new LocalSearch();
        localSearch.swap();

        int[] arr = {3, 5, 1, 6, 0, 2, 9, 4, 8, 7, 0};
        for(int i = 0; i < arr.length; i++){
            System.out.print(arr[i]);
        }
        System.out.println();
        arr = LocalSearch.insert(arr);
        for(int i = 0; i < arr.length; i++){
            System.out.print(arr[i]);
        }




        AssignmentProblem ap1 = new AssignmentProblem(fileCondition);
        String fileNameResult = "Result" + fileNameCondition;
        File fileResult = new File(fileNameResult);
        try(BufferedWriter out = new BufferedWriter(new FileWriter(fileResult))) {
            out.write(Integer.toString(ap1.f));
            for(int i =0; i<ap1.n; i++){
                out.write(" ");
                out.write(Integer.toString(ap1.pi.get(i)));
            }
            out.newLine();

            List<Integer> updateList = new ArrayList<>(ap1.pi);
            for(int i=0;i<100; i++){
                LocalSearch.randomPer(updateList, out);
                ap1.optCheck(updateList);
                out.write(" " + Integer.toString(ap1.f));
                for(int j =0; j<ap1.n; j++){
                    out.write(" ");
                    out.write(Integer.toString(ap1.pi.get(j)));
                }
                out.newLine();
            }


            ap1.optCheck(OptimalAssignmentSolver.kuhnMunkres(ap1.costArray));
            out.write("\n\n OptimalAssignmentSolver " + ap1.f);

        }catch(IOException e) { System.out.println(e.getMessage()); }
 */