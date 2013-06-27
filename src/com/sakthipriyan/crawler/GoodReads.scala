package com.sakthipriyan.crawler

object GoodReads {

  
  private def readUrl(url: String) = {
    val source = scala.io.Source.fromURL(url)
    val lines = source.mkString
    source.close()
    lines
  }
}