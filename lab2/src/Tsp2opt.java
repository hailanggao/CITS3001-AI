
public class Tsp2opt {
    /**
     * Uses 2-OPT repeatedly to improve cs, choosing the shortest option in each iteration.
     * You can assume that cs is a valid tour initially.
     * table[i][j] == table[j][i] gives the cost of travel between City i and City j.
     */
    public static int[] tsp2opt(int[] cs, double[][] table)
    {
        double bestDistance = totalDistance(cs, table);
        int[] bestTour = cs;
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 0; i < cs.length - 1; i++) {
                for (int j = i + 1; j < cs.length; j++) {
                    int[] newTour = twoOptSwap(bestTour, i, j);
                    double newDistance = totalDistance(newTour, table);
                    if (newDistance < bestDistance) {
                        bestTour = newTour;
                        bestDistance = newDistance;
                        improved = true;
                    }
                }
            }
    }
        return bestTour;
    }

    public static double totalDistance(int[] cs, double[][] table){
        double res = 0;
        for(int i=0; i<cs.length-1; i++){
            res += table[cs[i]][cs[i+1]];
        }
        res += table[cs[cs.length-1]][cs[0]];
        return res;
    }

    public static int[] twoOptSwap(int[] cs, int i, int k){
        int[] res = new int[cs.length];
        for(int n=0; n<i; n++){
            res[n] = cs[n];
        }

        int dec=0;
        for(int n=i; n<=k; n++){
            res[n] = cs[k-dec];
            dec++;
        }

        for(int n=k+1; n <cs.length; n++){
            res[n] = cs[n];
        }
        return res;
    }

    //    public static int[] deepCopy(int[] cs){
//            int[] res = new int[cs.length];
//            for(int i=0; i<cs.length; i++) res[i] = cs[i];
//            return res;
//    }


    public static void main(String[] args) {
        int[] cs = new int[] {1,2,3,4,5,6,7,8};
        int[] res = twoOptSwap(cs, 3, 6);
//        double[][] table = new double[][]{{}};
//        int[] res = tsp2opt(cs, table);
        for (int re : res) {
            System.out.println(re);
        }
    }
}
