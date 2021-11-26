import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object Main {
    def main(args: Array[String]): Unit = {
      val spark = SparkSession.builder()
        .config("spark.driver.bindAddress", "127.0.0.1")
        .master("local[*]")
        .appName("hw4")
        .getOrCreate()

      def getIdf(df: Long, tf: Long): Double =
        math.log((df.toDouble + 1) / (tf.toDouble + 1))

      val dataset = spark.read
        .option("header", "true")
        .option("inferSchema", "true")
        .csv("data/tripadvisor_hotel_reviews.csv")
        .withColumn("Review", lower(col("Review")))
        .withColumn("Review", regexp_replace(col("Review"), "[^a-z0-9 ]", " "))
        .withColumn("Review", regexp_replace(col("Review"), "\\s+", " "))
        .withColumn("Review", trim(col("Review")))
        .withColumn("Review", split(col("Review"), " "))
        .withColumn("review_id", monotonically_increasing_id())

      val dataset_review = dataset
        .select(
          col("review_id"),
          explode(col("Review")).as("word")
        )
      val val_tf = dataset_review
        .groupBy("review_id", "word")
        .agg(countDistinct("review_id") as "tf")

      val val_tf_lim = dataset_review
        .groupBy("word")
        .agg(countDistinct("review_id") as "tf")
        .orderBy(desc("tf"))
        .limit(100)

      val df_count = dataset.count()
      val val_idf = udf{tf: Long => getIdf(df_count, tf)}
      val idf = val_tf_lim
        .withColumn("idf", val_idf(col("tf")))
        .drop("tf")
        .cache()

      val tfIdf = idf
        .join(val_tf, Seq("word"), "left")
        .withColumn("tfIdf", col("tf") * col("idf"))
        .drop("tf", "idf")

      val res = tfIdf
        .groupBy("review_id").pivot("word").max("tfIdf")
      res.show()
    }
}
