#! /bin/sh

ps aux | grep -Ei 'chromedriver' |grep -v 'grep'|awk '{print $2}'|xargs  kill
ps aux | grep -Ei 'chrome' |grep -v 'grep'|awk '{print $2}'|xargs  kill
