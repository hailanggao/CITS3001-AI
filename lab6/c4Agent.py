import math
import random
import copy
class C4Agent:

    def __init__(self) -> None:
        global row_height
        global col_width
        global my_board
        global symbols
        global my_symbol
        global op_symbol

        row_height=6
        col_width=7
        my_board=[[''] * row_height for i in range(col_width)]
        symbols = ['X', 'O']
        my_symbol = ''
        op_symbol= ''

    def move(self, symbol, board, last_move):
        global my_board
        global symbols
        global my_symbol
        global op_symbol

        if symbol == 'X':
            my_symbol = 'X'
            op_symbol = 'O'
        else:
            my_symbol = 'O'
            op_symbol = 'X'

        print('last_move', last_move)
        # print('修改前my_board: ', my_board)
        if last_move != -1:
            r = self.get_next_open_row(my_board, last_move)
            # print('r return:', r) #0
            # print(my_board[last_move])
            my_board[last_move][r]= op_symbol #myboard[4][0] = 'O'
            print('修改后my_board: ', my_board)

        col, minimax_score = self.minimax(my_board, 3, -math.inf, math.inf, True)
        r = self.get_next_open_row(my_board, col)
        my_board[col][r] = symbol
        print('My move:', col)
        return col


        # print('symbol: ', symbol)
        # print(board)
        # print('last_move', last_move)
        # return 0
    
    def winning_move(self, board, symbol):
        # check horizontal locations for win
        # print("winning move board:", board)
        # print('symbol: ', symbol)
        for r in range(row_height):
            # print('r:', r)
            for c in range(col_width-3):
                # print('c', c)
                # print('board[c][r]: ', board[c][r])
                if board[c][r] == symbol and board[c+1][r] == symbol and board[c+2][r] == symbol and board[c+3][r] == symbol:
                    # print('horizontal win True')
                    return True

        # check vertical locations for win
        for r in range(row_height-3):
            for c in range(col_width):
                if board[c][r] == symbol and board[c][r+1] == symbol and board[c][r+2] == symbol and board[c][r+3] == symbol:
                    # print('vertical win True')
                    return True
        
        # check positively slopped diaganols
        for r in range(row_height -3):
            for c in range(col_width-3):
                if board[c][r] == symbol and board[c+1][r+1] == symbol and board[c+2][r+2] == symbol and board[c+3][r+3] == symbol:
                    # print('positive slop win True')
                    return True
        
        # check negatively slopped diaganols
        for r in range(3, row_height):
            for c in range(col_width-3):
                if board[c][r] == symbol and board[c+1][r-1] == symbol and board[c+2][r-2] == symbol and board[c+3][r-3] == symbol:
                    # print('negative slop win True')
                    return True
        return False

    def evaluate_window(self, window, symbol):
        score = 0
        if window.count(symbol) == 4:
            score += 100
        elif window.count(symbol) == 3 and window.count('') == 1:
            score += 10
        elif window.count(symbol) == 2 and window.count('') == 2:
            score == 2
        if window.count(op_symbol) == 3 and window.count('') == 1:
            score -= 6
        return score

    def score_position(self, board, symbol):
        score = 0
        # # score center column
        center_array = board[col_width//2]
        certer_count = center_array.count(symbol)
        score += certer_count * 3

        # score horizontal column
        for r in range(row_height):
            row_array = [c[r] for c in board]
            window = row_array[:4]
            right = 3
            score += self.evaluate_window(window, symbol)
            while right < col_width-1:
                right += 1
                window.append(row_array[right])
                window.pop(0)
                score += self.evaluate_window(window, symbol)

        # score vertical column
        for col_array in board:
            window = col_array[:4]
            up = 3
            score += self.evaluate_window(window, symbol)
            while up < row_height-1:
                up += 1
                window.append(col_array[up])
                window.pop(0)
                score += self.evaluate_window(window, symbol)
        
        # score positive slopped diagnol
        for r in range(row_height-3):
            for c in range(col_width-3):
                window = [board[c+i][r+i] for i in range(4)]
                score += self.evaluate_window(window, symbol)
        
        # score negative slopped diagnol
        for c in range(3, col_width):
            for r in range(row_height-3):
                window = [board[c-3+i][r+i] for i in range(4)]
                score += self.evaluate_window(window, symbol)

        return score

    def minimax(self, board, depth, alpha, beta, maximizingplayer):
        valid_locations = self.get_valid_locations(board)
        is_terminal = self.is_terminal_node(board)
        # print('is_terminal:', is_terminal)
        if depth == 0 or is_terminal:
            if is_terminal:
                if self.winning_move(board, op_symbol):
                    return (None, 1000000000)
                elif self.winning_move(board, my_symbol):
                    return (None, 1000000000)
                else:
                    return (None, 0)
            else:
                return(None, self.score_position(board, my_symbol))

        if maximizingplayer:
            # print('我是max')
            value = -math.inf
            column = random.choice(valid_locations)
            for col in valid_locations:
                row = self.get_next_open_row(board, col)
                b_copy = copy.deepcopy(board)
                b_copy[col][row] = my_symbol
                new_score = self.minimax(b_copy, depth-1, alpha, beta, False)[1]
                if new_score > value:
                    value = new_score
                    column = col
                alpha = max(alpha, value)
                if alpha >= beta:
                    break
            return column, value

        else:
            # print('我是min')
            value = math.inf
            column = random.choice(valid_locations)
            for col in valid_locations:
                row = self.get_next_open_row(board, col)
                b_copy = copy.deepcopy(board)
                b_copy[col][row] = op_symbol
                new_score = self.minimax(b_copy, depth-1, alpha, beta, True)[1]
                if new_score < value:
                    value = new_score
                    column = col
                beta = min(beta, value)
                if alpha >= beta:
                    break
            return column, value

    
    def get_next_open_row(self, board, col):
        for r in range(row_height):
            if board[col][r] == '':
                return r
    
    def is_terminal_node(self, board):
        return self.winning_move(board, my_symbol) or self.winning_move(board, op_symbol) or len(self.get_valid_locations(board)) == 0

    def is_valid_location(self, board, col):
        return board[col][-1] == ''

    def get_valid_locations(self, board):
        valid_locatiuons=[]
        for col in range(col_width):
            if self.is_valid_location(board, col):
                valid_locatiuons.append(col)
        return valid_locatiuons
