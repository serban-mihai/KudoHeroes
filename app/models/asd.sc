import scala.collection.mutable
import services.UserService



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
  val tacos = countWords(list.toString()).filter(_._1.equals("taco"))
  val counter = tacos.get("taco").getOrElse(0)
  (list, counter)

}


val list1 = "<@UCTFF4RC2><@URTFR4RC3><@URTFR4RC4>:taco:"

val reg = "[:A-z0-9]+".r
val list = reg.findAllIn(list1).toList

val nList = extractTaco(list1)
nList._1.map{ l=>
  println(l)
}
