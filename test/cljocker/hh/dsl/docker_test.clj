(ns cljocker.hh.dsl.docker-test
  (:require [clojure.test :refer :all]
            [cljocker.hh.dsl.docker :as d]))

(defn- java-cmd-with-heap-size [heap]
  (str "java " "-Xmx" heap "m " "-jar " "artifact.jar"))

(deftest ^:unit test-docker-dsl
  (testing "happy-case"
    (is (= ["FROM java:8"
            "CMD java -Xmx512m -jar artifact.jar"]
           (d/docker [:from "java:8"
                      :bla "blub"
                      :cmd (java-cmd-with-heap-size 512)]))))

  (testing "edgecase"
    (is (= [] (d/docker [:from])))
    (is (= [] (d/docker []))))

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

(deftest ^:unit write-file
  (let [definition [:from "java:8"
                    :cmd (java-cmd-with-heap-size 512)]
        path "target"
        _ (d/docker-file definition path)]
    (is (= "FROM java:8\nCMD java -Xmx512m -jar artifact.jar"
           (slurp "target/Dockerfile")))))