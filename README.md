# rid3

Rid3: [**R**eagent](https://github.com/reagent-project/reagent) **i**nterface to [**d3**](https://d3js.org/).

[demo](http://rid3.s3-website-us-west-1.amazonaws.com/)

[clojure meetup slides](http://rawgit.com/gadfly361/rid3/master/docs/cph_meetup/rid3_intro.html) based on v0.1.0-alpha-3.

To use rid3, add the following to the `:dependencies` vector in your project.clj file:

```clojure
[rid3 "0.2.0"]
```

Using an older version?

- [Migrating from v0.1.0-alpha-3?](https://github.com/gadfly361/rid3/blob/master/CHANGES.md)
- [Previous docs](https://github.com/gadfly361/rid3/tree/master/docs/READMEs)

## The Problem

In my experience, there is a lot of boilerplate involved when trying to get reagent (i.e., react) and d3 to play nicely together.  The crux of the problem is you only want to append containing g tags (or static elements) to the DOM during reagent's `component-did-mount` lifecycle method and not during the `component-did-update` lifecycle method. However, more often than not, you want d3 to modify the stuff contained in the g tag in the same manner whether or not the component just mounted or just updated.

## Rid3's Solution

Rid3 exposes a single reagent component, `viz`, with the aim to make 80% of what you'll likely want to make with d3 easier (bar charts, pie charts, scatter plots, etc.).  Rid3 attempts to achieve this by making containing g tags for you. The benefit of doing this is that you can now use the *same* function in the did-mount and did-update lifecycle methods.  However, rid3 goes a step further and will *default* the did-update lifecycle method to whatever you supply as the did-mount lifecycle method.

## Viz Component

The `viz` component takes a hash-map of the following:

| key              | type               | default                                         | required? |
|------------------|--------------------|-------------------------------------------------|-----------|
| :id              | string             |                                                 | yes       |
| :ratom           | reagent.core/atom  |                                                 | yes       |
| :svg             | svg                |                                                 | yes       |
| :main-container  | main-container     |                                                 | no        |
| :pieces          | [ piece ]          |                                                 | no        |

- an `:id` is required to differentiate between different rid3's
- a `:ratom` can be a reagent atom, reagent cursor, or a re-frame subscription. This should be used to store all of the data relevant to your rid3.

### :svg

Where an svg hash-map looks like:

| key         | type                  | default   | required? |
|-------------|-----------------------|-----------|-----------|
| :did-mount  | (fn [node ratom] ...) |           | yes       |
| :did-update | (fn [node ratom] ...) | did-mount | no        |


### :main-container

Where a main-container hash-map looks like:

| key         | type                  | default   | required? |
|-------------|-----------------------|-----------|-----------|
| :did-mount  | (fn [node ratom] ...) |           | no        |
| :did-update | (fn [node ratom] ...) | did-mount | no        |

### :pieces

And where :pieces is a vector of piece hash-maps.  There are four kinds of piece hash-maps:

- `:container` for when you want to group *elem* or *elem-with-data* pieces under the same g tag.

| key         | type                  | default   | required? |
|-------------|-----------------------|-----------|-----------|
| :class      | string                |           | yes       |
| :did-mount  | (fn [node ratom] ...) |           | yes       |
| :did-update | (fn [node ratom] ...) | did-mount | no        |
| :children   | [ piece ]             |           | no        |

- `:elem` for when you want to add an element like a text or a circle.

| key         | type                  | default   | required? |
|-------------|-----------------------|-----------|-----------|
| :class      | string                |           | yes       |
| :tag        | string                |           | yes       |
| :did-mount  | (fn [node ratom] ...) |           | yes       |
| :did-update | (fn [node ratom] ...) | did-mount | no        |

- `:elem-with-data` for when you want to add a series of elements that are joined to a dataset

| key              | type                  | default                                         | required? |
|------------------|-----------------------|-------------------------------------------------|-----------|
| :class           | string                |                                                 | yes       |
| :tag             | string                |                                                 | yes       |
| :did-mount       | (fn [node ratom] ...) |                                                 | yes       |
| :did-update      | (fn [node ratom] ...) | did-mount                                       | no        |
| :prepare-dataset | (fn [ratom] ...)      | (fn [ratom] (-> @ratom (get :dataset) clj->js)) | no        |
| :key-fn          | (fn [d i] ...)        |                                                 | no        |

- `:raw` for when you want to either trigger some side-effect or have an escape hatch from the rid3

| key         | type             | default | required? |
|-------------|------------------|---------|-----------|
| :did-mount  | (fn [ratom] ...) |         | yes       |
| :did-update | (fn [ratom] ...) |         | yes       |

## Joy Rid3 (Minimal Example)

Just so you can get a sense of a `viz`component, let's create a minimal example ...  a circle with text on top of it.

### Add rid3 to your project

Add the following to the `:dependencies` vector of your project.clj file.

```clojure
[rid3 "0.2.0"]
```

### Require rid3 in your namespace

```clojure
(ns foo.core
  (:require
   [rid3.core :as rid3]))
```

### Create an svg

```clojure
(defn viz [ratom]
  [rid3/viz
   {:id    "some-id"
    :ratom ratom
    :svg   {:did-mount (fn [node _]
                         (-> node
                             (.attr "width" 200)
                             (.attr "height" 200)
                             (.style "background-color" "grey")))}
    }])
```

Which will result in the following:

```html
<div id="some-id">
  <svg width="200" height="200" style="background-color: grey;">
    <g class="rid3-main-container">
    </g>
  </svg>
</div>
```

You probably noticed that rid3 added a g tag with the class `.rid3-main-container` inside of your svg.  This is where rid3 will place all of your pieces.

*Note: All viz components need to provide a ratom. All relevant data for the component should be stored here. If anything changes in this ratom, then rid3 will trigger a re-render of the viz component for you.*

### Add a circle

```clojure
(defn viz [ratom]
  [rid3/viz
   {:id    "some-id"
    :ratom ratom
    :svg   {:did-mount (fn [node _]
                         (-> node
                             (.attr "width" 200)
                             (.attr "height" 200)
                             (.style "background-color" "grey")))}
;; ATTENTION \/
    :pieces
    [{:kind      :elem
      :class     "backround"
      :tag       "circle"
      :did-mount (fn [node _]
                   (-> node
                       (.attr "cx" 100)
                       (.attr "cy" 100)
                       (.attr "r" 50)))}]
    }])
```

Which will result in the following:

```html
<div id="some-id">
  <svg width="200" height="200" style="background-color: grey;">
    <g class="rid3-main-container">
      <g class="backround">
	<circle cx="100" cy="100" r="50">
	</circle>
      </g>
    </g>
  </svg>
</div>
```

Please note, that there is **no** `.append` method in our did-mount function! Rid3 created a containing g tag and placed the circle inside of it for us.  The node that is passed to the did-mount function is the circle itself.  You can confirm this by looking at where the attributes from our did-mount function are added.

### Add text

```clojure
(defn viz [ratom]
  [rid3/viz
   {:id    "some-id"
    :ratom ratom
    :svg   {:did-mount (fn [node _]
                         (-> node
                             (.attr "width" 200)
                             (.attr "height" 200)
                             (.style "border" "solid 1px grey")))}
    :pieces
    [{:kind      :elem
      :class     "backround"
      :tag       "circle"
      :did-mount (fn [node _]
                   (-> node
                       (.attr "cx" 100)
                       (.attr "cy" 100)
                       (.attr "r" 50)
                       (.attr "fill" "grey")))}
;; ATTENTION \/
     {:kind      :elem
      :class     "foreground"
      :tag       "text"
      :did-mount (fn [node _]
                   (-> node
                       (.attr "x" 100)
                       (.attr "y" 100)
                       (.attr "text-anchor" "middle")
                       (.attr "alignment-baseline" "middle")
                       (.attr "fill" "green")
                       (.attr "font-size" "24px")
                       (.attr "font-family" "sans-serif")
                       (.text "RID3")))}]
    }])
```	

Which will result in the following:

```html
<div id="some-id">
  <svg width="200" height="200" style="background-color: grey;">
    <g class="rid3-main-container">
      <g class="backround">
	<circle cx="100" cy="100" r="50">
	</circle>
      </g>
      <g class="foreground">
	<text x="100" y="100" text-anchor="middle" alignment-baseline="middle" fill="green" font-size="24px" font-family="sans-serif">RID3
	</text>
      </g>
    </g>
  </svg>
</div>
```

*Note: the order of pieces matters.  If we reversed the order of our text and circle, we wouldn't be able to see the text (because it would be behind the circle).*

---

Please check out the [demo](http://rid3.s3-website-us-west-1.amazonaws.com/) and its [source](https://github.com/gadfly361/rid3/tree/master/src/demo/rid3)

## License

```
The MIT License (MIT)

Copyright © 2017 Matthew Jaoudi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
