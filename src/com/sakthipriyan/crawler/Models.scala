package com.sakthipriyan.crawler

case class Book(title: String, followReviewer:Boolean = false)

case class Review(book:String,user: String, text: String, star: String)