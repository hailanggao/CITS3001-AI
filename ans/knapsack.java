public static Map<Integer, Integer> exactChange(int amount, int[] den){
           Arrays.sort(den);
           int originalAmount = amount;
	   int[][] soln = new int[den.length][amount+1];
	   for(int a = 0; a<=amount; a++){
              soln[0][a] = a/den[0];
	   }
	   for(int d = 1; d<den.length; d++){
              for(int a = 0; a<=amount; a++){
                 if(den[d]<=a && (soln[d][a-den[d]]+1) < soln[d-1][a])
                    soln[d][a] = soln[d][a-den[d]]+1;
                 else soln[d][a] = soln[d-1][a];
               }
           }
           Map<Integer, Integer> result = new HashMap<Integer,Integer>();
	   for(int d =0; d<den.length;d++)result.put(den[d],0);
	   int d = den.length-1;
	   int a = amount;
	   while(a>0 && d>0){
		   if (soln[d][a]!=soln[d-1][a]){
			   result.put(den[d],result.get(den[d])+1);
			   a = a-den[d];
		   }
		   else d = d-1;
	   }
	   if(d==0) result.put(den[d],a/den[d]);
           return result;  
		   
	}

