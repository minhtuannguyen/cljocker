(defproject minhtuannguyen/cljocker "0.1.8-SNAPSHOT"
  :description "a dockerfile dsl written in clojure"
  :url "https://github.com/minhtuannguyen/cljocker"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :target-path "target/%s"
  :test-selectors {:default (constantly true)
                   :unit    :unit
                   :focused :focused
                   :all     (constantly true)}
  :aliases {"test-refresh" ["do" ["cljfmt" "fix"] "test-refresh"]}
  :profiles {:uberjar {:aot :all}
             :test    {:resource-paths ["test-resources"]}
             :dev     {:dependencies [[pjstadig/humane-test-output "0.8.3"]]
                       :plugins      [[lein-cljfmt "0.5.7"]
                                      [lein-cloverage "1.0.10"]
                                      [jonase/eastwood "0.2.5"]
                                      [lein-kibit "0.1.6"]]}})
