package com.sakthipriyan.crawler

import scala.collection.mutable.ArrayBuffer

object GoodReads {

  def listBooks(limit: Integer) = {
    val output = new ArrayBuffer[Book]
    val index = limit / 100 + 1
    for (a <- 1 to index) {
      val page = getPage(a)
      output ++= getBooks(page)
    }
    output.slice(0, limit).toArray
  }

  def listComments(book: String, limit: Integer = 30) = {
    val page = getBook(book)
    val output = new ArrayBuffer[Review]
    var startIndex = page.indexOf("""<div class="friendReviews elementListBrown">""")
    while (startIndex != -1) {
      startIndex = page.indexOf("""<div class="left bodycol">""", startIndex)
      startIndex = page.indexOf("""<a href="/user/show/""", startIndex) + 20
      var endIndex = page.indexOf("""class=""", startIndex) - 2
      val user = page.substring(startIndex, endIndex)
      startIndex = page.indexOf("staticStars", startIndex) + 12
      endIndex = page.indexOf("title=", startIndex) - 2
      val stars = page.substring(startIndex, endIndex)
      startIndex = page.indexOf("""<span id="reviewTextContainer""", startIndex)
      startIndex = page.indexOf("""style="display:none">""", startIndex) + 21
      endIndex = page.indexOf("""<a""", startIndex)
      val comments = page.substring(startIndex, endIndex).
        replaceAll("\\<.*?>", "").replaceAll("[^a-zA-Z']", " ").replaceAll(" +", " ").trim()

      val star = stars match {
        case "stars_1" => "bad"
        case "stars_2" => "average"
        case "stars_3" => "average"
        case "stars_4" => "good"
        case "stars_5" => "good"
        case _ => "xyz"
      }
      output += Review(book, user, comments, star)
      startIndex = page.indexOf("""<div class="friendReviews elementListBrown">""", startIndex)
    }
    output.slice(0, limit).toArray
  }

  def listReviewedBooks(user: String, limit: Integer = 30) = {
    val output = new ArrayBuffer[String]
    val page = getUser(user)
    var startIndex = page.indexOf("""<tr id="review_""")
    while (startIndex != -1) {
      startIndex = page.indexOf("""<td class="field title">""", startIndex)
      startIndex = page.indexOf("""<a href="""", startIndex) + 20
      val endIndex = page.indexOf("""title="""", startIndex) - 2
      output += page.substring(startIndex, endIndex)
      startIndex = page.indexOf("""<tr id="review_""", startIndex)
    }
    output.slice(0, limit).toArray
  }

  private def getBooks(page: String): Array[Book] = {
    val output = new ArrayBuffer[Book]
    var startIndex = page.indexOf("""<tr itemscope itemtype="http://schema.org/Book">""")
    while (startIndex != -1) {
      startIndex = page.indexOf("""<a href="/book/show/""", startIndex) + 20
      val endIndex = page.indexOf("""title""", startIndex) - 2
      output += Book(page.substring(startIndex, endIndex),true)
      startIndex = page.indexOf("""<tr itemscope itemtype="http://schema.org/Book">""", startIndex)
    }
    output.toArray
  }

  private def getBook(book: String) = readUrl("http://www.goodreads.com/book/show/" + book)

  private def getPage(page: Integer) = readUrl("http://www.goodreads.com/list/show/6.Best_Books_of_the_20th_Century?page=" + page)

  private def getUser(user: String) = readUrl("http://www.goodreads.com/review/list/" + user + "?sort=review&view=reviews")

  private def readUrl(url: String) = {
    println("Fetching url:" + url)
    val source = scala.io.Source.fromURL(url)
    val lines = source.mkString
    source.close()
    lines
  }

}