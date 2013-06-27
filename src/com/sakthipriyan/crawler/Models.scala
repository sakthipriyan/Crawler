package com.sakthipriyan.crawler

case class Book(title: String)

case class Review(book:String,user: String, text: String, star: String)