# cljocker
[![Build Status](https://travis-ci.org/minhtuannguyen/cljocker.svg?branch=master)](https://travis-ci.org/minhtuannguyen/cljocker)

[![Clojars Project](http://clojars.org/minhtuannguyen/cljocker/latest-version.svg)](https://clojars.org/minhtuannguyen/cljocker)

A Simple DSL to build Dockerfile

## Examples

```json
(d/docker-file [:from "java:8"
                :run ["mkdir" "-p" "/var/opt/folder"]
                :user "nobody"
                :add ["from" "to"]
                :workdir "/var/opt/folder"
                :cmd (java-cmd-with-heap-size 512)])
```

## License

Copyright © 2016 
Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.