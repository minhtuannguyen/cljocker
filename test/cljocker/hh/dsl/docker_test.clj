(ns cljocker.hh.dsl.docker-test
  (:require [clojure.test :refer :all]
            [cljocker.hh.dsl.docker :as d]
            [clojure.string :as str]))

(deftest ^:unit validate
  (is (= [:valid]
         (-> [:from "image" :cmd "echo"]
             (d/new-dockerfile)
             (d/validate))))

  (is (= [:invalid "spec is empty"]
         (-> nil
             (d/new-dockerfile)
             (d/validate))))

  (is (= [:invalid "some instruction has empty argument"]
         (-> [:from ""]
             (d/new-dockerfile)
             (d/validate))
         (-> [:from "image" :cmd ""]
             (d/new-dockerfile)
             (d/validate))))

  (is (= [:invalid "spec is not well-formed"]
         (-> [:from "image" :cmd]
             (d/new-dockerfile)
             (d/validate))))

  (is (= [:invalid "spec has some unknown instruction"]
         (-> [:from "image" :bla "blub" :cmd "echo"]
             (d/new-dockerfile)
             (d/validate))))

  (is (= [:invalid "There can only be one CMD instruction"]
         (-> [:from "image"]
             (d/new-dockerfile)
             (d/validate))
         (-> [:from "image" :cmd "echo 1" :cmd "echo 2"]
             (d/new-dockerfile)
             (d/validate))))

  (is (= [:invalid "first instruction must be FROM"]
         (-> [:cmd "echo"]
             (d/new-dockerfile)
             (d/validate))
         (-> []
             (d/new-dockerfile)
             (d/validate)))))

(deftest ^:unit valid?
  (is (-> [:from "image" :cmd "echo"]
          (d/new-dockerfile)
          (d/valid?)
          (true?)))

  (is (-> nil
          (d/new-dockerfile)
          (d/valid?)
          (false?))))

(defn- heap [heap] (str "-Xmx=" heap "m "))
(defn- port [port] (str "-Dport=" port))

(deftest ^:unit test-docker-dsl
  (testing "throw exception if spec is invalid"
    (is (thrown? IllegalArgumentException (-> [:from]
                                              (d/new-dockerfile)
                                              (d/as-str)))))

  (testing "happy case"
    (is (= ["FROM java:8"
            "RUN mkdir -p /var/opt/folder"
            "USER nobody"
            "ADD from to"
            "WORKDIR /var/opt/folder"
            "CMD java -Xmx=512m  -Dport=512 -jar artifact.jar"]

           (-> [:from "java:8"
                :run (lazy-seq ["mkdir" "-p" "/var/opt/folder"])
                :user "nobody"
                :add ["from" "to"]
                :workdir "/var/opt/folder"
                :cmd ["java" (heap 512) (port 512) ["-jar" "artifact.jar"]]]
               (d/new-dockerfile)
               (d/as-str)
               (str/split-lines))

           (-> [:from "java:8"
                :run ["mkdir" "-p" "/var/opt/folder"]
                :user "nobody"
                :add ["from" "to"]
                :workdir "/var/opt/folder"
                :cmd ["java" (heap 512) (port 512) "-jar" "artifact.jar"]]
               (d/new-dockerfile)
               (d/as-str)
               (str/split-lines))))))

(deftest ^:unit write-docker-file-to-disk
  (let [dockerfile (-> [:from "java:8"
                        :cmd ["java " "-jar artifact.jar"]]
                       (d/new-dockerfile))

        _ (d/write! dockerfile "target")]
    (is (= "FROM java:8\nCMD java  -jar artifact.jar"
           (slurp "target/Dockerfile")))))