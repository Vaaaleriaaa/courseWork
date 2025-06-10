package org.example;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// абстрактный класс задачи о назначениях
public abstract class AbstractAssignmentProblem {
    public int n; // количество работников и ддолжностей
    public boolean max; // Задача на максимум(true) или на минимум(false)
    public double decisionSolverOrTools; // значение целевой функции найденное решателем библиотеки orTools
    public MPSolver.ResultStatus resultStatus; // Статус найденного решения (допустимое, оптимальное, задача некорректная и т.д.) с помощью orTools
    public long wall_time; // время, за которое solver orTools нашел решение
    public long time_limit_milliseconds = 1800000 * 4; // 1800000 - 30 минут, 7200000 - 2 часа, ограничение на время решения solver

    public void setN(int n) {
        this.n = n;
    }

    public void setDecisionSolverOrTools(int decisionSolverOrTools) {
        this.decisionSolverOrTools = decisionSolverOrTools;
    }

    // чтобы посчитать значение целевой функции в классе LocalSearch
    public abstract int function(List<Integer> list);

    public int getN() {
        return n;
    }

    public boolean getMax() {
        return max;
    }

    // Создает умное начального решения
    public abstract ArrayList<Integer> generateSmartStart();

    // Создает начального решения с помощью солвера
    public abstract ArrayList<Integer> generateSolverStart();

    // Создает список конфликтных работников
    public abstract ArrayList<Integer> getInvalidList(List<Integer> pi);

}
