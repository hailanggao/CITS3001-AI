import java.util.*;


class MazeAgent{
    
    class Coord
    {
        int X;
        int Y;
        Coord(int x, int y)
        {
            X = x;
            Y = y;
        }
    }
    int[][] visited;
    Stack<Coord> stack;

    public static void main(String args[])
    {
        int agent_x = 3;
        int agent_y = 3;

        MazeAgent debug = new MazeAgent();
        
        int[][] maze = 
        {
            {1, 0, 0, 0},
            {0, 0, 0, 1},
            {1, 0, 1, 1},
            {0, 0, 0, 0}
        };
        
        
        int step = 0;
        while( step != 30 )
        {
			if(agent_x == 0 && agent_y == 0)
			{
				break;
			}

			char direction = debug.getNextMove(agent_x, agent_y);
			
			if(direction == 'U' && agent_y+1 <= 3 && maze[-agent_y+3-1][agent_x] == 0)
			{
				agent_y += 1;
			} 
			if(direction == 'R' && agent_x+1 <= 3 && maze[-agent_y+3][agent_x+1] == 0)
			{
				agent_x += 1;
			} 
			if(direction == 'D' && agent_y-1 >= 0 && maze[-agent_y+3+1][agent_x] == 0)
			{
				agent_y -= 1;
			} 
			if(direction == 'L' && agent_x-1 >= 0 && maze[-agent_y+3][agent_x-1] == 0)
			{
				agent_x -= 1;
			} 


            step++;
        }
		System.out.printf("X%d Y%d", agent_x, agent_y);

    }

	public MazeAgent()
	{
        visited = new int[4][4];
        stack = new Stack<Coord>();
    }

	public char getNextMove(int x, int y)
	{
		//1 visited/wall
		//0 empty
	    //fill in code
	    //U R D L, clockwise
	    //[-y+3][x]
		System.out.printf("getNextMove:\n");
		System.out.printf("X:%d Y:%d \n", x, y);

		
		
	    
		stack.push(new Coord(x, y));
	    
	    if(visited[-y+3][x] == 1) //last move hitted a wall
	    {
			stack.pop();
	        Coord wall = stack.pop();
			System.out.printf("Wall X:%d Y%d \n", wall.X, wall.Y);
	        visited[-wall.Y+3][wall.X] = 1;
	    }
	    
	    visited[-y+3][x] = 1;
	    if(y+1 <= 3 && visited[-y+3-1][x] == 0) //U
	    {
			stack.push(new Coord(x, y+1));
	        return 'U';
	    }
	    if(x+1 <= 3 && visited[-y+3][x+1] == 0) //R
	    {
			stack.push(new Coord(x+1, y));
	        return 'R';
	    }
	    if(y-1 >= 0 && visited[-y+3+1][x] == 0) //D
	    {
			stack.push(new Coord(x, y-1));
	        return 'D';
	    }
	    if(x-1 >= 0 && visited[-y+3][x-1] == 0) //L
	    {
			stack.push(new Coord(x-1, y));
	        return 'L';
	    }
	    
	    //Reached dead end
	    Coord dead_end = stack.pop();
	    Coord backtrack = stack.pop(); //backtrack cell is popped off the stack to avoid beinng re-added in next call
	    visited[-backtrack.Y+3][backtrack.X] = 0; //make backtrack cell unvisited to avoid it being misakened as wall
	    char direction = 'J';
	    if(dead_end.Y+1 == backtrack.Y && dead_end.X == backtrack.X)
	    {
	        direction = 'U';
	    }
	    if(dead_end.X+1 == backtrack.X && dead_end.Y == backtrack.Y)
	    {
	        direction = 'R';
	    }
	    if(dead_end.Y-1 == backtrack.Y && dead_end.X == backtrack.X)
	    {
	        direction = 'D';
	    }
	    if(dead_end.X-1 == backtrack.X && dead_end.Y == backtrack.Y)
	    {
	        direction = 'L';
	    }
	    
	    return direction;
	    
	}
	
}
