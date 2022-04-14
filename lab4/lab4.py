def find_path(dict, start, end):
    dict = set(dict)
    dict.add(start)
    dict.add(end)
    
    result, cur, visited, found, trace = [], [start], set([start]), False, {word: [] for word in dict}  
    # print(trace)
    while cur and not found:
        for word in cur:
            visited.add(word)
            
        next = set()
        for word in cur:
            for i in range(len(word)):
                for j in 'ABCDEFGHIJKLMNOPQRSTUVWXYZ':
                    candidate = word[:i] + j + word[i+1 :]
                    if candidate not in visited and candidate in dict:
                        if candidate == end:
                            found = True
                        next.add(candidate)
                        trace[candidate].append(word)
        # print(next)
        cur = next
    # print(trace)
    if found:
        backtrack(result, trace, [], end)
    
    return result[0]

def backtrack(result, trace, path, word):
    if not trace[word]:
        result.append([word] + path)
    else:
        for prev in trace[word]:
            backtrack(result, trace, [word] + path, prev)

dictionary = ["AIM", "ARM", "ART", "RIM", "RAM", "RAT", "ROT", "RUM", "RUN", "BOT", "JAM", "JOB", "JAB", "LAB", "LOB", "LOG", "SUN"]
start_word = "JAM"
end_word = "LAB"
print(find_path(dictionary, start_word, end_word))