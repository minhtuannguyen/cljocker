(ns cljocker.hh.dsl.docker-test
  (:require [clojure.test :refer :all]
            [cljocker.hh.dsl.docker :as d]))

(defn java-cmd-with-heap-size [heap]
  (str "java " "-Xmx" heap "m " "-jar " "artifact.jar"))

(deftest ^:focused test-docker-dsl
  (testing "happy-case"
    (is (= ["FROM java:8"
            "CMD java -Xmx512m -jar artifact.jar"]
           (d/docker [:from "java:8"
                      :bla "blub"
                      :cmd (java-cmd-with-heap-size 512)]))))

  (testing "edgecase"
    (is (= [] (d/docker [:from]))))

  (testing "happy-case"
    (is (= ["FROM java:8"
            "RUN mkdir -p /var/opt/folder"
            "USER nobody"
            "ADD from to"
            "WORKDIR /var/opt/folder"
            "CMD java -Xmx512m -jar artifact.jar"]
           (d/docker [:from "java:8"
                      :run ["mkdir" "-p" "/var/opt/folder"]
                      :user "nobody"
                      :add ["from" "to"]
                      :workdir "/var/opt/folder"
                      :cmd (java-cmd-with-heap-size 512)])))))
