This project is to include selenium-standalone-server.jar to the target as a plugin in the system.

The reason selenium-standalone-server have to be imported this way is because selenium-standalone-server itself contains many packages that are the same as Eclipse built-in ones, so importing it in the target file would cause dependency confliction.

Alternavetively, if you try to import all the selenium packages available maven, it would cause errors that you cannot run test.
This is because Eclipse will be confused between the packages that have same name but in different plugins and will produce the NoClassDefFoundError when the class it's looking for is not on the first package with name it found.