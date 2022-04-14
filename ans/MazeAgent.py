import random

###student code here####

class ModelAgent():

    def __init__(self):
        self.seen = [[False]*10 for _ in range(10)]
        self.path = []
        
    def reset(self):
        self.seen = [[False]*10 for _ in range(10)]
        self.path = []

    def get_next_move(self, x, y):
        self.seen[x][y] = True
        if self.path!=[]:
            (u,v,d) = self.path.pop()
            if (x,y)==(u,v):#last move unsuccessful
                if  d=='U' and y<9:
                    self.seen[x][y+1] = True
                if d=='D' and y>0:
                    self.seen[x][y-1] = True
                if d=='L' and x>0:
                    self.seen[x-1][y] = True
                if d=='R' and x<9:
                    self.seen[x+1][y] = True
            else:#last move successful add in
                 self.path.append((u,v,d))
        #get next move
        if y<9 and not self.seen[x][y+1]: mv = 'U'
        elif y>0 and not self.seen[x][y-1]: mv= 'D'
        elif x<9 and not self.seen[x+1][y]: mv = 'R'
        elif x>0 and not self.seen[x-1][y]: mv = 'L'
        else:#step back
            if self.path == []: return 'U'
            (u,v,d) = self.path.pop()
            if d == 'U': mv = 'D'
            elif d == 'D': mv = 'U'
            elif d == 'L': mv = 'R'
            elif d == 'R': mv = 'L'
            return mv
        self.path.append((x,y,mv))
        return mv

def string_to_maze(maze_str):
    maze = [[True]*10 for _ in range(10)]
    for i in range(10):
        for j in range(10):
            maze[i][j] = maze_str[::-1][j*11+9-i]=='.'
    return maze

def maze_to_str(maze_array):
    s = ''
    for i in range(10):
        for j in range(9,-1,-1):
            s += '.' if maze_array[i][j] else '#'
        s+='\n'
    return s

def run_maze(agent, maze):
    cx = 9
    cy = 9
    count = 0
    agent.reset()
    while count<200 and (cx !=0 or cy!=0):
        count +=1
        mv = agent.get_next_move(cx,cy)
        if mv=='U' and cy<9 and maze[cx][cy+1]: cy = cy+1
        elif mv=='D' and cy>0 and maze[cx][cy-1]: cy = cy-1
        elif mv=='L' and cx>0 and maze[cx-1][cy]: cx = cx-1
        elif mv=='R' and cx<9 and maze[cx+1][cy]: cx = cx+1
    return count < 200

def maze_gen(density):
    '''density is percentage of cells that are solid'''
    while(True):
        d = density
        maze = [[True]*10 for _ in range(10)]
        while d > 0:
            i = random.randint(0,9)
            j = random.randint(0,9)
            if maze[i][j]:
                maze[i][j]=False
                d-=1
            maze[0][0] = True
            maze[9][9] = True
        if run_maze(ModelAgent(),maze):
            return maze

maze_one='''...####...
#.#...#.#.
..#.#.#.#.
#.#.#.#.#.
..#.#.#.#.
#.#.#.#.#.
..#.#.#.#.
#...#...#.
#########.
..........'''

maze_two='''..........
.########.
.#......#.
.#.####.#.
.#.#..#.#.
.#.#..#.#.
.#.#..#.#.
.#.#....#.
.#.######.
.#........'''

def test_one():
    print('maze test one')
    print(maze_one)
    if run_maze(ModelAgent(),string_to_maze(maze_one)): print('Passed')
    else: print('Failed')

def test_two():
    print('maze test two')
    print(maze_two)
    if run_maze(ModelAgent(),string_to_maze(maze_two)): print('Passed')
    else: print('Failed')

def random_test(density):
    maze = maze_gen(density)
    print('random maze density:',density)
    print(maze_to_str(maze))
    if run_maze(ModelAgent(),maze): print('Passed')
    else: print('Failed')

test_one()
test_two()
for i in range(10,50,10):
    random_test(i)




