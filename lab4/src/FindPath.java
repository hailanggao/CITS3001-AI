import java.util.*;

public class FindPath {

    /**
     * Finds a shortest sequence of words in the dictionary such that the first word is the startWord,
     * the last word is the endWord, and each word is equal to the previous word with one letter changed.
     * All words in the sequence are the same length. If no sequence is possible, an empty array is returned.
     * It is assumed that both startWord and endWord are elements of the dictionary.
     * @param dictionary The set of words that can be used in the sequence; all words in the dictionary are capitalised.
     * @param startWord the first word on the sequence.
     * @param endWord the last word in the sequence.
     * @return an array containing a shortest sequence from startWord to endWord, in order,
     * using only words from the dictionary that differ by a single character.
     * */
    public static String[] findPath(String[] dictionary, String startWord, String endWord){

        List<List<String>> res = new ArrayList<>();
        Set<String> dict = new HashSet<>(Arrays.asList(dictionary));
        Map<String, List<String>> map = new HashMap<>();
        Set<String> startSet = new HashSet<>();
        startSet.add(startWord);
        bfs(startSet, endWord, map, dict);

        List<String> list = new ArrayList<>();
        list.add(startWord);
        dfs(res, list, startWord, endWord, map);
        String[] finalPath = new String[res.get(0).size()];
        finalPath = res.get(0).toArray(finalPath);
        return finalPath;
    }

    private static void dfs(List<List<String>> res, List<String> list, String word, String endWord, Map<String, List<String>> map){
        if(word.equals(endWord)){
            res.add(new ArrayList<>(list));
            return;
        }
        if(map.get(word) == null) return;
        for(String next : map.get(word)){
            list.add(next);
            dfs(res, list, next, endWord, map);
            list.remove(list.size()-1);
        }
    }

    private static void bfs(Set<String> startSet, String endWord, Map<String, List<String>> map, Set<String> dict){
        if(startSet.size() == 0) return;
        Set<String> tmp = new HashSet<>();
        dict.removeAll(startSet);
        boolean found = false;

        for(String s: startSet){
            char[] chars = s.toCharArray();
            for(int i = 0; i<chars.length; i++){
                char old = chars[i];
                for(char c = 'A'; c <= 'Z'; c++){
                    chars[i] = c;
                    String newWord = new String(chars);

                    if(dict.contains(newWord)){
                        if(newWord.equals(endWord)){
                            found = true;
                        }else{
                            tmp.add(newWord);
                        }

                        map.computeIfAbsent(s, k -> new ArrayList<>());
                        map.get(s).add(newWord);
                    }
                }
                chars[i] = old;
            }
        }

        if(!found){
            bfs(tmp,endWord,map,dict);
        }
    }
//    public static String[] findPath(String[] dictionary, String startWord, String endWord){
//        int indexStart = -1;
//        int indexEnd = -1;
//        int minDistance = Integer.MAX_VALUE;
//
//        ArrayList<String> newDictionary = new ArrayList<>(Arrays.asList(dictionary));
//        List<String> path = new ArrayList<>();
//        for(int i=0; i<dictionary.length; i++){
//            if(dictionary[i].equals(startWord)) indexStart=i;
//            if(dictionary[i].equals(endWord)) indexEnd=i;
//            if (indexStart != -1 && indexEnd != -1){
//                if (Math.abs(indexStart - indexEnd) < minDistance){
//                    minDistance = Math.abs(indexStart-indexEnd);
//                    if (indexStart < indexEnd) path = newDictionary.subList(indexStart, indexEnd+1);
//                    else path= newDictionary.subList(indexEnd, indexStart);
//                }
//            }
//        }
//        String[] wordList = new String[path.size()];
//        wordList = path.toArray(wordList);
//
//        int currentIndex = 0;
//        int nextIndex = 1;
//        int wordLen = wordList[0].length();
//        int diff = 0;
//        while(currentIndex < wordList.length-1){
//            while(nextIndex < wordList.length){
//                boolean flag = false; // true if difference of two words > 1
//                int count = 0;
//                while(!flag){
//                    for (int i = 0; i < wordLen; i++) {
//                        if (wordList[currentIndex].charAt(i) != wordList[nextIndex].charAt(i)) {
//                            count++;
//                        }
//                        if (count > 1){
//                            flag = true;
//                            wordList[nextIndex] = "";
//                            diff ++;
//                            break;
//                        }
//                    }
//                    if(!flag){
//                        currentIndex = nextIndex;
//                        break;
//                    }
//                }
//                nextIndex++;
//            }
//        }
//
//        String[] finalPath = new String[wordList.length - diff];
//        int newIndex=0;
//        for (String word : wordList) {
//            if (!word.equals("")) {
//                finalPath[newIndex] = word;
//                newIndex++;
//            }
//        }
//        return finalPath;
//        }

    public static void main(String[] args) {
        String[] dictionary = new String[]{"AIM", "ARM", "ART", "RIM", "RAM", "RAT", "ROT", "RUM", "RUN", "BOT", "JAM", "JOB", "JAB", "LAB", "LOB", "LOG", "SUN"};
        String startWord = "JAM";
        String endWord = "LAB";
        String[] ans = findPath(dictionary,startWord,endWord);
        for (String an : ans) System.out.print(an + ", ");
        System.out.println(ans.length);
    }
}
