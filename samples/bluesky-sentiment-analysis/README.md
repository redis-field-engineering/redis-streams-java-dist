# Bluesky Sentiment Analysis with Redis Streams Java

This sample shows you how to use Redis Streams Java to scale out consumption on a Redis Stream.

## What it does

This demo listens to the the Bluesky firehose, parses the messages that come from posts, and posts them to a Redis Stream. Then a scalable group of consumers consumes the
stream, runs sentiment analysis on it, and then posts the analyzed docs to Redis. In the background the API service controls which hashtags and keywords are monitored,
as well as periodically aggregating sentiment and posting those aggregated sentiments to prometheus, you can then check in on the sentiments for a given keyword or hashtag
in the frontend with Grafana.