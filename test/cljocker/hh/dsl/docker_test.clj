(ns cljocker.hh.dsl.docker-test
  (:require [clojure.test :refer :all]
            [cljocker.hh.dsl.docker :as d]))

(deftest ^:unit valid?
  (is (true? (d/valid? [:from "image"])))
  (is (false? (d/valid? [:from ""])))
  (is (false? (d/valid? [:from "image" :cmd])))
  (is (false? (d/valid? [:from "image" :cmd ""])))
  (is (false? (d/valid? [:from "image" :bla "blub"])))
  (is (false? (d/valid? [:cmd "echo"]))))

(defn- heap [heap] (str "-Xmx=" heap "m "))
(defn- port [port] (str "-Dport=" port))

(deftest ^:unit test-docker-dsl
  (testing "edge cases"
    (is (= [] (d/docker [:from])))
    (is (= [] (d/docker []))))

  (testing "happy case"
    (is (= ["FROM java:8"
            "RUN mkdir -p /var/opt/folder"
            "USER nobody"
            "ADD from to"
            "WORKDIR /var/opt/folder"
            "CMD java  -Xmx=512m  -Dport=512  -jar artifact.jar"]
           (d/docker [:from "java:8"
                      :run ["mkdir" "-p" "/var/opt/folder"]
                      :user "nobody"
                      :add ["from" "to"]
                      :workdir "/var/opt/folder"
                      :cmd ["java " (heap 512) (port 512) " -jar artifact.jar"]])))))

(deftest ^:unit write-docker-file-to-disk
  (let [definition [:from "java:8"
                    :cmd ["java " "-jar artifact.jar"]]
        path "target"
        _ (d/docker-file definition path)]
    (is (= "FROM java:8\nCMD java  -jar artifact.jar"
           (slurp "target/Dockerfile")))))