#!/bin/bash
docker images | grep "none" | awk '{print $3}' | xargs docker rmi -f
docker images | grep "session" | awk '{print $3}' | xargs docker rmi -f
