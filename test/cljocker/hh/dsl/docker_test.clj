(ns cljocker.hh.dsl.docker-test
  (:require [clojure.test :refer :all]
            [cljocker.hh.dsl.docker :as d]))

(deftest ^:unit validate
  (is (= [:valid]
         (d/validate [:from "image" :cmd "echo"])))

  (is (= [:invalid "spec is empty"]
         (d/validate nil)))

  (is (= [:invalid "some instruction has empty argument"]
         (d/validate [:from ""])
         (d/validate [:from "image" :cmd ""])))

  (is (= [:invalid "spec is not well-formed"]
         (d/validate [:from "image" :cmd])))

  (is (= [:invalid "spec has some unknown instruction"]
         (d/validate [:from "image" :bla "blub" :cmd "echo"])))

  (is (= [:invalid "There can only be one CMD instruction"]
         (d/validate [:from "image"])
         (d/validate [:from "image" :cmd "echo 1" :cmd "echo 2"])))

  (is (= [:invalid "first instruction must be FROM"]
         (d/validate [:cmd "echo"])
         (d/validate []))))

(deftest ^:unit valid?
  (is (true? (d/valid? [:from "image" :cmd "echo"])))
  (is (false? (d/valid? nil))))

(defn- heap [heap] (str "-Xmx=" heap "m "))
(defn- port [port] (str "-Dport=" port))

(deftest ^:unit test-docker-dsl
  (testing "throw exception if spec is invalid"
    (is (thrown? IllegalArgumentException (d/docker [:from]))))

  (testing "happy case"
    (is (= ["FROM java:8"
            "RUN mkdir -p /var/opt/folder"
            "USER nobody"
            "ADD from to"
            "WORKDIR /var/opt/folder"
            "CMD java -Xmx=512m  -Dport=512 -jar artifact.jar"]
           (d/docker [:from "java:8"
                      :run (lazy-seq ["mkdir" "-p" "/var/opt/folder"])
                      :user "nobody"
                      :add ["from" "to"]
                      :workdir "/var/opt/folder"
                      :cmd ["java" (heap 512) (port 512) ["-jar" "artifact.jar"]]])
           (d/docker [:from "java:8"
                      :run ["mkdir" "-p" "/var/opt/folder"]
                      :user "nobody"
                      :add ["from" "to"]
                      :workdir "/var/opt/folder"
                      :cmd ["java" (heap 512) (port 512) "-jar" "artifact.jar"]])))))

(deftest ^:unit write-docker-file-to-disk
  (let [spec [:from "java:8"
              :cmd ["java " "-jar artifact.jar"]]
        path "target"
        _ (d/write-dockerfile! spec path)]
    (is (= "FROM java:8\nCMD java  -jar artifact.jar"
           (slurp "target/Dockerfile")
           (d/dockerfile-str spec)))))