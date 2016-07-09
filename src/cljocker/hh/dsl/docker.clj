(ns cljocker.hh.dsl.docker
  (:require [clojure.string :as str]
            [clojure.test :refer :all]))

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

(defn build-instruction [instruction v]
  (cond
    (not (contains? DOCKER-INSTRUCTION instruction)) ""
    (vector? v) (instruction-concat instruction v)
    (function? v) (instruction-concat instruction (v))
    :else (instruction-concat instruction [v])))

(defn build [instruction v m]
  (let [result (build-instruction instruction v)]
    (if (empty? result)
      m
      (conj m result))))

(defn docker
  ([definition]
   (if (even? (count definition))
     (docker definition []) []))
  ([[instruction values & rest] m]
   (let [m (build instruction values m)]
     (if (seq rest)
       (docker rest m) m))))

(defn docker-file [definition]
  (str/join "\n" (docker definition)))