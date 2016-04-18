# WordWolfAppAndroid
WordWolf - client app for Android

Hello! Welcome to the client codebase for WordWolf, an Android app written in Java, a project by Jason Mortara.

This app is designed from the ground up to be a two-player experience. It's a simple word-find game where players create an account, log in, find an available opponent, and play the word find with them. Due to the two-player nature of the app, a server is required-- in this case a Java socket server, which can be found alongside this project in GitHub. 

A third integral project here is a group of Java object classes designed to be serialized native code that is shared between client and server, so that no extra libraries are required for JSON, XML, REST, etc. All objects go out as Java and come in as Java with no parsing or translation needed. 

The client sends these serialized objects to the server, which is deployed to a remote Linux environment. The server handles account creation and login via JDBC to a MySQL database and sends confirmation to the client. 

ObjectInputStream and ObjectOutputStream are used to send and receive the intact Java objects, which typically take the form of a Request or a matching Response. An asynchronous task handles all I/O, and incoming objects from the server are received by the client, and forwarded to whichever Activity is currently active. Irrelevant objects are ignored, and incoming objects relevant to the current Activity (such as a LoginResponse during login, or a GameMove object during gameplay) are triaged by the Activity and the UI may be updated if necessary. The main UI thread's work is not interrupted by this process, as the client code overrides the publish() and progress() commands in the Android API which are designed for this purpose. This system works well.

Once an account is logged into, clients seek other players in the available players list retreived from the server. A player can challenge another player to a match, which is accepted or declined by the potential opponent. If accepted, the server generates a single randomized game board which is sent to both clients. The server pairs the clients in a match. 

During the match, which lasts for a finite duration via a timer on the client, each player taps their board to form words. Up, down, left and right moves are currently supported-- no diagonals. When the player wishes to submit his or her word for points, first the word is validated on the client side, to be sure it does not violate any rules and is found in a 172,000-word dictionary which is loaded at startup both by clients and by the server. If the word is valid on the client, it is packaged up into an object and sent to the server for a second validation step. Server-validated words generate points and the result is sent to the client, which displays a points-awarded mini-Toast. 

When time runs out, each player's scores are verified and sent to the opponent so that each player knows who scored what and who won or lost. A rematch is offered, which may be accepted or declined. Also, fun is had.

All code is currently in active development and performs extensive logging, which would be turned off in production.

-Jason Mortara, 2016
