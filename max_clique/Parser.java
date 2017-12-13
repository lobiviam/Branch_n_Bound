package max_clique;

import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Parser {
    public String fileName;

    public static SimpleGraph readData(String fileName) throws IOException {
        String str = "";
        Scanner sc = new Scanner(new File(fileName));
        while (sc.hasNext() && !str.equals("p")) str = sc.next();
        sc.next();
        int n = sc.nextInt();
        SimpleGraph<Integer, DefaultEdge> graph = new SimpleGraph(DefaultEdge.class);
        //graph.setVertexNumber(n);
        int m = sc.nextInt();
        //graph.setEdgesNumber(m);
        while (sc.hasNext()) {
            sc.next();
            int i = sc.nextInt();
            int j = sc.nextInt();
            graph.addVertex(i);
            graph.addVertex(j);
            //Vertex x1 = new Vertex();
            //Vertex x2 = new Vertex();
            //x1.setX(i);
            //x2.setX(j);
            graph.addEdge(i, j);
        }
        sc.close();
        //System.out.print(graph.getVertexMap());
        return graph;
    }
}
