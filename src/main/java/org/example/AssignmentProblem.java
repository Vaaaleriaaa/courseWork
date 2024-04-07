package org.example;
import java.io.*;
import java.util.*;

// Задача о назначении
public class AssignmentProblem {
    public int n; // количество должностей и работников
    public int[][] costArray; // матрица стоимости
    public List<Integer> pi; // оптимальное решение
    public int fPi; // значение целевой функции при оптимальном решении

    public AssignmentProblem(File file){
        try(Scanner scanner = new Scanner(file)) {
            n = scanner.nextInt();
            if (n <= 0) {
                throw new IllegalArgumentException("n <= 0");
            }
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
    }

    // чтобы посчитать значение целевой функции в классе LocalSearch
    public int function(List<Integer> list){
        int f=0;
        for (int i=0; i<n; i++){
            f += costArray[i][list.get(i)];
          //  System.out.println("F: " + f);
        }
        return f;
    }

    public static void generateAssignmentProblem(){
        System.out.println("Генерация случайно задачи о назначении.\nВведите название файла, в который сохранить условие задачи: ");
        Scanner s = new Scanner(System.in);
        String fileNameCondition = "D:\\ярлыкиРабочегоСтола\\3course\\courceWork\\localSearch\\src\\main\\java\\org\\example\\problems\\" + s.nextLine() + ".txt";
        File fileCondition = new File(fileNameCondition);
        try(BufferedWriter out = new BufferedWriter(new FileWriter(fileCondition))) {
            System.out.println("Введите количество должностей: ");
            int n = s.nextInt();
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
        System.out.println("Готово!");
    }

    public static void generateAssignmentProblem(int n, File file){
        try(BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            out.write(Integer.toString(n));
            out.newLine();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    out.write(Integer.toString((new Random()).nextInt(100)));
                    out.write(" ");
                }
                out.newLine();
            }
        }catch(IOException e) { System.out.println(e.getMessage()); }
    }

}

