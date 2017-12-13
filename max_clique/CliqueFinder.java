package max_clique;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.alg.color.RandomGreedyColoring;

import java.math.BigDecimal;
import java.util.*;


public class CliqueFinder {

    long startTime;
    long timeLimit;

    private Set<Integer> maxClique = new TreeSet();
    private SimpleGraph<Integer, DefaultEdge> graph;
    private IloCplex problem;
    private Integer graphSize;
    private IloNumVar[] x;

    public CliqueFinder(SimpleGraph<Integer, DefaultEdge> graph) throws IloException {
        this.graph = graph;
        this.problem = new IloCplex();
        problem.setOut(null);
        graphSize = graph.vertexSet().size();
        double lb_array[] = new double[graphSize];
        Arrays.fill(lb_array, 0.0);
        double ub_array[] = new double[graphSize];
        Arrays.fill(ub_array, 1.0);
        String[] varname = new String[graphSize];
        for (int i = 0; i < graphSize; i++) {
            varname[i] = "x" + i;
        }
        x = problem.numVarArray(graphSize, lb_array, ub_array, varname);
        double[] objvals = ub_array;

        problem.addMaximize(problem.scalProd(x, objvals));

        List<ColoringCollection> coloringRestrictions = getGreedyColoringCollections();
        for (ColoringCollection coloring : coloringRestrictions) {
            Set<Integer> colorSet = coloring.getColorSet();
            if (colorSet.size() > 1) {
                IloNumExpr iloNumExpr = problem.numExpr();
                for (Integer node : colorSet) {
                    iloNumExpr = problem.sum(iloNumExpr, x[node - 1]);
                }
                problem.addLe(iloNumExpr, 1);
            }

        }

        Set<Integer> nodes = graph.vertexSet();
        for (Integer node : nodes) {
            for (Integer node_ : nodes) {
                if (node.intValue() != node_.intValue()) {
                    if (!graph.containsEdge(node, node_)) {
                        IloNumExpr iloNumExpr = problem.sum(x[node - 1], x[node_ - 1]);
                        problem.addLe(iloNumExpr, 1);
                    }
                }


            }
        }
    }

    private List<ColoringCollection> getGreedyColoringCollections() {
        List coloringCollection = new LinkedList();
        for (int i = 0; i <= 20; i++) {
            coloringCollection.addAll(new RandomGreedyColoring(this.graph).getColoring().getColorClasses());
            //System.out.println(coloringCollection);
        }
        Iterator it = coloringCollection.iterator();
        List<ColoringCollection> resultList = new LinkedList<ColoringCollection>();
        while (it.hasNext()) {
            resultList.add(new ColoringCollection((Set) it.next()));
        }
        return resultList;
    }

    private void branching() throws IloException {
        if (problem.solve()) {
            double currentObjFuncValue = problem.getObjValue();
            if (maxClique.size() < Math.floor(currentObjFuncValue)) {
                double[] currentValues = problem.getValues(x);
                if (isIntegerSolution(currentValues)) {
                    maxClique.clear();
                    for (int i = 0; i < currentValues.length; i++) {
                        if (currentValues[i] == 1.0) {
                            maxClique.add(i + 1);
                        }
                    }
                    return;
                } else {
                    Integer branchingIndex = 0;
                    Integer branchingScale = 0;
                    for (int i = 0; i < currentValues.length; i++) {
                        Integer currScale = BigDecimal.valueOf(currentValues[i]).scale();
                        if (currScale > branchingScale) {
                            branchingScale = currScale;
                            branchingIndex = i;
                        }
                    }
                    IloRange firstBranchConstraint = problem.addGe(x[branchingIndex], 1);
                    branching();
                    problem.remove(firstBranchConstraint);

                    IloRange secBranchConstraint = problem.addLe(x[branchingIndex], 0);
                    branching();
                    problem.remove(secBranchConstraint);
                    return;
                }
            } else {
                return;
            }
        }
    }

    public boolean isIntegerSolution(double[] funcValues) {
        boolean result = true;
        for (double value :
                funcValues) {
            if (value != 1.0 && value != 0.0) {
                return false;
            }
        }
        return result;
    }

    public Set<Integer> findMaxClique() throws IloException {
        startTime = System.currentTimeMillis();
        branching();
        return maxClique;
    }

    private class ColoringCollection {
        Set colorSet;

        public ColoringCollection(Set colorSet) {
            this.colorSet = colorSet;
        }

        public Set getColorSet() {
            return colorSet;
        }
    }

    void setTimeLimit(String time) {
        timeLimit = Long.valueOf(time);
    }
}

