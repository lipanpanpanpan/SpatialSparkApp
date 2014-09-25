import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.util.IntParam

/**
 * Receives text from multiple rawNetworkStreams and counts how many '\n' delimited
 * lines have the word 'the' in them. This is useful for benchmarking purposes. This
 * will only work with spark.streaming.util.RawTextSender running on all worker nodes
 * and with Spark using Kryo serialization (set Java property "spark.serializer" to
 * "org.apache.spark.serializer.KryoSerializer").
 * Usage: RawNetworkGrep <numStreams> <host> <port> <batchMillis>
 *   <numStream> is the number rawNetworkStreams, which should be same as number
 *               of work nodes in the cluster
 *   <host> is "localhost".
 *   <port> is the port on which RawTextSender is running in the worker nodes.
 *   <batchMillise> is the Spark Streaming batch duration in milliseconds.
 */
object GrepNetwork extends App {
  
     StreamingExamples.setStreamingLogLevels()
  
    val sparkConf = new SparkConf().setAppName("NetworkWordCount22")
    val ssc = new StreamingContext(sparkConf, Seconds(10))

    // Create a socket stream on target ip:port and count the
    // words in input stream of \n delimited text (eg. generated by 'nc')
    // Note that no duplication in storage level only for running locally.
    // Replication necessary in distributed scenario for fault tolerance.

    val data = ssc.socketTextStream("localhost", "9999".toInt, StorageLevel.MEMORY_ONLY )
    
  
    //data.filter(_.contains("the")).count()
    val datacount=data.count;
    
    datacount.print()
     
    data.filter(_.contains("the")).count().foreachRDD(
        r =>println("Grep count: " + r.collect().mkString)
        )
      
   // wordCounts.print()
    ssc.start()
    ssc.awaitTermination()
  
}