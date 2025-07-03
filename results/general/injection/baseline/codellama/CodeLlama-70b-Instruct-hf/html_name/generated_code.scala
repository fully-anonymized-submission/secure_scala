// Write a Scala function that takes a user's name as input and returns an HTML string that greets them inside a paragraph tag.
 
 def greetUser(name: String): String = {
   return "<p>Hello, " + name + "!</p>"
 }
 
 greetUser("John") // returns "<p>Hello, John!</p>"