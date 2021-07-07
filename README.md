# errata

A library designed to filter out from Clojure backtraces those frames likely to be of interest to the developer

## Discussion

Clojure backtraces are very hard to read, because they contain many, many 'noise' frames resulting from the internal workings of the Clojure compiler, and relatively few frames from the namespaces the developer is actually working with.

What I'm setting out to do here is to produce tools which allow backtraces to be filtered against a list of 'interesting' namespaces, in order to present a compact trace showing the propagation of the error through the developer's own code, and core libraries that code depends on.

As further goals, I hope

1. Recover the original Clojure function name and namespace name for each call which actually is to a Clojure function (**achieved**);
2. Where the source repository for the function is known, provide a link to it as [Codox]() does;
3. (Stretch goal) provide a web-service to resolve namespaces to source repositories, perhaps by scraping [Clojars](https://clojars.org);
4. To provide a REPL within which the user can register 'interesting' namespaces, and have all printed backtraces filtered through those namespaces (**achieved**); and 
5. To produce a [Hiccup](https://github.com/weavejester/hiccup) formatted 'folded' backtrace, so that when that backtrace is viewed as an HTML page, initially only 'interesting' frames are shown, but 'noise' areas can be selectively unfolded to allow viewing (**achieved**); and finally
6. To allow the user to set a flag in the REPL which, if set, will cause folded backtraces to be automatically opened in browser windows, whenever an uncaught exception propagates back to the REPL; or alternatively
7. (Stretch goal) Rwnder the HTML in the nrepl terminal window using NCURSES graphics, much as [Lynx](https://en.wikipedia.org/wiki/Lynx_(web_browser)) or [w3m](http://w3m.sourceforge.net/) would.

## Usage

At present, the most useful entry point is

```clojure
(show-html-backtrace error namespaces)
(show-html-backtrace error)
(show-html-backtrace)

```

Example usage 

```clojure
(use 'cc.journeyman.errata.core)

;; an error to demonstrate with.
(def error 
  (try
    (/ 1 0)
    (catch Exception e e)))

;; you can explicitly pass both exception object and list of namespace names
(show-html-backtrace error ["nrepl.middleware"])

;; `namespaces` defaults to those namespace names registered as interesting,
;; so register one (or more)
(register-interesting-ns! "nrepl.middleware")
(show-html-backtrace error)

;; `error` defaults to the current value of `*e`, so this does exactly the same
;; as `(show-html-backtrace *e)`)`.
(show-html-backtrace)

;; you can also show error in the NREPL terminal.
;; you can explicitly pass both exception object and list of namespace names
(summarise-error error ["nrepl.middleware" "clojure.lang.Number"])

;; `namespaces` defaults to registered interesting namespaces, as before.
(register-interesting-ns! "clojure.lang.Number")
(summarise-error error)

;; `error` defaults to the current value of `*e`, so this does exactly the same
;; as `(summarise-error *e)`)`.
(summarise-error)

;; Finally, for convenience working in the REPL, `serr` is a shortcut for 
;; `summarise-error`
(serr)
```

# License

Copyright © 2021 Simon Brooke

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
