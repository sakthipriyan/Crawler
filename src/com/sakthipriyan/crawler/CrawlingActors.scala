package com.sakthipriyan.crawler

import akka.actor.Actor
import akka.actor.Scheduler
import akka.actor.Props
import akka.actor.ActorSystem
import akka.routing.SmallestMailboxRouter
import Crawler._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class BookActor extends Actor {
  def receive = {
    case book: Book => {
      if (hadoop.isBookAvailable(book.title))
        println("Skipping available book " + book)
      else {
        for (review <- GoodReads.listComments(book.title, config.getReviewersLimit())) {
          hadoop.writeReviewToFile(review)
          if (book.followReviewer)
            CrawlingActors.userActor ! review.user
        }
      }
    }
    case "shutdown" => context.system.shutdown()
    case _ => println("Wrong message to BookActor")
  }
}

class UserActor extends Actor {
  def receive = {
    case user: String => {
      for (title <- GoodReads.listReviewedBooks(user, config.getBooksLimit()))
        CrawlingActors.bookActor ! Book(title)
    }

    case _ => println("Wrong message to UserActor")
  }
}

object CrawlingActors {
  val system = ActorSystem("CrawlingActors")
  val bookActor = system.actorOf(Props[BookActor].withRouter(SmallestMailboxRouter(config.getBookActors())), name = "bookactor")
  val userActor = system.actorOf(Props[UserActor].withRouter(SmallestMailboxRouter(config.getUserActors())), name = "useractor")
  system.scheduler.scheduleOnce(1 hour) {
    println("Shutting Akka system")
    bookActor ! "shutdown"
  }
  system.scheduler.schedule(1 minute, 30 seconds){
    println("Hinting garbage collection to JVM")
    System.gc()
  }
}
