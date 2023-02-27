# Lowcy Gier checker
Simple tool to inform that article with provided tag is available on lowcygier.pl

Works perfectly fine on rapsberry pi

## Build
To generate fat-jar use
./gradlew jar 

## Running
To run this app execute following:

```
java -jar build/libs/LowcyGierPinger-1.0-SNAPSHOT.jar <<yourSourceMailAddress>> <<yourSourceMailApplicationPassword>> <<notificationAddress>> <<searchPhrase>>
```

## Example

I want to be inform each time discount for zelda game is available on lowcygier.pl
I execute following command to get mail to my private email address.

```
java -jar build/libs/LowcyGierPinger-1.0-SNAPSHOT.jar app@gmail.com pass123 myAddress@gmail.com zelda
```
