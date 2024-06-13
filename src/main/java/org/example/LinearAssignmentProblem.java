package org.example;
import java.io.*;
import java.util.*;

// Задача о назначении
public class LinearAssignmentProblem /*extends AssignmentProblem*/ {
    private int n; // количество зданий и цехов
    public int[][] costArray; // матрица стоимости
    public List<Integer> pi; // оптимальное решение
    public int fPi; // значение целевой функции при оптимальном решении

    // загрузка задачи из файла
    public LinearAssignmentProblem(File file){
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

    // генерация задачи о назначении
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

    // сохранение задачи о назначениях в файл
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

    public int getN() {
        return n;
    }

    // построим начальное решение из максимальных элементов в столбцах(столбцы не повторяются)
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
}

