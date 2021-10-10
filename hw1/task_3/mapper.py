import sys
import csv


CONST = 0

def read_csv_file(
    file
):
    """
    Read file csv.
    """
    for rows in csv.reader(file):
        yield rows


def main():
    """
    Run.
    """
    price_column = CONST
    for iterr, lines in enumerate(read_csv_file(sys.stdin)):
        if iterr == CONST:
            for k, items in enumerate(lines):
                if items == 'price':
                    price_column = k
        else:
            if len(lines) > price_column:
                val_col = str(lines[price_column])
                print(val_col, 1, sep="\t", end="\n")


if __name__ == "__main__":
    main()
