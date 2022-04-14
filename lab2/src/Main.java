import java.util.*;
public class Main {


    /**
     * Returns the shortest tour found by exercising the NN algorithm
     * from each possible starting city in table.
     * table[i][j] == table[j][i] gives the cost of travel between City i and City j.
     */
    public static int[] tspnn(double[][] table) {
        int[] shortestPath = new int[table.length+1];
        int[] currentPath = new int[table.length+1];
        double bestDistance = Double.POSITIVE_INFINITY;
        boolean[] isVisited = new boolean[table.length];

        for(int startCity=0; startCity<table.length; startCity++){
            double currentDistance = 0.0; //total distance we have visited
            int i = startCity;
            int count=0; //for the index of currentPath
//            System.out.println("Start city: " + startCity);
            currentPath = resetCurrentPath(currentPath);
            isVisited = resetIsVisited(isVisited);
            while(i < table.length && count <= table.length){
                isVisited[i] = true; // means that we have visited current city
                currentPath[count] = i; //put it into the current path
                double currentMin = Double.POSITIVE_INFINITY; //start to get the nearest city distance
                int nearestCity = -1;
                //after this loop, we should find the nearest city and distance
                for(int j=0; j<table[0].length; j++){
                    if(!isVisited[j] && table[i][j] != Double.POSITIVE_INFINITY && table[i][j] != 0){
                        if(currentMin > table[i][j]){
                            currentMin = table[i][j];
                            nearestCity = j;
                        }
                    }
                }

                if (nearestCity == -1){
                    if(isAllTrue(isVisited) && table[startCity][i] != 0 && table[startCity][i] != Double.POSITIVE_INFINITY){
                        count ++; //found the nearest city and update the path index so that can put it into the path
                        currentPath[count] = startCity;
                        currentDistance += table[startCity][i];
//                        System.out.println("Reached the end city and get back to the start city: " + currentPath[5]);
                        break;
                    }
                }
                else{
                    currentDistance += currentMin;
//                    System.out.print("Found nearest city: " + nearestCity);
//                    System.out.println(" and the distance is: " + currentMin);
                    i = nearestCity;
                    count++;
                }
            }

            if (currentDistance < bestDistance){
                bestDistance = currentDistance;
                System.arraycopy(currentPath,0, shortestPath, 0, currentPath.length);
                //shortestPath.equals(currentPath);
//                for(int city: currentPath){
//                    System.out.println(city);
//                }
            }
//            System.out.println("Current total distance: " + currentDistance);
//            System.out.println("Shorest distance: " + bestDistance);
        }

         return shortestPath;
    }

    public static int[] resetCurrentPath(int[] currentPath){
        for(int m=0; m<currentPath.length; m++){
            currentPath[m] = -1;
        }
        return currentPath;
    }

    public static boolean[] resetIsVisited(boolean[] isVisited){
        for(int n=0; n<isVisited.length; n++){
            isVisited[n] = false;
        }
        return isVisited;
    }

    public static boolean isAllTrue(boolean[] isVisited){
        for(int i=0; i<isVisited.length; i++){
            if(!isVisited[i]){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        double[][] distances;
        double INF = Double.POSITIVE_INFINITY;
//        distances = new double[][]{{0,4,2,INF,INF},
//                                    {4,0,3,3,5},
//                                    {2,3,0,INF,2},
//                                    {INF,3,INF,0,2},
//                                    {INF,5,2,2,0}};
        distances = new double[][]{{0, 2, INF, 3, 1},
                {2, 0, 4, INF, 4},
                {INF, 4, 0, 3, 5},
                {3, INF, 3, 0, 2},
                {1, 4, 5, 2, 0}};
        int[] res = tspnn(distances);
        for(int value: res){
            System.out.println(value);
        }
    }
}

