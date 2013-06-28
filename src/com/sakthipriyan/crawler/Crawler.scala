package com.sakthipriyan.crawler

object Crawler {

  //Load from properties files.
  val config = Config.getInstance()
  val hadoop = Hadoop.getInstance()

  def main(args: Array[String]) {
    //Get First n books from Top books of 20th century. 
    val books = GoodReads.listBooks(config.getInitBooksLimit())
    for (book <- books)
      CrawlingActors.bookActor ! book
  }
}