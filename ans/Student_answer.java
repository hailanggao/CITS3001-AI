/**
     * Returns the shortest tour found by exercising the NN algorithm 
     * from each possible starting city in table.
     * table[i][j] == table[j][i] gives the cost of travel between City i and City j.
     */
    public static int[] tspnn(double[][] table)
    {
        // the number of cities
        int n = table.length;
        // used to store the best tour found so far and its length
        int[] csbest = null;
        double min = Double.POSITIVE_INFINITY;
        // used to store the tour built in each iteration
        int[] cs = new int[n];
        // starting at each city in turn
        for (int c = 0; c < n; c++)
        {
            // set up the n cities starting from c
            for (int k = 0; k < n ; k++)
                cs[k] = (c + k) % n;
            // for the other n-1 slots in the tour
            for (int k = 1; k < n-1; k++)
            {
                // find the nearest unused city to cs[k-1] and put it in cs[k]
                int next = k;
                for (int p = k + 1; p < n; p++)
                    if (table[cs[k-1]][cs[p]] < table[cs[k-1]][cs[next]])
                       next = p;
                swap(cs, next, k);
            }
            // if this tour is better, save it
            double z = roundtrip(cs, table);
            if (z < min)
            {
                min = z;
                csbest = Arrays.copyOf(cs, n);
            }
        }
        return csbest;
    }
    
    /**
     * Reverses cs[a..b] inclusive in-place.
     */
    private static void flip(int[] cs, int a, int b)
    {
        for (int k = 0; k < (b - a + 1) / 2; k++)
            swap(cs, a + k, b - k);
    }
    
    /**
     * Swaps cs[a] and cs[b] in-place.
     */
    private static void swap(int[] cs, int a, int b)
    {
        int x = cs[a];
        cs[a] = cs[b];
        cs[b] = x;
    }
    
    /**
     * Returns the total cost for the circular tour cs using table.
     */
    private static double roundtrip(int[] cs, double[][] table)
    {
        int n = cs.length;
        double z = table[cs[0]][cs[n-1]];
        for (int k = 0; k < n-1; k++) 
            z += table[cs[k]][cs[k+1]];
        return z;
    }
