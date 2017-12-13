package max_clique;

import ilog.concert.IloException;
import org.jgrapht.UndirectedGraph;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;


import java.io.IOException;
import java.util.Set;

import static java.lang.Integer.parseInt;


public class Main {
    public static void main(String[] args) throws IOException, IloException {
        //String fileName = "C:\\Users\\User\\IdeaProjects\\MaxCliqueBnB\\clq\\c-fat200-1.clq.txt";
        int timeLimit = args.length > 1 ? parseInt(args[1]) : 3600;
        Time mThing = new Time();
        Time.setTimeLimit(timeLimit);
        Thread myThready = new Thread(mThing);	//Создание потока "myThready"
        myThready.start();
        String fileName = args[0];
        long startTime = System.currentTimeMillis();
        SimpleGraph<Integer, DefaultEdge> graph = Parser.readData(fileName);
        Set clique = new CliqueFinder(graph).findMaxClique();
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Max clique size: " + clique.size());
        System.out.println("Clique nodes: " + clique.toString());
        System.out.println("Time : " + estimatedTime);
        myThready.interrupt();
    }
}
