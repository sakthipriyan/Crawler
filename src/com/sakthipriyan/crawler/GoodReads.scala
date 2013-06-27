package com.sakthipriyan.crawler

import scala.collection.mutable.ArrayBuffer

object GoodReads {

  def getBook(book: String) = readUrl("http://www.goodreads.com/book/show/" + book)

  def getPage(page: Integer) = readUrl("http://www.goodreads.com/list/show/6.Best_Books_of_the_20th_Century?page=" + page)

  def getUser(user: String) = readUrl("http://www.goodreads.com/review/list/" + user + "?sort=review&view=reviews")

  def getBooks(page: String): Array[String] = {
    val output = new ArrayBuffer[String]
    val start = """<tr itemscope itemtype="http://schema.org/Book">"""
    var startIndex = page.indexOf(start)
    while (startIndex != -1) {
      startIndex = page.indexOf("""<a href="/book/show/""", startIndex)
      val endIndex = page.indexOf("""title""", startIndex)
      output += page.substring(startIndex + 20, endIndex - 2)
      startIndex = page.indexOf(start, startIndex)
    }
    output.toArray
  }

  def listReviewedBooks(user: String, limit:Integer = 30) = {
    val output = new ArrayBuffer[String]
    val page = getUser(user)
    var startIndex = page.indexOf("""<tr id="review_""")
    while (startIndex != -1) {
      startIndex = page.indexOf("""<td class="field title">""", startIndex)
      startIndex = page.indexOf("""<a href="""", startIndex)
      val endIndex = page.indexOf("""title="""", startIndex) - 2
      output += page.substring(startIndex + 20, endIndex)
      startIndex = page.indexOf("""<tr id="review_""",startIndex)
    }
    output.slice(0,limit).toSet
  }

  def listBooks(limit: Integer): Set[String] = {
    val output = new ArrayBuffer[String]
    val index = limit / 100 + 1
    for (a <- 1 to index) {
      val page = getPage(a)
      output ++= getBooks(page)
    }
    output.slice(0, limit).toSet
  }

  def listComments(book: String, limit:Integer = 30) : Set[Review] = {
    val page = getBook(book)
    val output = new ArrayBuffer[Review]
    var startIndex = page.indexOf("""<div class="friendReviews elementListBrown">""")
    while (startIndex != -1) {
      startIndex = page.indexOf("""<div class="left bodycol">""", startIndex + 1)
      startIndex = page.indexOf("""<a href="/user/show/""", startIndex + 1)
      var endIndex = page.indexOf("""class=""", startIndex + 1)
      val user = page.substring(startIndex + 20, endIndex - 2)
      startIndex = page.indexOf("staticStars", startIndex + 1)
      endIndex = page.indexOf("title=", startIndex + 1)
      val stars = page.substring(startIndex + 12, endIndex - 2)
      startIndex = page.indexOf("""<span id="reviewTextContainer""", startIndex + 1)
      startIndex = page.indexOf("""style="display:none">""", startIndex)
      endIndex = page.indexOf("""<a""", startIndex)
      val comments = page.substring(startIndex + 21, endIndex).replaceAll("""<(?!\/?a(?=>|\s.*>))\/?.*?>""", "")
      val star = stars match {
        case "stars_1" => "bad"
        case "stars_2" => "avg"
        case "stars_3" => "avg"
        case "stars_4" => "gud"
        case "stars_5" => "gud"
        case _ => "xyz"
      }
      output += Review(book,user, comments, star)
      startIndex = page.indexOf("""<div class="friendReviews elementListBrown">""", startIndex)
    }
    output.slice(0, limit).toSet
  }

  private def readUrl(url: String) = {
    println("Fetching url:" + url)
    val source = scala.io.Source.fromURL(url)
    val lines = source.mkString
    source.close()
    lines.stripLineEnd
  }

}