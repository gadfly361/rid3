# Changes

## 0.2.1-alpha (2018-02-24)

Add `rid3->` macro to api. To include it:

```
(ns foo.bar
  (:require [rid3.core :as rid3 :refer [rid3->]]))
```

The purpose of `rid3->` is to be able to use a hiccup-like attribute map when setting attributes on a node.  For example

```
(fn [node ratom]
  (-> node
	(.attr "fill" "grey")
	(.attr "text-anchor" "middle")
	(.style "font-weight" 400)
	(.text "some text")))
```

 would become

 ```
 (fn [node ratom]
  (rid3-> node
	 {:fill "grey"
	  :text-anchor "middle"
	  :style {:font-weight 400}}
	  (.text "some text")))
 ```

## 0.2.0 (2017-09-27)

- add `:key-fn` to `:elem-with-data` piece
- remove `:children` from `:elem` piece
- remove top-level `:prepare-dataset`

### Migrating from 0.1.0-alpha-3?

- If you were using the top-level `:prepare-dataset`, you will need to move it inside all of the `:elem-with-data` pieces.
- If you were using `:children` from within an `:elem` piece, add a `:container` piece and use its `:children` instead

## 0.1.0-alpha-3 (2017-06-14)

- fix broken demo
- move resources to dev-resources, fixes [#3](https://github.com/gadfly361/rid3/issues/3)

## 0.1.0-alpha-2 (2017-05-29)

- rename "main-container" class to "rid3-main-container" to avoid conflicts

## 0.1.0-alpha (2017-05-17)

- initial release
