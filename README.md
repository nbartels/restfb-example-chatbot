# RestFB example ChatBot

The RestFB example chatbot is a simple example implementation that shows the possibilities of
RestFB. You see an example how a Facebook Chatbot is implemented.

## Compiling

You need to copy the sample configuration `config.sample.properties` 
to `config.properties` and add your own values.

At the end of the compiling you get a nice one-file jar.

```shell 
mvn package
```

## Usage

To run the example server you simply need to call this on your shell:

```shell
java -jar target/Chatbots-1.0-SNAPSHOT.jar
```

The server is started with the included configuration.

In our default configuration the server runs on localhost and port 8444.

## Changes between RestFB and nbartels repository

A special bot is added to this repository. It's a demo SEPA bot. The function of that bot is to simulate
a get the necessary information to create a SEPA transaction by talking to the user and let him/her provide
everything needed by plain text or button selection.

The main idea is to show a developer how a interaction between a user and the bot can be implemented. Therefore
several Chatbot features are used to give a small overview over the possibilities the Chatbot API provides.

Don't be confused, the text output of this bot is in German.


# Der RestFB Beispiel ChatBot

Der RestFB Beispiel Chatbot ist eine einfache Beispielimplementierung, die die Möglichkeiten von RestFB anhand eines Chatbots aufzeigt.

## Compiling

Bevor man den ChatBot kompilieren kann muss man die Beispielkonfiguration `config.sample.properties` in die Datei `config.properties` kopieren und die persönlichen Werte eintragen (bspw Access Token).

Nach dem Kompilieren erhält man eine Jar Datei, die alle libs beinhaltet und automatisch den WebhookServer startet:

```shell 
mvn package
```

## Benutzung

Um den Webhook Server zu starten muss man nur folgenden Befehl eingeben:

```shell
java -jar target/Chatbots-1.0-SNAPSHOT.jar
```

Der Server wird dann mit der inkludierten Konfiguration gestaret. Normalerweise läuft der Server dann unter `localhost` und hört auf Port `8444` auf eingehende Verbindungen.

## Änderungen zwischen dem RestFB und nbartels Repository

Es wurde ein zusätzlicher Bot implementiert, der eine SEPA Überweisung simuliert. Dabei werden alle notwendigen Daten, die man für eine solche 
Überweisung benötigt vom Benutzer in Form einer Konversation abgefragt. Es wird sowohl Texteingaben als auch Buttons benutzt um mit dem
Benutzer zu interagieren.

Die Hauptidee des Bots ist es einem Entwickler zu zeigen, wie man eine Interaktion zwischen Benutzer und Bot implementiert. Es werden daher auch
verschiedene Features der Facebook Chatbot API verwendet, die einen kleinen Überblick über die Möglichkeiten der API geben sollen.

Die Textausgabe des Bots ist in Deutsch.