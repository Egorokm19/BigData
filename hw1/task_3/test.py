import pandas as pd
import numpy as np


dataset = pd.read_csv('AB_NYC_2019.csv')
mean_val = np.mean(dataset.price.values)
var_val = np.var(dataset.price.values)

print(f'Mean: {mean_val}, Variance: {var_val}')
