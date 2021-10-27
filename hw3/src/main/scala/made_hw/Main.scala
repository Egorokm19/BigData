package made_hw

import breeze.io.CSVReader
import breeze.linalg._
import breeze.stats.mean

import java.io.{File, FileReader}


object Main {
  def main(args: Array[String]): Unit = {

    def getMSE(y: DenseVector[Double], pred: DenseVector[Double]): Double = {
      val mse = y - pred
      mean(mse * mse)
    }

    var data = CSVReader.read(new FileReader(new File("data/train.csv")), ',', skipLines = 1)
    data = data.takeWhile(line => line.nonEmpty && line.head.nonEmpty)
    val dataset = DenseMatrix.tabulate(data.length, data.head.length)((i, j) => data(i)(j).toDouble)

    val train = dataset(::, 0 to 21)
    val labels = dataset(::, -1)
    val lrReg = new LinearRegress
    lrReg.fit(train, labels)
    val pred = lrReg.predict(train)
    println(s"mse on test = ${getMSE(labels, pred)}")
    csvwrite(new File("data/pred.csv"), pred.toDenseMatrix.t, ',')
  }
}