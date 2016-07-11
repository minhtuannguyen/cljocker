(ns cljocker.hh.dsl.docker
  (:require [clojure.string :as s]
            [clojure.test :refer :all]
            [clojure.set :refer :all]))

(def INSTRUCTIONS
  #{:from
    :maintainer
    :run
    :cmd
    :label
    :expose
    :env
    :add
    :copy
    :entrypoint
    :volume
    :user
    :workdir
    :arg
    :onbuild
    :stopsignal
    :healthcheck
    :shell})

(defn instruction-concat [instruction v]
  (str
   (s/upper-case (name instruction))
   " "
   (s/join " " v)))

(defn build-instruction [instruction args]
  (cond
    (not (contains? INSTRUCTIONS instruction)) ""
    (vector? args) (instruction-concat instruction args)
    (function? args) (instruction-concat instruction (args))
    :else (instruction-concat instruction [args])))

(defn build [instruction args m]
  (let [result (build-instruction instruction args)]
    (if (empty? result)
      m
      (conj m result))))

(defn validate [[first & rest :as spec]]
  (cond
    (nil? spec)
    [:invalid "spec is empty"]

    (not (even? (count spec)))
    [:invalid "spec is not well-formed"]

    (not= :from first)
    [:invalid "first instruction must be FROM"]

    (seq (filter empty? (take-nth 2 rest)))
    [:invalid "some instruction has empty argument"]

    (not= 1 (count (filter #(= :cmd %) spec)))
    [:invalid "There can only be one CMD instruction"]

    (not (subset? (set (take-nth 2 spec)) INSTRUCTIONS))
    [:invalid "spec has some unknown instruction"]

    :else [:valid]))

(defn valid? [spec]
  (= :valid (first (validate spec))))

(defn docker
  ([spec]
   (let [[status reason] (validate spec)]
     (if (= :valid status)
       (docker spec [])
       (throw (new IllegalArgumentException reason)))))
  ([[instruction args & rest] m]
   (let [m (build instruction args m)]
     (if (seq rest)
       (docker rest m) m))))

(defn docker-file [spec path]
  (->> spec
       (docker)
       (s/join "\n")
       (spit (str path "/Dockerfile"))))