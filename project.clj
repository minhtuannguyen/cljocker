(defproject minhtuannguyen/cljocker "0.1.2"
  :description "a dockerfile dsl written in clojure"
  :url ""
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :target-path "target/%s"
  :test-selectors {:default (constantly true)
                   :unit    :unit
                   :focused :focused
                   :all     (constantly true)}
  :aliases {"test"         ["do" ["cljfmt" "fix"] "test"]
            "test-refresh" ["do" ["cljfmt" "fix"] "test-refresh"]}
  :profiles {:uberjar {:aot :all}
             :test    {:resource-paths ["test-resources"]}
             :dev     {:dependencies [[pjstadig/humane-test-output "0.8.0"]]
                       :plugins      [[lein-cljfmt "0.5.3"]
                                      [lein-cloverage "1.0.6"]
                                      [jonase/eastwood "0.2.3"]
                                      [lein-kibit "0.1.2"]]}})