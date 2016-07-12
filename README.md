# cljocker
[![Build Status](https://travis-ci.org/minhtuannguyen/cljocker.svg?branch=master)](https://travis-ci.org/minhtuannguyen/cljocker)
[![Coverage Status](https://coveralls.io/repos/github/minhtuannguyen/cljocker/badge.svg?branch=master)](https://coveralls.io/github/minhtuannguyen/cljocker?branch=master)
[![Dependencies Status](http://jarkeeper.com/minhtuannguyen/cljocker/status.svg)](http://jarkeeper.com/minhtuannguyen/cljocker)


[![Clojars Project](http://clojars.org/minhtuannguyen/cljocker/latest-version.svg)](https://clojars.org/minhtuannguyen/cljocker)

A simple DSL to define and generate Dockerfile

## Examples

```clojure
(:require [cljocker.hh.dsl.docker :as d])
            
(defn- heap [heap] (str "-Xmx=" heap "m "))
(defn- port [port] (str "-Dport=" port))

(d/write-dockerfile! [:from     "java:8"
                      :run      (lazy-seq ["mkdir" "-p" "/var/opt/folder"])
                      :user     "nobody"
                      :add      ["from" "to"]
                      :workdir  "/var/opt/folder"
                      :cmd      ["java" (heap 512) (port 512) "-jar" "artifact.jar"]]
                      "path/to/dockerfile")       
```

The content of the generated Dockerfile will be:

```shell
FROM java:8
RUN mkdir -p /var/opt/folder
USER nobody
ADD from to
WORKDIR /var/opt/folder
CMD java  -Xmx=512m  -Dport=512  -jar artifact.jar    
```


## License

Copyright © 2016 
Distributed under the Eclipse Public License