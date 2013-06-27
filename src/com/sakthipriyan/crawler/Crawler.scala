package com.sakthipriyan.crawler

import scala.actors.Actor
import scala.actors.Actor._

object Crawler {

  //Load from properties files.
  val config = Config.getInstance()
  val hadoop = Hadoop.getInstance()

  def saveReviews(book: String) = {
    if (hadoop.isBookAvailable(book)) {
      println("Skipping available book " + book)
      None
    } else {
      val reviews = GoodReads.listComments(book, config.getReviewersLimit())
      for (review <- reviews) {
        hadoop.writeReviewToFile(review)
      }
      Some(reviews)
    }
  }

  def main(args: Array[String]) {

    //Get First n books from Top books of 20th century. 
    val books = GoodReads.listBooks(config.getInitBooksLimit())

    for (book <- books) {
      saveReviews(book) map { reviews =>
        {
          for (review <- reviews) {
            val reviewedBooks = GoodReads.listReviewedBooks(review.user, config.getBooksLimit())
            for (reviewBook <- reviewedBooks) {
              saveReviews(reviewBook)
            }
          }
        }
      }
    }
  }
}