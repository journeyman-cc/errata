# errata

A library designed to filter out from Clojure backtraces those frames likely to be of interest to the developer

## Discussion

Clojure backtraces are very hard to read, because they contain many, many 'noise' frames resulting from the internal workings of the Clojure compiler, and relatively few frames from the namespaces the developer is actually working with.

What I'm setting out to do here is to produce tools which allow backtraces to be filtered against a list of 'interesting' namespaces, in order to present a compact trace showing the propagation of the error through the developer's own code, and core libraries that code depends on.

As stretch goals, I hope

1. Recover the original Clojure function name and namespace name for each call which actually is to a Clojure function;
2. Where the source repository for the function is known, provide a link to it as [Codox]() does;
3. (Stetch goal) provide a web-service to resolve namespaces to source repositories, perhaps by scraping [Clojars](https://clojars.org);
4. To provide a REPL within which the user can register 'interesting' namespaces, and have all printed backtraces filtered through those namespaces; and 
5. To produce a [Hiccup](https://github.com/weavejester/hiccup) formatted 'folded' backtrace, so that when that backtrace is viewed as an HTML page, initially only 'interesting' frames are shown, but 'noise' areas can be selectively unfolded to allow viewing (**achieved**); and finally
6. To allow the user to set a flag in the REPL which, if set, will cause folded backtraces to be automatically opened in browser windows, whenever an uncaught exception propagates back to the REPL; or alternatively
7. (Stretch goal) Rwnder the HTML in the nrepl terminal window using NCYRSES graphics, much as [Lynx]() or [w3m]() would.

## Usage

At present, the most useful entry point is

```clojure
(show-html-back-trace error namespaces)
```

Example usage 

```clojure
(show-html-back-trace err ["nrepl.middleware"])
```

## License

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
