import sys

CONST = 0

def val_mean(
    prev_size,
    prev_mean,
    cur_size,
    cur_mean
):
    """
    Calculation of the average.
    """
    mean_val = (prev_size * prev_mean + cur_size * cur_mean) / (prev_size + cur_size)
    return mean_val

def val_var(
    prev_size,
    prev_var,
    prev_mean,
    cur_size,
    cur_mean,
    var_cur
):
    """
    Calculation of the covariance.
    """
    res_val_cov = (prev_size * prev_var + cur_size * var_cur) / (prev_size + cur_size) + prev_size * cur_size * (((prev_mean - cur_mean) / (prev_size + cur_size)) ** 2)
    return res_val_cov

def main():
    """
    Run.
    """
    cur_mean = None
    cur_size, var_cur, prev_size = CONST, CONST, CONST

    for line in sys.stdin:
        k, v = line.strip().split("\t")
        k = float(k)
        v = float(v)

        if k == cur_mean:
            cur_size += v
        else:
            if cur_mean is not None:
                prev_size += cur_size
                prev_var = var_cur
                prev_mean = cur_mean

                cur_mean = k
                cur_size = v

                cur_mean = val_mean(prev_size, prev_mean, cur_size, cur_mean)
                var_cur = val_var(prev_size, prev_var, prev_mean, cur_size, k, CONST)
                continue

            cur_mean = k
            cur_size = v
    
    print(f'Mean: {cur_mean}, Variance: {var_cur}')


if __name__ == "__main__":
    main()

