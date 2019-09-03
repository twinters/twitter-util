# Twitter-Util
🔧🐦 Twitter utilities for creating interactive Twitterbots

## Introduction
This Java library helps modelling complex Twitterbot behaviours using `TweetsFetcher` objects
(which model streams of Tweets using fixed parameters, combination and cascading)
in `TwitterBotBehaviour` objects, that model how bots should post tweets and reply to other tweets.

## Dependencies

In order for this code to work, you will need to install [Gradle](https://gradle.org/), as well as download the following repositories in the same folder as `twitter-util`:
- [text-util](https://github.com/twinters/text-util)
- [generator-util](https://github.com/twinters/generator-util)
- [chatbot-util](https://github.com/twinters/chatbot-util)

## Examples

Several different Twitterbots have been implemented using this framework, such as
- TorfsBot ([Twitter](https://twitter.com/TorfsBot), [code](https://github.com/twinters/torfs-bot))
- SamsonBot ([Twitter](https://twitter.com/SamsonRobot), [code](https://github.com/twinters/samson-bot))
- GertBot ([Twitter](https://twitter.com/Gert_Bot), [code](https://github.com/twinters/samson-bot))
- BurgemeesterBot ([Twitter](https://twitter.com/BurgemeesterBot), [code](https://github.com/twinters/burgemeester-bot))
- AlbertoBot ([Twitter](https://twitter.com/AlbertBot), [code](https://github.com/twinters/alberto-bot))
- OctaafBot ([Twitter](https://twitter.com/OctaafBot), [code](https://github.com/twinters/octaaf-bot))
- JeannineBot ([Twitter](https://twitter.com/JeannineBot), [code](https://github.com/twinters/jeannine-bot))
- MopjesBot ([Twitter](https://twitter.com/MopjesBot))
