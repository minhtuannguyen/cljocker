# cljocker

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