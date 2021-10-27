package made_hw

import breeze.linalg.{DenseMatrix, DenseVector, inv}

class LinearRegress {

  var w: DenseVector[Double] = DenseVector()

  def fit(X: DenseMatrix[Double], y: DenseVector[Double]): LinearRegress = {
    val n = X.rows
    val train = DenseMatrix.horzcat(X, DenseMatrix.ones[Double](n,1))
    w = inv(train.t * train) * train.t * y
    this
  }

  def predict(X: DenseMatrix[Double]): DenseVector[Double] = {
    val n = X.rows
    val train = DenseMatrix.horzcat(X, DenseMatrix.ones[Double](n,1))
    train * w
  }

}