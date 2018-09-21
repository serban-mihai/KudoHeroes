
import scala.collection.mutable

var message1 = "<@UCTCJA03V :taco::taco:>"

def countWords(text: String) = {
  val counts = mutable.Map.empty[String, Int].withDefaultValue(0)
  for (rawWord <- text.split("[:,!.]+")) {
    val word = rawWord
    counts(word) += 1
  }
  counts
}

def extractTaco(message: String) = {

  val reg = "[:A-z0-9]+".r
  val list = reg.findAllIn(message).toList
  val tacos = countWords(list.tail.toString()).filter(_._1.equals("taco"))
  val count = tacos.get("taco").getOrElse(0)

  (list.head, count)
}
val finalResult = extractTaco(message1)



val seqT: Seq[(String, Int)] = Seq(("a",0), ("b",1), ("c", 2))
seqT.toMap