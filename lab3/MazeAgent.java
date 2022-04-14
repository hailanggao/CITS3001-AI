import java.util.Stack;

class MazeAgent {
    int[][] isVisited;
    Stack<Position> stack;

    class Position{
        int x, y;

        public Position(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Constructs a simple instance of the agent.
     * Must have a 0 parameter constructor to work with the marking program.
     **/
    public MazeAgent() {

        isVisited = new int[10][10];
        stack = new Stack<>();
    }
    /**
         * Resets the agent's knowledge state.
         **/
    public void reset(){
        isVisited = new int[10][10];
        stack = new Stack<>();
    }

    /**
     * Returns the agents next move given their environment.
     * @return the character U, D, L, R, to move, respectively Up Down Left or Right.
     * @param x the agent's current x coordinate
     * @param y the agent's current y coordinate
     **/
    public char getNextMove ( int x, int y){
        Position currentPosition = new Position(x, y);
        stack.push(currentPosition);

        if(isVisited[-y+9][x] == 1){
            stack.pop();
            Position wall = stack.pop();
            isVisited[-wall.y+9][wall.x] = 1;
        }
        isVisited[-y+9][x] = 1;

        if( y+1 <= 9 && isVisited[-y+9-1][x] == 0){
            stack.push(new Position(x, y+1));
            return 'U';
        }
        if(x-1 >= 0 && isVisited[-y+9][x-1] == 0){
            stack.push(new Position(x-1, y));
            return 'L';
        }
        if(y-1 >= 0 && isVisited[-y+9+1][x] == 0){
            stack.push(new Position(x, y-1));
            return 'D';
        }
        if(x+1 <= 9 && isVisited[-y+9][x+1] == 0){
            stack.push(new Position(x+1, y));
            return 'R';
        }

        //Otherwise, we arrived at dead-end
        Position deadEnd = stack.pop();
        Position trackBack = stack.pop();
        char move = 'F';
        isVisited[-trackBack.y+9][trackBack.x] = 0;
        if(deadEnd.x-1 == trackBack.x && deadEnd.y == trackBack.y){
            move = 'L';
        }
        if(deadEnd.y+1 == trackBack.y && deadEnd.x == trackBack.x){
            move = 'U';
        }
        if(deadEnd.x+1 == trackBack.x && deadEnd.y == trackBack.y){
            move = 'R';
        }
        if(deadEnd.y-1 == trackBack.y && deadEnd.x == trackBack.x){
            move = 'D';
        }
        return move;
    }



    public static void main(String args[])
    {
        int agent_x = 3;
        int agent_y = 3;

        MazeAgent debug = new MazeAgent();

        int[][] maze =
                {
                        {0,0,0,1,1,1,1,0,0,0},
                        {1,0,1,0,0,0,1,0,1,0},
                        {0,0,1,0,1,0,1,0,1,0},
                        {1,0,1,0,1,0,1,0,1,0},
                        {0,0,1,0,1,0,1,0,1,0},
                        {1,0,1,0,1,0,1,0,1,0},
                        {0,0,1,0,1,0,1,0,1,0},
                        {1,0,0,0,1,0,0,0,1,0},
                        {1,1,1,1,1,1,1,1,1,0},
                        {0,0,0,0,0,0,0,0,0,0}
                };


        int step = 0;
        while( step != 300 )
        {
            if(agent_x == 0 && agent_y == 0)
            {
                System.out.println("Got the end");
                break;
            }

            char direction = debug.getNextMove(agent_x, agent_y);

            if(direction == 'U' && agent_y+1 <= 9 && maze[-agent_y+9-1][agent_x] == 0)
            {
                agent_y += 1;
            }
            if(direction == 'R' && agent_x+1 <= 9 && maze[-agent_y+9][agent_x+1] == 0)
            {
                agent_x += 1;
            }
            if(direction == 'D' && agent_y-1 >= 0 && maze[-agent_y+9+1][agent_x] == 0)
            {
                agent_y -= 1;
            }
            if(direction == 'L' && agent_x-1 >= 0 && maze[-agent_y+9][agent_x-1] == 0)
            {
                agent_x -= 1;
            }
            step++;
        }
        System.out.printf("X%d Y%d", agent_x, agent_y);

    }
}
