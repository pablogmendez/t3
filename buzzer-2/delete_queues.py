#!/usr/bin/env python
from sys import stdin
from pika import BlockingConnection, ConnectionParameters

connection = BlockingConnection(ConnectionParameters('localhost'))
channel = connection.channel()

queues = stdin.readlines()
for x in queues:
    q = x.split()[0]
    print 'Deleting %s...' %(q)
    channel.queue_delete(queue=q)

connection.close()

