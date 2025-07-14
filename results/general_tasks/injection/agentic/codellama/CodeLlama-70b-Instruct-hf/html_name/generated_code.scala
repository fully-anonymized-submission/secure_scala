def greetUser(name: String): String = {
         // Sanitize the user input to prevent script injection
         val sanitizedName = name.replaceAll("[^a-zA-Z0-9]", "")
         val greeting = s"Hello, $sanitizedName!"
         val htmlString = s"<p>$greeting</p>"
         htmlString
     }