# npm-registry-follow

[![Clojars Project](https://img.shields.io/clojars/v/open-services/npm-registry-follow.svg)](https://clojars.org/open-services/npm-registry-follow)

A Clojure library designed to read a stream of changes from the
npm registry.

## Install

#### Leiningen/Boot
```
[open-services/npm-registry-follow "1.1.0"]
```

#### Clojure CLI/deps.edn
```
open-services/npm-registry-follow {:mvn/version "1.1.0"}
```

## Usage

```clojure
user=> (def stop (npm-registry-follow.core/listen-for-changes #(println %)))
#'user/stop
"chung-rn"
"flycomponents"
"react"
...
user=> (stop)
;; No longer following changes

;; Alternative to wait for stream to properly close
user=> @(stop)

;; Or, using polling
user=> (def interval (* 1000 3)) ;; three seconds
#'user/interval
user=> (def stop (npm-registry-follow.core/poll-for-changes interval #(println %)))
#'user/stop
"chung-rn"
"flycomponents"
"react"
...
user=> (stop)
;; No longer doing any polling
```

## License

Copyright 2019 Open-Registry

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
