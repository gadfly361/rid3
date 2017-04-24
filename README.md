# rid3

Rid3: [**R**eagent](https://github.com/reagent-project/reagent) **i**nterface to [**d3**](https://d3js.org/).

[demo](http://rid3.s3-website-us-west-1.amazonaws.com/)

**Caution**: Rid3 is alpha status and should only be used for toy applications.  I fully anticipate breaking changes in the near future. However, with your help/feedback, hopefully we can get to a stable release.

To use rid3, add the following to the `:dependencies` vector in your project.clj file:

```clojure
[rid3.core "0.1.0-SNAPSHOT"]
```

## The Problem

In my experience, there is a lot of boilerplate involved when trying to get reagent (i.e., react) and d3 to play nicely together.  The crux of the problem is you only want to append containing g tags to the DOM during reagent's `component-did-mount` lifecycle method and not during the `component-did-update` lifecycle method. However, more often than not, you want d3 to modify the stuff contained in the g tag in the same manner whether or not the component just mounted or just updated.

## Rid3's Solution

Rid3 exposes a single reagent component, `viz`, with the aim to make 80% of what you'll likely want to make with d3 easier (bar charts, pie charts, scatter plots, etc.).  Rid3 attempts to achieve this by making containing g tags for you. The benefit of doing this is that you can now use the *same* function in the did-mount and did-update lifecycle methods.  However, rid3 goes a step further and will *default* the did-update lifecycle method to whatever you supply as the did-mount lifecycle method.

A minimal `viz` component will produce the following structure:

```
div#some-id
└── svg
    └── g.main-container
```

As you can see, there are three parts: 
- a div with an id
- an svg, and
- a g tag with a class of *main-container*. 

To produce the above, which is truly the uneventful and minimal way to go for a rid3, you'll need to:

```clojure
(ns foo.core
  (:require
   [rid3.core :as d3]))

(defn viz [ratom]
  [d3/viz
   {:id    "some-id"
    :ratom ratom
    :svg   {:did-mount (fn [node _]
                         (-> node
                             (.attr "width" 900)
                             (.attr "height" 500)))}
    }])
```

*Note: All viz components need to provide a `ratom`. All relevant data for the component should be stored here. If anything changes in this ratom, then rid3 will trigger a re-render of the viz component for you.*

However, to drive around the block, you'll want to put some stuff in your main container. Let's put a text element inside.  I have decided to call the things that go inside the main-container `pieces` (... naming is hard).

```clojure
div#some-id
└── svg
    └── g.main-container
        └── g.label
            text
```

Our updated viz component looks like this:

```clojure
(ns foo.core
  (:require
   [rid3.core :as d3]))

(defn viz [ratom]
  [d3/viz
   {:id    "some-id"
    :ratom ratom
    :svg   {:did-mount (fn [node _]
                         (-> node
                             (.attr "width" 900)
                             (.attr "height" 500)))}
    :pieces
    [{:kind      :elem
      :class     "label"
      :tag       "text"
      :did-mount (fn [node _]
                   (-> node
                       (.attr "fill" "green")
                       (.attr "font-size" "24px")
                       (.text "RID3")))}]
    }])
```

So `pieces` takes a vector of obects.  There are four kinds of objects:

- `elem` which is when you want to add an element to your main container like a text or a circle.
    - kind --> :elem
	- class --> string
	- tag --> "text", "circle", etc
	- did-mount --> (fn [node ratom] ... )
	- did-update -->  (fn [node ratom] ... )
	    - defaults to did-mount
	- children --> [ pieces ]
- `elem-with-data` which is when you want to add a series of elements that are joined to a dataset
    - kind --> :elem-with-data
	- class --> string
	- prepare-dataset --> (fn [ratom] ... )
	    - defaults to: (fn [ratom] (-> @ratom (get :dataset) clj->js))
	- tag --> "text", "circle", etc
	- did-mount --> (fn [node ratom] ... )
	- did-update -->  (fn [node ratom] ... )
	    - defaults to did-mount
	- children --> [ pieces ]
- `container` which is when you want to group *elem* or *elem-with-data*'s together.
    - kind --> :container
	- class --> string
	- did-mount --> (fn [node ratom] ... )
	- did-update -->  (fn [node ratom] ... )
	    - defaults to did-mount
	- children --> [ pieces ]
- `raw` which is when you want to either trigger some side-effect or have an escape hatch from the rid3
    - kind (:raw)
	- class
	- did-mount (fn [ratom] ... )
	- did-update (fn [ratom] ... )]
	    - no default

In this example, we:
- used an `elem` piece
- definded a class ("text") for the containing g tag
- defined the tag ("text") of the elment we want to add inside the containing g tag

We also added a `did-mount` function.  This is what will work on the `text` element.  As you can see, `did-mount` takes two arguments, the first is the DOM node and the second is the ratom we defined earlier.

If you look closely, when we added the text element, rid3 added a containg g tag with the class name *label* and placed the text element inside of it.  The reason rid3 adds a containing g tag with a class name is to be able select the correct node to pass to your `did-mount` function.

All pieces can also accept a `did-update` function, but if you don't define one, then rid3 will default it to your `did-mount` function.

---

TODO: complete documentation.

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
