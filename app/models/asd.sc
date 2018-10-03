import org.joda.time.DateTime
//val dt = new DateTime(ts.toLong * 1000).toDate

import org.joda.time.format.DateTimeFormat

def getTime(ts: Long) = {

  val dtFormatter = DateTimeFormat.forPattern("dd-MM-yyyy")
  val res = dtFormatter.print(ts * 1000)
  res

}

val date = "1537779021.000100".toDouble.toLong

getTime(date)