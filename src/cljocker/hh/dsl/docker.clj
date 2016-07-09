(ns cljocker.hh.dsl.docker
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [clojure.set :refer :all]))

(def DOCKER-INSTRUCTION
  #{:from
    :maintainer
    :run
    :env
    :add
    :expose
    :copy
    :cmd
    :entrypoint
    :user
    :workdir
    :volume})

(defn instruction-concat [instruction v]
  (str
   (str/upper-case (name instruction))
   " "
   (str/join " " v)))

(defn build-instruction [instruction args]
  (cond
    (not (contains? DOCKER-INSTRUCTION instruction)) ""
    (vector? args) (instruction-concat instruction args)
    (function? args) (instruction-concat instruction (args))
    :else (instruction-concat instruction [args])))

(defn build [instruction args m]
  (let [result (build-instruction instruction args)]
    (if (empty? result)
      m
      (conj m result))))

(defn valid? [definition]
  (and (not (nil? definition))
       (even? (count definition))
       (= :from (first definition))
       (subset? (set (take-nth 2 definition))
                DOCKER-INSTRUCTION)))

(defn docker
  ([definition]
   (if (valid? definition)
     (docker definition []) []))
  ([[instruction args & rest] m]
   (let [m (build instruction args m)]
     (if (seq rest)
       (docker rest m) m))))

(defn docker-file [definition path]
  (->> definition
       (docker)
       (str/join "\n")
       (spit (str path "/Dockerfile"))))