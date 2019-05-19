package org.insa.algo.shortestpath;

import org.insa.algo.ArcInspectorFactory;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Point;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Random;

public class PerformanceShortestPathTest {

    private static int nombreTest;
    private static Graph carte;

    private static Random rand;

    private static int borneInf;
    private static int borneSup;

    private static String separateur;

    private static Node origine;
    private static Node destination;


    private static PrintWriter writer;

    @BeforeClass
    public static void initAll() throws Exception {

        nombreTest=10;
        borneInf=0;
        borneSup=5;

        separateur = ";";
        // Init la carte
        String mapName="/home/kompe/Bureau/Doc_BE/Maps/mauritius-island.mapgr";

        GraphReader reader=new BinaryGraphReader(new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
        carte=reader.read();

        rand = new Random();

        // On random
        writer = new PrintWriter("/home/kompe/Bureau/Projet/BE_Graphe/out/" + carte.getMapName() + borneInf + "-" + borneSup + ".csv", "UTF-8");
        writer.println( "algo" + separateur + "originId" + separateur + "destId" + separateur + "distance_vol(m)" 
                              + separateur + "distance_Algo(m)" + separateur + "nombre_noeud" 
                              + separateur + "temps(ms)" + separateur + "nombreSommetVisite" );
    }

    @Test
    public void generateTest(){

        int i=0;

        while ( i < nombreTest ){
            System.out.println( "Chemin trouvé : " + (i+1) );
            System.out.println( "Avancement :" + (((double)i )/(((double) nombreTest)*100.0))+"%");
            origine = carte.get(rand.nextInt(carte.size()-1));
            destination = carte.get(rand.nextInt(carte.size()-1));
            if (calculDijkstra(origine,destination)){
                calculAStar(origine,destination);
                i+=1;
            }
        }
        writer.close();
    }

    public boolean calculDijkstra(Node origine, Node dest){

        boolean retour = false;
        StringBuilder ret = new StringBuilder();
        double distance_vol = Point.distance(origine.getPoint(),dest.getPoint());

        //pour mesure le temps d'execution
        long debut = System.nanoTime();
        long end = System.nanoTime();

        ret.append("dijkstra" + separateur + origine.getId()+ separateur + dest.getId() + separateur + distance_vol + separateur);

        ShortestPathData data = new ShortestPathData(carte,origine,dest,ArcInspectorFactory.getAllFilters().get(0));
        ShortestPathAlgorithm dijkstraAlgo = new DijkstraAlgorithm(data);

        debut = System.nanoTime();
        ShortestPathSolution solution = dijkstraAlgo.doRun();
        end = System.nanoTime();

        double time=((double)(end - debut)/1000000.0);

        if ((solution.isFeasible()) && (solution.getPath().getLength() < (float) borneSup*1000.0) && (solution.getPath().getLength() > (float) borneInf*1000.0)){
            
            System.out.println(time);
            int size = solution.getPath().size()+1;

            ret.append( solution.getPath().getLength() + separateur + size + separateur + time + separateur + solution.getNombreSommetVisite());
            writer.println(ret.toString().replaceAll("\\.",","));
            System.out.println(ret.toString().replaceAll("\\.",","));

            retour=true;
        }
        return retour;
    }

    public boolean calculAStar(Node origine,Node dest){
        Boolean retour=false;
        StringBuilder ret=new StringBuilder();
        double distance_vol= Point.distance(origine.getPoint(),dest.getPoint());
        ret.append("astar"+separateur+origine.getId()+separateur+dest.getId()+separateur+distance_vol+separateur);
        ShortestPathData data=new ShortestPathData(carte,origine,dest,ArcInspectorFactory.getAllFilters().get(0));
        ShortestPathAlgorithm aStarAlgo=new AStarAlgorithm(data);
        long debut=System.nanoTime();
        ShortestPathSolution solution=aStarAlgo.doRun();
        long end=System.nanoTime();
        double time=((double)(end-debut)/1000000.0);
        if ((solution.isFeasible())&&(solution.getPath().getLength()<(float) borneSup*1000.0) && (solution.getPath().getLength()> (float) borneInf*1000.0)){
            ret.append(solution.getPath().getLength()+separateur+solution.getPath().getArcs().size()+1+separateur+time+separateur+solution.getNombreSommetVisite());
            writer.println(ret.toString().replaceAll("\\.",","));
            System.out.println(ret.toString().replaceAll("\\.",","));
            retour=true;
        }
        return retour;
    }
}