import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.io.*;

public class GreedyChange{

    public static Map<Integer,Integer> greedyChange(int amount, int[] denominations){
        Arrays.sort(denominations);
        Map<Integer, Integer> Res = new HashMap<>();
        Integer tmp;
        // System.out.println(loopInvariant(tmp));
        for(int i = denominations.length - 1; i >= 0; i--){
            tmp = amount / denominations[i];
            Res.put(denominations[i], tmp);
            amount = amount % denominations[i];
            // System.out.println(loopInvariant(tmp));
        }
        return Res;
    }

    public static boolean loopInvariant(Integer num){
        return num >= 0;
    }


    public static Map<Integer, Integer> exactChange(int amount, int[] denominations){
        Arrays.sort(denominations);
        int[] bestSub = new int[amount+1];
        Arrays.fill(bestSub, amount+1);
        bestSub[0] = 0;
        HashMap<Integer, Integer> innerExactCoins = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Integer>> outerBestAmount = new HashMap<>();

        for (int denomination : denominations) {
            innerExactCoins.put(denomination, 0);
        }
        outerBestAmount.put(0, innerExactCoins);

        for(int i=1; i<amount+1; i++){              //i for each amount
            for (int currentCoin : denominations) {    //j in denominations
                if (i - currentCoin >= 0) {
                    if (bestSub[i] > 1 + bestSub[i - currentCoin]) {
                        bestSub[i] = 1 + bestSub[i - currentCoin];
                        innerExactCoins = myClone(outerBestAmount.get(i - currentCoin));
                        innerExactCoins.replace(currentCoin, 1 + innerExactCoins.get(currentCoin));
                    }
                }
            }
            HashMap<Integer, Integer> tmp;
            tmp=myClone(innerExactCoins);
            outerBestAmount.put(i, tmp);
        }
        return outerBestAmount.get(amount);
    }

    public static <T extends Serializable> T myClone(T obj) {
        T clonedObj = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            clonedObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clonedObj;
    }

    public static void main(String[] args) {
        int[] denominations = {2,3,4,5};
        // Map<Integer,Integer> change = greedyChange(5, denominations);
        Map<Integer,Integer> change = exactChange(7, denominations);
        Integer[] keys = change.keySet().toArray(new Integer[0]);
        Arrays.sort(keys);
        for(Integer i: keys)
            System.out.println(i+":"+change.get(i));
    }
}