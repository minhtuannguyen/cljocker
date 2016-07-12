(ns cljocker.hh.dsl.docker
  (:refer-clojure :exclude [resolve])
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

(defn- instruction-concat [instruction v]
  (str
   (s/upper-case (name instruction))
   " "
   (s/join " " v)))

(defn- resolve [args]
  (cond
    (or (vector? args) (seq? args)) (flatten args)
    (function? args) (args)
    :else [args]))

(defn- build-instruction [instruction args m]
  (if (contains? INSTRUCTIONS instruction)
    (->> args
         (resolve)
         (instruction-concat instruction)
         (conj m))
    m))

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
   (let [m (build-instruction instruction args m)]
     (if (seq rest)
       (docker rest m) m))))

(defn dockerfile-str [spec]
  (s/join "\n" (docker spec)))

(defn write-dockerfile! [spec path]
  (spit (str path "/Dockerfile")
        (dockerfile-str spec)))